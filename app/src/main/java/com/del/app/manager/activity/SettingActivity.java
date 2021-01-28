package com.del.app.manager.activity;

import com.del.app.manager.R;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import com.del.app.manager.fragment.FragmentSetting;
import com.del.app.manager.util.HelperSharedPref;
import androidx.appcompat.app.AppCompatDelegate;


public class SettingActivity extends AppCompatActivity {
	
	private Toolbar toolbar;
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
		setContentView(R.layout.activity_setting);
		initView();
		initLogic();
	}

	private void initLogic() {
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("Settings");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View _v) {
					onBackPressed();
				}
			});
		//fragments
		getFragmentManager()
			.beginTransaction()
			.replace(R.id.setting_content,
					 new FragmentSetting()).commit();
	}

	private void initView() {
		toolbar =(Toolbar) findViewById(R.id.toolbar);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
