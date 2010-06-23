package com.pyxistech.android.rabbitreminder.adaptaters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.pyxistech.android.rabbitreminder.R;
import com.pyxistech.android.rabbitreminder.models.AlertItem;
import com.pyxistech.android.rabbitreminder.models.AlertList;

public class AlertListAdapter extends BaseAdapter {

	public AlertListAdapter(Activity context, AlertList list) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		this.list = list;
	}

	public void addItem(AlertItem item){
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
		AlertViewWrapper wrapper = null;
		if (convertView == null){
			convertView = inflater.inflate(R.layout.alert_list_item, null);
			wrapper = new AlertViewWrapper(convertView);
			convertView.setTag(wrapper);
		}
		else {
			wrapper = (AlertViewWrapper) convertView.getTag();
		}
		
		setItemText(position, wrapper);
		
		checkItemIfDone(position, wrapper);
		strikeThroughtItemIfDone(position, wrapper);
		
		return convertView;
	}

	private void setItemText(int position, AlertViewWrapper wrapper) {
		wrapper.getCheckedTextView().setText(list.getItemAt(position).getText());
	}

	private void strikeThroughtItemIfDone(int position, AlertViewWrapper wrapper) {
		if( isDone(position) ){
			wrapper.getCheckedTextView().setPaintFlags(wrapper.getCheckedTextView().getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);
		}
		else {
			wrapper.getCheckedTextView().setPaintFlags(wrapper.getCheckedTextView().getPaintFlags()& (~Paint.STRIKE_THRU_TEXT_FLAG));
		}
	}

	private boolean isDone(int position) {
		return list.getItemAt(position).isDone();
	}

	private void checkItemIfDone(int position, AlertViewWrapper wrapper) {
		wrapper.getCheckedTextView().setChecked(isDone(position));
	}

	public AlertList getList(){
		return list;
	}
	
	public void setList(AlertList list){
		this.list = list;
	}
	
	public int getCount() {
		return list.size();
	}

	public AlertItem getItem(int position) {
		return list.getItemAt(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	public void clearList() {
		list.clear();
	}
	
	private LayoutInflater inflater;
	private AlertList list;
}
