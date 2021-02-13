package com.del.app.manager.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.View;
import com.del.app.manager.adapter.TabsFragmentPagerAdapter;
import com.del.app.manager.fragment.UserFragment;
import com.del.app.manager.fragment.SystemFragment;
import com.del.app.manager.R;
import com.del.app.manager.util.MainUtils;
import android.os.Handler;
import androidx.appcompat.app.AppCompatDelegate;
import com.del.app.manager.util.HelperSharedPref;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
	private TabLayout tabLayout;
    private ViewPager viewPager;
	private Boolean pressAgainToExit = false;
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
        setContentView(R.layout.activity_main);
        initView(); 
		initLogic();
		setSupportActionBar(toolbar);
        
    }
    private void initView(){
		toolbar=(Toolbar)findViewById(R.id.toolbar);
		viewPager = (ViewPager) findViewById(R.id.view_pager);
		tabLayout = (TabLayout) findViewById(R.id.tabs);
    }
	
	private void initLogic(){
		addFragmentsToViewPager(viewPager);
		tabLayout.setupWithViewPager(viewPager);
	}
	
	
	private void addFragmentsToViewPager(final ViewPager viewPager) {
        TabsFragmentPagerAdapter adapter = new TabsFragmentPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new UserFragment(), "Installed");
        adapter.addFragment(new SystemFragment(), "System");
        viewPager.setAdapter(adapter);
		viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
		tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

				@Override
				public void onTabSelected(TabLayout.Tab tab) {
					viewPager.setCurrentItem(tab.getPosition());
				}

				@Override
				public void onTabUnselected(TabLayout.Tab p1) {
					
				}

				@Override
				public void onTabReselected(TabLayout.Tab p1) {
					
				}
			});
    }

	@Override
	public void onBackPressed() {
		if(pressAgainToExit){
		super.onBackPressed();
		}
		this.pressAgainToExit = true;
		MainUtils.showMessage(this, "Press again to exit");
		
		new Handler().postDelayed(new Runnable(){

				@Override
				public void run() {
					pressAgainToExit = false;
				}
			}, 2000);
	}
}
