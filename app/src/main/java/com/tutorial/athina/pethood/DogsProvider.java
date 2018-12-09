package com.tutorial.athina.pethood;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

public class DogsProvider extends ContentProvider {

    public static final String TAG = DogsProvider.class.getSimpleName();
    private DbHelperDogs dbHelperDogs;

    public static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(DogsContract.AUTHORITY, DogsContract.TABLE, DogsContract.STATUS_DIR);
        sURIMatcher.addURI(DogsContract.AUTHORITY, DogsContract.TABLE + "/#", DogsContract.STATUS_ITEM);
    }
    @Override
    public boolean onCreate() {
        dbHelperDogs = new DbHelperDogs(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(DogsContract.TABLE);

        switch (sURIMatcher.match(uri)) {
            case DogsContract.STATUS_DIR:
                break;
            case DogsContract.STATUS_ITEM:
                qb.appendWhere(DogsContract.Dogs.ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        String orderBy = (TextUtils.isEmpty(sortOrder) ? DogsContract.DEFAULT_SORT : sortOrder);

        SQLiteDatabase db = dbHelperDogs.getReadableDatabase();
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        Log.d(TAG, " queried records: " + cursor.getCount());
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case DogsContract.STATUS_DIR:
                Log.d(TAG, "gotType: " + DogsContract.STATUS_TYPE_DIR);
                return DogsContract.STATUS_TYPE_DIR;
            case DogsContract.STATUS_ITEM:
                Log.d(TAG, "gotType: " + DogsContract.STATUS_TYPE_ITEM);
                return DogsContract.STATUS_TYPE_ITEM;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri ret = null;

        if (sURIMatcher.match(uri) != DogsContract.STATUS_DIR)
            throw new IllegalArgumentException("Illegal uri : " + uri);

        SQLiteDatabase db = dbHelperDogs.getWritableDatabase();
        long rowId = db.insertWithOnConflict(DogsContract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        if (rowId != -1) {
            long id = values.getAsLong(DogsContract.Dogs.ID);
            ret = ContentUris.withAppendedId(uri, id);
            Log.d(TAG, "inserted uri: " + ret);

            getContext().getContentResolver().notifyChange(uri, null);
        }

        return ret;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String where;
        switch (sURIMatcher.match(uri)) {
            case DogsContract.STATUS_DIR:
                where = (selection == null) ? "1" : selection;
                break;
            case DogsContract.STATUS_ITEM:
                long id = ContentUris.parseId(uri);
                where = DogsContract.Dogs.ID +
                        "=" +
                        id +
                        (TextUtils.isEmpty(selection) ? "" : " and ( "
                                + selection + ")");
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }
        SQLiteDatabase db = dbHelperDogs.getWritableDatabase();
        int ret = db.delete(DogsContract.TABLE, where, selectionArgs);

        if (ret > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        Log.d(TAG, "deleted records: " + ret);
        return ret;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        String where;

        switch (sURIMatcher.match(uri)) {
            case DogsContract.STATUS_ITEM:
                long id = ContentUris.parseId(uri);
                where = DogsContract.Dogs.ID + "=" + id +
                        (TextUtils.isEmpty(selection) ? "" : " and ( "
                                + selection + " ) ");
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = dbHelperDogs.getWritableDatabase();
        int ret = db.update(DogsContract.TABLE, values, where, selectionArgs);

        if (ret > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        Log.d(TAG, "updated records: " + ret);
        return ret;
    }
}
