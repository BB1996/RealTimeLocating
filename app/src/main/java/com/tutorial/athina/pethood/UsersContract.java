package com.tutorial.athina.pethood;
import android.net.Uri;
import android.provider.BaseColumns;

public class UsersContract {

    public static final String DB_NAME = "users.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "users";

    public static final String AUTHORITY = "com.tutorial.athina.pethood.UsersProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TABLE);
    public static final int STATUS_ITEM = 1;
    public static final int STATUS_DIR = 2;
    public static final String STATUS_TYPE_ITEM =
            "vnd.android.cursor.item/vnd.com.tutorial.athina.pethood.provider.users";
    public static final String STATUS_TYPE_DIR =
            "vnd.android.cursor.dir/vnd.com.tutorial.athina.pethood.provider.users";

    public static final String DEFAULT_SORT = Users.NAME + " DESC";

    public class Users {
        public static final String ID = BaseColumns._ID;
        public static final String NAME = "name";
        public static final String PHONE = "phone";
        public static final String AGE = "age";

    }
}
