/**
	RABBIT REMINDER
	Copyright (C) 2010  Pyxis Technologies
	
	This program is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.
	
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License along
	with this program; if not, write to the Free Software Foundation, Inc.,
	51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/

package com.pyxistech.android.rabbitreminder.models;

import android.os.Parcel;
import android.os.Parcelable;

public class AlertItem implements Parcelable {

	public static final int NOTIFY_WHEN_NEAR_OF = 0;
	public static final int NOTIFY_WHEN_GO_OUT = 1;
	
	public AlertItem(int index, String text, boolean done, Double latitude, Double longitude, int notificationMode) {
		this.index = index;
		this.text = text;
		this.done = done;
		this.latitude = latitude;
		this.longitude = longitude;
		this.notificationMode = notificationMode;
	}
	
	public AlertItem(String text, boolean done, Double latitude, Double longitude, int notificationMode) {
		this(-1, text, done, latitude, longitude, notificationMode);
	}
	
	public AlertItem(Parcel in) {
		this.index = in.readInt();
		this.text = in.readString();
		this.done = in.readInt() == 1;
		this.latitude = in.readDouble();
		this.longitude = in.readDouble();
		this.notificationMode = in.readInt();
	}
	
	public static final Parcelable.Creator<AlertItem> CREATOR = new Parcelable.Creator<AlertItem>() {

		public AlertItem createFromParcel(Parcel in) {
			return new AlertItem(in);
		}

		public AlertItem[] newArray(int size) {
			return new AlertItem[size];
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
	
	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public int getIndex() {
		return index;
	}
	
	public int getNotificationMode() {
		return notificationMode;
	}
	
	public boolean equals(AlertItem item) {
		return (item.getText().equals(text)) 
				&& (item.isDone() == done)
				&& (item.latitude.equals(latitude))
				&& (item.longitude.equals(longitude))
				&& (item.notificationMode == notificationMode);
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
		out.writeInt(notificationMode);
	}

	private String text;
	private boolean done;
	private Double latitude;
	private Double longitude;
	private int notificationMode;

	private int index = -1;
}
