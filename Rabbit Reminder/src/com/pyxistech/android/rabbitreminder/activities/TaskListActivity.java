package com.pyxistech.android.rabbitreminder.activities;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.pyxistech.android.rabbitreminder.R;
import com.pyxistech.android.rabbitreminder.adaptaters.TaskListAdaptater;
import com.pyxistech.android.rabbitreminder.models.TaskItem;
import com.pyxistech.android.rabbitreminder.models.TaskList;

public class TaskListActivity extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null)
        	list = buildList();
        else
        	list = savedInstanceState.getParcelable("TaskList");
        
        setListAdapter(new TaskListAdaptater(this, list));
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	
    	outState.putParcelable("TaskList", list);
    }
    
    @Override
    public void onListItemClick(ListView parent, View v, int position, long id) {
    	CheckedTextView checkableItem = (CheckedTextView) v.findViewById(R.id.task_item);
    	checkableItem.toggle();
    	getModel(position).setDone(checkableItem.isChecked());
    }
    
    private TaskItem getModel(int position) {
    	return ((TaskListAdaptater)getListAdapter()).getItem(position);
    }
     
    private TaskList buildList() {
    	TaskList newList = new TaskList();
    	
    	newList.addItem(new TaskItem("item 1", false));
    	newList.addItem(new TaskItem("item 2", true));
    	newList.addItem(new TaskItem("item 3", true));
    	newList.addItem(new TaskItem("item 4", false));
    	newList.addItem(new TaskItem("item 5", false));
    	newList.addItem(new TaskItem("item 6", false));
    	newList.addItem(new TaskItem("item 7", true));
    	newList.addItem(new TaskItem("item 8", true));
    	newList.addItem(new TaskItem("item 9", true));
    	newList.addItem(new TaskItem("item 10", false));
    	newList.addItem(new TaskItem("item 11", false));
    	newList.addItem(new TaskItem("item 12", false));
    	newList.addItem(new TaskItem("item 13", false));
    	newList.addItem(new TaskItem("item 14", false));
    	
    	return newList;
    }
    
    private TaskList list;
}