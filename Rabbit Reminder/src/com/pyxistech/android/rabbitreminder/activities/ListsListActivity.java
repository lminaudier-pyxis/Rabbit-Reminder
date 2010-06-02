package com.pyxistech.android.rabbitreminder.activities;

import android.app.ListActivity;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;

import com.pyxistech.android.rabbitreminder.R;
import com.pyxistech.android.rabbitreminder.adaptaters.ListsListAdapter;
import com.pyxistech.android.rabbitreminder.models.ListsList;

public class ListsListActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.listslist);
    	
    	Resources res = getResources();
    	getListView().setCacheColorHint(0);
    	getListView().setDivider(res.getDrawable(android.R.drawable.divider_horizontal_bright));
    	
    	Cursor cursor = managedQuery(ListsList.Items.CONTENT_URI, 
         		PROJECTION, null, null, 
         		ListsList.Items.DEFAULT_SORT_ORDER);
    	
    	ListsListAdapter adapter = new ListsListAdapter(this, cursor);
    	setListAdapter(adapter);   	
    }
    
    private static final String[] PROJECTION = new String[] {
        ListsList.Items._ID,
        ListsList.Items.NAME,
        ListsList.Items.REMAINING_TASK_COUNT,
        ListsList.Items.LOCATION_ID
    };
}
