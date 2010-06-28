package com.pyxistech.android.rabbitreminder.models;

import java.util.Vector;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

public class AlertList implements Parcelable {
	public static final String AUTHORITY = "com.pyxistech.rabbitreminder.models.AlertList";
	
	public static final class Items implements BaseColumns {
		private Items() {}

		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/items");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.pyxistech.android.rabbitreminder.providers.alertlist";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.pyxistech.android.rabbitreminder.providers.alertlist";
		public static final String DEFAULT_SORT_ORDER = "modified DESC";
		public static final String NAME = "name";
		public static final String DONE = "done";
		public static final String LATITUDE = "latitude";
		public static final String LONGITUDE = "longitude";
		public static final String NOTIFICATION_MODE = "notification_mode";
		public static final String CREATED_DATE = "created";
		public static final String MODIFIED_DATE = "modified";
	}
	
	public AlertList() {
	}
	
	public AlertList(Parcel in) {
		int size = in.readInt();
		for (int i = 0; i < size; i++) {
			AlertItem item = in.readParcelable(getClass().getClassLoader());
			items.add(item);
		}
	}
	
	public static final Parcelable.Creator<AlertList> CREATOR = new Parcelable.Creator<AlertList>() {

		public AlertList createFromParcel(Parcel in) {
			return new AlertList(in);
		}

		public AlertList[] newArray(int size) {
			return new AlertList[size];
		}
	};
	
	public boolean equals(AlertList list) {
		return items.equals(list.items);
	}
	
	public void addItem(AlertItem item) {
		items.add(item);
	}
	
	public void deleteItem(int index) {
		items.remove(index);
	}
	
	public AlertItem getItemAt(int index) {
		return items.elementAt(index);
	}
	
	public AlertItem[] toArray() {
		return (AlertItem[]) items.toArray(new AlertItem[items.size()]);
	}

	public int describeContents() {
		return 0;
	}
	
	public int size(){
		return items.size();
	}

	public void writeToParcel(Parcel out, int flag) {
		out.writeInt(items.size());
		for (AlertItem item : items) {
			out.writeParcelable(item, 0);
		}
	}

	public void updateItem(int index, String newText) {
		items.elementAt(index).setText(newText);
	}
	
	public void clear() {
		items.clear();
	}
	
	private Vector<AlertItem> items = new Vector<AlertItem>();
}
