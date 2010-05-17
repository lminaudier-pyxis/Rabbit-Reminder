package com.pyxistech.android.rabbitreminder;

import java.util.Vector;

public class TaskList {
	
	public void addItem(TaskItem item) {
		items.add(item);
	}
	
	public TaskItem getItemAt(int index) {
		return items.elementAt(index);
	}
	
	public TaskItem[] toArray() {
		return (TaskItem[]) items.toArray(new TaskItem[items.size()]);
	}
	
	private Vector<TaskItem> items = new Vector<TaskItem>();
}
