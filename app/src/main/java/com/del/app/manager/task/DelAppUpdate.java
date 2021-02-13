package com.del.app.manager.task;

import android.content.Context;
import android.os.StrictMode;
import android.os.AsyncTask;
import android.util.Log;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import org.json.JSONObject;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONException;
import org.json.JSONArray;
import android.app.ProgressDialog;
import java.net.URLConnection;
import java.io.OutputStream;
import java.io.FileOutputStream;
import android.os.Environment;
import android.content.Intent;
import android.net.Uri;
import java.io.File;


/**
 * Author Ghodel
 */
public class DelAppUpdate extends AsyncTask<String, String, JSONObject> {

    public static String TAG = DelAppUpdate.class.getSimpleName();

    private Context context;
    private UpdateListener listener;
    private StrictMode.VmPolicy.Builder builder;

    public DelAppUpdate(Context context, UpdateListener listener){
        this.context = context;
        this.listener = listener;
        builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (context == null || listener == null){
            Log.d(TAG, "onPreExecute: context == null || listener == null || jsonUrl == null");
            listener.onError("Null");
            cancel(true);
        } else if (!isNetworkAvailable(context)) {
            listener.onError("No Connection");
            cancel(true);
        }
    }

    @Override
    protected JSONObject doInBackground(String... URL) {

        try {
            return new JSONObject(makeServiceCall(URL[0]));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        if(result != null){

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

                listener.onResponse(vName, sb.toString(), urlDownload);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else{
            listener.onError("Json Data Null");
        }
    }

    public static void startDownloadApp(final Context context, final String urlDownload){
        new AsyncTask<String, String, String>(){

            private ProgressDialog progressDialog;
            private String uniquename;
            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Please wait...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setCancelable(false);
                progressDialog.dismiss();
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                int count;
                uniquename = String.valueOf(System.currentTimeMillis());
                try {
                    URL url = new URL(params[0]);
                    URLConnection conection = url.openConnection();
                    conection.connect();
                    // getting file length
                    int lenghtOfFile = conection.getContentLength();

                    // input stream to read file - with 8k buffer
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);

                    // Output stream to write file

                    OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory()+"/Download/"+ uniquename);


                    byte data[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress(""+(int)((total*100)/lenghtOfFile));

                        // writing data to file
                        output.write(data, 0, count);
                    }

                    // flushing output
                    output.flush();

                    // closing streams
                    output.close();
                    input.close();

                } catch (Exception e) {
                    Log.e("catchError: ", e.getMessage());
                    Log.d("catchOutputFile","no output file");
                }

                return params[0];
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
                    intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory()+"/Download/"+ uniquename)), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        }.execute(urlDownload);

    }



    private String makeServiceCall(String reqUrl) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.85 Safari/537.36");
            conn.setRequestMethod("GET");
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("checking [" + url + "]" + "----->[OK]");
            } else {
                System.out.println("checking [" + url + "]" + "----->[BAD]" + "; Code--->" + responseCode);
            }

            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }


    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
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

    public static  int getCurrentVersionCode(Context context) {
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

    public static interface UpdateListener{
        void onResponse(int newVersion, String changeLog, String UrlDownload)
        void onError(String error)
    }

}
