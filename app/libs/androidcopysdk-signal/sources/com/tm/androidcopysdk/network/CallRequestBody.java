package com.tm.androidcopysdk.network;

import android.content.Context;
import android.text.TextUtils;
import com.tm.androidcopysdk.BackupService;
import com.tm.androidcopysdk.database.DBHeadersTable;
import com.tm.androidcopysdk.database.UriUtils;
import com.tm.logger.Log;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/CallRequestBody.class */
public class CallRequestBody extends RequestBody {
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json; charset=utf-8");
    CallLogMessageRecorder data;
    Context context;

    public CallRequestBody(Context context, CallLogMessageRecorder data) {
        this.data = data;
        this.context = context;
    }

    public MediaType contentType() {
        return MEDIA_TYPE_MARKDOWN;
    }

    public void writeTo(BufferedSink sink) throws IOException {
        EnrichedContact[] toEnriched;
        JsonWriter writer = new JsonWriter(sink);
        writer.beginObject();
        writer.name(CallLogMessageRecorder.subscriberId_key).value(this.data.getSubscriberId());
        if (!TextUtils.isEmpty(this.data.getTextContent())) {
            writer.name(CallLogMessageRecorder.textContent_key).value(this.data.getTextContent());
            writer.name(CallLogMessageRecorder.textSize_key).value(this.data.getTextContent().length());
        } else {
            writer.name(CallLogMessageRecorder.textContent_key).value(this.data.getSubject());
            writer.name(CallLogMessageRecorder.textSize_key).value(this.data.getSubject().length());
        }
        writer.name(CallLogMessageRecorder.msisdn_key).value(this.data.getMsisdn());
        writer.name(CallLogMessageRecorder.messageId_key).value(this.data.getMessageId());
        writer.name(CallLogMessageRecorder.messageContext_key).value(this.data.getMessageContext());
        writer.name(CallLogMessageRecorder.messageTime_key).value(NetworkManager.formatter().format(this.data.getMessageTime()));
        writer.name(CallLogMessageRecorder.from_key).value(this.data.getFrom());
        writer.name(CallLogMessageRecorder.to_key);
        writer.beginArray();
        for (int i = 0; i < this.data.getTo().length; i++) {
            writer.value(this.data.getTo()[i]);
        }
        writer.endArray();
        writer.name(CallLogMessageRecorder.groupMessage_key).value(this.data.getGroupMessage());
        writer.name("subject").value(this.data.getSubject());
        writer.name("direction").value(this.data.getDirection());
        if (this.data.attachment != null && this.data.attachment.size() > 0) {
            writer.name(CallLogMessageRecorder.attachment_key);
            writer.beginArray();
            for (int i2 = 0; i2 < this.data.attachment.size(); i2++) {
                writer.beginObject();
                writer.name("name").value(this.data.attachment.get(i2).getName());
                writer.name(CallLogMessageRecorder.contentType_key).value(this.data.attachment.get(i2).getContentType());
                writer.name(BackupService.SCHEME);
                InputStream input = this.context.getContentResolver().openInputStream(this.data.attachment.get(i2).getUri());
                int total = writer.value(UriUtils.getDecipheredInStream(this.context, input));
                writer.name("attachSize").value(total);
                writer.endObject();
            }
            writer.endArray();
        }
        writer.name(CallLogMessageRecorder.callinfo_key);
        writer.beginObject();
        writer.name(CallLogMessageRecorder.callinfo_call_id_key).value(this.data.callInfo.getCallID());
        writer.name(CallLogMessageRecorder.callinfo_recipient_num_key).value(this.data.callInfo.getRecipientNum());
        writer.name(CallLogMessageRecorder.callinfo_call_status_key).value(this.data.callInfo.getCallStatus());
        writer.name(CallLogMessageRecorder.callinfo_start_time_key).value(NetworkManager.formatter().format(this.data.callInfo.getStartTime()));
        writer.name(CallLogMessageRecorder.callinfo_end_time_key).value(NetworkManager.formatter().format(this.data.callInfo.getEndTime()));
        writer.endObject();
        writer.name("fromEnriched");
        writer.beginObject();
        if (!TextUtils.isEmpty(this.data.getFromEnriched().getFullName().getFirstName())) {
            writer.name("fullName");
            writer.beginObject();
            writer.name("firstName").value(this.data.getFromEnriched().fullName.firstName);
            writer.name("lastName").value(this.data.getFromEnriched().fullName.lastName);
            writer.endObject();
        }
        writer.name(DBHeadersTable.HeadersEntry.COLUMN_NAME_VALUE).value(this.data.getFromEnriched().value);
        writer.endObject();
        writer.name("toEnriched");
        writer.beginArray();
        for (EnrichedContact contact : this.data.getToEnriched()) {
            writer.beginObject();
            if (contact.getFullName() != null && !TextUtils.isEmpty(contact.getFullName().getFirstName())) {
                writer.name("fullName");
                writer.beginObject();
                writer.name("firstName").value(contact.fullName.firstName);
                writer.name("lastName").value(contact.fullName.lastName);
                writer.endObject();
            }
            writer.name(DBHeadersTable.HeadersEntry.COLUMN_NAME_VALUE).value(contact.value);
            writer.endObject();
        }
        writer.endArray();
        writer.endObject();
        writer.flush();
        Log.d("network", writer.toString());
        Log.d("network CallRequestBody ", writer.toString());
    }
}
