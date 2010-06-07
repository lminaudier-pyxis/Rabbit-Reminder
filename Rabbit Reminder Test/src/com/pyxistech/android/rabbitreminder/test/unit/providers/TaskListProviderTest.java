package com.pyxistech.android.rabbitreminder.test.unit.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;

import com.pyxistech.android.rabbitreminder.providers.TaskList;
import com.pyxistech.android.rabbitreminder.providers.TaskListProvider;

public class TaskListProviderTest extends ProviderTestCase2<TaskListProvider> {

	public TaskListProviderTest(){
		this(TaskListProvider.class, TaskList.AUTHORITY);
	}
	
	public TaskListProviderTest(Class<TaskListProvider> providerClass, String providerAuthority) {
		super(providerClass, providerAuthority);
	}
	
	public void setUp() throws Exception {
		super.setUp();
		
		provider = getProvider();
		
		uri = com.pyxistech.android.rabbitreminder.providers.TaskList.Items.CONTENT_URI;
		
		projection = new String[2];
		projection[0] = TaskList.Items.NAME;
		projection[1] = TaskList.Items.DONE;
	}
	
	public void testItemAreSavedInDatabaseOnInsertion() {
		provider.insert(uri, null);
		provider.insert(uri, null);
		provider.insert(uri, null);
		provider.insert(uri, null);
		
		Cursor result = provider.query(uri, projection, null, null, TaskList.Items.DEFAULT_SORT_ORDER);
		
		assertEquals(4, result.getCount());
	}
	
	public void testDatabaseCanBeCleared() {
		provider.insert(uri, null);
		provider.insert(uri, null);
		provider.insert(uri, null);
		provider.insert(uri, null);
		
		provider.delete(uri, "", null);
		
		Cursor result = provider.query(uri, projection, null, null, TaskList.Items.DEFAULT_SORT_ORDER);
		
		assertEquals(0, result.getCount());
	}
	
	public void testItemCanBeRemovedFromDatabase() {
		provider.insert(uri, null);
		provider.insert(uri, null);
		provider.insert(uri, null);
		Uri itemUri = provider.insert(uri, null);
		provider.delete(itemUri, "", null);
		
		Cursor result = provider.query(uri, projection, null, null, TaskList.Items.DEFAULT_SORT_ORDER);
		
		assertEquals(3, result.getCount());
	}
	
	public void testItemCanBeUpdatedInDatabase() {
		provider.insert(uri, null);
		provider.insert(uri, null);
		provider.insert(uri, null);
		Uri itemUri = provider.insert(uri, null);
		
		ContentValues newValues = new ContentValues();
		newValues.put(TaskList.Items.NAME, "foo");
		
		provider.update(itemUri, newValues, null, null);
		
		Cursor result = provider.query(itemUri, projection, null, null, TaskList.Items.DEFAULT_SORT_ORDER);
		
		assertTrue(result.moveToFirst());
		assertEquals("foo", result.getString(0));
	}
	
	private TaskListProvider provider;
	private Uri uri;
	private String[] projection;
}
