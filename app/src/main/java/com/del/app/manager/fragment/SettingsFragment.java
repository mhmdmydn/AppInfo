package com.del.app.manager.fragment;

import com.del.app.manager.R;
import android.preference.PreferenceFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.preference.PreferenceScreen;
import android.preference.Preference;
import com.del.app.manager.util.MainUtil;
import android.preference.EditTextPreference;
import android.preference.SwitchPreference;
import android.preference.ListPreference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import com.del.app.manager.util.SharedPref;

public class SettingsFragment extends PreferenceFragment implements 
    SharedPreferences.OnSharedPreferenceChangeListener {
	
	private static String THEME = "theme";
	private static String NOTIF = "notif_key";
	private static String FOLDER = "folder_name";
	private SharedPreferences sharedPref;
	private SharedPref sp;
	
	
	public SettingsFragment newIsntance(){
		SettingsFragment settingFragment = new SettingsFragment();
		Bundle args = new Bundle();
		settingFragment.setArguments(args);
		return settingFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		sp = new SharedPref(getActivity());
		MainUtil.debugShared(sharedPref);
		setUpAppVersion();
	    setSummaryPath();
	}
	
	
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if(key.equals(THEME)){
			MainUtil.restartActivity(getActivity());
		}
		if(key.equals(FOLDER)){
			findPreference(FOLDER).setSummary(getResources().getString(R.string.storage) + sp.getPath());
		}
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
	
	
	private void setUpAppVersion(){
        try {
            PackageManager pm = getActivity().getPackageManager();

            PackageInfo packageInfo = pm.getPackageInfo(getActivity().getPackageName(), 0);

            findPreference("check_update").setSummary(packageInfo.versionName);

        } catch (PackageManager.NameNotFoundException e) {

        }
    }
	
	private void setSummaryPath(){
		EditTextPreference edp = (EditTextPreference) findPreference(FOLDER);
		try {
			if(sp.getPath() == null){
				
				edp.setSummary(getResources().getString(R.string.summary_path));
			}else{
				edp.setSummary(getResources().getString(R.string.storage) + sp.getPath());
			}
			
		} catch(Exception e){
			
		}
	}
}
