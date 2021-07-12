package com.del.app.manager.activity;

import com.del.app.manager.R;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import com.del.app.manager.fragment.SettingsFragment;
import com.del.app.manager.util.SharedPref;
import androidx.appcompat.app.AppCompatDelegate;
import com.del.app.manager.util.MainUtil;


public class SettingActivity extends BaseActivity {
	
	private Toolbar toolbar;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		MainUtil.changeActivityFont(this, "sans_medium");
		
		initView();
		initLogic();
		initListener();
	}


	@Override
	public void initView() {
		toolbar =(Toolbar) findViewById(R.id.toolbar);

		//fragments
		getFragmentManager()
			.beginTransaction()
			.replace(R.id.setting_content, new SettingsFragment())
		    .commit();

	}

	@Override
	public void initLogic() {
		//MainUtil.changeActivityFont(this, "sans_medium");
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("Settings");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public void initListener() {
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View _v) {
					onBackPressed();
				}
			});
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
