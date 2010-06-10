package com.pyxistech.android.rabbitreminder.providers;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.pyxistech.android.rabbitreminder.models.TaskList;

public class TaskListProvider extends AbstractListProvider {

    private static final String DATABASE_NAME = "tasks_list.db";
    private static final int DATABASE_VERSION = 6;
    private static final String TABLE_NAME = "tasks";

    private static HashMap<String, String> projectionMap;
    
    private static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(TaskList.AUTHORITY, "items", ITEMS);
        sUriMatcher.addURI(TaskList.AUTHORITY, "items/#", ITEM_ID);

        projectionMap = new HashMap<String, String>();
        projectionMap.put(TaskList.Items._ID, TaskList.Items._ID);
        projectionMap.put(TaskList.Items.NAME, TaskList.Items.NAME);
        projectionMap.put(TaskList.Items.DONE, TaskList.Items.DONE);
        projectionMap.put(TaskList.Items.LATITUDE, TaskList.Items.LATITUDE);
        projectionMap.put(TaskList.Items.LONGITUDE, TaskList.Items.LONGITUDE);
        projectionMap.put(TaskList.Items.CREATED_DATE, TaskList.Items.CREATED_DATE);
        projectionMap.put(TaskList.Items.MODIFIED_DATE, TaskList.Items.MODIFIED_DATE);
    }
    
    public class TaskListDatabaseHelper extends SQLiteOpenHelper {

    	public TaskListDatabaseHelper(Context context) {
    		super(context, DATABASE_NAME, null, DATABASE_VERSION);
    	}

    	@Override
    	public void onCreate(SQLiteDatabase db) {
    		db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                    + TaskList.Items._ID + " INTEGER PRIMARY KEY,"
                    + TaskList.Items.NAME + " TEXT,"
                    + TaskList.Items.DONE + " INTEGER,"
                    + TaskList.Items.LATITUDE + " FLOAT,"
                    + TaskList.Items.LONGITUDE + " FLOAT,"
                    + TaskList.Items.CREATED_DATE + " INTEGER,"
                    + TaskList.Items.MODIFIED_DATE + " INTEGER"
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
		return TaskList.Items.CONTENT_TYPE;
	}

	@Override
	public Uri getContentUti() {
		return TaskList.Items.CONTENT_URI;
	}

	@Override
	public String getDefaultSortOrder() {
		return TaskList.Items.DEFAULT_SORT_ORDER;
	}

	@Override
	public String getItemContentType() {
		return TaskList.Items.CONTENT_ITEM_TYPE;
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
        if (values.containsKey(TaskList.Items.CREATED_DATE) == false) {
            values.put(TaskList.Items.CREATED_DATE, now);
        }

        if (values.containsKey(TaskList.Items.MODIFIED_DATE) == false) {
            values.put(TaskList.Items.MODIFIED_DATE, now);
        }

        if (values.containsKey(TaskList.Items.NAME) == false) {
            values.put(TaskList.Items.NAME, "unamed item");
        }

        if (values.containsKey(TaskList.Items.DONE) == false) {
            values.put(TaskList.Items.DONE, 0);
        }
	}
}
