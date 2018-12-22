package com.tutorial.athina.pethood;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelperDogs extends SQLiteOpenHelper {

    public static final String TAG = DbHelperDogs.class.getSimpleName();

    public DbHelperDogs(Context context) {
        super(context, DogsContract.DB_NAME, null, DogsContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        @SuppressLint("DefaultLocale") String sql = String.format(
                "create table %s (%s int primary key not null, %s text, %s text, %s text, %s text, %s text, %s text, %s text)",
                DogsContract.TABLE,
                DogsContract.Dogs.ID,
                DogsContract.Dogs.NAME,
                DogsContract.Dogs.OWNER,
                DogsContract.Dogs.BREED,
                DogsContract.Dogs.SIZE,
                DogsContract.Dogs.MATE_FLAG,
                DogsContract.Dogs.COLOR,
                DogsContract.Dogs.AGE);
        Log.d(TAG, " onCreate with SQL: " + sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists " + DogsContract.TABLE);
        onCreate(db);
    }

}
