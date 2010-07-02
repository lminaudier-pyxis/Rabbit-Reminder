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
