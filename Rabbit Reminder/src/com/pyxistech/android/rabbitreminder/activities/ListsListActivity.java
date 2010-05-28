package com.pyxistech.android.rabbitreminder.activities;

import android.app.ListActivity;
import android.content.res.Resources;
import android.os.Bundle;

import com.pyxistech.android.rabbitreminder.R;
import com.pyxistech.android.rabbitreminder.adaptaters.ListsListAdapter;

public class ListsListActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.listslist);
    	
    	Resources res = getResources();
    	getListView().setCacheColorHint(0);
    	getListView().setDivider(res.getDrawable(android.R.drawable.divider_horizontal_bright));
    	
    	ListsListAdapter adapter = new ListsListAdapter(this);
    	setListAdapter(adapter);
    }
}
