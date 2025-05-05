package com.tm.androidcopysdk.database;

import android.provider.BaseColumns;
import net.zetetic.database.sqlcipher.SQLiteDatabase;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/database/DBMessagesContractSignal.class */
public final class DBMessagesContractSignal {
    private static String SQL_CREATE_ENTRIES = "create table if not exists messages (_id INTEGER PRIMARY KEY , date TEXT ,address_from TEXT ,address_to TEXT ,orig_id TEXT UNIQUE,keep_alive_sent integer default 0,messageType TEXT ,sentToServerTime TEXT ,serverResponse integer default 0,pendingReason TEXT ,rearchivingFlag integer default 0,requestRearchiveTime TEXT ,subject TEXT ,_data TEXT ,updated_files TEXT ,_PATH TEXT ,mime TEXT ,status TEXT ,type TEXT ,direction TEXT ,call_type TEXT ,duration TEXT ,body TEXT ,comp_retries integer default 0,chat_mode integer default 0,_ATTCHMENT_FILE_UPDATE integer default 0,extra_data integer default 0,multiple_index integer default 0,native_message_id TEXT,from_name TEXT,from_value TEXT,to_name TEXT,to_value TEXT,chat_name TEXT,chat_id TEXT,thread_id TEXT,thread_name TEXT,mode integer default 0,_data_native TEXT  );";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS messages";

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/database/DBMessagesContractSignal$MessageEntry.class */
    public static class MessageEntry implements BaseColumns {
        public static final String TABLE_NAME = "messages";
        public static final String COLUMN_NAME_FROM = "address_from";
        public static final String COLUMN_NAME_TO = "address_to";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TITLE = "subject";
        public static final String COLUMN_NAME_BODY = "body";
        public static final String COLUMN_NAME_DURATION = "duration";
        public static final String COLUMN_NAME_CALL_TYPE = "call_type";
        public static final String COLUMN_NAME_CALL_MODE = "mode";
        public static final String COLUMN_NAME_ORIG_ID = "orig_id";
        public static final String COLUMN_NAME_KEEP_ALIVE_ALREADY_SENT = "keep_alive_sent";
        public static final String COLUMN_NAME_MESSAGE_TYPE = "messageType";
        public static final String COLUMN_NAME_SENT_TO_SERVER_TIME = "sentToServerTime";
        public static final String COLUMN_NAME_SERVER_RESPONSE_CODE = "serverResponse";
        public static final String COLUMN_NAME_PENDING_REASON = "pendingReason";
        public static final String COLUMN_NAME_REARCHIVING_FLAG = "rearchivingFlag";
        public static final String COLUMN_NAME_REQUEST_REACHRIVE_TIME = "requestRearchiveTime";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_DIRECTION = "direction";
        public static final String COLUMN_NAME_ATTACHMENT = "_data";
        public static final String COLUMN_NAME_UPDATED_FILES_LIST = "updated_files";
        public static final String COLUMN_PATH = "_PATH";
        public static final String COLUMN_ATTACHMENT_COUNT_UPDATED = "_ATTCHMENT_FILE_UPDATE";
        public static final String COLUMN_NAME_MIME_TYPE = "mime";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_COMP_RETRIES = "comp_retries";
        public static final String COLUMN_NAME_NATIVE_MESSAGE_ID = "native_message_id";
        public static final String DATA_NATIVE = "_data_native";
        public static final String EXTRA_DATA = "extra_data";
        public static final String COLUMN_NAME_CAHT_MODE = "chat_mode";
        public static final String COLUMN_NAME_CAHT_NAME = "chat_name";
        public static final String COLUMN_NAME_CAHT_ID = "chat_id";
        public static final String COLUMN_NAME_THREAD_ID = "thread_id";
        public static final String COLUMN_NAME_THREAD_NAME = "thread_name";
        public static final String COLUMN_MULTIPLE_INDEX = "multiple_index";
        public static final String COLUMN_FROM_NAME = "from_name";
        public static final String COLUMN_FROM_VALUE = "from_value";
        public static final String COLUMN_TO_NAME = "to_name";
        public static final String COLUMN_TO_VALUE = "to_value";
    }

    private DBMessagesContractSignal() {
    }

    public static void deleteEntries(SQLiteDatabase database) {
        database.execSQL(SQL_DELETE_ENTRIES);
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(SQL_CREATE_ENTRIES);
    }
}
