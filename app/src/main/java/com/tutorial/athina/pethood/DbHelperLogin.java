package com.tutorial.athina.pethood;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelperLogin extends SQLiteOpenHelper {

    public static final String TAG = DbHelperLogin.class.getSimpleName();

    public DbHelperLogin(Context context) {
        super(context, LoginContract.DB_NAME, null, LoginContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format(
                "create table %s (%s int primary key, %s text, %s text )",
                LoginContract.TABLE, LoginContract.Login.ID,
                LoginContract.Login.EMAIL,
                LoginContract.Login.PASSWORD);
        Log.d(TAG, " onCreate with SQL: " + sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists " + LoginContract.TABLE);
        onCreate(db);
    }
}
