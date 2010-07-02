package com.pyxistech.android.rabbitreminder.activities;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
	}
	
	@Override
	public void onStop() {
		super.onStop();
		deactivateGpsService();
	}

	private void deactivateGpsService() {
		locationManager.removeUpdates(this);
	}
	
	@Override
	public void onRestart() {
		super.onRestart();
		activateGpsService();
	}

	private void activateGpsService() {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_UPDATE_RATE, 0, this);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putInt("index", index);
		outState.putInt("notificationMode", notificationMode);
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
		setCurrentGpsLocation(null);
	}

	public void onProviderEnabled(String provider) {
		setCurrentGpsLocation(null);
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		setCurrentGpsLocation(null);
	}

	public void onCoordinatesTouched(Double x, Double y) {
		setOverlayOnCoordinates(x, y);
		setCurrentLocation(x, y);
		setTaskCoordinates();
	}

	@Override
	public void setIntent (Intent newIntent) {
		super.setIntent(newIntent);
		
		getParametersFromBundle(newIntent.getExtras());
		refreshUi();
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

	private boolean isSetTaskCoordinates() {
		return taskLatitude != null && taskLongitude != null;
	}

	private void initializeOverlays() {
		mapView = (AlertMapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setCoordinatesTouchedListener(this);
		
		mapOverlays = mapView.getOverlays();
		drawable = this.getResources().getDrawable(overlayDrawable);
		
		itemizedOverlay = new RabbitItemizedOverlay(drawable);
	}

	private GeoPoint createGeoPointFromCoordinates(Double latitude, Double longitude) {
		Double latitudeE6 = latitude*1E6;
		Double longitudeE6 = longitude*1E6;
		
		GeoPoint point = new GeoPoint(latitudeE6.intValue(), longitudeE6.intValue());
		return point;
	}

	private Bundle getBundle() {
		return getIntent().getExtras();
	}
	
	private void getParametersFromSavedInstanceState(Bundle savedInstanceState) {
		index = savedInstanceState.getInt("index");
		notificationMode = savedInstanceState.getInt("notificationMode");
		if (savedInstanceState.getBoolean("taskOverlayExist")) {
			taskLatitude = savedInstanceState.getDouble("latitude");
			taskLongitude = savedInstanceState.getDouble("longitude");
		}
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

	private void refreshUi() {
		Button addTaskButton = (Button) findViewById(R.id.add_task_button);
		EditText taskText = (EditText) findViewById(R.id.new_task_text);
		ImageButton editAlertButton = (ImageButton) findViewById(R.id.edit_alert_button);

		taskText.setText(text);
		
		addTaskButton.setOnClickListener(okButtonListener);
		editAlertButton.setOnClickListener(editAlertButtonListener);
		
		setCurrentGpsLocation(null);
	}

	private void setCurrentGpsLocation(Location location) {
		updateLocationManagerIfNeeded(location);
		location = getLastKnownPosition();
		
		if( location != null ) { 
			setCurrentLocation(location.getLatitude(), location.getLongitude());
		}
		else 
			setCurrentLocation(null, null);
	
		if ((taskLatitude == null || taskLongitude == null) && index == -1) {
			setTaskCoordinates();
			setOverlayAndMoveToCoordinates(taskLatitude, taskLongitude);
		}
	}

	private void setOverlayOnCoordinates(Double latitude, Double longitude) {
		GeoPoint point = createGeoPointFromCoordinates(latitude, longitude);
		OverlayItem overlayitem = new OverlayItem(point, "", "");
		itemizedOverlay.setOverlay(overlayitem);
		mapOverlays.clear();
		mapOverlays.add(itemizedOverlay);
	}

	private void setOverlayAndMoveToCoordinates(Double latitude,
			Double longitude) {
		if (latitude != null && longitude != null) {
			setOverlayOnCoordinates(latitude, longitude);
			mapView.getController().animateTo(createGeoPointFromCoordinates(latitude, longitude));
		}
	}

	private void setTaskCoordinates() {
		taskLatitude = currentLatitude;
		taskLongitude = currentLongitude;
	}
	
	private void setCurrentLocation(Double latitude, Double longitude) {
		currentLatitude = latitude;
		currentLongitude = longitude;
	}

	private Location getLastKnownPosition() {
		return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}

	private void updateLocationManagerIfNeeded(Location location) {
		if (location == null) {
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); 
			activateGpsService();
		}
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
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


	
	private void updateMapView() {
		setOverlayAndMoveToCoordinates(taskLatitude, taskLongitude);
	}

	private void checkGPSAvailability() {
		if (currentLatitude == null || currentLongitude == null) {
			Toast toast = Toast.makeText(getApplicationContext(), R.string.gps_availability_warning, Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
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
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(Menu.NONE, MY_LOCATION, Menu.NONE, R.string.go_to_my_location_menu_text).setIcon(android.R.drawable.ic_menu_mylocation);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case MY_LOCATION:
    		setCurrentGpsLocation(null);
			checkGPSAvailability();
			setTaskCoordinates();
			updateMapView();
    		return true;
    	}
    	
    	return super.onOptionsItemSelected(item);
    }
	
	private int index = -1;
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
	
	private static final int GPS_UPDATE_RATE = 60000;
	
	private static final int MY_LOCATION = Menu.FIRST + 1;
	private int overlayDrawable = R.drawable.carrot;
}
