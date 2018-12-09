package com.tutorial.athina.pethood;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class UsersProvider extends ContentProvider {

    public static final String TAG = UsersProvider.class.getSimpleName();
    private DbHelperUsers dbHelperUsers;

    public static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(UsersContract.AUTHORITY, UsersContract.TABLE, UsersContract.STATUS_DIR);
        sURIMatcher.addURI(UsersContract.AUTHORITY, UsersContract.TABLE + "/#", UsersContract.STATUS_ITEM);
    }

    @Override
    public boolean onCreate() {

        dbHelperUsers = new DbHelperUsers(getContext());
        Log.d(TAG, "onCreated");
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(UsersContract.TABLE);

        switch (sURIMatcher.match(uri)) {
            case UsersContract.STATUS_DIR:
                break;
            case UsersContract.STATUS_ITEM:
                qb.appendWhere(UsersContract.Users.ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        String orderBy = (TextUtils.isEmpty(sortOrder) ? UsersContract.DEFAULT_SORT : sortOrder);

        SQLiteDatabase db = dbHelperUsers.getReadableDatabase();
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        Log.d(TAG, " queried records: " + cursor.getCount());
        return cursor;
    }


    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case UsersContract.STATUS_DIR:
                Log.d(TAG, "gotType: " + UsersContract.STATUS_TYPE_DIR);
                return UsersContract.STATUS_TYPE_DIR;
            case UsersContract.STATUS_ITEM:
                Log.d(TAG, "gotType: " + UsersContract.STATUS_TYPE_ITEM);
                return UsersContract.STATUS_TYPE_ITEM;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri ret = null;

        if (sURIMatcher.match(uri) != UsersContract.STATUS_DIR)
            throw new IllegalArgumentException("Illegal uri : " + uri);

        SQLiteDatabase db = dbHelperUsers.getWritableDatabase();
        long rowId = db.insertWithOnConflict(UsersContract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        if (rowId != -1) {
            long id = values.getAsLong(UsersContract.Users.ID);
            ret = ContentUris.withAppendedId(uri, id);
            Log.d(TAG, "inserted uri: " + ret);

            getContext().getContentResolver().notifyChange(uri, null);
        }

        return ret;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String where;
        switch (sURIMatcher.match(uri)) {
            case UsersContract.STATUS_DIR:
                where = (selection == null) ? "1" : selection;
                break;
            case UsersContract.STATUS_ITEM:
                long id = ContentUris.parseId(uri);
                where = UsersContract.Users.ID +
                        "=" +
                        id +
                        (TextUtils.isEmpty(selection) ? "" : " and ( "
                                + selection + ")");
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }
        SQLiteDatabase db = dbHelperUsers.getWritableDatabase();
        int ret = db.delete(UsersContract.TABLE, where, selectionArgs);

        if (ret > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        Log.d(TAG, "deleted records: " + ret);
        return ret;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String where;

        switch (sURIMatcher.match(uri)) {
            case UsersContract.STATUS_ITEM:
                long id = ContentUris.parseId(uri);
                where = UsersContract.Users.ID + "=" + id +
                        (TextUtils.isEmpty(selection) ? "" : " and ( "
                                + selection + " ) ");
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = dbHelperUsers.getWritableDatabase();
        int ret = db.update(UsersContract.TABLE, values, where, selectionArgs);

        if (ret > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        Log.d(TAG, "updated records: " + ret);
        return ret;
    }
}
