package com.del.app.manager.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.View;
import com.del.app.manager.adapter.ViewPagerAdapter;
import com.del.app.manager.fragment.SystemFragment;
import com.del.app.manager.R;
import com.del.app.manager.util.MainUtil;
import android.os.Handler;
import androidx.appcompat.app.AppCompatDelegate;
import com.del.app.manager.util.SharedPref;
import com.del.app.manager.fragment.InstalledFragment;
import android.content.DialogInterface;

public class MainActivity extends BaseActivity {

    private Toolbar toolbar;
	private TabLayout tabLayout;
    private ViewPager viewPager;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView(); 
		initLogic();
		initListener();
		MainUtil.changeActivityFont(this, "sans_medium");
    }
	
	private void addFragmentsToViewPager(final ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(InstalledFragment.newInstance(false), "Installed");
        adapter.addFragment(SystemFragment.newInstance(true), "System");
        viewPager.setAdapter(adapter);
		viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
		tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }


	@Override
	public void initView() {
		toolbar=(Toolbar)findViewById(R.id.toolbar);
		viewPager = (ViewPager) findViewById(R.id.view_pager);
		tabLayout = (TabLayout) findViewById(R.id.tabs);
	}

	@Override
	public void initLogic() {
		//MainUtil.changeActivityFont(this, "sans_medium");
		setSupportActionBar(toolbar);
		addFragmentsToViewPager(viewPager);
		tabLayout.setupWithViewPager(viewPager);
        
	}

	@Override
	public void initListener() {

	}
	
	@Override
	public void onBackPressed() {
		finishAffinity();
	}
	
}

