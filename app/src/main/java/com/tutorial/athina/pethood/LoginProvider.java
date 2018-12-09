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

public class LoginProvider extends ContentProvider {

    public static final String TAG = LoginProvider.class.getSimpleName();
    private DbHelperLogin dbHelperLogin;

    public static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(LoginContract.AUTHORITY, LoginContract.TABLE, LoginContract.STATUS_DIR);
        sURIMatcher.addURI(LoginContract.AUTHORITY, LoginContract.TABLE + "/#", LoginContract.STATUS_ITEM);
    }

    @Override
    public boolean onCreate() {

        dbHelperLogin = new DbHelperLogin(getContext());
        Log.d(TAG, "onCreated");
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(LoginContract.TABLE);

        switch (sURIMatcher.match(uri)) {
            case LoginContract.STATUS_DIR:
                break;
            case LoginContract.STATUS_ITEM:
                qb.appendWhere(LoginContract.Login.ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        String orderBy = (TextUtils.isEmpty(sortOrder) ? LoginContract.DEFAULT_SORT : sortOrder);

        SQLiteDatabase db = dbHelperLogin.getReadableDatabase();
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        Log.d(TAG, " queried records: " + cursor.getCount());
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case LoginContract.STATUS_DIR:
                Log.d(TAG, "gotType: " + LoginContract.STATUS_TYPE_DIR);
                return LoginContract.STATUS_TYPE_DIR;
            case LoginContract.STATUS_ITEM:
                Log.d(TAG, "gotType: " + LoginContract.STATUS_TYPE_ITEM);
                return LoginContract.STATUS_TYPE_ITEM;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri ret = null;

        if (sURIMatcher.match(uri) != LoginContract.STATUS_DIR)
            throw new IllegalArgumentException("Illegal uri : " + uri);

        SQLiteDatabase db = dbHelperLogin.getWritableDatabase();
        long rowId = db.insertWithOnConflict(LoginContract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        if (rowId != -1) {
            long id = values.getAsLong(LoginContract.Login.ID);
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
            case LoginContract.STATUS_DIR:
                where = (selection == null) ? "1" : selection;
                break;
            case LoginContract.STATUS_ITEM:
                long id = ContentUris.parseId(uri);
                where = LoginContract.Login.ID +
                        "=" +
                        id +
                        (TextUtils.isEmpty(selection) ? "" : " and ( "
                                + selection + ")");
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }
        SQLiteDatabase db = dbHelperLogin.getWritableDatabase();
        int ret = db.delete(LoginContract.TABLE, where, selectionArgs);

        if (ret > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        Log.d(TAG, "deleted records: " + ret);
        return ret;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String where;

        switch (sURIMatcher.match(uri)) {
            case LoginContract.STATUS_ITEM:
                long id = ContentUris.parseId(uri);
                where = LoginContract.Login.ID + "=" + id +
                        (TextUtils.isEmpty(selection) ? "" : " and ( "
                                + selection + " ) ");
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = dbHelperLogin.getWritableDatabase();
        int ret = db.update(LoginContract.TABLE, values, where, selectionArgs);

        if (ret > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        Log.d(TAG, "updated records: " + ret);
        return ret;
    }
}
