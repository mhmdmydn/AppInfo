package com.del.app.manager.task;

import android.os.AsyncTask;
import org.json.JSONObject;
import android.content.Context;
import java.net.URL;
import java.io.InputStream;
import java.io.BufferedReader;
import android.util.Log;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import java.nio.charset.Charset;
import java.io.InputStreamReader;
import org.json.JSONException;
import org.json.JSONArray;
import android.app.ProgressDialog;
import java.net.HttpURLConnection;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.io.IOException;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import java.io.File;
import android.os.StrictMode;
import com.del.app.manager.Interface.UpdateListener;

public class ApkUpdate extends AsyncTask <Void, String, JSONObject> {
    
     private String TAG = getClass().getSimpleName();
    
     private Context context;
     private String jsonUrl;
     private UpdateListener listener;
     private StrictMode.VmPolicy.Builder builder;
     
     
    public ApkUpdate(Context context, String jsonUrl, UpdateListener listener) {
        this.context = context;
        this.jsonUrl = jsonUrl;
        this.listener = listener;
        builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        
        if (context == null || listener == null || jsonUrl == null) {
            Log.d(TAG, "onPreExecute: context == null || listener == null || jsonUrl == null");
            cancel(true);
        } else if (!isNetworkAvailable(context)) {
            listener.onError("Please check your network connection");
            cancel(true);
        } else if (jsonUrl.isEmpty()) {
            listener.onError("Please provide a valid JSON URL");
            cancel(true);
        }
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        try {
            URL url = new URL(jsonUrl);
            InputStream is = url.openStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = bufferedReader.read()) != -1) {
                sb.append((char) cp);
            }

            Log.d(TAG, "doInBackground: JSON DATA: " + sb.toString());

            return new JSONObject(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        
        if (result != null) {
            try {
            JSONObject obj = result;
            int vName = obj.getInt("versionName");
            String vCode = obj.getString("versionCode");
            String urlDownload = obj.getString("url");
            
            JSONArray arr = obj.getJSONArray("release");
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < arr.length(); i++){
                sb.append(arr.getString(i).trim());
                if(i != arr.length() -1){
                    sb.append(System.getProperty("line.separator"));
                }
            }
            
            listener.onJsonDataReceived(vName, sb.toString(), urlDownload);
            
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            listener.onError("JSON data null");
        }
    }
   
    /**
     * Check Network
     */
    
    private boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager
            = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    
    
    /**
     * Checked current version
     */
    
    public static int getCurrentVersionCode(Context context) {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (pInfo != null)
            return pInfo.versionCode;

        return 0;
    }
    
    
    /**
    * Asynctask to download new app
    */
    
    
    public static void downloadNewApp(final Context con, final String UrlApk){
        
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
