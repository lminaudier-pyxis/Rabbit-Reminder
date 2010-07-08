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

package com.pyxistech.android.rabbitreminder.adaptaters;

import com.pyxistech.android.rabbitreminder.R;

import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

public class AlertViewWrapper {
	public AlertViewWrapper(View taskItem) {
		this.base = taskItem;
	}
	
	public CheckedTextView getCheckedTextView() {
		if (checkedTextView == null) {
			checkedTextView = (CheckedTextView) base.findViewById(R.id.task_item);
		}
		return checkedTextView;
	}
	
	public TextView getNotificationModeTextView() {
		if (notificationModeTextView == null) {
			notificationModeTextView = (TextView) base.findViewById(R.id.alert_notification_mode);
		}
		return notificationModeTextView;
	}
	
	private View base;
	private CheckedTextView checkedTextView;
	private TextView notificationModeTextView;
}
