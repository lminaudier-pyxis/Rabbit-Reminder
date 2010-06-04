package com.pyxistech.android.rabbitreminder.adaptaters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.pyxistech.android.rabbitreminder.R;
import com.pyxistech.android.rabbitreminder.models.ListItem;
import com.pyxistech.android.rabbitreminder.models.ListsList;

public class ListsListAdapter extends BaseAdapter {
	
	private ListsList items = new ListsList();
	
	public ListsListAdapter(Activity context, Cursor cursor) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		Resources resources = context.getResources();
		remainingTasksString = resources.getString(R.string.list_item_number_of_tasks);

		while( cursor.moveToNext() ){
			String listName = cursor.getString(1);
			int remaningsTasks = cursor.getInt(2);
			int locationId = cursor.getInt(3); 
			
			items.addItem(new ListItem(listName, 1337, remaningsTasks, locationId));
		}
	}

	public int getCount() {
		return items.size();
	}

	public Object getItem(int position) {
		return items.getItem(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ListViewWrapper wrapper = null;
		if (convertView == null){
			convertView = inflater.inflate(R.layout.list_item, null);
			wrapper = new ListViewWrapper(convertView);
			convertView.setTag(wrapper);
		}
		else {
			wrapper = (ListViewWrapper) convertView.getTag();
		}
		
		ListItem item = items.getItem(position);
		
		setItemName(wrapper, item);
		setItemRemainingTasks(wrapper, item);
		setItemLocationIcon(wrapper, item);
		
		return convertView;
	}

	private void setItemLocationIcon(ListViewWrapper wrapper, ListItem item) {
		if (item.getLocation() < 0)
			wrapper.getImageItemView().setVisibility(View.INVISIBLE);
		else
			wrapper.getImageItemView().setVisibility(View.VISIBLE);
	}

	private void setItemRemainingTasks(ListViewWrapper wrapper, ListItem item) {
		wrapper.getTasksRemainingText().setText(
					String.format(remainingTasksString, 
							item.getTaskCount(), 
							item.getRemainingTaskCount())
				);
	}

	private void setItemName(ListViewWrapper wrapper, ListItem item) {
		wrapper.getTextItemView().setText(item.getName());
	}
	
	private LayoutInflater inflater;
	private String remainingTasksString;
}
