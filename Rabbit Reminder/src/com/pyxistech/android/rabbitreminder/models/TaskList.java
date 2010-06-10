package com.pyxistech.android.rabbitreminder.models;

import java.util.Vector;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

public class TaskList implements Parcelable {
	public static final String AUTHORITY = "com.pyxistech.rabbitreminder.models.TaskList";
	
	public static final class Items implements BaseColumns {
		private Items() {}

		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/items");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.pyxistech.android.rabbitreminder.providers.tasklist";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.pyxistech.android.rabbitreminder.providers.tasklist";
		public static final String DEFAULT_SORT_ORDER = "modified DESC";
		public static final String NAME = "name";
		public static final String DONE = "done";
		public static final String LATITUDE = "latitude";
		public static final String LONGITUDE = "longitude";
		public static final String CREATED_DATE = "created";
		public static final String MODIFIED_DATE = "modified";
	}
	
	public TaskList() {
	}
	
	public TaskList(Parcel in) {
		int size = in.readInt();
		for (int i = 0; i < size; i++) {
			TaskItem item = in.readParcelable(getClass().getClassLoader());
			items.add(item);
		}
	}
	
	public boolean equals(TaskList list) {
		return items.equals(list.items);
	}
	
	public void addItem(TaskItem item) {
		items.add(item);
	}
	
	public void deleteItem(int index) {
		items.remove(index);
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
			out.writeParcelable(item, 0);
		}
	}

	public void updateItem(int index, String newText) {
		items.elementAt(index).setText(newText);
	}
	
	public void clear() {
		items.clear();
	}
	
	private Vector<TaskItem> items = new Vector<TaskItem>();
}
