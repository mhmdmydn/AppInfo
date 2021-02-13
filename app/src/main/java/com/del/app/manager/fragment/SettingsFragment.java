package com.del.app.manager.fragment;

import com.del.app.manager.R;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.EditTextPreference;
import android.preference.SwitchPreference;
import android.preference.ListPreference;
import com.del.app.manager.util.MainUtils;
import com.del.app.manager.util.HelperSharedPref;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.preference.PreferenceGroup;
import android.content.Intent;
import com.del.app.manager.activity.SplashScreen;

public class SettingsFragment extends PreferenceFragment implements
SharedPreferences.OnSharedPreferenceChangeListener {
	
	private HelperSharedPref sharedPref;
	private EditTextPreference etPref;
	private SwitchPreference switchPref;
	private ListPreference listPref;
	private Preference prefSendFeedback, checkupdate;
    
	public SettingsFragment(){
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
		sharedPref = new HelperSharedPref(getActivity());
		initSummary(getPreferenceScreen());
		
		checkupdate = (Preference)findPreference("check_update");
		checkupdate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){

				@Override
				public boolean onPreferenceClick(Preference p1) {
					//updaterTask = new UpdaterTask(getActivity(), true);
					return true;
				}
			});
		
		prefSendFeedback = (Preference)findPreference("report_bug");
		prefSendFeedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){

				@Override
				public boolean onPreferenceClick(Preference p1) {
					MainUtils.sendFeedback(getActivity());
					return true;
				}
			});
		
//		listPref = (ListPreference)findPreference("example_list");
//		listPref.setOnPreferenceChangeListener(new ListPreference.OnPreferenceChangeListener(){
//
//				@Override
//				public boolean onPreferenceChange(Preference pref, Object newValue) {
//                    listPref.setDefaultValue(newValue.toString());
//					pref.setSummary(listPref.getEntry());
//					try {
//						sharedPref.saveToSharedPref("Theme", newValue.toString());
//                        MainUtils.restart(getActivity());
//					} catch (Exception e) {
//					}
//					return true;
//				}
//			});
			
		switchPref = (SwitchPreference)findPreference("notif_key");
        try {
            switchPref.setDefaultValue(sharedPref.loadBooleanFromSharedPref("Notif"));
            switchPref.setSummary(sharedPref.loadBooleanFromSharedPref("Notif")== false ? "Disabled" : "Enabled");
        } catch (Exception e) {
            
        }
		switchPref.setOnPreferenceClickListener(new SwitchPreference.OnPreferenceClickListener(){

                @Override
                public boolean onPreferenceClick(Preference pref) {
                    boolean off= ((SwitchPreference) pref).isChecked();
                    try {
                        sharedPref.saveToSharedPref("Notif", off);
                        switchPref.setSummary(off == false ? "Disabled" : "Enabled");
                    } catch (Exception e) {
                        
                    }
                    return false;
                }
            });
	}
	
	
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
		updatePrefSummary(findPreference(key));
		
	}
	
	private void initSummary(Preference p) {
        if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummary(pGrp.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }
    }

	private void updatePrefSummary(Preference p) {
        if (p instanceof EditTextPreference) {
            etPref = (EditTextPreference) p;
            if (p.getTitle().toString().contains("DelAppManager"))
            {
                p.setSummary("DelAppManager");
            } else {
				try {
					sharedPref.saveToSharedPref("Setting", etPref.getText().trim());
				} catch (Exception e) {
					
				}
                p.setSummary(etPref.getText());
            }
        }
		
		if(p instanceof ListPreference){
			listPref = (ListPreference)p;
			listPref.setOnPreferenceChangeListener(new ListPreference.OnPreferenceChangeListener(){

					@Override
					public boolean onPreferenceChange(Preference pref, Object newValue) {
                        listPref.setDefaultValue(newValue.toString());
                        pref.setSummary(listPref.getEntry());
						try {
							sharedPref.saveToSharedPref("Theme", newValue.toString());
                            MainUtils.restart(getActivity());
                            
						} catch (Exception e) {
                            MainUtils.showMessage(getActivity(), e.toString());
						}
                        
						return true;
					}
				});
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
}
