package com.tutorial.athina.pethood;

import android.net.Uri;
import android.provider.BaseColumns;

public class StatusContract {

    public static final String DB_NAME = "timeline.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "status";

    public static final String AUTHORITY = "com.tutorial.athina.pethood.StatusProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TABLE);
    public static final int STATUS_ITEM = 1;
    public static final int STATUS_DIR = 2;
    public static final String STATUS_TYPE_ITEM =
            "vnd.android.cursor.item/vnd.com.tutorial.athina.pethood.provider.status";
    public static final String STATUS_TYPE_DIR =
            "vnd.android.cursor.dir/vnd.com.tutorial.athina.pethood.provider.status";

    public static final String DEFAULT_SORT = Column.ID + " DESC";

    public class Column {
        public static final String ID = BaseColumns._ID;
        public static final String USER = "user";
        public static final String MESSAGE = "message";
        ;

    }
}