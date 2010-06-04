package com.pyxistech.android.rabbitreminder.models;

import java.util.Vector;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

public class ListsList implements Parcelable {
	
	public ListsList() {
	}
	
	public ListsList(Parcel in) {
	}
	
	public void addItem(ListItem item) {
		items.add(item);
	}
	
	public void deleteItem(int index) {
		items.remove(index);
	}
	
	public ListItem getItem(int index) {
		return items.elementAt(index);
	}
	
	public void updateItem(int index, ListItem item) {
		items.set(index, item);
	}
	
	public int size() {
		return items.size();
	}
	
	public boolean equals(Object o) {
		if (o instanceof ListsList) {			
			return equals((ListsList) o);
		}
		else {
			return false;
		}
	}
	
	public boolean equals(ListsList list) {
		boolean result = checkThatListsHaveTheSameSize(list);
		
		if (result)
			result = checkThatListsHaveTheSameValues(list);
		
		return result;
	}

	private boolean checkThatListsHaveTheSameValues(ListsList list) {
		boolean result = true;
		
		for (int i = 0; i < size(); i++) {
			if (!this.getItem(i).equals(list.getItem(i))) {
				result = false;
			}
		}
		return result;
	}

	private boolean checkThatListsHaveTheSameSize(ListsList list) {
		return (list.size() == this.size());
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
	}
	
	private Vector<ListItem> items = new Vector<ListItem>();
	
	public static final String AUTHORITY = "com.pyxistech.rabbitreminder.models.ListsList";
	
	public static final class Items implements BaseColumns {
		private Items() {}

		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/items");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.pyxistech.android.rabbitreminder.providers.listslist";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.pyxistech.android.rabbitreminder.providers.listitem";
		public static final String DEFAULT_SORT_ORDER = "modified DESC";
		public static final String NAME = "name";
		public static final String REMAINING_TASK_COUNT = "remaining_task_count";
		public static final String LOCATION_ID = "location_id";
		public static final String CREATED_DATE = "created";
		public static final String MODIFIED_DATE = "modified";
	}
}
