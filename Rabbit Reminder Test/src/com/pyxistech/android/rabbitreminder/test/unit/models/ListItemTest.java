package com.pyxistech.android.rabbitreminder.test.unit.models;

import junit.framework.TestCase;
import android.os.Bundle;

import com.pyxistech.android.rabbitreminder.models.ListItem;

public class ListItemTest extends TestCase {
	public void setUp() {
		item = new ListItem(42, "item", 1337, 2, -1);
	}
	public void tearDown() {
	}
	
	public void testEqualsMethodWorksProperly() {
		ListItem item1 = new ListItem(42, "item", 1337, 2, -1);
		ListItem item2 = new ListItem(40, "item", 1337, 2, -1);
		ListItem item3 = new ListItem(42, "item other", 1337, 2, -1);
		ListItem item4 = new ListItem(42, "item", 0, 2, -1);
		ListItem item5 = new ListItem(42, "item", 1337, 1, -1);
		ListItem item6 = new ListItem(42, "item", 1337, 2, 17);
		
		assertTrue(item.equals(item1));
		assertFalse(item.equals(item2));
		assertFalse(item.equals(item3));
		assertFalse(item.equals(item4));
		assertFalse(item.equals(item5));
		assertFalse(item.equals(item6));
	}
	
	public void testListItemIsParcelable() {
		Bundle bundle = new Bundle();
		bundle.putParcelable("listItem", item);
	
		ListItem newItem = bundle.getParcelable("listItem");
	
		assertTrue(item.equals(newItem));
	}
	
	private ListItem item;
}
