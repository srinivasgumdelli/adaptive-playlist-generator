package com.play;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.garmin.android.connectiq.ConnectIQ;
import com.garmin.android.connectiq.ConnectIQ.ConnectIQListener;
import com.garmin.android.connectiq.ConnectIQ.IQApplicationEventListener;
import com.garmin.android.connectiq.ConnectIQ.IQDeviceEventListener;
import com.garmin.android.connectiq.ConnectIQ.IQMessageStatus;
import com.garmin.android.connectiq.ConnectIQ.IQSdkErrorStatus;
import com.garmin.android.connectiq.IQApp;
import com.garmin.android.connectiq.IQDevice;
import com.garmin.android.connectiq.IQDevice.IQDeviceStatus;
import com.garmin.android.connectiq.exception.ServiceUnavailableException;

public class PlayActivity extends Activity {

    private static final String TAG = "Play";

    private ConnectIQ mConnectIQ;
    private TextView  tvSdkStatus;
    private TextView  tvConnectionStatus;
    private TextView  tvDevices;
    private TextView  tvCurrentSong;

    private IQDevice mDevice;
    private IQApp    mApp;

    /**
     * Listener for SDK specific events.
     */
    ConnectIQListener listener = new ConnectIQListener() {

        /**
         * Received when the SDK is ready for additional method calls after calling initialize().
         */
        @Override
        public void onSdkReady() {
            tvSdkStatus.setText(String.format(getString(R.string.initialized_format), mConnectIQ.getAdbPort()));

            /**
             * Retrieve a list of available (aka currently connected via Garmin Connect Mobile) to display
             * to the user.
             */
            List<IQDevice> devicelist = mConnectIQ.getAvailableDevices();

            if (devicelist.size() == 0) {
                tvDevices.setText(R.string.no_paired_devices);
            } else {
                StringBuilder builder = new StringBuilder();
                for (IQDevice device : devicelist) {
                    builder.append(device.getFriendlyName());
                    builder.append("\r\n");
                }
                tvDevices.setText(builder.toString());
            }

            /**
             * Retrieves a list of paired ConnectIQ devices.  This will return a device even if it is not
             * currently connected but is paired with the Garmin Connect Mobile application.  This allows
             * us to register for events to be notified when a device connects or disconnects.
             */
            List<IQDevice> deviceList = mConnectIQ.getPairedDevices();

            StringBuilder builder = new StringBuilder();
            for (IQDevice device : deviceList) {

                /**
                 * Register for event for each device.   This will allow us to receive connect / disconnect
                 * notifications for the devices.  This can be useful if wanting to display information
                 * regarding the currently connected device.
                 */
                mConnectIQ.registerForEvents(device, eventListener, mApp, appEventListener);

                builder.append(device.getFriendlyName());
                builder.append("\r\n");
            }

            tvDevices.setText(builder.toString());

            /**
             * Check the connection status.  This is necessary because our call
             * to registerForEvents will only notify us of changes from the devices
             * current state.  So if it is already connected when we register for
             * events, we will not be notified that it is connected.
             *
             * For this sample we are just going to deal with the first device
             * from the list, but it is probably better to look at the status
             * for each device if multiple and possibly display a UI for the
             * user to select which device they want to use if multiple are
             * connected.
             */
            IQDevice device = deviceList.get(0);
            try {
                IQDeviceStatus status = mConnectIQ.getStatus(device);
                updateStatus(status);
            } catch (IllegalStateException e) {
                Log.e(TAG, "Illegal state calling getStatus", e);
            } catch (ServiceUnavailableException e) {
                Log.e(TAG, "Service Unavailable", e);
            }
        }

        /**
         * Called if the SDK failed to initialize.  Inspect IQSdkErrorStatus for specific
         * reason initialization failed.
         */
        @Override
        public void onInitializeError(IQSdkErrorStatus status) {
            tvSdkStatus.setText(status.toString());
        }

        /**
         * Called when the ConnectIQ::shutdown() method is called.  ConnectIQ is a singleton so
         * any call to shutdown() will uninitialize the SDK for all references.
         */
        @Override
        public void onSdkShutDown() {
            tvSdkStatus.setText("Shut Down");
        }
    };

    /**
     * Listener for receiving device specific events.
     */
    IQDeviceEventListener eventListener = new IQDeviceEventListener() {
        @Override
        public void onDeviceStatusChanged(IQDevice device, IQDeviceStatus newStatus) {
            updateStatus(newStatus);
        }
    };

