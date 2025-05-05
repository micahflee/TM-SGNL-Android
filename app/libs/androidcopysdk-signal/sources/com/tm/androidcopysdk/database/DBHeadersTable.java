package com.tm.androidcopysdk.database;

import android.provider.BaseColumns;
import net.zetetic.database.sqlcipher.SQLiteDatabase;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/database/DBHeadersTable.class */
public final class DBHeadersTable {
    private static String SQL_CREATE_ENTRIES = "create table if not exists headers (_id INTEGER PRIMARY KEY , message_id TEXT ,key TEXT ,value TEXT ,status TEXT );";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS headers";

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/database/DBHeadersTable$HeadersEntry.class */
    public static class HeadersEntry implements BaseColumns {
        public static final String TABLE_NAME = "headers";
        public static final String COLUMN_NAME_MSG_ID = "message_id";
        public static final String COLUMN_NAME_KEY = "key";
        public static final String COLUMN_NAME_VALUE = "value";
        public static final String COLUMN_NAME_STATUS = "status";
    }

    private DBHeadersTable() {
    }

    public static void deleteEntries(SQLiteDatabase database) {
        database.execSQL(SQL_DELETE_ENTRIES);
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(SQL_CREATE_ENTRIES);
    }
}
