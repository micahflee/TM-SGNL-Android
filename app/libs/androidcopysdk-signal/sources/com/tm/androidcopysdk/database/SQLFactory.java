package com.tm.androidcopysdk.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.tm.androidcopysdk.utils.FlavorSettings;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/database/SQLFactory.class */
public class SQLFactory {
    public static Object getInstance(Context context) {
        if (FlavorSettings.getInstance().supportSQLCipher()) {
            return DBMessagesHelper.getInstance(context);
        }
        return DBMessagesHelperSignal.getInstance(context);
    }

    public static String getMessageTable() {
        if (FlavorSettings.getInstance().supportSQLCipher()) {
            return "messages";
        }
        return "messages";
    }

    public static String getSQLinfo(Context context, String password) {
        if (FlavorSettings.getInstance().supportSQLCipher()) {
            return DBMessagesHelper.getInstance(context).getSQLinfo(password);
        }
        return DBMessagesHelperSignal.getInstance(context).getSQLinfo(password);
    }

    public static void changeDataBasePassword(Context context) {
        if (FlavorSettings.getInstance().supportSQLCipher()) {
            DBMessagesHelper.getInstance(context).changeDataBasePassword();
        } else {
            DBMessagesHelperSignal.getInstance(context).changeDataBasePassword();
        }
    }

    public static Cursor query(Context context, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        if (FlavorSettings.getInstance().supportSQLCipher()) {
            return DBMessagesHelper.getInstance(context).m37getReadableDatabase().query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        }
        return DBMessagesHelperSignal.getInstance(context).m40getReadableDatabase().query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public static long insertWithOnConflict(Context context, String table, String nullColumnHack, ContentValues initialValues, int conflictAlgorithm) {
        if (FlavorSettings.getInstance().supportSQLCipher()) {
            return DBMessagesHelper.getInstance(context).m38getWritableDatabase().insertWithOnConflict(table, nullColumnHack, initialValues, conflictAlgorithm);
        }
        return DBMessagesHelperSignal.getInstance(context).m41getWritableDatabase().insertWithOnConflict(table, nullColumnHack, initialValues, conflictAlgorithm);
    }

    public static int delete(Context context, String table, String whereClause, String[] whereArgs) {
        if (FlavorSettings.getInstance().supportSQLCipher()) {
            return DBMessagesHelper.getInstance(context).m38getWritableDatabase().delete(table, whereClause, whereArgs);
        }
        return DBMessagesHelperSignal.getInstance(context).m41getWritableDatabase().delete(table, whereClause, whereArgs);
    }

    public static int update(Context context, String table, ContentValues values, String whereClause, String[] whereArgs) {
        if (FlavorSettings.getInstance().supportSQLCipher()) {
            return DBMessagesHelper.getInstance(context).m38getWritableDatabase().update(table, values, whereClause, whereArgs);
        }
        return DBMessagesHelperSignal.getInstance(context).m41getWritableDatabase().update(table, values, whereClause, whereArgs);
    }
}
