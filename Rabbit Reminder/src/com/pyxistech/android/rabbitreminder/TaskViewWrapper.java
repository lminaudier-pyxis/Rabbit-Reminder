package com.pyxistech.android.rabbitreminder;

import android.view.View;
import android.widget.CheckedTextView;

public class TaskViewWrapper {
	public TaskViewWrapper(View taskItem) {
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
