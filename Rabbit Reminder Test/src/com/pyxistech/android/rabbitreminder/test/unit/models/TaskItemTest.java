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
