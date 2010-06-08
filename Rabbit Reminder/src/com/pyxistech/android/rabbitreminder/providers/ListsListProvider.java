package com.pyxistech.android.rabbitreminder.providers;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.pyxistech.android.rabbitreminder.models.ListsList;

public class ListsListProvider extends AbstractListProvider {
	ListsListDatabaseHelper mOpenHelper;
	
    private static final String DATABASE_NAME = "lists_list.db";
    private static final int DATABASE_VERSION = 1;
    static final String TABLE_NAME = "lists";

    static HashMap<String, String> projectionMap;
    
    static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(ListsList.AUTHORITY, "items", ITEMS);
        sUriMatcher.addURI(ListsList.AUTHORITY, "items/#", ITEM_ID);

        projectionMap = new HashMap<String, String>();
        projectionMap.put(ListsList.Items._ID, ListsList.Items._ID);
        projectionMap.put(ListsList.Items.NAME, ListsList.Items.NAME);
        projectionMap.put(ListsList.Items.REMAINING_TASK_COUNT, ListsList.Items.REMAINING_TASK_COUNT);
        projectionMap.put(ListsList.Items.LOCATION_ID, ListsList.Items.LOCATION_ID);
        projectionMap.put(ListsList.Items.CREATED_DATE, ListsList.Items.CREATED_DATE);
        projectionMap.put(ListsList.Items.MODIFIED_DATE, ListsList.Items.MODIFIED_DATE);
    }
    
	public class ListsListDatabaseHelper extends SQLiteOpenHelper {
		public ListsListDatabaseHelper(Context context) { 
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                    + ListsList.Items._ID + " INTEGER PRIMARY KEY,"
                    + ListsList.Items.NAME + " TEXT,"
                    + ListsList.Items.REMAINING_TASK_COUNT + " INTEGER,"
                    + ListsList.Items.LOCATION_ID + " INTEGER,"
                    + ListsList.Items.CREATED_DATE + " INTEGER,"
                    + ListsList.Items.MODIFIED_DATE + " INTEGER"
                    + ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
            onCreate(db);
		}
		
	}
	
	public void validate(ContentValues values) {
		Long now = Long.valueOf(System.currentTimeMillis());

        // Make sure that the fields are all set
        if (values.containsKey(ListsList.Items.CREATED_DATE) == false) {
            values.put(ListsList.Items.CREATED_DATE, now);
        }

        if (values.containsKey(ListsList.Items.MODIFIED_DATE) == false) {
            values.put(ListsList.Items.MODIFIED_DATE, now);
        }

        if (values.containsKey(ListsList.Items.NAME) == false) {
            values.put(ListsList.Items.NAME, "unamed list");
        }

        if (values.containsKey(ListsList.Items.REMAINING_TASK_COUNT) == false) {
            values.put(ListsList.Items.REMAINING_TASK_COUNT, 0);
        }
        
        if (values.containsKey(ListsList.Items.LOCATION_ID) == false) {
            values.put(ListsList.Items.LOCATION_ID, -1);
        }
	}

	
	
	@Override
	public boolean onCreate() {
		mOpenHelper = new ListsListDatabaseHelper(getContext());
		return true;
	}

	@Override
	public SQLiteOpenHelper getOpenHelper() {
		return mOpenHelper;
	}

	@Override
	public HashMap<String, String> getProjectionMap() {
		return projectionMap;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public UriMatcher getUriMatcher() {
		return sUriMatcher;
	}

	@Override
	public String getContentType() {
		return ListsList.Items.CONTENT_TYPE;
	}

	@Override
	public String getItemContentType() {
		return ListsList.Items.CONTENT_ITEM_TYPE;
	}

	@Override
	public Uri getContentUti() {
		return ListsList.Items.CONTENT_URI;
	}

	@Override
	public String getDefaultSortOrder() {
		return ListsList.Items.DEFAULT_SORT_ORDER;
	}
}
