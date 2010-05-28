package com.pyxistech.android.rabbitreminder.test;

import junit.framework.TestCase;

import com.pyxistech.android.rabbitreminder.models.ListItem;
import com.pyxistech.android.rabbitreminder.models.ListsList;

public class ListsListTest extends TestCase {
	
	public void setUp() {
		list = new ListsList();
		list.addItem(new ListItem("item 1", 1, 2, 3));
		list.addItem(new ListItem("item 2", 4, 5, 6));
		list.addItem(new ListItem("item 3", 7, 8, 9));
	}
	
	public void tearDown() {	
	}
	
	public void testEqualsMethodWorksProperly() {
		ListsList list1 = new ListsList(); 
		list1.addItem(new ListItem("item 1", 1, 2, 3));
		list1.addItem(new ListItem("item 2", 4, 5, 6));
		list1.addItem(new ListItem("item 3", 7, 8, 9));
		
		ListsList list2 = new ListsList(); 
		list2.addItem(new ListItem("item 1", 1, 2, 3));
		list2.addItem(new ListItem("other item", 4, 5, 6));
		list2.addItem(new ListItem("item 3", 7, 8, 9));
		
		ListsList list3 = new ListsList(); 
		list3.addItem(new ListItem("item 1", 1, 2, 3));
		list3.addItem(new ListItem("item 2", 4, 5, 6));
		
		assertTrue(list.equals(list1));
		assertFalse(list.equals(list2));
		assertFalse(list.equals(list3));
	}
	
	private ListsList list;
}
