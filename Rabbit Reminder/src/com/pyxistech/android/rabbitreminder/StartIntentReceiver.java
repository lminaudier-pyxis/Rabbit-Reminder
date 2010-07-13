package com.pyxistech.android.rabbitreminder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.pyxistech.android.rabbitreminder.services.AlertService;


public class StartIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent alertServiceIntent = new Intent(context, AlertService.class);
		
		if( isServiceEnabled(context) ) {
			context.startService(alertServiceIntent);
		}
	}
	
	private boolean isServiceEnabled(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getBoolean("rabbit reminder alert service", true);
	}
	
	
}
