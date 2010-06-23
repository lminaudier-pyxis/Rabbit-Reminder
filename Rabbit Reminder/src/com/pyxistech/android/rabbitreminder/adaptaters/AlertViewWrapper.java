package com.pyxistech.android.rabbitreminder.adaptaters;

import com.pyxistech.android.rabbitreminder.R;

import android.view.View;
import android.widget.CheckedTextView;

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
	
	private View base;
	private CheckedTextView checkedTextView;
}
