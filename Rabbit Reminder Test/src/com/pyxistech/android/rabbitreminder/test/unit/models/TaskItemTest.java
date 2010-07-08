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

public class TaskItemTest extends AndroidTestCase {
	public void setUp() {

	}

	public void testEqualsWorks() {
		AlertItem item1 = new AlertItem("item", true, 0.0, 0.0, AlertItem.NOTIFY_WHEN_NEAR_OF);
		AlertItem item2 = new AlertItem("item", true, 0.0, 0.0, AlertItem.NOTIFY_WHEN_NEAR_OF);
		AlertItem item3 = new AlertItem("item", false, 0.0, 0.0, AlertItem.NOTIFY_WHEN_NEAR_OF);
		AlertItem item4 = new AlertItem("foo", true, 0.0, 0.0, AlertItem.NOTIFY_WHEN_NEAR_OF);

		assertTrue(item1.equals(item2));
		assertFalse(item1.equals(item3));
		assertFalse(item1.equals(item4));
	}

	public void testTaskItemIsParcelable() {
		Bundle bundle = new Bundle();
		AlertItem item = new AlertItem("last item", true, 42.0, 1337.42, AlertItem.NOTIFY_WHEN_NEAR_OF);
		bundle.putParcelable("item", item);
		AlertItem newItem = (AlertItem) bundle.getParcelable("item");

		assertEquals(item, newItem);
	}

}
