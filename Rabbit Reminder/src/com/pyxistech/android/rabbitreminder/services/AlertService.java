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
	public IBinder onBind(Intent intent) {
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
		Notification notification = new Notification(R.drawable.locationsdashboard_button, getString(R.string.alert_service_ongoing_notification_info_message), System.currentTimeMillis());
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
        AlertList.Items.LONGITUDE, // 4
        AlertList.Items.NOTIFICATION_MODE // 5
    };
    
    public AlertThread(AlertService context) {
    	this.context = context;
    }
    
	public void run() {
		Vector<Integer> localAndAlreadySeenAlerts = new Vector<Integer>();
		
		Looper.prepare();
		
		lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, NOTIFICATION_REFRESH_RATE, 10, locationListener);
		
		while (!interrupted) {
			Location myLocation = getLocation();
			Vector<AlertItem> undoneAlerts = getUndoneAlerts();
			
			removeDoneAlertFromAlreadySeenArray(localAndAlreadySeenAlerts);		
			
			if (myLocation != null) {
				Vector<AlertItem> localUndoneAlerts = getLocalUndoneAlerts(myLocation, undoneAlerts);
				Vector<AlertItem> nonLocalUndoneAlerts = getNonLocalUndoneAlerts(myLocation, undoneAlerts);
				
				notifyUserComingNearLocalAlert(localAndAlreadySeenAlerts, localUndoneAlerts);
				notifyUserGoingAwayFromLocalAlert(localAndAlreadySeenAlerts, nonLocalUndoneAlerts);
			}

			threadWait(NOTIFICATION_REFRESH_RATE);
		}
		
		lm.removeUpdates(locationListener);
	}

	public void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
	}

	private void removeDoneAlertFromAlreadySeenArray(Vector<Integer> localAndAlreadySeenAlerts) {
		for (AlertItem doneAlerts : getDoneAlerts()) {
			if (localAndAlreadySeenAlerts.indexOf(doneAlerts.getIndex()) != -1) {
				localAndAlreadySeenAlerts.remove(localAndAlreadySeenAlerts.indexOf(doneAlerts.getIndex()));
			}
		}
	}

	private void notifyUserGoingAwayFromLocalAlert( Vector<Integer> localAndAlreadySeenAlerts, Vector<AlertItem> nonLocalUndoneAlerts) {
		for (AlertItem alertItem : nonLocalUndoneAlerts) {
			if (localAndAlreadySeenAlerts.indexOf(alertItem.getIndex()) != -1) {	
				if (alertItem.getNotificationMode() == AlertItem.NOTIFY_WHEN_GO_OUT) {
					Intent intent = buildNotificationIntent(alertItem);
					notifyUser("End of " + alertItem.getText(), intent);
				}
				localAndAlreadySeenAlerts.remove(localAndAlreadySeenAlerts.indexOf(alertItem.getIndex()));
			}
		}
	}

	private void notifyUserComingNearLocalAlert( Vector<Integer> localAndAlreadySeenAlerts, Vector<AlertItem> localUndoneAlerts) {
		for (AlertItem alertItem : localUndoneAlerts) {
			if (localAndAlreadySeenAlerts.indexOf(alertItem.getIndex()) == -1) {
				if (alertItem.getNotificationMode() == AlertItem.NOTIFY_WHEN_NEAR_OF) {
					Intent intent = buildNotificationIntent(alertItem);
					notifyUser(alertItem.getText(), intent);
				}
				localAndAlreadySeenAlerts.add(alertItem.getIndex());
			}
		}
	}

	private Intent buildNotificationIntent(AlertItem alertItem) {
		Intent intent = new Intent(context, AlertActivity.class);
		intent.putExtra("index", (int) alertItem.getIndex());
		intent.putExtra("item", alertItem);
		return intent;
	}

	private void notifyUser(String alertMessage, Intent notificationIntent) {					
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.icon, "You have to accomplish a task here!", System.currentTimeMillis());
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, alertMessage, "", pendingIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
	
		nm.notify(notificationId++, notification);
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

	private Vector<AlertItem> getNonLocalUndoneAlerts(Location myLocation, Vector<AlertItem> tasks) {
		Vector<AlertItem> localTasks = new Vector<AlertItem>();
		for (AlertItem taskItem : tasks) {
			Location taskItemLocation = buildLocationFromTaskItem(taskItem);
			
			if (isTaskFarFromMyLocation(myLocation, taskItemLocation)) {
				localTasks.add(taskItem);
			}
		}
		return localTasks;
	}

	private boolean isTaskFarFromMyLocation(Location myLocation, Location taskItemLocation) {
		return taskItemLocation.distanceTo(myLocation) > DEFAULT_DISTANCE_THRESHOLD_FOR_LOCAL_ALERT;
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

	private void threadWait(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private Vector<AlertItem> getUndoneAlerts() {
		return getAlerts(undoneAlertWhereClause());
	}
	
	private Vector<AlertItem> getDoneAlerts() {
		return getAlerts(doneAlertWhereClause());
	}
	
	private Vector<AlertItem> getAlerts(String whereClause) {
		Vector<AlertItem> alerts = new Vector<AlertItem>();
		
		Cursor alertsCursor = context.getContentResolver().query(AlertList.Items.CONTENT_URI, 
				PROJECTION, whereClause, null, 
				AlertList.Items.DEFAULT_SORT_ORDER);
		
		if( alertsCursor.moveToFirst() ) {
			do {
				addAlertFromCursor(alerts, alertsCursor);
	        } while(alertsCursor.moveToNext());
        }
		
		return alerts;
	}

	private void addAlertFromCursor(Vector<AlertItem> alerts, Cursor alertsCursor) {
		alerts.add(new AlertItem(
					Integer.valueOf(alertsCursor.getString(0)), 
					alertsCursor.getString(1), 
					alertsCursor.getInt(2) == 1, 
					alertsCursor.getDouble(3), 
					alertsCursor.getDouble(4),
					alertsCursor.getInt(5)
			));
	}

	private String doneAlertWhereClause() {
		return AlertList.Items.DONE + "=1";
	}


	private String undoneAlertWhereClause() {
		return AlertList.Items.DONE + "=0";
	}
	
	private Location getLocation() {
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
	
	private static final int NOTIFICATION_REFRESH_RATE = 60000;
    private static final int DEFAULT_DISTANCE_THRESHOLD_FOR_LOCAL_ALERT = 100;
    
	private boolean interrupted = false;
	private AlertService context;
	private int notificationId = 1;

	private LocationManager lm;
}
