package com.pyxistech.android.rabbitreminder.providers;

import android.net.Uri;
import android.provider.BaseColumns;

public class TaskList {
	public static final String AUTHORITY = "com.pyxistech.rabbitreminder.providers.TaskList";
	
	private TaskList() {}
	
	public static final class Items implements BaseColumns {
		private Items() {}

		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/items");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.pyxistech.android.rabbitreminder.providers.tasklist";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.pyxistech.android.rabbitreminder.providers.tasklist";
		public static final String DEFAULT_SORT_ORDER = "modified DESC";
		public static final String NAME = "name";
		public static final String DONE = "done";
		public static final String LIST_ID = "listId";
		public static final String CREATED_DATE = "created";
		public static final String MODIFIED_DATE = "modified";
	}
}
