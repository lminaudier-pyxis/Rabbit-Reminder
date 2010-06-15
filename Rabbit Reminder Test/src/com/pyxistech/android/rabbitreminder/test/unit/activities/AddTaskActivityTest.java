package com.pyxistech.android.rabbitreminder.test.unit.activities;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.test.ActivityInstrumentationTestCase2;

import com.pyxistech.android.rabbitreminder.activities.AddTaskActivity;

public class AddTaskActivityTest extends ActivityInstrumentationTestCase2<AddTaskActivity> {
	
	
	public AddTaskActivityTest() {
		super("com.pyxistech.android.rabbitreminder", AddTaskActivity.class);
	}
	
	public AddTaskActivityTest(String pkg, Class<AddTaskActivity> activityClass) {
		super(pkg, activityClass);
	}
	
	public void setUp() throws Exception {		
//		final int index = 0;
//		final boolean done = false;
//		final double latitude = 42.0;
//		final double longitude = 1337.0;	
//		editMode(index, done, latitude, longitude);
		
		manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		
		try {
			manager.removeTestProvider(MOCK_GPS_PROVIDER);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

//	private void editMode(final int index, final boolean done,
//						  final double latitude, final double longitude) throws Exception {
//		try {
//			runTestOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					Intent intent = new Intent();
//					intent.putExtra("index", index);
//					TaskItem item = new TaskItem("Item", done, latitude, longitude);
//					intent.putExtra("item", item);
//					
//					getActivity().setIntent(intent);
//				}
//			});
//		} catch (Throwable e) {
//			throw new Exception();
//		}
//	}
	
	public void testPreconditions() {
		assertNotNull(getActivity());
	}
	
	public void testAddingLocationToATask() {
		manager.addTestProvider(MOCK_GPS_PROVIDER, false, false, false, false, true, true, true, 0, 5);
		
		//Setting a fake location to the mock provider
		Location location = new Location(MOCK_GPS_PROVIDER);
		location.setLatitude(42.0);
		location.setLongitude(1337.0);
		location.setTime(System.currentTimeMillis()); //necessary to wake up LocationListener
		manager.setTestProviderLocation(MOCK_GPS_PROVIDER, location);
		
		//Enable mock provider
		manager.setTestProviderEnabled(MOCK_GPS_PROVIDER, true);
		
		//Getting location updates
		manager.requestLocationUpdates(MOCK_GPS_PROVIDER, 0, 0, getActivity());
		
		Location gpsLocation = manager.getLastKnownLocation(MOCK_GPS_PROVIDER);
	
		assertNotNull("No GPS available on the test device", gpsLocation);
		
		assertEquals(location.getLatitude(), gpsLocation.getLatitude());
		assertEquals(location.getLongitude(), gpsLocation.getLongitude());
	}
	
	private LocationManager manager;
	private final String MOCK_GPS_PROVIDER = LocationManager.GPS_PROVIDER;
}
