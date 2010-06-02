package com.pyxistech.android.rabbitreminder.models;

import android.location.Location;

public class TaskLocation {
	String name;
	Location location;
	
	public TaskLocation(String name, Location location){
		setName(name);
		setLocation(location);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public boolean equals(TaskLocation location) {
		return false;
	}
}
