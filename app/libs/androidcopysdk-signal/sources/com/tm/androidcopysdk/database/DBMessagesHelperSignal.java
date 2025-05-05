package com.tm.androidcopysdk.database;

import android.content.Context;
import android.text.TextUtils;
import com.tm.androidcopysdk.R;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.androidcopysdk.utils.TMCredentialsStore;
import com.tm.logger.Log;
import java.io.File;
import net.zetetic.database.DatabaseErrorHandler;
import net.zetetic.database.sqlcipher.SQLiteDatabase;
import net.zetetic.database.sqlcipher.SQLiteDatabaseHook;
import net.zetetic.database.sqlcipher.SQLiteOpenHelper;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/database/DBMessagesHelperSignal.class */
public class DBMessagesHelperSignal extends SQLiteOpenHelper {
    private static final String TAG = "DBMessagesHelper";
    public static final int DATABASE_VERSION = 13;
    public static final String DATABASE_NAME = "message.db";
    public static final String DATABASE_NAME_ENC = "message_enc.db";
    private Context context;
    private static String _informationData = "";
    private static DBMessagesHelperSignal mInstance = null;

    private DBMessagesHelperSignal(Context context) {
        super(context, "message_enc.db", TMCredentialsStore.getInstance(context).password(context), (SQLiteDatabase.CursorFactory) null, 13, 0, (DatabaseErrorHandler) null, (SQLiteDatabaseHook) null, true);
        this.context = context;
    }

