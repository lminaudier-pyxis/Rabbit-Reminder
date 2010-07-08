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

package com.pyxistech.android.rabbitreminder.providers;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

public abstract class AbstractListProvider extends ContentProvider {

	@Override
	public abstract boolean onCreate();

	public abstract String getTableName();
	public abstract HashMap<String, String> getProjectionMap();
	public abstract UriMatcher getUriMatcher();
	public abstract SQLiteOpenHelper getOpenHelper();
	public abstract String getItemContentType();
	public abstract String getContentType();
	public abstract void validate(ContentValues values);
	public abstract Uri getContentUti();
	public abstract String getDefaultSortOrder();
	
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
			        orderBy = getDefaultSortOrder();
			    } else {
			        orderBy = sortOrder;
			    }
			    
			    SQLiteDatabase db = getOpenHelper().getReadableDatabase();
			    Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
			    
			    c.setNotificationUri(getContext().getContentResolver(), uri);
				return c;
			}

    @Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = getOpenHelper().getWritableDatabase();
		int count;
		switch (getUriMatcher().match(uri)) {
		case ITEMS:
			count = db.update(getTableName(), values, selection, selectionArgs);
			break;
	
		case ITEM_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.update(getTableName(), values, BaseColumns._ID + "=" + noteId
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
			break;
	
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (getUriMatcher().match(uri)) {
		case ITEMS:
			return getContentType();
	
		case ITEM_ID:
			return getItemContentType();
	
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = getOpenHelper().getWritableDatabase();
	    int count;
	    
	    switch (getUriMatcher().match(uri)) {
	    case ITEMS:
	        count = db.delete(getTableName(), where, whereArgs);
	        break;
	
	    case ITEM_ID:
	        String noteId = uri.getPathSegments().get(1);
	        count = db.delete(getTableName(), BaseColumns._ID + "=" + noteId
	                + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
	        break;
	
	    default:
	        throw new IllegalArgumentException("Unknown URI " + uri);
	    }
	
	    getContext().getContentResolver().notifyChange(uri, null);
	    return count;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
	    if (getUriMatcher().match(uri) != ITEMS) {
	        throw new IllegalArgumentException("Unknown URI " + uri);
	    }
	
	    ContentValues values;
	    if (initialValues != null) {
	        values = new ContentValues(initialValues);
	    } else {
	        values = new ContentValues();
	    }
	
	    validate(values);
	
	    SQLiteDatabase db = getOpenHelper().getWritableDatabase();
	    long rowId = db.insert(getTableName(), "", values);
	    
	    if (rowId > 0) {
	        Uri noteUri = ContentUris.withAppendedId(getContentUti(), rowId);
	        getContext().getContentResolver().notifyChange(noteUri, null);
	        return noteUri;
	    }
	
	    throw new SQLException("Failed to insert row into " + uri);
	}

	static final int ITEMS = 1;
    static final int ITEM_ID = 2;
}
