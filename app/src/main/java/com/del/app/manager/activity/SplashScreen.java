package com.del.app.manager.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import com.del.app.manager.R;
import android.util.Log;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import com.del.app.manager.util.SharedPref;
import androidx.appcompat.app.AppCompatDelegate;
import com.del.app.manager.util.MainUtil;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import com.del.app.manager.loader.DelAppUpdate;

public class SplashScreen extends BaseActivity {
 
    private ImageView img;
    private TextView text;
    private static String URL = "https://raw.githubusercontent.com/mhmdmydn/DaftarKoleksiAplikasi/main/test.json";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
		initListener();
        showPermission();
		
		MainUtil.changeActivityFont(this, "sans_medium");
	}
    
	@Override
	public void initView() {
		img = (ImageView)findViewById(R.id.app_logo);
		text = (TextView)findViewById(R.id.app_name);
	}

	@Override
	public void initLogic() {
		new DelAppUpdate(SplashScreen.this, new DelAppUpdate.UpdateListener(){

				@Override
				public void onResponse(int newVersion, String changeLog, final String UrlDownload) {
					if (DelAppUpdate.getCurrentVersionCode(SplashScreen.this) < newVersion) {
                        new AlertDialog.Builder(SplashScreen.this)
                            .setTitle("Update available")
                            .setMessage(changeLog)
                            .setCancelable(false)
                            .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DelAppUpdate.startDownloadApp(SplashScreen.this, UrlDownload);
                                }
                            })
                            .show();
                    } else{
                        new Handler().postDelayed(new Runnable(){
                                @Override
                                public void run() {
                                    Intent next = new Intent();
                                    next.setClass(getApplicationContext(), MainActivity.class);
                                    startActivity(next);
                                    finishAffinity();
                                }
                            }, 1500);
                    }
				}

				@Override
				public void onError(String error) {
					if(error == "No Connection"){
						new Handler().postDelayed(new Runnable(){
                                @Override
                                public void run() {
                                    Intent next = new Intent();
                                    next.setClass(getApplicationContext(), MainActivity.class);
                                    startActivity(next);
                                    finishAffinity();
                                }
                            }, 3500);
					}
				}
			}).execute(URL);
	}

	@Override
	public void initListener() {

	}
    
   private void showPermission(){
       if (ActivityCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
           Log.d("PLAYGROUND", "Permission is not granted, requesting");
           new AlertDialog.Builder(SplashScreen.this)
               .setTitle("Permission Required")
               .setMessage("This app requires external storage permission to save apk files.")
               .setCancelable(false)
               .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       ActivityCompat.requestPermissions(SplashScreen.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
                   }
               })
               .setNegativeButton("CLOSE", new DialogInterface.OnClickListener(){

                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                       finishAffinity();
                   }
               })
               .show();
             } else {
           Log.d("PLAYGROUND", "Permission is granted");
           initLogic();
       }
       
   }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PLAYGROUND", "Permission has been granted");
				initLogic();
            } else {
                Log.d("PLAYGROUND", "Permission has been denied or request cancelled");
				finishAffinity();
            }
        }
    }
    
    
}
