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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends Activity {
	TextView t;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		t = (TextView) findViewById(R.id.text);
		t.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new GetMyPlaylistAsyncTask().execute("https://api.soundcloud.com/me/playlists.json?oauth_token=1-121740-68965868-12aa62d68fcaaf");
			}
		});
		
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

}
