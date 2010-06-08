package com.pyxistech.android.rabbitreminder.providers;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class TaskListProvider extends ContentProvider {

    private static final String DATABASE_NAME = "task_list.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TASKITEM_TABLE_NAME = "tasks";

    private static final int TASKS = 1;
    private static final int TASK_ID = 2;

    private static HashMap<String, String> projectionMap;
    
    private static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(TaskList.AUTHORITY, "items", TASKS);
        sUriMatcher.addURI(TaskList.AUTHORITY, "items/#", TASK_ID);

        projectionMap = new HashMap<String, String>();
        projectionMap.put(TaskList.Items._ID, TaskList.Items._ID);
        projectionMap.put(TaskList.Items.NAME, TaskList.Items.NAME);
        projectionMap.put(TaskList.Items.DONE, TaskList.Items.DONE);
        projectionMap.put(TaskList.Items.LIST_ID, TaskList.Items.LIST_ID);
        projectionMap.put(TaskList.Items.CREATED_DATE, TaskList.Items.CREATED_DATE);
        projectionMap.put(TaskList.Items.MODIFIED_DATE, TaskList.Items.MODIFIED_DATE);
    }
    
    public class TaskListDatabaseHelper extends SQLiteOpenHelper {

    	public TaskListDatabaseHelper(Context context) {
    		super(context, DATABASE_NAME, null, DATABASE_VERSION);
    	}

    	@Override
    	public void onCreate(SQLiteDatabase db) {
    		db.execSQL("CREATE TABLE " + TASKITEM_TABLE_NAME + " ("
                    + TaskList.Items._ID + " INTEGER PRIMARY KEY,"
                    + TaskList.Items.NAME + " TEXT,"
                    + TaskList.Items.DONE + " INTEGER,"
                    + TaskList.Items.LIST_ID + " INTEGER,"
                    + TaskList.Items.CREATED_DATE + " INTEGER,"
                    + TaskList.Items.MODIFIED_DATE + " INTEGER"
                    + ");");
    	}

    	@Override
    	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    		db.execSQL("DROP TABLE IF EXISTS " + TASKITEM_TABLE_NAME);
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
	public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
        case TASKS:
            return TaskList.Items.CONTENT_TYPE;

        case TASK_ID:
            return TaskList.Items.CONTENT_ITEM_TYPE;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != TASKS) {
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
        
        if (values.containsKey(TaskList.Items.LIST_ID) == false) {
            values.put(TaskList.Items.LIST_ID, -1);
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(TASKITEM_TABLE_NAME, "item", values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(TaskList.Items.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case TASKS:
            count = db.delete(TASKITEM_TABLE_NAME, where, whereArgs);
            break;

        case TASK_ID:
            String noteId = uri.getPathSegments().get(1);
            count = db.delete(TASKITEM_TABLE_NAME, TaskList.Items._ID + "=" + noteId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case TASKS:
            count = db.update(TASKITEM_TABLE_NAME, values, selection, selectionArgs);
            break;

        case TASK_ID:
            String noteId = uri.getPathSegments().get(1);
            count = db.update(TASKITEM_TABLE_NAME, values, TaskList.Items._ID + "=" + noteId
                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		qb.setTables(TASKITEM_TABLE_NAME);
        qb.setProjectionMap(projectionMap);
		
        switch (sUriMatcher.match(uri)) {
        case TASKS:
            break;

        case TASK_ID:
            qb.appendWhere(TaskList.Items._ID + "=" + uri.getPathSegments().get(1));
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = TaskList.Items.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }
        
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        
        c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

}
