package com.pyxistech.android.rabbitreminder.views;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.OverlayItem;

public class RabbitItemizedOverlay extends com.google.android.maps.ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();

	public RabbitItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker)); 
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);
	}

	@Override
	public int size() {
		return overlays.size();
	}

	public void setOverlay(OverlayItem overlay) {
		overlays.clear();
	    overlays.add(overlay);
	    populate();
	}
	
}
