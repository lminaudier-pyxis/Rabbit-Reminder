package com.pyxistech.android.rabbitreminder.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.CheckedTextView;

import com.jayway.android.robotium.solo.Solo;
import com.pyxistech.android.rabbitreminder.activities.TaskListActivity;
import com.pyxistech.android.rabbitreminder.adaptaters.TaskListAdapter;
import com.pyxistech.android.rabbitreminder.models.TaskItem;

public class TaskListActivityTest extends ActivityInstrumentationTestCase2<TaskListActivity> {
	
	public TaskListActivityTest() {
        super("com.pyxistech.android.rabbitreminder", TaskListActivity.class);
    }
	
	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		
		try {
			runTestOnUiThread(new Runnable() {
				@Override
				public void run() {
					buildList();
				}
			});
		} catch (Throwable e) {
			throw new Exception(e);
		}
	}

	private void buildList() {
		getListAdapter().clearList();
		for (int i = 0; i < 50; i++) {
			getListAdapter().addItem(new TaskItem("item " + i, false));
		}
	}
	
	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}
	
	public void testPrecondition() {
		assertNotNull(getActivity());
	}
	
	@UiThreadTest
	public void testAddingElementsThroughtAdapter() {
		int numberOfNewItems = 10;
		
		int initialCount = getListCount();
		for (int i = 0; i < numberOfNewItems; i++) {
			getListAdapter().addItem(new TaskItem("test item " + i, false));
		}
		int afterCount = getListCount();
		
		assertEquals(numberOfNewItems, afterCount - initialCount);
	}
	
	public void testAddingElementsThroughtAddTaskActivity() {
		int numberOfNewItems = 5;
		
		int initialCount = getListCount();
		for (int i = 0; i < numberOfNewItems; i++) {
			solo.clickOnMenuItem("Add Item");
			solo.enterText(0, "item " + String.valueOf(initialCount + i + 1).toUpperCase() );
			solo.clickOnButton("OK");
		}
		
		solo.sleep(500);
		
		int afterCount = getListCount();
		
		assertEquals(numberOfNewItems, afterCount - initialCount);
	}
	
	
	public void testCheckStateAreSavedOnScrolling() {
		solo.clickOnText("item 0");
		solo.sleep(1000);
		solo.scrollDown();
		solo.sleep(1000);
		solo.scrollUp();
		solo.scrollUp();
		
		assertTrue(((CheckedTextView) solo.getCurrentListViews().get(0).getChildAt(0)).isChecked());
	}
	
	public void testCheckStateAreSavedOnRotation() {
		solo.clickOnText("item 0");
		solo.sleep(1000);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		assertTrue(((CheckedTextView) solo.getCurrentListViews().get(0).getChildAt(0)).isChecked());
		solo.sleep(1000);
		solo.setActivityOrientation(Solo.PORTRAIT);
		assertTrue(((CheckedTextView) solo.getCurrentListViews().get(0).getChildAt(0)).isChecked());
	}

	private int getListCount() {
		return getListAdapter().getCount();
	}

	private TaskListAdapter getListAdapter() {
		return ((TaskListAdapter) getActivity().getListAdapter());
	}
	
	private Solo solo;
}
