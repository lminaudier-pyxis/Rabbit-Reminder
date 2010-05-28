package com.pyxistech.android.rabbitreminder.adaptaters;

import java.util.Vector;

import com.pyxistech.android.rabbitreminder.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ListsListAdapter extends BaseAdapter {
	
	Vector<String> items = new Vector<String>();
	
	public ListsListAdapter(Activity context) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		for (int i = 0; i < 50; i++)
			items.add("List " + i);
	}

	public int getCount() {
		return items.size();
	}

	public Object getItem(int position) {
		return items.elementAt(position);
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
		
		wrapper.getTextItemView().setText(items.elementAt(position));

		if (position % 3 == 0)
			wrapper.getImageItemView().setVisibility(View.INVISIBLE);
		else
			wrapper.getImageItemView().setVisibility(View.VISIBLE);
		
		return convertView;
	}

	private LayoutInflater inflater;
}
