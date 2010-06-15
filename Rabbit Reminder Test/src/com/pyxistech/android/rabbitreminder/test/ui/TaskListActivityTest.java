package com.pyxistech.android.rabbitreminder.test.ui;

import android.content.ContentValues;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.CheckedTextView;

import com.jayway.android.robotium.solo.Solo;
import com.pyxistech.android.rabbitreminder.activities.TaskListActivity;
import com.pyxistech.android.rabbitreminder.adaptaters.TaskListAdapter;
import com.pyxistech.android.rabbitreminder.models.TaskItem;
import com.pyxistech.android.rabbitreminder.models.TaskList;

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
		
		initialListSize = getListSize();
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
		
		for (int i = 0; i < numberOfNewItems; i++) {
			getListAdapter().addItem(new TaskItem("test item " + i, false, null, null));
		}
		int afterCount = getListSize();
		
		assertEquals(numberOfNewItems, afterCount - initialListSize);
	}

	public void testAddingElementsThroughtAddTaskActivity() {
		int numberOfNewItems = 5;
		
		for (int i = 0; i < numberOfNewItems; i++) {
			solo.clickOnMenuItem("Add Task");
			solo.enterText(0, "item " + String.valueOf(initialListSize + i + 1).toUpperCase() );
			solo.clickOnButton("OK");
		}
		
		solo.sleep(500);
		
		int afterCount = getListSize();
		
		assertEquals(numberOfNewItems, afterCount - initialListSize);
	}
	
	public void testCheckStateAreSavedOnScrolling() {
		solo.clickOnText("item 49");
		solo.sleep(1000);
		solo.scrollDown();
		solo.sleep(1000);
		solo.scrollUp();
		solo.scrollUp();
		
		assertTrue(getListItemView(0).isChecked());
	}
	
	public void testCheckStateAreSavedOnRotation() {
		solo.clickOnText("item 49");
		solo.sleep(1000);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		assertTrue(getListItemView(0).isChecked());
		solo.sleep(1000);
		solo.setActivityOrientation(Solo.PORTRAIT);
		assertTrue(getListItemView(0).isChecked());
	}
	
	public void testItemCanBeDeleted() {
		solo.clickLongOnText("item 1");
		solo.sleep(1000);
		solo.clickOnText("Delete");
		solo.sleep(1000);
		solo.clickOnText("OK");
		solo.sleep(1000);
		
		assertEquals(initialListSize - 1, getListSize());
	}
	
	public void testItemDeletionCanBeCancelled() {
		solo.clickLongOnText("item 1");
		solo.sleep(1000);
		solo.clickOnText("Delete");
		solo.sleep(1000);
		solo.clickOnText("Cancel");
		solo.sleep(1000);
		
		assertEquals(initialListSize, getListSize());
	}
	
	public void testItemCanBeEdited() {
		solo.clickLongOnText("item 49");
		solo.sleep(1000);
		solo.clickOnText("Edit");
		solo.sleep(1000);
		solo.enterText(0, " edited");
		solo.sleep(1000);
		solo.clickOnText("OK");
		solo.sleep(1000);
		
		assertEquals(initialListSize, getListSize());
		assertEquals("item 49 edited", getListItemView(0).getText());
	}
	
	public void testRotationDoesNotAffectEdition() {
		solo.clickLongOnText("item 49");
		solo.sleep(1000);
		solo.clickOnText("Edit");
		solo.sleep(1000);
		solo.enterText(0, " edited");
		solo.sleep(1000);
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(1000);
		solo.clickOnText("OK");
		solo.sleep(1000);
		
		assertEquals(initialListSize, getListSize());
		assertEquals("item 49 edited", getListItemView(0).getText());
	}
	
	public void testCancelingTheCurrentTaskEditionWorks() {
		solo.clickOnMenuItem("Add Task");
		solo.sleep(1000);
		solo.goBack();
		solo.sleep(1000);
	}
	


	private void buildList() {
		getListAdapter().clearList();
		getActivity().getContentResolver().delete(TaskList.Items.CONTENT_URI, "", null);
		for (int i = 0; i < 50; i++) {
			getListAdapter().addItem(new TaskItem("item " + i, false, null, null));

	    	ContentValues values = new ContentValues();
	    	values.put(TaskList.Items.NAME, "item " + i);
	    	values.put(TaskList.Items.DONE, 0);
	    	getActivity().getContentResolver().insert(TaskList.Items.CONTENT_URI, values);
		}
		getActivity().refreshList((TaskListAdapter)getActivity().getListAdapter());
	}

	private CheckedTextView getListItemView(int index) {
		return ((CheckedTextView) solo.getCurrentListViews().get(0).getChildAt(index));
	}

	private int getListSize() {
		return getListAdapter().getCount();
	}

	private TaskListAdapter getListAdapter() {
		return ((TaskListAdapter) getActivity().getListAdapter());
	}
	
	private Solo solo;
	private int initialListSize;
}
