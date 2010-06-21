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
import com.pyxistech.android.rabbitreminder.activities.TaskActivity;
import com.pyxistech.android.rabbitreminder.models.TaskItem;
import com.pyxistech.android.rabbitreminder.models.TaskList;

public class AlertService extends Service {

	Thread notificationThread;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		if (notificationThread == null) {
			
			notificationThread = new Thread( new Runnable() {

			    private final String[] PROJECTION = new String[] {
			        TaskList.Items._ID, // 0
			        TaskList.Items.NAME, // 1
			        TaskList.Items.DONE, // 2
			        TaskList.Items.LATITUDE, // 3
			        TaskList.Items.LONGITUDE // 4
			    };
			    
				public void run() {
					Looper.prepare();
					
					while (true) {
						Location myLocation = getLocation();
						Vector<TaskItem> tasks = getUndoneTasks();
						
						if (myLocation != null) {
							for (TaskItem taskItem : tasks) {
								Location taskItemLocation = buildLocationFromTaskItem(taskItem);
								
								if (isTaskLocationNearMyLocation(myLocation, taskItemLocation)) {
									Intent intent = buildNotificationIntent(taskItem);
									notifyUser(taskItem, intent);
									threadWait(10000);
								}
							}
						}

						threadWait(1000);
					}
				}

				private boolean isTaskLocationNearMyLocation(Location myLocation, Location taskItemLocation) {
					return taskItemLocation.distanceTo(myLocation) < 100;
				}

				private Location buildLocationFromTaskItem(TaskItem taskItem) {
					Location taskItemLocation = new Location("com.pyxistech.android.rabbitreminder.providers.TaskListProvider");
					taskItemLocation.setLatitude(taskItem.getLatitude());
					taskItemLocation.setLongitude(taskItem.getLongitude());
					return taskItemLocation;
				}

				private Intent buildNotificationIntent(TaskItem taskItem) {
					Intent intent = new Intent(AlertService.this, TaskActivity.class);
					intent.putExtra("index", (int) -1);
					intent.putExtra("item", taskItem);
					return intent;
				}

				private void notifyUser(TaskItem taskItem, Intent notificationIntent) {
					NotificationManager nm = (NotificationManager) AlertService.this.getSystemService(Context.NOTIFICATION_SERVICE);
					Notification notification = new Notification(R.drawable.icon, "You have to accomplish a task here!", System.currentTimeMillis());
					PendingIntent pendingIntent = PendingIntent.getActivity(AlertService.this, 0, notificationIntent, 0);
					notification.setLatestEventInfo(AlertService.this, taskItem.getText(), "", pendingIntent);
					
					nm.notify(android.R.string.cancel, notification);
				}

				private void threadWait(int time) {
					try {
						Thread.sleep(time);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				private Vector<TaskItem> getUndoneTasks() {
					Vector<TaskItem> tasks = new Vector<TaskItem>();
					
					Cursor tasksCursor = getContentResolver().query(TaskList.Items.CONTENT_URI, 
		    				PROJECTION, undoneTaskWhereClause(), null, 
		    				TaskList.Items.DEFAULT_SORT_ORDER);
					
					if( tasksCursor.moveToFirst() ) {
						do {
							tasks.add(new TaskItem(
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
					return TaskList.Items.DONE + "=0";
				}
				
				private Location getLocation() {
					LocationManager lm = (LocationManager) AlertService.this.getSystemService(Context.LOCATION_SERVICE);
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
			});
			
			notificationThread.start();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		notificationThread.interrupt();
		notificationThread = null;
	}
	
	@Override
	public void onStart (Intent intent, int startId) {
		super.onStart(intent, startId);
	}
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
