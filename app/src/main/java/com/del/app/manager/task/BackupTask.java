package com.del.app.manager.task;

import android.os.AsyncTask;
import com.kaopiz.kprogresshud.KProgressHUD;
import android.content.Context;
import com.del.app.manager.model.ModelApk;
import java.io.File;
import android.os.Environment;
import com.del.app.manager.R;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.channels.FileChannel;
import java.io.IOException;
import com.del.app.manager.util.MainUtils;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import java.text.DecimalFormat;
import java.io.PrintStream;
import com.del.app.manager.util.HelperSharedPref;
import android.os.StrictMode;


public class BackupTask extends AsyncTask<String, Integer, String>  {
	
	private KProgressHUD hud;
	private Context con;
	private ResolveInfo info;
	private HelperSharedPref sharedPrefFolder;
    
	public BackupTask(Context con){
		this.con = con;
		sharedPrefFolder = new HelperSharedPref(con);
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(con != null){
		
		hud = KProgressHUD.create(con);
		hud.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
		hud.show();
		}
	}

	@Override
	protected String doInBackground(String... params) {
		String fileName = null;
		try {
		Intent intent = new Intent("android.intent.action.MAIN", null);
		intent.setPackage(params[0]);
		intent.addCategory("android.intent.category.LAUNCHER");
		info = con.getPackageManager().resolveActivity(intent, 128);
		File decimalFormat = new File(info.activityInfo.applicationInfo.publicSourceDir);
		String charSquence = info.loadLabel(con.getPackageManager()).toString();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(Environment.getExternalStorageDirectory().toString());
			try {
				stringBuilder.append("/"+ sharedPrefFolder.loadStringFromSharedPref("Setting"));
			} catch (Exception e) {
				
			}
		File file = new File(stringBuilder.toString());
		file.mkdirs();
		StringBuilder stringBuilder2 = new StringBuilder();
		stringBuilder2.append(file.getPath());
		stringBuilder2.append("/");
		stringBuilder2.append(charSquence);
		stringBuilder2.append("_backup.apk");
		fileName = stringBuilder2.toString();
		File file2 = new File(stringBuilder2.toString());
		file2.createNewFile();
		FileInputStream  fis = new FileInputStream(decimalFormat);
		FileOutputStream fos = new FileOutputStream(file2);
		byte[] bArr = new byte[1024];
		while(true){
			int read = fis.read(bArr);
			if(read<=0){
				break;
			}
			fos.write(bArr, 0, read);
		}
		fis.close();
		fos.close();
		} catch(FileNotFoundException e){
			PrintStream printStream = System.out;
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(e.getMessage());
            stringBuilder3.append(" in the specified directory.");
            printStream.println(stringBuilder3.toString());
		} catch (IOException e2) {
			System.out.println(e2.getMessage());
		}

		return fileName;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if(hud != null && hud.isShowing()){
            try {
                if (sharedPrefFolder.loadBooleanFromSharedPref("Notif") == true){
                    MainUtils.showNotif(con, result);
                }
            } catch (Exception e) {
                
            }
            hud.dismiss();
        }
	}
}