    public static synchronized DBMessagesHelperSignal getInstance(Context context) {
        if (mInstance == null) {
            SqlCipherLibraryLoader.load();
            mInstance = new DBMessagesHelperSignal(context);
        }
        return mInstance;
    }

    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        DBMessagesContractSignal.onCreate(db);
        DBEventsContractSignal.onCreate(db);
        DBHeadersTable.onCreate(db);
    }

    /* JADX WARN: Removed duplicated region for block: B:103:0x0212 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:104:0x0213  */
    /* JADX WARN: Removed duplicated region for block: B:116:0x024b A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:117:0x024c  */
    /* JADX WARN: Removed duplicated region for block: B:129:0x0283 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:130:0x0284  */
    /* JADX WARN: Removed duplicated region for block: B:142:0x02bc A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:143:0x02bd  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x00be A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:26:0x00bf  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x00f6 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00f7  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x012e A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:52:0x012f  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x0167 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:65:0x0168  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x01a0 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:78:0x01a1  */
    /* JADX WARN: Removed duplicated region for block: B:90:0x01d9 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:91:0x01da  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onUpgrade(net.zetetic.database.sqlcipher.SQLiteDatabase r5, int r6, int r7) {
        /*
            Method dump skipped, instructions count: 752
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tm.androidcopysdk.database.DBMessagesHelperSignal.onUpgrade(net.zetetic.database.sqlcipher.SQLiteDatabase, int, int):void");
    }

    private void upgradeDatabaseToVersion11(SQLiteDatabase db) {
        Log.d(DBMessagesContract.class.getName(), "upgradeDatabaseToVersion11");
        try {
            db.execSQL("ALTER TABLE messages ADD COLUMN mode integer default 0;");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void upgradeDatabaseToVersion12(SQLiteDatabase db) {
        Log.d(DBMessagesContract.class.getName(), "upgradeDatabaseToVersion12");
        try {
            db.execSQL("ALTER TABLE messages ADD COLUMN keep_alive_sent integer default 0;");
            db.execSQL("ALTER TABLE messages ADD COLUMN messageType TEXT;");
            db.execSQL("ALTER TABLE messages ADD COLUMN sentToServerTime TEXT;");
            db.execSQL("ALTER TABLE messages ADD COLUMN serverResponse integer default 0;");
            db.execSQL("ALTER TABLE messages ADD COLUMN pendingReason TEXT;");
            db.execSQL("ALTER TABLE messages ADD COLUMN rearchivingFlag integer default 0;");
            db.execSQL("ALTER TABLE messages ADD COLUMN requestRearchiveTime TEXT;");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void upgradeDatabaseToVersion2(SQLiteDatabase db) {
        Log.d(DBMessagesContract.class.getName(), "upgradeDatabaseToVersion2");
        try {
            db.execSQL("ALTER TABLE messages ADD COLUMN comp_retries integer default 0;");
            db.execSQL("ALTER TABLE messages ADD COLUMN native_message_id TEXT;");
            db.execSQL("ALTER TABLE messages ADD COLUMN _data_native TEXT;");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void upgradeDatabaseToVersion3(SQLiteDatabase db) {
        Log.d(DBMessagesContract.class.getName(), "upgradeDatabaseToVersion3");
        try {
            db.execSQL("ALTER TABLE messages ADD COLUMN extra_data integer default 0;");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void upgradeDatabaseToVersion4(SQLiteDatabase db) {
        Log.d(DBMessagesContract.class.getName(), "upgradeDatabaseToVersion4");
        try {
            db.execSQL("ALTER TABLE messages ADD COLUMN multiple_index integer default 0;");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void upgradeDatabaseToVersion5(SQLiteDatabase db) {
        Log.d(DBMessagesContract.class.getName(), "upgradeDatabaseToVersion5");
        try {
            db.execSQL("ALTER TABLE messages ADD COLUMN chat_mode integer default 0;");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void upgradeDatabaseToVersion6(SQLiteDatabase db) {
        Log.d(DBMessagesContract.class.getName(), "upgradeDatabaseToVersion6");
        try {
            db.execSQL("ALTER TABLE messages ADD COLUMN _PATH  TEXT;");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void upgradeDatabaseToVersion7(SQLiteDatabase db) {
        Log.d(DBMessagesContract.class.getName(), "upgradeDatabaseToVersion7");
        try {
            db.execSQL("ALTER TABLE messages ADD COLUMN from_name  TEXT;");
            db.execSQL("ALTER TABLE messages ADD COLUMN from_value  TEXT;");
            db.execSQL("ALTER TABLE messages ADD COLUMN to_name  TEXT;");
            db.execSQL("ALTER TABLE messages ADD COLUMN to_value  TEXT;");
            db.execSQL("ALTER TABLE messages ADD COLUMN chat_name  TEXT;");
            db.execSQL("ALTER TABLE messages ADD COLUMN chat_id  TEXT;");
            db.execSQL("ALTER TABLE messages ADD COLUMN thread_id  TEXT;");
            db.execSQL("ALTER TABLE messages ADD COLUMN thread_name  TEXT;");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void upgradeDatabaseToVersion8(SQLiteDatabase db) {
        Log.d(DBMessagesContract.class.getName(), "upgradeDatabaseToVersion8");
        try {
            db.execSQL("ALTER TABLE messages ADD COLUMN from_name TEXT;");
            db.execSQL("ALTER TABLE messages ADD COLUMN from_value  TEXT;");
            db.execSQL("ALTER TABLE messages ADD COLUMN to_name  TEXT;");
            db.execSQL("ALTER TABLE messages ADD COLUMN to_value  TEXT;");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void upgradeDatabaseToVersion9(SQLiteDatabase db) {
        Log.d(DBMessagesContract.class.getName(), "upgradeDatabaseToVersion8");
        try {
            db.execSQL("ALTER TABLE messages ADD COLUMN updated_files TEXT;");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* renamed from: getWritableDatabase */
    public SQLiteDatabase m41getWritableDatabase() {
        return super.getWritableDatabase();
    }

    /* renamed from: getReadableDatabase */
    public SQLiteDatabase m40getReadableDatabase() {
        return super.getReadableDatabase();
    }

    public synchronized void changeDataBasePassword() {
        Log.d(TAG, "changeDataBasePassword old password = ");
        if (TextUtils.isEmpty(_informationData)) {
            close();
            File encryptedDatabase = this.context.getDatabasePath("message.db");
            execute(SQLiteDatabase.openOrCreateDatabase(encryptedDatabase, _informationData, (SQLiteDatabase.CursorFactory) null, (DatabaseErrorHandler) null));
            mInstance = new DBMessagesHelperSignal(this.context);
            DBMessagesHelperSignal dBMessagesHelperSignal = mInstance;
            _informationData = TMCredentialsStore.getInstance(this.context).password(this.context);
            mInstance.m41getWritableDatabase();
        } else {
            File encryptedDatabase2 = this.context.getDatabasePath("message_enc.db");
            SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(encryptedDatabase2, _informationData, (SQLiteDatabase.CursorFactory) null, (DatabaseErrorHandler) null);
            database.rawExecSQL(String.format("PRAGMA rekey = '%s'", TMCredentialsStore.getInstance(this.context).password(this.context)), new Object[0]);
            database.close();
            mInstance = new DBMessagesHelperSignal(this.context);
            DBMessagesHelperSignal dBMessagesHelperSignal2 = mInstance;
            _informationData = TMCredentialsStore.getInstance(this.context).password(this.context);
        }
        Log.d(TAG, "end changeDataBasePassword <><><><><><><><><><><><><><><>");
    }

    public String getSQLinfo(String info) {
        _informationData = info;
        if (_informationData == null) {
            _informationData = "";
        }
        return TAG;
    }

    public void replaceDataBasePassword() {
        close();
        File encryptedDatabase = this.context.getDatabasePath("message.db");
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(encryptedDatabase, _informationData, (SQLiteDatabase.CursorFactory) null, (DatabaseErrorHandler) null);
        if (TextUtils.isEmpty(_informationData)) {
            database.rawExecSQL(String.format("PRAGMA key = '%s'", TMCredentialsStore.getInstance(this.context).password(this.context)), new Object[0]);
        } else {
            database.rawExecSQL(String.format("PRAGMA rekey = '%s'", TMCredentialsStore.getInstance(this.context).password(this.context)), new Object[0]);
        }
        database.close();
        mInstance = new DBMessagesHelperSignal(this.context);
        DBMessagesHelperSignal dBMessagesHelperSignal = mInstance;
        _informationData = TMCredentialsStore.getInstance(this.context).password(this.context);
    }

    public boolean execute(SQLiteDatabase database) {
        File dataBaseFile = this.context.getDatabasePath("encrypted.db");
        dataBaseFile.mkdirs();
        dataBaseFile.delete();
        try {
            database.rawExecSQL(String.format("ATTACH DATABASE '%s' AS encrypted KEY '%s'", dataBaseFile.getAbsolutePath(), TMCredentialsStore.getInstance(this.context).password(this.context)), new Object[0]);
            database.rawExecSQL("select sqlcipher_export('encrypted')", new Object[0]);
            database.rawExecSQL("DETACH DATABASE encrypted", new Object[0]);
            database.close();
            File encryptedDatabase = this.context.getDatabasePath("message_enc.db");
            encryptedDatabase.mkdirs();
            encryptedDatabase.delete();
            dataBaseFile.renameTo(encryptedDatabase);
            PrefManager.setBooleanPref(this.context, R.string.encrypt, true);
            return true;
        } catch (Throwable th) {
            File encryptedDatabase2 = this.context.getDatabasePath("message_enc.db");
            encryptedDatabase2.mkdirs();
            encryptedDatabase2.delete();
            dataBaseFile.renameTo(encryptedDatabase2);
            PrefManager.setBooleanPref(this.context, R.string.encrypt, true);
            return true;
        }
    }
}
