package com.pyxistech.android.rabbitreminder.services;

import java.util.Vector;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;

import com.pyxistech.android.rabbitreminder.R;
import com.pyxistech.android.rabbitreminder.activities.AlertActivity;
import com.pyxistech.android.rabbitreminder.activities.SettingsActivity;
import com.pyxistech.android.rabbitreminder.models.AlertItem;
import com.pyxistech.android.rabbitreminder.models.AlertList;

public class AlertService extends Service {
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart (Intent intent, int startId) {
		super.onStart(intent, startId);
		
		if (isThreadNotStarted()) {
			startThread();
			startOnGoingNotification();
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		interruptThread();
		stopOnGoingNotification();
	}

	private boolean isThreadNotStarted() {
		return notificationThread == null;
	}

	private void startThread() {
		notificationThread = new AlertThread(this);
		notificationThread.setInterrupted(false);
		notificationThread.start();
	}

	private void interruptThread() {
		notificationThread.setInterrupted(true);
		notificationThread = null;
	}

	private void startOnGoingNotification() {
		Intent intent = new Intent(this, SettingsActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
		
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.icon, getString(R.string.alert_service_ongoing_notification_info_message), System.currentTimeMillis());
		notification.setLatestEventInfo(this, getString(R.string.alert_service_ongoing_notification_title), getString(R.string.alert_service_ongoing_notification_description), pendingIntent);
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		
		nm.notify(ONGOING_SERVICE_NOTIFICATION_ID, notification);
	}
	
	private void stopOnGoingNotification() {
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(ONGOING_SERVICE_NOTIFICATION_ID);
	}
	
	private static final int ONGOING_SERVICE_NOTIFICATION_ID = 0;
	private AlertThread notificationThread;
}

class AlertThread extends Thread {

	private final String[] PROJECTION = new String[] {
        AlertList.Items._ID, // 0
        AlertList.Items.NAME, // 1
        AlertList.Items.DONE, // 2
        AlertList.Items.LATITUDE, // 3
        AlertList.Items.LONGITUDE // 4
    };
    
    public AlertThread(AlertService context) {
    	this.context = context;
    }
    
	public void run() {
		Looper.prepare();
		
		while (!interrupted) {
			Location myLocation = getLocation();
			Vector<AlertItem> undoneAlerts = getUndoneAlerts();
			
			if (myLocation != null) {
				Vector<AlertItem> localAlerts = getLocalUndoneAlerts(myLocation, undoneAlerts);
				
				if (localAlerts.size() > 0) {
					Intent intent = buildNotificationIntent(localAlerts);
					notifyUser(localAlerts.size(), intent);
					threadWait(10000);
				}
			}

			threadWait(NOTIFICATION_REFRESH_RATE);
		}
	}

	private Intent buildNotificationIntent(Vector<AlertItem> taskItems) {
			Intent intent = new Intent(context, AlertActivity.class);
	//		intent.putExtra("index", (int) -1);
	//		intent.putExtra("item", taskItem);
			return intent;
		}

	private Vector<AlertItem> getLocalUndoneAlerts(Location myLocation, Vector<AlertItem> tasks) {
		Vector<AlertItem> localTasks = new Vector<AlertItem>();
		for (AlertItem taskItem : tasks) {
			Location taskItemLocation = buildLocationFromTaskItem(taskItem);
			
			if (isTaskLocationNearMyLocation(myLocation, taskItemLocation)) {
				localTasks.add(taskItem);
			}
		}
		return localTasks;
	}

	private boolean isTaskLocationNearMyLocation(Location myLocation, Location taskItemLocation) {
		return taskItemLocation.distanceTo(myLocation) < DEFAULT_DISTANCE_THRESHOLD_FOR_LOCAL_ALERT;
	}

	private Location buildLocationFromTaskItem(AlertItem taskItem) {
		Location taskItemLocation = new Location("com.pyxistech.android.rabbitreminder.providers.TaskListProvider");
		taskItemLocation.setLatitude(taskItem.getLatitude());
		taskItemLocation.setLongitude(taskItem.getLongitude());
		return taskItemLocation;
	}

	private void notifyUser(int taskItemsSize, Intent notificationIntent) {					
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.icon, "You have to accomplish a task here!", System.currentTimeMillis());
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, "There is " + taskItemsSize + " local tasks", "", pendingIntent);

		nm.notify(1, notification);
	}

	private void threadWait(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private Vector<AlertItem> getUndoneAlerts() {
		Vector<AlertItem> tasks = new Vector<AlertItem>();
		
		Cursor tasksCursor = context.getContentResolver().query(AlertList.Items.CONTENT_URI, 
				PROJECTION, undoneTaskWhereClause(), null, 
				AlertList.Items.DEFAULT_SORT_ORDER);
		
		if( tasksCursor.moveToFirst() ) {
			do {
				tasks.add(new AlertItem(
							Integer.valueOf(tasksCursor.getString(0)), 
							tasksCursor.getString(1), 
							tasksCursor.getInt(2) == 1, 
							tasksCursor.getDouble(3), 
							tasksCursor.getDouble(4)
					));
	        } while(tasksCursor.moveToNext());
        }
		
		return tasks;
	}

	private String undoneTaskWhereClause() {
		return AlertList.Items.DONE + "=0";
	}
	
	private Location getLocation() {
		LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		return lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}
	
	public LocationListener locationListener = new LocationListener() {
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
		
		public void onProviderEnabled(String provider) {
		}
		
		public void onProviderDisabled(String provider) {
		}
		
		public void onLocationChanged(Location location) {
		}
	};
	
	public void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
	}

	private static final int NOTIFICATION_REFRESH_RATE = 1000;
    private static final int DEFAULT_DISTANCE_THRESHOLD_FOR_LOCAL_ALERT = 100;
    
	private boolean interrupted = false;
	private AlertService context;
}
