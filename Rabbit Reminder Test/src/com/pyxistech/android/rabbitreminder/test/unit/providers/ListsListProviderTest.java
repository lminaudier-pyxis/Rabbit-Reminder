package com.pyxistech.android.rabbitreminder.test.unit.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;

import com.pyxistech.android.rabbitreminder.models.ListsList;
import com.pyxistech.android.rabbitreminder.providers.ListsListProvider;
import com.pyxistech.android.rabbitreminder.providers.TaskList;

public class ListsListProviderTest extends ProviderTestCase2<ListsListProvider> {
	public ListsListProviderTest() {
		super(ListsListProvider.class, ListsList.AUTHORITY);
	}
	
	public ListsListProviderTest(Class<ListsListProvider> providerClass, String providerAuthority) {
		super(providerClass, providerAuthority);
	}

	public void setUp() throws Exception {
		super.setUp();
		provider = getProvider();
		
		uri = ListsList.Items.CONTENT_URI;
		
		projection = new String[1];
		projection[0] = ListsList.Items.NAME;
	}
	
	public void testItemAdditionSavesItemInDatabase() {
		provider.insert(uri, null);
		provider.insert(uri, null);
		
		Cursor result = provider.query(uri, projection, null, null, ListsList.Items.DEFAULT_SORT_ORDER);
		
		assertEquals(2, result.getCount());
	}
	
	public void testDatabaseCanBeCleared() {
		provider.insert(uri, null);
		provider.insert(uri, null);
		provider.insert(uri, null);
		provider.insert(uri, null);
		
		provider.delete(uri, "", null);
		
		Cursor result = provider.query(uri, projection, null, null, ListsList.Items.DEFAULT_SORT_ORDER);
		
		assertEquals(0, result.getCount());
	}
	
	public void testItemCanBeRemovedFromDatabase() {
		provider.insert(uri, null);
		provider.insert(uri, null);
		provider.insert(uri, null);
		Uri itemUri = provider.insert(uri, null);
		provider.delete(itemUri, "", null);
		
		Cursor result = provider.query(uri, projection, null, null, ListsList.Items.DEFAULT_SORT_ORDER);
		
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
	
	private ListsListProvider provider;
	private Uri uri;
	private String[] projection;
}
