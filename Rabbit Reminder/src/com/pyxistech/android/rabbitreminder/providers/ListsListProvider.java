package com.pyxistech.android.rabbitreminder.providers;

import java.util.HashMap;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;

import com.pyxistech.android.rabbitreminder.models.ListsList;

public class ListsListProvider extends AbstractListProvider {
    private static final String DATABASE_NAME = "lists_list.db";
    private static final int DATABASE_VERSION = 1;
    static final String TABLE_NAME = "lists";

    static final int LISTS = 1;
    static final int LIST_ID = 2;

    static HashMap<String, String> projectionMap;
    
    static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(ListsList.AUTHORITY, "items", LISTS);
        sUriMatcher.addURI(ListsList.AUTHORITY, "items/#", LIST_ID);

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
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        
        switch (sUriMatcher.match(uri)) {
        case LISTS:
            count = db.delete(TABLE_NAME, where, whereArgs);
            break;

        case LIST_ID:
            String noteId = uri.getPathSegments().get(1);
            count = db.delete(TABLE_NAME, ListsList.Items._ID + "=" + noteId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case LISTS:
			return ListsList.Items.CONTENT_TYPE;

		case LIST_ID:
			return ListsList.Items.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != LISTS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

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

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(TABLE_NAME, "NULL", values);
        
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(ListsList.Items.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
	}

	ListsListDatabaseHelper mOpenHelper;
	
	@Override
	public boolean onCreate() {
		mOpenHelper = new ListsListDatabaseHelper(getContext());
		return true;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case LISTS:
			count = db.update(TABLE_NAME, values, selection, selectionArgs);
			break;

		case LIST_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.update(TABLE_NAME, values, ListsList.Items._ID + "=" + noteId
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
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
}
