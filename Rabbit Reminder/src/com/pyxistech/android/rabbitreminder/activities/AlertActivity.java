package com.pyxistech.android.rabbitreminder.activities;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.pyxistech.android.rabbitreminder.R;
import com.pyxistech.android.rabbitreminder.models.AlertItem;
import com.pyxistech.android.rabbitreminder.views.AlertMapView;
import com.pyxistech.android.rabbitreminder.views.RabbitItemizedOverlay;

public class AlertActivity extends MapActivity implements LocationListener, AlertMapView.CoordinatesTouchedListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.alert_show);
		
		initializeOverlays();
		
		if (savedInstanceState == null) {
			restoreOverlayFromBundle();
		}
		else {
			restoreOverlayFromSavedInstance(savedInstanceState);
		}

		refreshUi();
		activateGpsService();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MY_LOCATION, Menu.NONE, R.string.go_to_my_location_menu_text).setIcon(android.R.drawable.ic_menu_mylocation);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case MY_LOCATION:
			checkGPSAvailability();
			setTaskCoordinates(currentLatitude, currentLongitude);
			updateMapView();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStop() {
		super.onStop();
		deactivateGpsService();
	}

	@Override
	public void onRestart() {
		super.onRestart();
		activateGpsService();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putInt("index", index);
		outState.putInt("notificationMode", notificationMode);
		outState.putBoolean("currentLocationExist", isSetCurrentLocation());
		if (isSetCurrentLocation()) {
			outState.putDouble("currentLatitude", currentLatitude);
			outState.putDouble("currentLongitude", currentLongitude);
		}
		outState.putBoolean("taskOverlayExist", isSetTaskCoordinates());
		if (isSetTaskCoordinates()) {
			outState.putDouble("latitude", taskLatitude);
			outState.putDouble("longitude", taskLongitude);
		}
	}

	public void onLocationChanged(Location location) {
		setCurrentGpsLocation(location);
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	public void onCoordinatesTouched(Double x, Double y) {
		setOverlayOnCoordinates(x, y);
		setTaskCoordinates(x, y);
	}

	private Bundle getBundle() {
		return getIntent().getExtras();
	}

	private void getParametersFromBundle(Bundle bundle) {
		AlertItem item = bundle.getParcelable("item");
		if( item != null ) {
			text = item.getText();
			
			taskLatitude = item.getLatitude();
			taskLongitude = item.getLongitude();
			
			notificationMode = item.getNotificationMode();
			
			index = bundle.getInt("index");
		}
	}

	private void getParametersFromSavedInstanceState(Bundle savedInstanceState) {
		index = savedInstanceState.getInt("index");
		notificationMode = savedInstanceState.getInt("notificationMode");
		if (savedInstanceState.getBoolean("taskOverlayExist")) {
			taskLatitude = savedInstanceState.getDouble("latitude");
			taskLongitude = savedInstanceState.getDouble("longitude");
		}
		if (savedInstanceState.getBoolean("currentLocationExist")) {
			currentLatitude = savedInstanceState.getDouble("currentLatitude");
			currentLongitude = savedInstanceState.getDouble("currentLongitude");
		}
	}

	private void activateGpsService() {
		if (locationManager == null) {
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); 
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			locationManager.requestLocationUpdates(locationManager.getBestProvider(criteria, true), GPS_UPDATE_RATE, 0, this);
		}
	}

	private void deactivateGpsService() {
		if (locationManager != null) {
			locationManager.removeUpdates(this);
			locationManager = null;
		}
	}

	private void setCurrentGpsLocation(Location location) {		
		if( location != null ) { 
			setCurrentLocation(location.getLatitude(), location.getLongitude());
		}
	
		if (!isSetTaskCoordinates() && isNewAlert()) {
			setTaskCoordinates(currentLatitude, currentLongitude);
			setOverlayAndMoveToCoordinates(taskLatitude, taskLongitude);
		}
	}

	private boolean isNewAlert() {
		return index == NEW_ALERT_ID;
	}

	private void checkGPSAvailability() {
		if (currentLatitude == null || currentLongitude == null) {
			Toast toast = Toast.makeText(getApplicationContext(), R.string.gps_availability_warning, Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	private void restoreOverlayFromSavedInstance(Bundle savedInstanceState) {
		getParametersFromSavedInstanceState(savedInstanceState);
		if (isSetTaskCoordinates()) {
			setOverlayOnCoordinates(taskLatitude, taskLongitude);
		}
	}

	private void restoreOverlayFromBundle() {
		if (getBundle() != null) {
			getParametersFromBundle(getBundle());
		}
		
		if (isSetTaskCoordinates()) {
			mapView.getController().setZoom(11);
			setOverlayAndMoveToCoordinates(taskLatitude, taskLongitude);
		}
	}

	private void initializeOverlays() {
		mapView = (AlertMapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setCoordinatesTouchedListener(this);
		
		mapOverlays = mapView.getOverlays();
		drawable = this.getResources().getDrawable(overlayDrawable);
		
		itemizedOverlay = new RabbitItemizedOverlay(drawable);
	}

	private void refreshUi() {
		Button addTaskButton = (Button) findViewById(R.id.add_task_button);
		EditText taskText = (EditText) findViewById(R.id.new_task_text);
		ImageButton editAlertButton = (ImageButton) findViewById(R.id.edit_alert_button);

		taskText.setText(text);
		
		addTaskButton.setOnClickListener(okButtonListener);
		editAlertButton.setOnClickListener(editAlertButtonListener);
	}

	private void updateMapView() {
		setOverlayAndMoveToCoordinates(taskLatitude, taskLongitude);
	}

	private void setOverlayOnCoordinates(Double latitude, Double longitude) {
		GeoPoint point = createGeoPointFromCoordinates(latitude, longitude);
		OverlayItem overlayitem = new OverlayItem(point, "", "");
		itemizedOverlay.setOverlay(overlayitem);
		mapOverlays.clear();
		mapOverlays.add(itemizedOverlay);
	}

	private void setOverlayAndMoveToCoordinates(Double latitude, Double longitude) {
		if (latitude != null && longitude != null) {
			setOverlayOnCoordinates(latitude, longitude);
			mapView.getController().animateTo(createGeoPointFromCoordinates(latitude, longitude));
		}
	}

	private void setTaskCoordinates(Double latitude, Double longitude) {
		taskLatitude = latitude;
		taskLongitude = longitude;
	}
	
	private void setCurrentLocation(Double latitude, Double longitude) {
		currentLatitude = latitude;
		currentLongitude = longitude;
	}

	private GeoPoint createGeoPointFromCoordinates(Double latitude, Double longitude) {
		Double latitudeE6 = latitude*1E6;
		Double longitudeE6 = longitude*1E6;
		
		GeoPoint point = new GeoPoint(latitudeE6.intValue(), longitudeE6.intValue());
		return point;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	private boolean isSetCurrentLocation() {
		return currentLatitude != null && currentLongitude != null;
	}

	private boolean isSetTaskCoordinates() {
		return taskLatitude != null && taskLongitude != null;
	}

	private OnClickListener editAlertButtonListener = new OnClickListener() {
		public void onClick(View v) {
			final String[] items = {AlertActivity.this.getString(R.string.alert_type_description_near_of_label),
					AlertActivity.this.getString(R.string.alert_type_description_go_out_of_label)};

			AlertDialog.Builder builder = new AlertDialog.Builder(AlertActivity.this);
			builder.setTitle(R.string.alert_type_title_text);
			builder.setSingleChoiceItems(items, notificationMode, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			    	notificationMode = item;
			    	dialog.cancel();
			    }
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
	};

	private OnClickListener okButtonListener = new OnClickListener() {
		
		public void onClick(View v) {
		EditText taskText = (EditText) findViewById(R.id.new_task_text);
			if(!isSetTaskCoordinates()) {
				Toast toast = Toast.makeText(AlertActivity.this, R.string.location_not_set_warning, Toast.LENGTH_LONG);
				toast.show();
			}
			else if (taskText.getText().toString().equals("")) {
				Toast toast = Toast.makeText(AlertActivity.this, R.string.alert_name_not_set_warning, Toast.LENGTH_LONG);
				toast.show();
			} else {
				Intent data = new Intent();
				
				String text = taskText.getText().toString();
				data.putExtra("newTaskText", text);
				data.putExtra("latitude", taskLatitude);
				data.putExtra("longitude", taskLongitude);
				data.putExtra("index", index);
				data.putExtra("notificationMode", notificationMode);
		
				AlertActivity.this.setResult(Activity.RESULT_OK, data);
				AlertActivity.this.finish();
			}
		}
	};
	
	private int index = NEW_ALERT_ID;
	private String text = "";
	private Double currentLatitude = null;
	private Double currentLongitude = null;
	private Double taskLatitude;
	private Double taskLongitude;
	private int notificationMode = AlertItem.NOTIFY_WHEN_NEAR_OF;

	private LocationManager locationManager;

	private AlertMapView mapView;

	private List<Overlay> mapOverlays;
	private Drawable drawable;
	private RabbitItemizedOverlay itemizedOverlay;
	private int overlayDrawable = R.drawable.carrot;

	private static final int MY_LOCATION = Menu.FIRST + 1;
	private static final int GPS_UPDATE_RATE = 10000;
	private static final int NEW_ALERT_ID = -1;
}
