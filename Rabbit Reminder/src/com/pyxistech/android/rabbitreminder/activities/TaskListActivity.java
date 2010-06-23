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
import com.pyxistech.android.rabbitreminder.adaptaters.TaskListAdapter;
import com.pyxistech.android.rabbitreminder.models.TaskItem;
import com.pyxistech.android.rabbitreminder.models.TaskList;
import com.pyxistech.android.rabbitreminder.services.AlertService;

public class TaskListActivity extends ListActivity {
	
    private static final String[] PROJECTION = new String[] {
        TaskList.Items._ID, // 0
        TaskList.Items.NAME, // 1
        TaskList.Items.DONE, // 2
        TaskList.Items.LATITUDE, // 3
        TaskList.Items.LONGITUDE // 4
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.tasklist);
    	    	
    	Resources res = getResources();
    	getListView().setCacheColorHint(0);
    	getListView().setDivider(res.getDrawable(android.R.drawable.divider_horizontal_bright));

    	TaskListAdapter adapter = new TaskListAdapter(this, new TaskList());

		refreshList(adapter);

		startService(new Intent(this, AlertService.class));
		
    	setListAdapter(adapter);
    	registerForContextMenu(getListView());
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	
    	TaskList list = getTaskListAdapter().getList();
		outState.putParcelable("TaskList", list);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(Menu.NONE, ADD_ITEM, Menu.NONE, R.string.add_item_menu_text).setIcon(android.R.drawable.ic_menu_add);
    	menu.add(Menu.NONE, CLEAR_DONE_ITEMS, Menu.NONE, R.string.delete_done_tasks_menu_text).setIcon(android.R.drawable.ic_menu_delete);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case ADD_ITEM:
    		addItem();
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
		Intent intent = new Intent(this, TaskActivity.class);
		intent.putExtra("index", (int) index);
		TaskItem item = getTaskListAdapter().getItem((int) index);
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
		Intent intent = new Intent(this, TaskActivity.class);
		startActivityForResult(intent, 0);
	}

	private void deleteItemFromListAndDatabase(final long index) {
		getContentResolver().delete(TaskList.Items.CONTENT_URI, 
				TaskList.Items._ID + "=" + getTaskListAdapter().getItem((int)index).getIndex(), 
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
		values.put(TaskList.Items.NAME, data);
		values.put(TaskList.Items.DONE, getTaskListAdapter().getItem(index).isDone() ? 1 : 0);
		values.put(TaskList.Items.LATITUDE, latitude);
		values.put(TaskList.Items.LONGITUDE, longitude);
		getContentResolver().update(TaskList.Items.CONTENT_URI, 
				values,
				TaskList.Items._ID + "=" + getTaskListAdapter().getItem(index).getIndex(), 
				null);
	}

	private void refreshListFromDatabase() {
		refreshList((TaskListAdapter)getListAdapter());
	}

	private void addItemInListAndDatabase(String data, Double latitude, Double longitude) {
		getTaskListAdapter().addItem(new TaskItem(data, false, latitude, longitude));

		ContentValues values = new ContentValues();
		values.put(TaskList.Items.NAME, data);
		values.put(TaskList.Items.DONE, 0);
		values.put(TaskList.Items.LATITUDE, latitude);
		values.put(TaskList.Items.LONGITUDE, longitude);
		getContentResolver().insert(TaskList.Items.CONTENT_URI, values);
	}

	@Override
    public void onListItemClick(ListView parent, View v, int position, long id) {
    	CheckedTextView checkableItem = (CheckedTextView) v.findViewById(R.id.task_item);
    	checkableItem.toggle();   	
   		checkableItem.setPaintFlags(checkableItem.getPaintFlags()^Paint.STRIKE_THRU_TEXT_FLAG);
    	
    	getModel(position).setDone(checkableItem.isChecked());
    	
    	ContentValues values = new ContentValues();
    	values.put(TaskList.Items.DONE, checkableItem.isChecked() ? 1 : 0);
    	getContentResolver().update(TaskList.Items.CONTENT_URI, 
    			values, TaskList.Items._ID + "=" + getTaskListAdapter().getItem((int)position).getIndex(), 
				null);
    }
    
    private TaskItem getModel(int position) {
    	return getTaskListAdapter().getItem(position);
    }

	private TaskListAdapter getTaskListAdapter() {
		return ((TaskListAdapter) getListAdapter());
	}
     
    public TaskList refreshList(TaskListAdapter adapter) {
    	adapter.clearList();
    	Cursor cursor = getRefreshedCursor();
        if( cursor.moveToFirst() ) {
			do {
				adapter.addItem( new TaskItem(Integer.valueOf(cursor.getString(0)), cursor.getString(1), cursor.getInt(2) == 1, cursor.getDouble(3), cursor.getDouble(4)) );
	        } while(cursor.moveToNext());
        }
    	
    	return adapter.getList();
    }

	private Cursor getRefreshedCursor() {
    	return managedQuery(TaskList.Items.CONTENT_URI, 
    				PROJECTION, null, null, 
    				TaskList.Items.DEFAULT_SORT_ORDER);
	}
    
    public static final int ADD_ITEM = Menu.FIRST + 1;
    public static final int CLEAR_DONE_ITEMS = Menu.FIRST + 2;
    public static final int EDIT_ITEM = Menu.FIRST + 3;
    public static final int DELETE_ITEM = Menu.FIRST + 4;
}