package com.pyxistech.android.rabbitreminder.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

public class ListItem implements Parcelable, BaseColumns {
	
	public ListItem(String name, int taskCount, int remainingTaskCount, int location) {
		this(-1, name, taskCount, remainingTaskCount, location);
	}
	
	public ListItem(int id, String name, int taskCount, int remainingTaskCount, int location) {
		this.id = id;
		this.name = name;
		this.taskCount = taskCount;
		this.remainingTaskCount = remainingTaskCount;
		this.location = location;
	}
	
	public ListItem(Parcelable in) {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(int taskCount) {
		this.taskCount = taskCount;
	}

	public int getRemainingTaskCount() {
		return remainingTaskCount;
	}

	public void setRemainingTaskCount(int remainingTaskCount) {
		this.remainingTaskCount = remainingTaskCount;
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ListItem) {
			ListItem item = (ListItem) o;
			return item.name.equals(this.name)
				& item.taskCount == this.taskCount
				& item.remainingTaskCount == this.remainingTaskCount
				& item.location == this.location
				& item.id == this.id;
		}
		else {
			return false;
		}
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {

	}
	
	private int id;
	private String name;
	private int taskCount;
	private int remainingTaskCount;
	private int location;
}
