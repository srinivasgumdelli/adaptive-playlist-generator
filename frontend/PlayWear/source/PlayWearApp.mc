using Toybox.Communications as Comm;
using Toybox.WatchUi as Ui;
using Toybox.Graphics as Gfx;
using Toybox.System as Sys;
using Toybox.Lang as Lang;
using Toybox.Time.Gregorian as Calendar;
using Toybox.Sensor as Snsr;
using Toybox.Application as App;
using Toybox.Position as GPS;

class HeartRate extends Ui.View
{
    var string_HR;
    var graph_HR;
    var string_currentsong;

    //! Constructor
    function initialize()
    {	
        Snsr.setEnabledSensors( [Snsr.SENSOR_HEARTRATE] );
        Snsr.enableSensorEvents( method(:onSnsr) );
        graph_HR = new LineGraph( 20, 10, Gfx.COLOR_RED );

        string_HR = "---bpm"; 
        string_currentsong = "";
        
    	Comm.setMailboxListener( method(:onMail) );
    }

    //! Handle the update event
    function onUpdate(dc)
    {
        dc.setColor(Gfx.COLOR_BLACK, Gfx.COLOR_BLACK);
        dc.clear();

        dc.setColor(Gfx.COLOR_WHITE, Gfx.COLOR_TRANSPARENT );

        dc.drawText( 100, 90, Gfx.FONT_LARGE, string_HR, Gfx.TEXT_JUSTIFY_CENTER);

		dc.drawText(2, 15, Gfx.FONT_SMALL, string_currentsong, Gfx.TEXT_JUSTIFY_LEFT);

        graph_HR.draw( dc, [5,30], [200,129] );
    }

    function onSnsr(sensor_info)
    {
        var HR = sensor_info.heartRate;
        var bucket;
        if( sensor_info.heartRate != null )
        {
            string_HR = HR.toString() + "bpm";
           
            var listener = new CommListener();
     		Comm.transmit( HR.toString(), null, listener );

            //Add value to graph
            graph_HR.addItem(HR);
        }
        else
        {
            string_HR = "---bpm";
        }

        Ui.requestUpdate();
    }
    
    // Read song name sent by host app
    function onMail(mailIter)
    {
        var mail = mailIter.next();
        
		if(mail != null) {
			string_currentsong = mail;
		}

        Comm.emptyMailbox();
        Ui.requestUpdate();
    }

}

class CommListener extends Comm.ConnectionListener
{
    function onComplete()
    {
        Sys.println( "Heart rate sent to host app" );
    }

    function onError()
    {
        Sys.println( "Heart rate transmission failed" );
    }
}

class PlayWearApp extends App.AppBase {

    //! onStart() is called on application start up
    function onStart() {
    }

    //! onStop() is called when your application is exiting
    function onStop() {
    }

    //! Return the initial view of your application here
    function getInitialView() {
        return [ new HeartRate() ];
    }

}