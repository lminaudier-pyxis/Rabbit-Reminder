package com.pyxistech.android.rabbitreminder.providers;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.pyxistech.android.rabbitreminder.models.ListsList;

public abstract class AbstractListProvider extends ContentProvider {

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	public abstract String getTableName();
	public abstract HashMap<String, String> getProjectionMap();
	public abstract UriMatcher getUriMatcher();
	public abstract SQLiteOpenHelper getOpenHelper();
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
				SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			
				qb.setTables(getTableName());
			    qb.setProjectionMap(getProjectionMap());
				
				switch (getUriMatcher().match(uri)) {
			    case ITEMS:
			        break;
			
			    case ITEM_ID:
			        qb.appendWhere(BaseColumns._ID + "=" + uri.getPathSegments().get(1));
			        break;
			
			    default:
			        throw new IllegalArgumentException("Unknown URI " + uri);
			    }
			    
			    String orderBy;
			    if (TextUtils.isEmpty(sortOrder)) {
			        orderBy = ListsList.Items.DEFAULT_SORT_ORDER;
			    } else {
			        orderBy = sortOrder;
			    }
			    
			    SQLiteDatabase db = getOpenHelper().getReadableDatabase();
			    Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
			    
			    c.setNotificationUri(getContext().getContentResolver(), uri);
				return c;
			}

    static final int ITEMS = 1;
    static final int ITEM_ID = 2;
}
