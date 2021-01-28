package com.del.app.manager.task;

import android.content.Context;
import android.os.AsyncTask;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.BufferedReader;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;
import android.util.Log;
import java.io.InputStreamReader;
import com.kaopiz.kprogresshud.KProgressHUD;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;
import android.util.Property;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import com.del.app.manager.BuildConfig;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageManager;
import android.app.ProgressDialog;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import java.io.File;
import androidx.core.content.FileProvider;
import com.del.app.manager.util.MainUtils;
import android.os.StrictMode;
import com.del.app.manager.util.ApkUtils;


public class UpdaterTask {
	
	private static String TAG = UpdaterTask.class.getSimpleName();
	private Context con;
	private static String URL = null;
	private AsyncTask<String, Void, String> getUpdate;
	private KProgressHUD hud; 
	private StrictMode.VmPolicy.Builder builder;
	private Boolean inSetting;
    public Boolean updateInfo;
	
	public UpdaterTask(Context con,Boolean inSetting){
		this.con = con;
		this.URL = "https://raw.githubusercontent.com/MuhammadMayudin/myapk/main/test.json";
		this.inSetting = inSetting;
		builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());
		checkVersion();
        updateInfo = false;
	}
	
	public void checkVersion(){
		getUpdate = new AsyncTask<String, Void, String>(){
        
			@Override
			protected void onPreExecute(){
				super.onPreExecute();
                if(inSetting == true){
                    hud = new KProgressHUD(con);
                    hud = KProgressHUD.create(con);
                    hud.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
                    hud.show();
                }
			}
			
			@Override
			protected String doInBackground(String... params) {
				HttpURLConnection urlConnection = null;
				InputStream is = null;
				BufferedReader br = null;
				String result = null;
				
				try {
					URL url = new URL(params[0]);
					urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");
					
					is = urlConnection.getInputStream();
					br = new BufferedReader(new InputStreamReader(is));

					StringBuilder strBuilder = new StringBuilder();
					String line;

					while ((line = br.readLine()) != null) {
						strBuilder.append(line);
					}

					result = strBuilder.toString();
					
				} catch (MalformedURLException e) {
					Log.d(TAG, e.toString());
				}catch (IOException e2) {
					Log.d(TAG, e2.toString());
				}finally {

					if (br != null) {
						try { br.close(); } catch (IOException ignored) {}
					}

					if (is != null) {

						try {
							is.close();
						} catch (IOException ignored) {}
					}

					if (urlConnection != null) {
						urlConnection.disconnect();
					}
				}
				
				return result;
			}
			
			@Override
			protected void onPostExecute(String result){
				super.onPostExecute(result);
				int versionCode = ApkUtils.getVersionCode(con, "com.del.app.manager");
				if(hud != null && hud.isShowing()){
					hud.dismiss();			
				if (result != null) {
					try {
						JSONObject obj = new JSONObject(result);
						int urlVersionName = obj.getInt("versionName");
						String urlVersionCode = obj.getString("versionCode");
						String urlNewApk = obj.getString("url");
						JSONArray arr = obj.getJSONArray("release");
						StringBuilder sb = new StringBuilder();
						for(int i = 0; i < arr.length(); i++){
							sb.append(arr.getString(i).trim());
							if(i != arr.length() -1){
								sb.append(System.getProperty("line.separator"));
							}
						}
						
						if(versionCode < urlVersionName){
                            updateInfo = true;
							showDialog(sb.toString(), urlNewApk);
						} else{
                            updateInfo = false;
							if(inSetting == true){
								MainUtils.showMessage(con, "This is the latest version");
							}
							
						}
							
					} catch (JSONException e) {
						Log.d(TAG, e.toString());
					}
				}
			}
		}
		}.execute(URL);
	}
	
	public void showDialog(final String msg, final String url){
		AlertDialog.Builder builder = new AlertDialog.Builder(con);
		builder.setTitle("App Update");
		builder.setMessage(msg);
		builder.setCancelable(false);
		builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					//do things
					downloadNewApp(url);
					dialog.dismiss();
				}
			});

		builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					//do things
					dialog.dismiss();
				}
			});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void downloadNewApp(String UrlApk){
		new AsyncTask<String, String, String>(){
			private ProgressDialog progressDialog;
			@Override
			protected void onPreExecute(){
				super.onPreExecute();
				progressDialog = new ProgressDialog(con);
				progressDialog.setMessage("Please wait...");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressDialog.setCancelable(false);
				progressDialog.dismiss();
				progressDialog.show();
			}
			
			
			@Override
			protected String doInBackground(String... params) {
				int count = 0;
				
				try {
					URL urlDown = new URL(params[0]);
					HttpURLConnection urlCon= (HttpURLConnection) urlDown.openConnection();
					int lenghtOfFile = urlCon.getContentLength();
					InputStream input = new BufferedInputStream(urlDown.openStream());
					OutputStream output = new FileOutputStream("/sdcard/myapp.apk");
					
					byte data[] = new byte[1024];
					
					long total = 0;
					
					while ((count = input.read(data)) != -1) 
					{
						total += count;
						publishProgress(""+(int)((total*100)/lenghtOfFile));
						output.write(data, 0, count);
					}

					output.flush();
					output.close();
					input.close();
					
				} catch (MalformedURLException e) {
					
				}catch (IOException e) {}

				return null;
			}
			
			@Override
			protected void onProgressUpdate(String... progress) {
				super.onProgressUpdate(progress);
				progressDialog.setProgress(Integer.parseInt(progress[0]));
			}
			
			@Override
			protected void onPostExecute(String install){
				super.onPostExecute(install);
				if(progressDialog != null && progressDialog.isShowing()){
					progressDialog.dismiss();
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/myapp.apk")), "application/vnd.android.package-archive");
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					con.startActivity(intent);
				}
			}
		}.execute(UrlApk);
	}
}
