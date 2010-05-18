package com.pyxistech.android.rabbitreminder.models;

import java.util.Vector;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskList implements Parcelable {
	
	public TaskList() {
	}
	
	public TaskList(Parcel in) {
		int size = in.readInt();
		for (int i = 0; i < size; i++) {
			String text = in.readString();
			boolean done = in.readString().equals("true") ? true : false;
			TaskItem item = new TaskItem(text, done);
			items.add(item);
		}
	}
	
	public boolean equals(TaskList list) {
		return items.equals(list.items);
	}
	
	public void addItem(TaskItem item) {
		items.add(item);
	}
	
	public TaskItem getItemAt(int index) {
		return items.elementAt(index);
	}
	
	public TaskItem[] toArray() {
		return (TaskItem[]) items.toArray(new TaskItem[items.size()]);
	}

	public int describeContents() {
		return 0;
	}
	
	public int size(){
		return items.size();
	}

	public void writeToParcel(Parcel out, int flag) {
		out.writeInt(items.size());
		for (TaskItem item : items) {
			out.writeString(item.getText());
			out.writeString(item.isDone() ? "true" : "false");
		}
	}
	
	private Vector<TaskItem> items = new Vector<TaskItem>();
}
