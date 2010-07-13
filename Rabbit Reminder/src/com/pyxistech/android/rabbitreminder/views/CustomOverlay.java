package com.pyxistech.android.rabbitreminder.views;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
import com.pyxistech.android.rabbitreminder.R;

public class CustomOverlay extends Overlay {
 
	public CustomOverlay(GeoPoint point) {
		geoPoint = point;
	}
 
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		Projection projection = mapView.getProjection();

	    if (drawable == null) {
	        initializeDrawable(mapView);
	    }	   
	    
	    projection.toPixels(geoPoint, center);
	    float radius = projection.metersToEquatorPixels(100);
	    
	    drawCircle(canvas, radius);
	    drawDrawable(canvas);
	}

	private void drawDrawable(Canvas canvas) {
		drawable.setBounds(center.x - width / 2, center.y - height, center.x + width / 2, center.y);
	    drawable.draw(canvas);
	}

	private void drawCircle(Canvas canvas, float radius) {
		accuracyPaint.setColor(0xffa94718);
	    accuracyPaint.setStyle(Style.STROKE);
	    canvas.drawCircle(center.x, center.y, radius, accuracyPaint);
	
	    accuracyPaint.setColor(0x18ff6924);
	    accuracyPaint.setStyle(Style.FILL);
	    canvas.drawCircle(center.x, center.y, radius, accuracyPaint);
	}

	private void initializeDrawable(MapView mapView) {
		accuracyPaint = new Paint();
		accuracyPaint.setAntiAlias(true);
		accuracyPaint.setStrokeWidth(2.0f);

		drawable = mapView.getContext().getResources().getDrawable(R.drawable.carrot);
		width = drawable.getIntrinsicWidth();
		height = drawable.getIntrinsicHeight();
		center = new Point();
	}
	
    private Paint accuracyPaint;
    private Point center;
    private Drawable drawable;
    private int width;
    private int height;
    private GeoPoint geoPoint;
}
