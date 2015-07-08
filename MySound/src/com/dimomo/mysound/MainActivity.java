package com.dimomo.mysound;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener, OnTouchListener, OnCompletionListener, OnBufferingUpdateListener{
	TextView t, t1;
	String track = "https://api.soundcloud.com/tracks/636890/stream?oauth_token=1-121740-68965868-12aa62d68fcaaf";
	private MediaPlayer mediaPlayer;
	private int mediaFileLengthInMilliseconds;
	private final Handler handler = new Handler();
	SeekBar seekBarProgress;
	int playPositionInMillisecconds = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		seekBarProgress = (SeekBar)findViewById(R.id.seekBar1);	
		seekBarProgress.setMax(99); // It means 100% .0-99
		seekBarProgress.setOnTouchListener(this);
		t = (TextView) findViewById(R.id.text);
		t1 = (TextView) findViewById(R.id.textView1);
		t.setText("play");
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnBufferingUpdateListener(this);
		mediaPlayer.setOnCompletionListener(this);
		t.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				new GetMyPlaylistAsyncTask().execute("https://api.soundcloud.com/me/playlists.json?oauth_token=1-121740-68965868-12aa62d68fcaaf");
				try {
					mediaPlayer.setDataSource(track);
					mediaPlayer.prepare();
//					mediaPlayer.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
				mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL
				
				if(!mediaPlayer.isPlaying()){
					mediaPlayer.start();
					t.setText("playing"+ mediaFileLengthInMilliseconds);
				}else {
					mediaPlayer.pause();
					t.setText("pause" +mediaFileLengthInMilliseconds);
				}
				primarySeekBarProgressUpdater();
			}
		});
		
	}
	 private void primarySeekBarProgressUpdater() {
		 seekBarProgress.setProgress((int)(((float)mediaPlayer.getCurrentPosition()/mediaFileLengthInMilliseconds)*100));
		 t1.setText(""+ (mediaFileLengthInMilliseconds/100 - playPositionInMillisecconds));// This math construction give a percentage of "was playing"/"song length"
			if (mediaPlayer.isPlaying()) {
				Runnable notification = new Runnable() {
			        public void run() {
			        	primarySeekBarProgressUpdater();
					}
			    };
			    handler.postDelayed(notification,1000);
	    	}
	    }
	private class GetMyPlaylistAsyncTask extends AsyncTask<String, Void, JSONArray> {

		@Override
		protected void onPreExecute() {
			Log.d("AsyncTask", "onPreExecute : " + this.getClass().getSimpleName());
//			pd = ProgressDialog.show(mContext, "", "Loading..", true);
		}

		@Override
		protected JSONArray doInBackground(String... urls) {
			if (urls.length > 0) {
				String url = urls[0];
				Log.i("BBB", url);
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(url);
				try {
					HttpResponse response = httpClient.execute(httpget);
					if (response != null) {
						// If status is OK 200
						if (response.getStatusLine().getStatusCode() == 200) {
							String result = EntityUtils.toString(response.getEntity());
							// Convert the string result to a JSON Object
							Log.i("BBB", result);
							return new JSONArray(result);
						}
					}
				} catch (Exception e) {
					Log.e("Authorize", "Error Http response " + e.getLocalizedMessage());
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONArray data) {
			Log.d("AsyncTask", "onPostExecute : " + this.getClass().getSimpleName());
//			if (pd != null && pd.isShowing()) {
//				pd.dismiss();
//			}
			if (data != null) {
				Log.e("data", "" + data);
			}
			t.setText(data.toString());
		}
	};
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		seekBarProgress.setSecondaryProgress(percent);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(v.getId() == R.id.seekBar1){
			/** Seekbar onTouch event handler. Method which seeks MediaPlayer to seekBar primary progress position*/
			if(mediaPlayer.isPlaying()){
		    	SeekBar sb = (SeekBar)v;
				playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
				t1.setText(""+playPositionInMillisecconds/3600);
				mediaPlayer.seekTo(playPositionInMillisecconds);
			}
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		t.setText("play");
	}

}
