package com.del.app.manager.adapter;

import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.Fragment;
import java.util.List;
import java.util.ArrayList;
import androidx.fragment.app.FragmentManager;

public class TabsFragmentPagerAdapter extends FragmentPagerAdapter {
	
	private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
	
	public TabsFragmentPagerAdapter(FragmentManager manager) {
        super(manager);
    }
	
	@Override
	public Fragment getItem(int position) {
		return mFragmentList.get(position);
	}
	
	

	@Override
	public int getCount() {
		return mFragmentList.size();
	}
	
    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}
