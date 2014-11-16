package com.play;

import java.util.Random;

import android.media.AudioManager;
import android.media.MediaPlayer;

public class Player {
	
	public static boolean playing = false;
	public static MediaPlayer mediaPlayer = new MediaPlayer();
	
	// poll for new song 4s before the current song is completed.
	public int buffer = 4;
	
	
	public void play(int length, String title, String url) {
		
		playing = true;
		
		if(mediaPlayer != null) {
			mediaPlayer.release();
		}
		
		mediaPlayer = new MediaPlayer();
		
		try {
			/* int n = new Random().nextInt(10);
			if(n > 7) {
				url = "http://freedownloads.last.fm/download/433759890/Morning%2Blight.mp3"; 
			} else if (n > 4) {
				url = "http://freedownloads.last.fm/download/65856051/The%2BEternal%2BSink%2BOf%2BDeath.mp3"; 
			} else {
				url = "https://d1l04yptno92u8.cloudfront.net/DigitalMusicDeliveryService/CloudDriveEmbed.mp3?e=1416118806&cid=A1TJOU0T358PE2&cdoid=0a736d01-8a9e-44d1-91e3-9192e81585dc&httpHeader_user-agent=Mozilla%2F5.0%20%28Macintosh%3B%20Intel%20Mac%20OS%20X%2010.9%3B%20rv%3A33.0%29%20Gecko%2F20100101%20Firefox%2F33.0&h=9dd7b8c528b5187d16f953768854a6c93ddfb641d57101863236d393e3004dd6"; 
			} */
			
			System.out.println(url+"---->");
			
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setDataSource(url);
			mediaPlayer.prepare();
			mediaPlayer.start(); 
			
			Thread.sleep((length + 2 - 2) * 1000);
		} catch(Exception e) {  
			e.printStackTrace();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		
		playing = false;
	}
	
}
