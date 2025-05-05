package com.tm.androidcopysdk.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Configuration;
import androidx.work.WorkManager;
import com.tm.androidcopysdk.database.DBHeadersTable;
import com.tm.androidcopysdk.events.PeriodicEventChecker;
import com.tm.androidcopysdk.utils.FlavorSettings;
import com.tm.logger.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/database/MessageContentProvider.class */
public class MessageContentProvider extends ContentProvider {
    public static final String PROVIDER_NAME = FlavorSettings.getInstance().getProviderName();
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME);
    public static final Uri CONTENT_URI_MESSAGES = Uri.parse("content://" + PROVIDER_NAME + "/messages");
    public static final Uri CONTENT_URI_EVENTS = Uri.parse("content://" + PROVIDER_NAME + "/events");
    public static final Uri CONTENT_URI_MEDIA = Uri.parse("content://" + PROVIDER_NAME + "/media");
    public static final Uri CONTENT_URI_HEADERS = Uri.parse("content://" + PROVIDER_NAME + "/headers");
    private static final UriMatcher sUriMatcher = new UriMatcher(-1);

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/database/MessageContentProvider$MessageDeliveryStatus.class */
    public enum MessageDeliveryStatus {
        Delivered,
        WaitingToBeDelivered,
        Failed,
        Sent,
        WaitingToBeDeliveredHistory,
        NotReadyToBeDelivered,
        WaitingToBeCompressed,
        Dummy,
        CallLogNotReadyToBeDelivered,
        EventDeliveredAsSms,
        EventDeliveredAsApi,
        WaitingToMultipleParticipants
    }

    static {
        sUriMatcher.addURI(PROVIDER_NAME, "messages", 1);
        sUriMatcher.addURI(PROVIDER_NAME, "messages/#", 2);
        sUriMatcher.addURI(PROVIDER_NAME, "messages/new", 3);
        sUriMatcher.addURI(PROVIDER_NAME, "messages/failed", 4);
        sUriMatcher.addURI(PROVIDER_NAME, "media/#", 5);
        sUriMatcher.addURI(PROVIDER_NAME, "media/", 6);
        sUriMatcher.addURI(PROVIDER_NAME, "media/#/#", 7);
        sUriMatcher.addURI(PROVIDER_NAME, "events", 9);
        sUriMatcher.addURI(PROVIDER_NAME, "media/#/*", 10);
        sUriMatcher.addURI(PROVIDER_NAME, DBHeadersTable.HeadersEntry.TABLE_NAME, 11);
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        Log.createInstance(getContext().getApplicationContext());
        Configuration myConfig = new Configuration.Builder().setMinimumLoggingLevel(4).build();
        WorkManager.initialize(getContext(), myConfig);
        SQLFactory.getInstance(getContext());
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        boolean eventQuery = false;
        boolean headersQuery = false;
        switch (sUriMatcher.match(uri)) {
            case 1:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = "_id ASC";
                    break;
                }
                break;
            case 2:
            case 5:
                if (selection != null) {
                    selection = selection + "_id = " + uri.getLastPathSegment();
                    break;
                } else {
                    selection = "_id = " + uri.getLastPathSegment();
                    break;
                }
            case 9:
                eventQuery = true;
                break;
            case PeriodicEventChecker.LOW_STORAGE_EVENT_CHECKER /* 11 */:
                headersQuery = true;
                break;
        }
        if (eventQuery) {
            cursor = SQLFactory.query(getContext(), "events", projection, selection, selectionArgs, null, null, sortOrder);
        } else if (headersQuery) {
            cursor = SQLFactory.query(getContext(), DBHeadersTable.HeadersEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        } else {
            cursor = SQLFactory.query(getContext(), "messages", projection, selection, selectionArgs, null, null, sortOrder);
        }
        return cursor;
    }

    @Override // android.content.ContentProvider
    @Nullable
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    public String getStreamTypes(@NonNull Uri uri) {
        return null;
    }

    @Override // android.content.ContentProvider
    @Nullable
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long id;
        int match = sUriMatcher.match(uri);
        String log = contentValues.toString();
        int end = log.indexOf("body");
        if (end > -1) {
            log = log.substring(0, end) + ".....";
        }
        Log.v("DB", "insert(), uri=" + uri + ", values=" + log);
        if (match == 9) {
            id = SQLFactory.insertWithOnConflict(getContext(), "events", null, contentValues, 4);
        } else if (match == 10) {
            id = SQLFactory.insertWithOnConflict(getContext(), DBHeadersTable.HeadersEntry.TABLE_NAME, null, contentValues, 4);
        } else {
            id = SQLFactory.insertWithOnConflict(getContext(), "messages", null, contentValues, 4);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override // android.content.ContentProvider
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int deleteRows;
        boolean eventDelete = false;
        boolean headersDelete = false;
        switch (sUriMatcher.match(uri)) {
            case 2:
                if (!TextUtils.isEmpty(s)) {
                    s = s + " AND _id = " + uri.getLastPathSegment();
                    break;
                } else {
                    s = "_id = " + uri.getLastPathSegment();
                    break;
                }
            case 9:
                eventDelete = true;
                break;
            case 10:
                headersDelete = true;
                break;
        }
        if (eventDelete) {
            deleteRows = SQLFactory.delete(getContext(), "events", s, strings);
        } else if (headersDelete) {
            deleteRows = SQLFactory.delete(getContext(), DBHeadersTable.HeadersEntry.TABLE_NAME, s, strings);
        } else {
            deleteRows = SQLFactory.delete(getContext(), "messages", s, strings);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return deleteRows;
    }

    @Override // android.content.ContentProvider
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        int result;
        boolean eventUpdate = false;
        boolean headersUpdate = false;
        switch (sUriMatcher.match(uri)) {
            case 2:
                if (!TextUtils.isEmpty(s)) {
                    s = s + " AND _id = " + uri.getLastPathSegment();
                    break;
                } else {
                    s = "_id = " + uri.getLastPathSegment();
                    break;
                }
            case 9:
                eventUpdate = true;
                break;
            case 10:
                headersUpdate = true;
                break;
        }
        if (eventUpdate) {
            result = SQLFactory.update(getContext(), "events", contentValues, s, strings);
        } else if (headersUpdate) {
            result = SQLFactory.update(getContext(), DBHeadersTable.HeadersEntry.TABLE_NAME, contentValues, s, strings);
        } else {
            result = SQLFactory.update(getContext(), "messages", contentValues, s, strings);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        Log.d("db", "number of rows updated: " + result + " with id: " + uri.getLastPathSegment() + " s value: " + s);
        return result;
    }

    @Override // android.content.ContentProvider
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        Log.d("openFile", "sUriMatcher.match(uri) uri:" + uri.toString() + " match=" + sUriMatcher.match(uri));
        if (sUriMatcher.match(uri) != 7 && sUriMatcher.match(uri) != 10) {
            throw new IllegalArgumentException("URI invalid. Use an id-based URI only.");
        }
        List<String> segments = uri.getPathSegments();
        String msdID = segments.get(segments.size() - 2);
        String filename = uri.getLastPathSegment();
        File file = new File(getContext().getFilesDir() + "/" + msdID, filename);
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            if (file.exists()) {
                Log.d("file creation", "succeeded");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, 805306368);
        return fileDescriptor;
    }
}
