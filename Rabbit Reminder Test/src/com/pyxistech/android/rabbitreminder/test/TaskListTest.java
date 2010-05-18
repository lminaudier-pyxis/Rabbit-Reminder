package com.pyxistech.android.rabbitreminder.test;

import android.os.Bundle;
import android.test.AndroidTestCase;

import com.pyxistech.android.rabbitreminder.models.TaskItem;
import com.pyxistech.android.rabbitreminder.models.TaskList;

public class TaskListTest extends AndroidTestCase {
	
	public void setUp() {
		list = buildList();
	}
	
	public void testTaskListCanBeTransformedInArray() {
		TaskItem[] array = list.toArray();
		
		assertEquals(10, array.length);
		assertEquals("item 1", array[0].toString());
		assertEquals("item 10", array[9].toString());
	}
	
	public void testTaskListIsParcelable() {
		Bundle bundle = new Bundle();
		bundle.putParcelable("TaskList", list);
		TaskList newList = bundle.getParcelable("TaskList");
		
		assertEquals(list, newList);
	}
	
    private TaskList buildList() {
    	TaskList newList = new TaskList();
    	
    	newList.addItem(new TaskItem("item 1", false));
    	newList.addItem(new TaskItem("item 2", false));
    	newList.addItem(new TaskItem("item 3", false));
    	newList.addItem(new TaskItem("item 4", false));
    	newList.addItem(new TaskItem("item 5", false));
    	newList.addItem(new TaskItem("item 6", false));
    	newList.addItem(new TaskItem("item 7", false));
    	newList.addItem(new TaskItem("item 8", false));
    	newList.addItem(new TaskItem("item 9", false));
    	newList.addItem(new TaskItem("item 10", false));
    	
    	return newList;
    }
    
    private TaskList list;
}
