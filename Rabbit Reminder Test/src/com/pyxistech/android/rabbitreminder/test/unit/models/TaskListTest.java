/**
	RABBIT REMINDER
	Copyright (C) 2010  Pyxis Technologies
	
	This program is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.
	
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License along
	with this program; if not, write to the Free Software Foundation, Inc.,
	51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/

package com.pyxistech.android.rabbitreminder.test.unit.models;

import android.os.Bundle;
import android.test.AndroidTestCase;

import com.pyxistech.android.rabbitreminder.models.AlertItem;
import com.pyxistech.android.rabbitreminder.models.AlertList;

public class TaskListTest extends AndroidTestCase {

	public void setUp() {
		list = buildList();
	}

	public void testTaskListCanBeTransformedInArray() {
		AlertItem[] array = list.toArray();

		assertEquals(10, array.length);
		assertEquals("item 1", array[0].toString());
		assertEquals("item 10", array[9].toString());
	}

	public void testTaskListIsParcelable() {
		Bundle bundle = new Bundle();
		bundle.putParcelable("TaskList", list);
		AlertList newList = bundle.getParcelable("TaskList");

		assertEquals(list, newList);
	}

	public void testTaskListIsParcelableAfterAnItemIsAdded() {
		Bundle bundle = new Bundle();
		list.addItem(new AlertItem("last item", true, 42.0, 1337.42, AlertItem.NOTIFY_WHEN_NEAR_OF));
		bundle.putParcelable("TaskList", list);
		AlertList newList = bundle.getParcelable("TaskList");

		assertEquals(list, newList);
	}

	public void testListCanBeCleared() {
		list.clear();
		assertEquals(0, list.size());
	}

	public void testListItemCanBeRemoved() {
		assertEquals(10, list.size());
		list.deleteItem(0);
		assertEquals(9, list.size());
	}

	public void testListItemCanBeEdited() {
		list.updateItem(0, "New Name");
		assertEquals("New Name", list.getItemAt(0).getText());
	}

	private AlertList buildList() {
		AlertList newList = new AlertList();

		newList.addItem(new AlertItem("item 1", false, null, null, AlertItem.NOTIFY_WHEN_NEAR_OF));
		newList.addItem(new AlertItem("item 2", false, null, null, AlertItem.NOTIFY_WHEN_NEAR_OF));
		newList.addItem(new AlertItem("item 3", false, null, null, AlertItem.NOTIFY_WHEN_NEAR_OF));
		newList.addItem(new AlertItem("item 4", false, null, null, AlertItem.NOTIFY_WHEN_NEAR_OF));
		newList.addItem(new AlertItem("item 5", false, null, null, AlertItem.NOTIFY_WHEN_NEAR_OF));
		newList.addItem(new AlertItem("item 6", false, null, null, AlertItem.NOTIFY_WHEN_NEAR_OF));
		newList.addItem(new AlertItem("item 7", false, null, null, AlertItem.NOTIFY_WHEN_NEAR_OF));
		newList.addItem(new AlertItem("item 8", false, null, null, AlertItem.NOTIFY_WHEN_NEAR_OF));
		newList.addItem(new AlertItem("item 9", false, null, null, AlertItem.NOTIFY_WHEN_NEAR_OF));
		newList.addItem(new AlertItem("item 10", false, null, null, AlertItem.NOTIFY_WHEN_NEAR_OF));

		return newList;
	}

	private AlertList list;
}
