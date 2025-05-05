package com.tm.androidcopysdk.database;

import android.content.Context;
import com.tm.androidcopysdk.R;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.androidcopysdk.utils.TMCredentialsStore;
import com.tm.logger.Log;
import java.io.File;
import net.zetetic.database.sqlcipher.SQLiteDatabase;
import net.zetetic.database.sqlcipher.SQLiteOpenHelper;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/database/DBMessagesHelper.class */
public class DBMessagesHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBMessagesHelper";
    public static final int DATABASE_VERSION = 10;
    public static final String DATABASE_NAME = "message.db";
    public static final String DATABASE_NAME_ENC = "message_enc.db";
    private Context context;
    private static String _informationData = "";
    private static DBMessagesHelper mInstance = null;

    private DBMessagesHelper(Context context) {
        super(context, PrefManager.getBooleanPref(context, R.string.encrypt, false) ? "message_enc.db" : "message.db", (SQLiteDatabase.CursorFactory) null, 10);
        this.context = context;
    }

    public static synchronized DBMessagesHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBMessagesHelper(context);
        }
        return mInstance;
    }

    /* JADX WARN: Removed duplicated region for block: B:103:0x0206 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:104:0x0207  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x00b2 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:26:0x00b3  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x00ea A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00eb  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x0122 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:52:0x0123  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x015b A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:65:0x015c  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x0194 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:78:0x0195  */
    /* JADX WARN: Removed duplicated region for block: B:90:0x01cd A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:91:0x01ce  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onUpgrade(net.zetetic.database.sqlcipher.SQLiteDatabase r5, int r6, int r7) {
        /*
            Method dump skipped, instructions count: 570
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tm.androidcopysdk.database.DBMessagesHelper.onUpgrade(net.zetetic.database.sqlcipher.SQLiteDatabase, int, int):void");
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
    public SQLiteDatabase m38getWritableDatabase() {
        return super.getWritableDatabase();
    }

    /* renamed from: getReadableDatabase */
    public SQLiteDatabase m37getReadableDatabase() {
        return super.getReadableDatabase();
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    public synchronized void changeDataBasePassword() {
        Log.d(TAG, "changeDataBasePassword false false false!!!!!!!!!!!!!!!!!!!!!");
        Log.d(TAG, "changeDataBasePassword false false false!!!!!!!!!!!!!!!!!!!!!");
        Log.d(TAG, "changeDataBasePassword false false false!!!!!!!!!!!!!!!!!!!!!");
        Log.d(TAG, "changeDataBasePassword false false false!!!!!!!!!!!!!!!!!!!!!");
        Log.d(TAG, "changeDataBasePassword false false false!!!!!!!!!!!!!!!!!!!!!");
        Log.d(TAG, "changeDataBasePassword false false false!!!!!!!!!!!!!!!!!!!!!");
        Log.d(TAG, "changeDataBasePassword false false false!!!!!!!!!!!!!!!!!!!!!");
        Log.d(TAG, "changeDataBasePassword false false false!!!!!!!!!!!!!!!!!!!!!");
        Log.d(TAG, "changeDataBasePassword false false false!!!!!!!!!!!!!!!!!!!!!");
        Log.d(TAG, "changeDataBasePassword false false false!!!!!!!!!!!!!!!!!!!!!");
        Log.d(TAG, "changeDataBasePassword false false false!!!!!!!!!!!!!!!!!!!!!");
        Log.d(TAG, "changeDataBasePassword false false false!!!!!!!!!!!!!!!!!!!!!");
        Log.d(TAG, "changeDataBasePassword false false false!!!!!!!!!!!!!!!!!!!!!");
        Log.d(TAG, "changeDataBasePassword false false false!!!!!!!!!!!!!!!!!!!!!");
        Log.d(TAG, "changeDataBasePassword false false false!!!!!!!!!!!!!!!!!!!!!");
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
        Log.d(TAG, "replaceDataBasePassword false false false!!!!!!!!!!!!!!!!!!!!!");
        Log.d(TAG, "replaceDataBasePassword false false false!!!!!!!!!!!!!!!!!!!!!");
        Log.d(TAG, "replaceDataBasePassword false false false!!!!!!!!!!!!!!!!!!!!!");
        Log.d(TAG, "replaceDataBasePassword false false false!!!!!!!!!!!!!!!!!!!!!");
        Log.d(TAG, "replaceDataBasePassword false false false!!!!!!!!!!!!!!!!!!!!!");
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
