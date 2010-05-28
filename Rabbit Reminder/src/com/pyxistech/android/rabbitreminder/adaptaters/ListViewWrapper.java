package com.pyxistech.android.rabbitreminder.adaptaters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pyxistech.android.rabbitreminder.R;

public class ListViewWrapper {
	public ListViewWrapper(View listItem) {
		this.base = listItem;
	}
	
	public TextView getTextItemView() {
		if (textItem == null) {
			textItem = (TextView) base.findViewById(R.id.list_item);
		}
		return textItem;
	}
	
	public ImageView getImageItemView() {
		if (imageItem == null) {
			imageItem = (ImageView) base.findViewById(R.id.list_item_image);
		}
		return imageItem;
	}
	
	public TextView getTasksRemainingText() {
		if (tasksRemainingText == null) {
			tasksRemainingText = (TextView) base.findViewById(R.id.list_item_number_of_tasks_remaining);
		}
		return tasksRemainingText;
	}

	private ImageView imageItem;
	private TextView textItem;
	private TextView tasksRemainingText;
	private View base;
}
