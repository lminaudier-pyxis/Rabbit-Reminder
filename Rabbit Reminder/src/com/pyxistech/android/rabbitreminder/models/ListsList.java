package com.pyxistech.android.rabbitreminder.models;

import java.util.Vector;

import android.os.Parcel;
import android.os.Parcelable;

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
}
