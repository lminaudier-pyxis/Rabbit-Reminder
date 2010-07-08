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

package com.pyxistech.android.rabbitreminder.test.unit.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;

import com.pyxistech.android.rabbitreminder.models.AlertList;
import com.pyxistech.android.rabbitreminder.providers.AlertListProvider;

public class TaskListProviderTest extends ProviderTestCase2<AlertListProvider> {

	public TaskListProviderTest() {
		this(AlertListProvider.class, AlertList.AUTHORITY);
	}

	public TaskListProviderTest(Class<AlertListProvider> providerClass,
			String providerAuthority) {
		super(providerClass, providerAuthority);
	}

	public void setUp() throws Exception {
		super.setUp();

		provider = getProvider();

		uri = AlertList.Items.CONTENT_URI;

		projection = new String[2];
		projection[0] = AlertList.Items.NAME;
		projection[1] = AlertList.Items.DONE;
	}

	public void testItemAreSavedInDatabaseOnInsertion() {
		provider.insert(uri, null);
		provider.insert(uri, null);
		provider.insert(uri, null);
		provider.insert(uri, null);

		Cursor result = provider.query(uri, projection, null, null,
				AlertList.Items.DEFAULT_SORT_ORDER);

		assertEquals(4, result.getCount());
	}

	public void testDatabaseCanBeCleared() {
		provider.insert(uri, null);
		provider.insert(uri, null);
		provider.insert(uri, null);
		provider.insert(uri, null);

		provider.delete(uri, "", null);

		Cursor result = provider.query(uri, projection, null, null,
				AlertList.Items.DEFAULT_SORT_ORDER);

		assertEquals(0, result.getCount());
	}

	public void testItemCanBeRemovedFromDatabase() {
		provider.insert(uri, null);
		provider.insert(uri, null);
		provider.insert(uri, null);
		Uri itemUri = provider.insert(uri, null);
		provider.delete(itemUri, "", null);

		Cursor result = provider.query(uri, projection, null, null,
				AlertList.Items.DEFAULT_SORT_ORDER);

		assertEquals(3, result.getCount());
	}

	public void testItemCanBeUpdatedInDatabase() {
		provider.insert(uri, null);
		provider.insert(uri, null);
		provider.insert(uri, null);
		Uri itemUri = provider.insert(uri, null);

		ContentValues newValues = new ContentValues();
		newValues.put(AlertList.Items.NAME, "foo");

		provider.update(itemUri, newValues, null, null);

		Cursor result = provider.query(itemUri, projection, null, null,
				AlertList.Items.DEFAULT_SORT_ORDER);

		assertTrue(result.moveToFirst());
		assertEquals("foo", result.getString(0));
	}

	private AlertListProvider provider;
	private Uri uri;
	private String[] projection;
}
