package com.tutorial.athina.pethood;

import android.net.Uri;
import android.provider.BaseColumns;

public class DogsContract {

    public static final String DB_NAME = "dogs.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "dogs";

    public static final String AUTHORITY = "com.tutorial.athina.pethood.DogsProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TABLE);
    public static final int STATUS_ITEM = 1;
    public static final int STATUS_DIR = 2;
    public static final String STATUS_TYPE_ITEM =
            "vnd.android.cursor.item/vnd.com.tutorial.athina.pethood.provider.dogs";
    public static final String STATUS_TYPE_DIR =
            "vnd.android.cursor.dir/vnd.com.tutorial.athina.pethood.provider.dogs";

    public static final String DEFAULT_SORT = Dogs.NAME + " DESC";

    public class Dogs {
        public static final String ID = BaseColumns._ID;
        public static final String NAME = "name";
        public static final String OWNER = "owner";
        public static final String BREED = "breed";
        public static final String SIZE = "size";
        public static final String MATE_FLAG = "mate_flag";
        public static final String COLOR = "color";
        public static final String AGE = "age";

    }
}
