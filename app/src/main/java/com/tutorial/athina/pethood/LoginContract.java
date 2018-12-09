package com.tutorial.athina.pethood;

import android.net.Uri;
import android.provider.BaseColumns;

public class LoginContract {

    public static final String DB_NAME = "login.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "login";

    public static final String AUTHORITY = "com.tutorial.athina.pethood.LoginProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TABLE);
    public static final int STATUS_ITEM = 1;
    public static final int STATUS_DIR = 2;
    public static final String STATUS_TYPE_ITEM =
            "vnd.android.cursor.item/vnd.com.tutorial.athina.pethood.provider.login";
    public static final String STATUS_TYPE_DIR =
            "vnd.android.cursor.dir/vnd.com.tutorial.athina.pethood.provider.login";

    public static final String DEFAULT_SORT = Login.EMAIL + " DESC";

    public class Login {
        public static final String ID = BaseColumns._ID;
        public static final String EMAIL = "email";
        public static final String PASSWORD = "pass";

    }
}
