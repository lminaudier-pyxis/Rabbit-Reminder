package com.pyxistech.android.rabbitreminder.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.KeyEvent;

import com.pyxistech.android.rabbitreminder.activities.TaskListActivity;
import com.pyxistech.android.rabbitreminder.adaptaters.TaskListAdapter;
import com.pyxistech.android.rabbitreminder.models.TaskItem;

public class TaskListActivityTest extends ActivityInstrumentationTestCase2<TaskListActivity> {
	public TaskListActivityTest() {
        super("com.pyxistech.android.rabbitreminder", TaskListActivity.class);
    }
	
	public void testPrecondition() {
		assertNotNull(getActivity());
	}
	
	@UiThreadTest
	public void testAddingElementsThroughtAdapter() {
		int initialCount = getListCount();
		for (int i = 0; i < 200; i++) {
			getListAdapter().addItem(new TaskItem("test item " + i, false));
		}
		int afterCount = getListCount();
		
		assertEquals(200, afterCount - initialCount);
	}
	
	public void testAddingElementsThroughtAddTaskActivity() {
		int numberOfNewItems = 10;
		
		int initialCount = getListCount();
		for (int i = 0; i < numberOfNewItems; i++) {
			sendKeys(KeyEvent.KEYCODE_MENU);
			sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
			sendKeys("I T E M SPACE");
			sendKeys(String.valueOf(i).toUpperCase());
			sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
			sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
		}
		int afterCount = getListCount();
		
		assertEquals(numberOfNewItems, afterCount - initialCount);
	}

	private int getListCount() {
		return getListAdapter().getCount();
	}

	private TaskListAdapter getListAdapter() {
		return ((TaskListAdapter) getActivity().getListAdapter());
	}
}
