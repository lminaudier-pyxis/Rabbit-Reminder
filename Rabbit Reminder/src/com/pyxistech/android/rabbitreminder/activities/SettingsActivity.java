package com.pyxistech.android.rabbitreminder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;

import com.pyxistech.android.rabbitreminder.R;
import com.pyxistech.android.rabbitreminder.services.AlertService;

public class SettingsActivity extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		
		CheckBoxPreference check = (CheckBoxPreference) findPreference("rabbit reminder alert service");
		check.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				Intent alertServiceIntent = new Intent(getBaseContext(), AlertService.class);
				
				if( !isServiceActive(preference) ) {
					startService(alertServiceIntent);
				} else {
					stopService(alertServiceIntent);
				}
				
				return true;
			}

			private boolean isServiceActive(Preference preference) {
				return ((CheckBoxPreference) preference).isChecked();
			}
		});
	}

}
