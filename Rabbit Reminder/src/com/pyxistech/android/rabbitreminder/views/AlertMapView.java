package com.pyxistech.android.rabbitreminder.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class AlertMapView extends MapView {
	
	public interface CoordinatesTouchedListener {
		public void onCoordinatesTouched(Double x, Double y);
	}

	public AlertMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AlertMapView(Context context, String apiKey) {
		super(context, apiKey);
	}

	public AlertMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public boolean onTouchEvent (MotionEvent event) {
		switch (event.getAction()) {
		
		case MotionEvent.ACTION_DOWN:
			startX = (int) event.getX();
			startY = (int) event.getY();
			break;
			
		case MotionEvent.ACTION_UP:
			if (movementLength(event) < MOVE_THRESHOLD) {
				GeoPoint point = geoPointFromPixels((int) event.getX(), (int) event.getY());
				notifyListener(point);
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	public void setCoordinatesTouchedListener(CoordinatesTouchedListener touchedListener) {
		this.touchedListener = touchedListener;
	}

	private GeoPoint geoPointFromPixels(int x, int y) {
		return this.getProjection().fromPixels(x, y);
	}

	private void notifyListener(GeoPoint point) {
		if (touchedListener != null) {
			touchedListener.onCoordinatesTouched( point.getLatitudeE6() / 1E6, point.getLongitudeE6() /1E6);
		}
	}

	private double movementLength(MotionEvent event) {
		return Math.sqrt(Math.pow((int) event.getX() - startX, 2) + Math.pow((int) event.getY() - startY, 2));
	}
	
	private int startX;
	private int startY;
	
	private CoordinatesTouchedListener touchedListener;

	private static final int MOVE_THRESHOLD = 20;

}
