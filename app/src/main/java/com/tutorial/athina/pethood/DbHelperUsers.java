package com.tutorial.athina.pethood;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelperUsers extends SQLiteOpenHelper {

    public static final String TAG = DbHelperUsers.class.getSimpleName();

    public DbHelperUsers(Context context) {
        super(context, UsersContract.DB_NAME, null, UsersContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        @SuppressLint("DefaultLocale") String sql = String.format(
                "create table %s (%s int primary key  not null, %s text, %s text, %s text)",
                UsersContract.TABLE, UsersContract.Users.ID,
                UsersContract.Users.NAME,
                UsersContract.Users.PHONE,
                UsersContract.Users.AGE);
        Log.d(TAG, " onCreate with SQL: " + sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists " + UsersContract.TABLE);
        onCreate(db);
    }
}
