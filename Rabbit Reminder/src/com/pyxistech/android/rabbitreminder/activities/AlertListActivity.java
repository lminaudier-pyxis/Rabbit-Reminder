package com.pyxistech.android.rabbitreminder.activities;

import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.pyxistech.android.rabbitreminder.R;
import com.pyxistech.android.rabbitreminder.adaptaters.AlertListAdapter;
import com.pyxistech.android.rabbitreminder.models.AlertItem;
import com.pyxistech.android.rabbitreminder.models.AlertList;
import com.pyxistech.android.rabbitreminder.services.AlertService;

public class AlertListActivity extends ListActivity {
	
    private static final String[] PROJECTION = new String[] {
        AlertList.Items._ID, // 0
        AlertList.Items.NAME, // 1
        AlertList.Items.DONE, // 2
        AlertList.Items.LATITUDE, // 3
        AlertList.Items.LONGITUDE // 4
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.alert_list);
    	    	
    	Resources res = getResources();
    	getListView().setCacheColorHint(0);
    	getListView().setDivider(res.getDrawable(android.R.drawable.divider_horizontal_bright));

    	AlertListAdapter adapter = new AlertListAdapter(this, new AlertList());

		refreshList(adapter);

		startService(new Intent(this, AlertService.class));
		
    	setListAdapter(adapter);
    	registerForContextMenu(getListView());
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	
    	AlertList list = getTaskListAdapter().getList();
		outState.putParcelable("TaskList", list);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(Menu.NONE, ADD_ITEM, Menu.NONE, R.string.add_item_menu_text).setIcon(android.R.drawable.ic_menu_add);
    	menu.add(Menu.NONE, SETTINGS, Menu.NONE, R.string.settings_menu_text).setIcon(android.R.drawable.ic_menu_preferences);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case ADD_ITEM:
    		addItem();
    		return true;
    	case SETTINGS:
    		startActivity(new Intent(this, SettingsActivity.class));
    		return true;
    	}
    	
    	return super.onOptionsItemSelected(item);
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
    	case EDIT_ITEM:
    		editItem(info.id);
    		return true;
    	case DELETE_ITEM:
    		deleteItem(info.id);
    		return true;
    	default:
    		return super.onContextItemSelected(item);
    	}
    }
    
    private void editItem(final long index) {
		Intent intent = new Intent(this, AlertActivity.class);
		intent.putExtra("index", (int) index);
		AlertItem item = getTaskListAdapter().getItem((int) index);
		intent.putExtra("item", item);
		
		startActivityForResult(intent, 0);
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
    
    private void addItem() {
		Intent intent = new Intent(this, AlertActivity.class);
		startActivityForResult(intent, 0);
	}

	private void deleteItemFromListAndDatabase(final long index) {
		getContentResolver().delete(AlertList.Items.CONTENT_URI, 
				AlertList.Items._ID + "=" + getTaskListAdapter().getItem((int)index).getIndex(), 
				null);
		getTaskListAdapter().deleteItem((int) index);
	}
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	if( intent != null ){
	    	String data = intent.getExtras().get("newTaskText").toString();
    		Double latitude = intent.getExtras().getDouble("latitude");
    		Double longitude = intent.getExtras().getDouble("longitude");
	    	int index = intent.getExtras().getInt("index");
	    	if (index == -1) {
	    		addItemInListAndDatabase(data, latitude, longitude);
	    	}
	    	else {
	    		editItemInListAndDatabase(data, index, latitude, longitude);
	    	}
	    	refreshListFromDatabase();
    	}
    }

	private void editItemInListAndDatabase(String data, int index, Double latitude, Double longitude) {
		getTaskListAdapter().updateItem(index, data);

		ContentValues values = new ContentValues();
		values.put(AlertList.Items.NAME, data);
		values.put(AlertList.Items.DONE, getTaskListAdapter().getItem(index).isDone() ? 1 : 0);
		values.put(AlertList.Items.LATITUDE, latitude);
		values.put(AlertList.Items.LONGITUDE, longitude);
		getContentResolver().update(AlertList.Items.CONTENT_URI, 
				values,
				AlertList.Items._ID + "=" + getTaskListAdapter().getItem(index).getIndex(), 
				null);
	}

	private void refreshListFromDatabase() {
		refreshList((AlertListAdapter)getListAdapter());
	}

	private void addItemInListAndDatabase(String data, Double latitude, Double longitude) {
		getTaskListAdapter().addItem(new AlertItem(data, false, latitude, longitude));

		ContentValues values = new ContentValues();
		values.put(AlertList.Items.NAME, data);
		values.put(AlertList.Items.DONE, 0);
		values.put(AlertList.Items.LATITUDE, latitude);
		values.put(AlertList.Items.LONGITUDE, longitude);
		getContentResolver().insert(AlertList.Items.CONTENT_URI, values);
	}

	@Override
    public void onListItemClick(ListView parent, View v, int position, long id) {
    	CheckedTextView checkableItem = (CheckedTextView) v.findViewById(R.id.task_item);
    	checkableItem.toggle();   	
   		checkableItem.setPaintFlags(checkableItem.getPaintFlags()^Paint.STRIKE_THRU_TEXT_FLAG);
    	
    	getModel(position).setDone(checkableItem.isChecked());
    	
    	ContentValues values = new ContentValues();
    	values.put(AlertList.Items.DONE, checkableItem.isChecked() ? 1 : 0);
    	getContentResolver().update(AlertList.Items.CONTENT_URI, 
    			values, AlertList.Items._ID + "=" + getTaskListAdapter().getItem((int)position).getIndex(), 
				null);
    }
    
    private AlertItem getModel(int position) {
    	return getTaskListAdapter().getItem(position);
    }

	private AlertListAdapter getTaskListAdapter() {
		return ((AlertListAdapter) getListAdapter());
	}
     
    public AlertList refreshList(AlertListAdapter adapter) {
    	adapter.clearList();
    	Cursor cursor = getRefreshedCursor();
        if( cursor.moveToFirst() ) {
			do {
				adapter.addItem( new AlertItem(Integer.valueOf(cursor.getString(0)), cursor.getString(1), cursor.getInt(2) == 1, cursor.getDouble(3), cursor.getDouble(4)) );
	        } while(cursor.moveToNext());
        }
    	
    	return adapter.getList();
    }

	private Cursor getRefreshedCursor() {
    	return managedQuery(AlertList.Items.CONTENT_URI, 
    				PROJECTION, null, null, 
    				AlertList.Items.DEFAULT_SORT_ORDER);
	}
    
    public static final int ADD_ITEM = Menu.FIRST + 1;
    public static final int SETTINGS = Menu.FIRST + 2;
    public static final int EDIT_ITEM = Menu.FIRST + 3;
    public static final int DELETE_ITEM = Menu.FIRST + 4;
}