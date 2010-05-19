package com.pyxistech.android.rabbitreminder.adaptaters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.pyxistech.android.rabbitreminder.R;
import com.pyxistech.android.rabbitreminder.models.TaskItem;
import com.pyxistech.android.rabbitreminder.models.TaskList;

public class TaskListAdapter extends BaseAdapter {

	public TaskListAdapter(Activity context, TaskList list) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		this.list = list;
	}

	public void addItem(TaskItem item){
		list.addItem(item);
		notifyDataSetChanged();
	}

	public void deleteItem(int index){
		list.deleteItem(index);
		notifyDataSetChanged();
	}
	
	public void updateItem(int index, String data) {
		list.updateItem(index, data);
		notifyDataSetChanged();
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		TaskViewWrapper wrapper = null;
		if (convertView == null){
			convertView = inflater.inflate(R.layout.task_item, null);
			wrapper = new TaskViewWrapper(convertView);
			convertView.setTag(wrapper);
		}
		else {
			wrapper = (TaskViewWrapper) convertView.getTag();
		}
		
		wrapper.getCheckedTextView().setText(list.getItemAt(position).getText());
		wrapper.getCheckedTextView().setChecked(list.getItemAt(position).isDone());
		
		return convertView;
	}

	public TaskList getList(){
		return list;
	}
	
	public void setList(TaskList list){
		this.list = list;
	}
	
	public int getCount() {
		return list.size();
	}

	public TaskItem getItem(int position) {
		return list.getItemAt(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	public void clearList() {
		list.clear();
	}
	
	private LayoutInflater inflater;
	private TaskList list;
}
