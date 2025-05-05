package com.tm.androidcopysdk.database;

import android.provider.BaseColumns;
import net.zetetic.database.sqlcipher.SQLiteDatabase;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/database/DBEventsContractSignal.class */
public final class DBEventsContractSignal {
    public static final String SQL_CREATE_ENTRIES = "create table if not exists events (_id INTEGER PRIMARY KEY AUTOINCREMENT , description TEXT ,status INTEGER ,date INTEGER ,type INTEGER ,orig_id TEXT UNIQUE );";
    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS events";

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/database/DBEventsContractSignal$EventEntry.class */
    public static class EventEntry implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_ORIG_ID = "orig_id";
    }

    private DBEventsContractSignal() {
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL("create table if not exists events (_id INTEGER PRIMARY KEY AUTOINCREMENT , description TEXT ,status INTEGER ,date INTEGER ,type INTEGER ,orig_id TEXT UNIQUE );");
    }
}
