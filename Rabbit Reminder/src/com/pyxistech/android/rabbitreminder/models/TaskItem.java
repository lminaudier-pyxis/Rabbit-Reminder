package com.pyxistech.android.rabbitreminder.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskItem implements Parcelable {
	
	public TaskItem(int index, String text, boolean done, Double latitude, Double longitude) {
		this.index = index;
		this.text = text;
		this.done = done;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public TaskItem(String text, boolean done, Double latitude, Double longitude) {
		this(-1, text, done, latitude, longitude);
	}
	
	public TaskItem(Parcel in) {
		this.index = in.readInt();
		this.text = in.readString();
		this.done = in.readInt() == 1;
		this.latitude = in.readDouble();
		this.longitude = in.readDouble();
	}
	
	public static final Parcelable.Creator<TaskItem> CREATOR = new Parcelable.Creator<TaskItem>() {

		public TaskItem createFromParcel(Parcel in) {
			return new TaskItem(in);
		}

		public TaskItem[] newArray(int size) {
			return new TaskItem[size];
		}
	};
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setDone(boolean done) {
		this.done = done;
	}
	
	public boolean isDone() {
		return done;
	}
	
	public String toString() {
		return text;
	}
	
	public boolean equals(TaskItem item) {
		return (item.getText().equals(text)) 
				&& (item.isDone() == done)
				&& (item.latitude.equals(latitude))
				&& (item.longitude.equals(longitude));
	}
	
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(index);
		out.writeString(text);
		out.writeInt(done ? 1 : 0);
		out.writeDouble(latitude);
		out.writeDouble(longitude);
	}

	public int getIndex() {
		return index;
	}

	private String text;
	private boolean done;
	private Double latitude;
	private Double longitude;
	private int index = -1;
}
