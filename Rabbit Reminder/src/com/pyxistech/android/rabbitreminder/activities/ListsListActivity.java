package com.pyxistech.android.rabbitreminder.activities;

import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;

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
    	
//    	ContentValues values = new ContentValues();
//		values.put(ListsList.Items.NAME, "list");
//		values.put(ListsList.Items.REMAINING_TASK_COUNT, 42);
//		values.put(ListsList.Items.LOCATION_ID, -1);
//    	Uri newElementUri = getContentResolver().insert(ListsList.Items.CONTENT_URI, values);
//    	
//    	
//    	ContentValues updatedValues = new ContentValues();
//    	updatedValues.put(ListsList.Items.NAME, "update list name");
//    	updatedValues.put(ListsList.Items.REMAINING_TASK_COUNT, 1337);
//    	updatedValues.put(ListsList.Items.LOCATION_ID, 0);
//    	getContentResolver().update(newElementUri, updatedValues, "" , null);
    	
    	registerForContextMenu(getListView());
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	menu.add(Menu.NONE, EDIT_ITEM, Menu.NONE,  R.string.edit_item_menu_text);
    	menu.add(Menu.NONE, DELETE_ITEM, Menu.NONE, R.string.delete_item_menu_text);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	switch (item.getItemId()) {
    	case DELETE_ITEM:
    		deleteItem(info.id);
    		return true;
    	default:
    		return super.onContextItemSelected(item);
    	}
    }
    
    private void deleteItem(final long index) {
    	Builder builder = new Builder(this);
    	builder.setIcon(android.R.drawable.ic_dialog_alert);
    	builder.setTitle(R.string.delete_item_confirmation_dialog_title);
    	builder.setMessage(R.string.delete_item_confirmation_dialog_text);
    	builder.setPositiveButton(R.string.validation_button_text, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				deleteItemFromListAndDatabase(index);	
			}
		});
    	builder.setNegativeButton(R.string.cancel_button_text, null);
    	builder.show();
    }
    
    private void deleteItemFromListAndDatabase(final long index) {
		getContentResolver().delete(ListsList.Items.CONTENT_URI, 
				ListsList.Items._ID + "=" + getListsListAdapter().getItem((int)index).getId(), 
				null);
		getListsListAdapter().deleteItem((int) index);
	}
    
    private ListsListAdapter getListsListAdapter() {
		return ((ListsListAdapter) getListAdapter());
	}
    
    public static final int ADD_ITEM = Menu.FIRST + 1;
    public static final int EDIT_ITEM = Menu.FIRST + 2;
    public static final int DELETE_ITEM = Menu.FIRST + 3;
    
    private static final String[] PROJECTION = new String[] {
        ListsList.Items._ID,
        ListsList.Items.NAME,
        ListsList.Items.REMAINING_TASK_COUNT,
        ListsList.Items.LOCATION_ID
    };
}
