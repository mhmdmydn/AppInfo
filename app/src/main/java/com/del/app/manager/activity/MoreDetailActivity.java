package com.del.app.manager.activity;

import com.del.app.manager.R;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;
import android.widget.LinearLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.AppBarLayout;
import java.io.File;
import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;
import android.graphics.drawable.Drawable;
import java.io.IOException;
import android.content.pm.PackageManager;
import com.del.app.manager.util.MainUtils;
import android.view.View;
import com.del.app.manager.util.ApkUtils;
import com.del.app.manager.task.BackupTask;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.del.app.manager.adapter.GroupAdapter;
import android.os.AsyncTask;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.ImageView;
import androidx.recyclerview.widget.DividerItemDecoration;
import com.del.app.manager.util.HelperSharedPref;
import androidx.appcompat.app.AppCompatDelegate;

public class MoreDetailActivity extends AppCompatActivity implements View.OnClickListener {
	
	private Toolbar toolbar;
	private ImageView Backdrop;
	private TextView appName, packName;
	private LinearLayout openApp, deleteApp, extractApp;
	private String packageName, appDirectory;
	private RecyclerView recyclerView;
	
	private LinearLayoutManager layoutManager;
	private GroupAdapter gAdapter;
	private ArrayList<String> titleList = new ArrayList<>();
	
	private ApkFile apkFile;
	private ApkMeta apkMeta;
	private String numMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			numMode = new HelperSharedPref(this).loadStringFromSharedPref("Theme");
			switch(numMode){
				case "0":
					AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
					break;
				case "1":
					AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
					break;
				case "2":
					AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
					break;
			}
		} catch (Exception e) {

		}
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more_detail);
		initView();
		initLogic();
		initListTitle();
	}

	private void initView() {
		toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Backdrop = (ImageView)findViewById(R.id.profile_image);
		appName = (TextView)findViewById(R.id.app_name);
		packName = (TextView)findViewById(R.id.pack_name);
		recyclerView = (RecyclerView)findViewById(R.id.rv_group);
		//Linear
		openApp = (LinearLayout) findViewById(R.id.open_app);
		openApp.setOnClickListener(this);
		deleteApp =(LinearLayout) findViewById(R.id.delete_app);
		deleteApp.setOnClickListener(this);
		extractApp =(LinearLayout) findViewById(R.id.extract_app);
		extractApp.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View v) {
		
		LinearLayout layout = (LinearLayout)v;
		
		switch(layout.getId()){
			case R.id.open_app:
				ApkUtils.openApp(getApplicationContext(), packageName);
				finish();
			break;
			
			case R.id.extract_app:
				new BackupTask(MoreDetailActivity.this).execute(new String[]{packageName});
			break;
			
			case R.id.delete_app:
				ApkUtils.uninstallAPK(getApplicationContext(), packageName);
				finish();
			break;
		}
	}
	
	private void initLogic() {
        
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("App Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View _v) {
                    onBackPressed();
                }
			});
        
		//Intent
		packageName = getIntent().getStringExtra("packageName");
		appDirectory = getIntent().getStringExtra("appDirectory");
		File dir = new File(appDirectory);
		try {
					apkFile = new ApkFile(dir);
					apkMeta = apkFile.getApkMeta();
					Drawable icon = getPackageManager().getApplicationIcon(packageName);
					Backdrop.setImageDrawable(icon);
					appName.setText(apkMeta.getName());
					packName.setText(apkMeta.getPackageName());
				} catch (IOException e) {
					
				}catch (PackageManager.NameNotFoundException e) {

				}
				
    }
	private void initListTitle(){
		titleList.add("Version Name");
		titleList.add("Version Code");
		titleList.add("Min SDK version");
		titleList.add("Target  SDK Version");
		titleList.add("UID");
		titleList.add("Apk Path");
		titleList.add("Apk Size");
		titleList.add("First Install");
		titleList.add("Last Update");
		titleList.add("List permissons");
		titleList.add("List Activity");
		titleList.add("Manifest");
		
		layoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(layoutManager);
		gAdapter = new GroupAdapter(MoreDetailActivity.this, titleList, packageName, appDirectory);
		recyclerView.setHasFixedSize(true);
		recyclerView.setAdapter(gAdapter);
		recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
		gAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
}
