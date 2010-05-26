package com.pyxistech.android.rabbitreminder.providers;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
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

    private static HashMap<String, String> sNotesProjectionMap;
    
    private static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(TaskList.AUTHORITY, "items", TASKS);
        sUriMatcher.addURI(TaskList.AUTHORITY, "items/#", TASK_ID);

        sNotesProjectionMap = new HashMap<String, String>();
        sNotesProjectionMap.put(TaskList.Items._ID, TaskList.Items._ID);
        sNotesProjectionMap.put(TaskList.Items.NAME, TaskList.Items.NAME);
        sNotesProjectionMap.put(TaskList.Items.DONE, TaskList.Items.DONE);
        sNotesProjectionMap.put(TaskList.Items.CREATED_DATE, TaskList.Items.CREATED_DATE);
        sNotesProjectionMap.put(TaskList.Items.MODIFIED_DATE, TaskList.Items.MODIFIED_DATE);
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
                    + TaskList.Items.CREATED_DATE + " INTEGER,"
                    + TaskList.Items.MODIFIED_DATE + " INTEGER"
                    + ");");
    		
    		db.execSQL("INSERT INTO " + TASKITEM_TABLE_NAME + "(" + TaskList.Items.NAME + ", " + TaskList.Items.DONE + ") VALUES (\"test\", 1);");
    	}

    	@Override
    	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    		db.execSQL("DROP TABLE IF EXISTS tasks");
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
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		return 0;
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

        switch (sUriMatcher.match(uri)) {
        case TASKS:
            qb.setTables(TASKITEM_TABLE_NAME);
            qb.setProjectionMap(sNotesProjectionMap);
            break;

        case TASK_ID:
            qb.setTables(TASKITEM_TABLE_NAME);
            qb.setProjectionMap(sNotesProjectionMap);
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
