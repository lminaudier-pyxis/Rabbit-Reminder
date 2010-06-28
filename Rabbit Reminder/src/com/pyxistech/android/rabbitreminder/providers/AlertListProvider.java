package com.pyxistech.android.rabbitreminder.providers;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.pyxistech.android.rabbitreminder.models.AlertList;

public class AlertListProvider extends AbstractListProvider {

    private static final String DATABASE_NAME = "alert_list.db";
    private static final int DATABASE_VERSION = 8;
    private static final String TABLE_NAME = "alerts";

    private static HashMap<String, String> projectionMap;
    
    private static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AlertList.AUTHORITY, "items", ITEMS);
        sUriMatcher.addURI(AlertList.AUTHORITY, "items/#", ITEM_ID);

        projectionMap = new HashMap<String, String>();
        projectionMap.put(AlertList.Items._ID, AlertList.Items._ID);
        projectionMap.put(AlertList.Items.NAME, AlertList.Items.NAME);
        projectionMap.put(AlertList.Items.DONE, AlertList.Items.DONE);
        projectionMap.put(AlertList.Items.LATITUDE, AlertList.Items.LATITUDE);
        projectionMap.put(AlertList.Items.LONGITUDE, AlertList.Items.LONGITUDE);
        projectionMap.put(AlertList.Items.NOTIFICATION_MODE, AlertList.Items.NOTIFICATION_MODE);
        projectionMap.put(AlertList.Items.CREATED_DATE, AlertList.Items.CREATED_DATE);
        projectionMap.put(AlertList.Items.MODIFIED_DATE, AlertList.Items.MODIFIED_DATE);
    }
    
    public class TaskListDatabaseHelper extends SQLiteOpenHelper {

    	public TaskListDatabaseHelper(Context context) {
    		super(context, DATABASE_NAME, null, DATABASE_VERSION);
    	}

    	@Override
    	public void onCreate(SQLiteDatabase db) {
    		db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                    + AlertList.Items._ID + " INTEGER PRIMARY KEY,"
                    + AlertList.Items.NAME + " TEXT,"
                    + AlertList.Items.DONE + " INTEGER,"
                    + AlertList.Items.LATITUDE + " FLOAT,"
                    + AlertList.Items.LONGITUDE + " FLOAT,"
                    + AlertList.Items.NOTIFICATION_MODE + " INTEGER,"
                    + AlertList.Items.CREATED_DATE + " INTEGER,"
                    + AlertList.Items.MODIFIED_DATE + " INTEGER"
                    + ");");
    	}

    	@Override
    	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
    	}
    }
    
    private TaskListDatabaseHelper mOpenHelper;

	@Override
	public boolean onCreate() {
        mOpenHelper = new TaskListDatabaseHelper(getContext());
        return true;
	}

	@Override
	public String getContentType() {
		return AlertList.Items.CONTENT_TYPE;
	}

	@Override
	public Uri getContentUti() {
		return AlertList.Items.CONTENT_URI;
	}

	@Override
	public String getDefaultSortOrder() {
		return AlertList.Items.DEFAULT_SORT_ORDER;
	}

	@Override
	public String getItemContentType() {
		return AlertList.Items.CONTENT_ITEM_TYPE;
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
	public void validate(ContentValues values) {
		Long now = Long.valueOf(System.currentTimeMillis());

        // Make sure that the fields are all set
        if (values.containsKey(AlertList.Items.CREATED_DATE) == false) {
            values.put(AlertList.Items.CREATED_DATE, now);
        }

        if (values.containsKey(AlertList.Items.MODIFIED_DATE) == false) {
            values.put(AlertList.Items.MODIFIED_DATE, now);
        }

        if (values.containsKey(AlertList.Items.NAME) == false) {
            values.put(AlertList.Items.NAME, "unamed item");
        }

        if (values.containsKey(AlertList.Items.DONE) == false) {
            values.put(AlertList.Items.DONE, 0);
        }
	}
}