    /**
     * Listener for receiving events from applications on a device.
     */
    IQApplicationEventListener appEventListener = new IQApplicationEventListener() {
 
        @Override
        public void onMessageReceived(IQDevice device, IQApp fromApp, List<Object> messageData, IQMessageStatus status) {
            StringBuilder builder = new StringBuilder();
            for (Object obj : messageData) {
                if (obj instanceof String) {
                    builder.append((String)obj);
                } else {
                    builder.append("Non string object received");
                }
                builder.append("\r\n"); 
            }
            
            System.out.println("heartrate-"+builder.toString());
            if(!Player.playing) {
            	System.out.println("new song .. ");
            	new NetworkTask().execute(new String[] {builder.toString()});
            } else {
            	System.out.println("skipped .. ");
            }
        }
    };

    public HttpResponse getSong(String heartrate) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = null;
        
        try {
			URI url = new URI("http://gumdelli1.pagekite.me/suggestion?heartrate="+heartrate.replaceAll("\r\n", ""));
			HttpGet httpGet = new HttpGet(url);
            response = httpclient.execute(httpGet); 
        } catch (URISyntaxException e1) {
			e1.printStackTrace();
		} catch (ClientProtocolException e) {
            response = null;
        } catch (IOException e) {
            response = null;
        } 
        
        return response;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        tvSdkStatus = (TextView)findViewById(R.id.sdkstatus);
        tvConnectionStatus = (TextView)findViewById(R.id.connectionstatus);
        tvDevices = (TextView)findViewById(R.id.devices);
        tvCurrentSong = (TextView)findViewById(R.id.songname);

        // Get an instance of ConnectIQ that does BLE simulation over ADB to the simulator.
        mConnectIQ = ConnectIQ.getInstance(ConnectIQ.IQCommProtocol.SIMULATED_BLE);
        mApp = new IQApp("", "Play", 1);
    }

    @Override
    public void onResume() {
        super.onResume();

        /**
         * Initializes the SDK.  This must be done before making any calls that will communicate with
         * a Connect IQ device.
         */
        mConnectIQ.initialize(this, true, listener);
    } 

    @Override
    public void onPause() {
        super.onPause();

        /**
         * Shutdown the SDK so resources and listeners can be released.
         */
        if (isFinishing()) {
            mConnectIQ.shutdown();
        } else {
            /**
             * Unregister for all events.  This is good practice to clean up to
             * allow the SDK to free resources and not listen for events that
             * no one is interested in.
             *
             * We do not call this if we are shutting down because the shutdown
             * method will call this for us during the clean up process.
             */
            mConnectIQ.unregisterAllForEvents();
        }
    }

    /**
     * When the device sends us a message we will just display a toast notification
     */
    private void displayMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void updateStatus(IQDeviceStatus newStatus) {
        switch(newStatus) {
            case CONNECTED:
                tvConnectionStatus.setText(R.string.connection_status_connected);
                break;
            case NOT_CONNECTED:
                tvConnectionStatus.setText(R.string.connection_status_not_connected);
                break;
            case NOT_PAIRED:
                tvConnectionStatus.setText(R.string.connection_status_not_paired);
                break;
            case UNKNOWN:
                tvConnectionStatus.setText(R.string.connection_status_unknown);
                break;
        }
    }
    
    private class NetworkTask extends AsyncTask<String, Void, String> {

    	HttpResponse response;
    	
        @Override
        protected String doInBackground(String... params) {
        	response = getSong(params[0]);
        	if(response != null) {
            HttpEntity entity = response.getEntity();
            
            if(entity != null) {
            	try {
            		String responseString = EntityUtils.toString(entity);
	            	JSONObject json = new JSONObject(responseString);
	            	final String title = json.getString("song_name")+"-"+(new Random().nextInt(100));
	            	final String titleAndHeartRate = title +", heart rate - "+params[0];
	            	final int length = json.getInt("duration");
	            	final String url = json.getString("track-url");
		            
		            // Start player
		            Player player = new Player();
		           
		            final IQMessageStatus status = mConnectIQ.sendMessage(mDevice, mApp, title);
		            
		            runOnUiThread(new Runnable() {
		                public void run() {
		                    tvCurrentSong.setText(titleAndHeartRate);
		                    
				            if (status != IQMessageStatus.SUCCESS) {
				                displayMessage(String.format(getString(R.string.message_send_error_format), status.name()));
				            } else {
				                displayMessage(getString(R.string.message_sent));
				            }

		                }
		            });
		            
		            player.play(length, titleAndHeartRate, url);
		            
		            runOnUiThread(new Runnable() {
		                public void run() {
		                    tvCurrentSong.setText("Searching .. ");
		                }
		            });
		            
            	} catch(JSONException ex) {
            		ex.printStackTrace();
            	} catch(IOException ex) {
            		ex.printStackTrace();
            	}
            }
        	}
        	return null;
        }

        @Override
        protected void onPostExecute(String result) {}

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
