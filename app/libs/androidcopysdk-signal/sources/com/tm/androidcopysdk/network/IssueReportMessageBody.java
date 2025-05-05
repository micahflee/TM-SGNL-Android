package com.tm.androidcopysdk.network;

import android.content.Context;
import com.tm.androidcopysdk.database.DBHeadersTable;
import com.tm.androidcopysdk.utils.TMCredentialsStore;
import com.tm.logger.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/IssueReportMessageBody.class */
public class IssueReportMessageBody extends RequestBody {
    private Context context;

    public IssueReportMessageBody(Context context) {
        this.context = context;
    }

    public MediaType contentType() {
        return null;
    }

    public void writeTo(BufferedSink sink) throws IOException {
        JsonWriter writer = new JsonWriter(sink);
        writer.beginArray();
        writer.beginObject();
        String storedUsername = TMCredentialsStore.getInstance(this.context).userName(this.context);
        String storedPassword = TMCredentialsStore.getInstance(this.context).password(this.context);
        writer.name("class").value("telemessage.web.services.AuthenticationDetails");
        writer.name("password").value(storedPassword);
        writer.name("username").value(storedUsername);
        writer.endObject();
        writer.beginObject();
        writer.name("class").value("telemessage.web.services.Message");
        writer.name("textMessage").value("archiver log & db files from: " + storedUsername);
        writer.name("subject").value("archiver Log & db message " + storedUsername);
        writer.name("recipients");
        writer.beginArray();
        writer.beginObject();
        writer.name(DBHeadersTable.HeadersEntry.COLUMN_NAME_VALUE).value("archiver_rep");
        writer.name("addrestype").value("0");
        writer.name("class").value("telemessage.web.services.Recipient");
        writer.endObject();
        writer.endArray();
        writer.name("fileMessages");
        writer.beginArray();
        Map<String, File> files = Log.getAttachmentsLog((String) null, storedUsername);
        for (int i = 0; i < files.size(); i++) {
            writer.beginObject();
            writer.name("fileName").value(NetworkConstance.SEND_LOGS_REQUEST_FILE_NAME);
            writer.name("mimeType").value(NetworkConstance.SEND_LOGS_REQUEST_FILE_TYPE);
            InputStream input = null;
            try {
                input = new FileInputStream(((File) files.values().toArray()[i]).getPath().toString());
                writer.name(DBHeadersTable.HeadersEntry.COLUMN_NAME_VALUE);
                writer.value(input);
                if (input != null) {
                    input.close();
                }
                writer.name("class").value("telemessage.web.services.FileMessage");
                writer.endObject();
            } catch (Throwable th) {
                if (input != null) {
                    input.close();
                }
                throw th;
            }
        }
        writer.endArray();
        writer.name("properties");
        writer.beginArray();
        writer.beginObject();
        writer.name("name").value("SendToInboxOnly");
        writer.name(DBHeadersTable.HeadersEntry.COLUMN_NAME_VALUE).value("");
        writer.name("class").value("telemessage.web.services.Property");
        writer.endObject();
        writer.beginObject();
        writer.name("class").value("telemessage.web.services.Property");
        writer.name("name").value("DoNotArchive");
        writer.name(DBHeadersTable.HeadersEntry.COLUMN_NAME_VALUE).value("true");
        writer.endObject();
        writer.endArray();
        writer.endObject();
        writer.endArray();
        writer.flush();
        writer.close();
    }
}
