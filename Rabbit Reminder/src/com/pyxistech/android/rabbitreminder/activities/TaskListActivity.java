package com.pyxistech.android.rabbitreminder.activities;

import java.util.Random;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.pyxistech.android.rabbitreminder.R;
import com.pyxistech.android.rabbitreminder.adaptaters.TaskListAdapter;
import com.pyxistech.android.rabbitreminder.models.TaskItem;
import com.pyxistech.android.rabbitreminder.models.TaskList;

public class TaskListActivity extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	TaskListAdapter adapter = new TaskListAdapter(this, new TaskList());

    	if (savedInstanceState == null)
    		buildList(adapter);
    	else
    		adapter.setList( (TaskList) savedInstanceState.getParcelable("TaskList") );

    	setListAdapter(adapter);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	
    	TaskList list = ((TaskListAdapter)getListAdapter()).getList();
		outState.putParcelable("TaskList", list);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(Menu.NONE, ADD_ITEM, Menu.NONE, "Add Item");
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
    
    private void addItem() {
		Intent intent = new Intent(this, AddTaskActivity.class);
		
		startActivityForResult(intent, 0);
	}
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	String data = intent.getExtras().get("newTaskText").toString();
		((TaskListAdapter) getListAdapter()).addItem(new TaskItem(data, false));
		
    }

	@Override
    public void onListItemClick(ListView parent, View v, int position, long id) {
    	CheckedTextView checkableItem = (CheckedTextView) v.findViewById(R.id.task_item);
    	checkableItem.toggle();
    	getModel(position).setDone(checkableItem.isChecked());
    }
    
    private TaskItem getModel(int position) {
    	return ((TaskListAdapter)getListAdapter()).getItem(position);
    }
     
    private TaskList buildList(TaskListAdapter adapter) {
    	adapter.addItem(new TaskItem("item 1", false));
    	adapter.addItem(new TaskItem("item 2", true));
    	adapter.addItem(new TaskItem("item 3", true));
    	adapter.addItem(new TaskItem("item 4", false));
    	adapter.addItem(new TaskItem("item 5", false));
    	adapter.addItem(new TaskItem("item 6", false));
    	adapter.addItem(new TaskItem("item 7", true));
    	adapter.addItem(new TaskItem("item 8", true));
    	adapter.addItem(new TaskItem("item 9", true));
    	adapter.addItem(new TaskItem("item 10", false));
    	adapter.addItem(new TaskItem("item 11", false));
    	adapter.addItem(new TaskItem("item 12", false));
    	adapter.addItem(new TaskItem("item 13", false));
    	adapter.addItem(new TaskItem("item 14", false));
    	
    	return adapter.getList();
    }
    
    public static final int ADD_ITEM = Menu.FIRST + 1;

	private int currentRequestCode;
}