package com.pyxistech.android.rabbitreminder.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskItem implements Parcelable {
	
	public TaskItem(int index, String text, boolean done, int listId) {
		this.index = index;
		this.text = text;
		this.done = done;
		this.setListId(listId);
	}
	
	public TaskItem(String text, boolean done, int listId) {
		this.text = text;
		this.done = done;
		this.setListId(listId);
	}

	public TaskItem(String text, boolean done) {
		this.text = text;
		this.done = done;
	}
	
	public TaskItem(Parcel in) {
		this.text = in.readString();
		this.done = in.readString().equals("true");
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
		return (item.getText().equals(text)) && (item.isDone() == done);
	}
	
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeString(text);
		out.writeString(done ? "true" : "false");
	}

	public int getIndex() {
		return index;
	}
	
	public void setListId(int listId) {
		this.listId = listId;
	}

	public int getListId() {
		return listId;
	}

	private String text;
	private boolean done;
	private int index = -1;
	private int listId = -1;
}
