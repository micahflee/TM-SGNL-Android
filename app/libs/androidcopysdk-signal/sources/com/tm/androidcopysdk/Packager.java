package com.tm.androidcopysdk;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;
import com.tm.androidcopysdk.DataGrabber;
import com.tm.androidcopysdk.database.DBHeadersTable;
import com.tm.androidcopysdk.database.DBKeepAliveQueryHelper;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.database.UriUtils;
import com.tm.androidcopysdk.events.AlertEvent;
import com.tm.androidcopysdk.events.EventAbsObj;
import com.tm.androidcopysdk.events.EventsAPI;
import com.tm.androidcopysdk.events.EventsAPIobj;
import com.tm.androidcopysdk.events.InstallEventAPI;
import com.tm.androidcopysdk.events.SimRemovedWithoutLogout;
import com.tm.androidcopysdk.model.ArchiveRecipient;
import com.tm.androidcopysdk.network.Attachment;
import com.tm.androidcopysdk.network.BodyBase;
import com.tm.androidcopysdk.network.CallLogMessageRecorder;
import com.tm.androidcopysdk.network.ContactName;
import com.tm.androidcopysdk.network.EnrichedContact;
import com.tm.androidcopysdk.network.MMSMessageRecorder;
import com.tm.androidcopysdk.network.NetworkConstance;
import com.tm.androidcopysdk.network.NetworkManager;
import com.tm.androidcopysdk.network.SMSEVENTMessageRecord;
import com.tm.androidcopysdk.network.SMSMessageRecord;
import com.tm.androidcopysdk.network.TeleMessageMMSArchive;
import com.tm.androidcopysdk.network.TelemessageArchiverMessage;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import com.tm.androidcopysdk.utils.Contact;
import com.tm.androidcopysdk.utils.FlavorSettings;
import com.tm.androidcopysdk.utils.StringsFlavorsUtils;
import com.tm.logger.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/Packager.class */
public class Packager {
    public static EventsAPI packEventToAPI(Context context, EventAbsObj event) {
        EventsAPI ret = new EventsAPI();
        ret.setAuthenticationDetails(AndroidCopySDK.getInstance(context).getAuthenticationDetails());
        EventsAPIobj eventsAPIobj = new EventsAPIobj();
        switch (AnonymousClass1.$SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[event.getType().ordinal()]) {
            case 1:
            case 2:
                InstallEventAPI installEventAPI = new InstallEventAPI();
                installEventAPI.setVersion(CommonUtils.versionNumber(context));
                installEventAPI.setInstallTime(event.getDate());
                eventsAPIobj.setInstall(installEventAPI);
                break;
            case 3:
            case 4:
                AlertEvent alertEvent = new AlertEvent();
                alertEvent.setTitle(event.getDescription());
                alertEvent.setBody(event.getDescription());
                alertEvent.setSeverity(3);
                alertEvent.setAlertTime(event.getDate());
                eventsAPIobj.setAlert(alertEvent);
                break;
            case 5:
                AlertEvent missingFolder = new AlertEvent();
                missingFolder.setTitle(context.getString(R.string.missing_foldr_header));
                missingFolder.setBody(event.getDescription());
                missingFolder.setSeverity(3);
                missingFolder.setAlertTime(event.getDate());
                eventsAPIobj.setAlert(missingFolder);
                break;
            case MyAudioFormat.AUDIO_SOURCE_VOICE_RECOGNITION /* 6 */:
                AlertEvent alertEventFailedRecordingsEvent = new AlertEvent();
                alertEventFailedRecordingsEvent.setTitle(event.getDescription());
                alertEventFailedRecordingsEvent.setBody(event.getDescription());
                alertEventFailedRecordingsEvent.setSeverity(1);
                alertEventFailedRecordingsEvent.setAlertTime(event.getDate());
                eventsAPIobj.setAlert(alertEventFailedRecordingsEvent);
                break;
            case MyAudioFormat.AUDIO_SOURCE_VOICE_COMMUNICATION /* 7 */:
                AlertEvent alertAutoRecordingIsOff = new AlertEvent();
                alertAutoRecordingIsOff.setTitle("Android " + Build.VERSION.BASE_OS + " – Automatic recording is OFF");
                alertAutoRecordingIsOff.setBody(event.getDescription());
                alertAutoRecordingIsOff.setAlertTime(event.getDate());
                eventsAPIobj.setAlert(alertAutoRecordingIsOff);
                break;
            default:
                AlertEvent alertEvent2 = new AlertEvent();
                alertEvent2.setTitle(event.getDescription());
                alertEvent2.setBody(event.getDescription());
                alertEvent2.setAlertTime(event.getDate());
                eventsAPIobj.setAlert(alertEvent2);
                break;
        }
        ret.setEvents(eventsAPIobj);
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.tm.androidcopysdk.Packager$1  reason: invalid class name */
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/Packager$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType = new int[EventAbsObj.EventType.values().length];

        static {
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.UpdateVersion.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.InstallEvent.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.MissingPermissionsEvent.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.BatterySDKEvent.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.WrongFolderEvent.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.FailedRecordingsEvent.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.AutoRecordingIsOff.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    public static SMSEVENTMessageRecord packEventToSMS(Context context, EventAbsObj event, String myNumber) {
        SMSEVENTMessageRecord sr = new SMSEVENTMessageRecord();
        sr.setDirection("In");
        sr.setFrom(myNumber);
        sr.setSubject(StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.EVENTS_SUBJECT : context.getString(R.string.events_subject));
        sr.setTo(new String[]{myNumber});
        sr.setMessageId(event.getUid());
        sr.setMsisdn(myNumber);
        sr.setTextContent(event.getDescription());
        sr.setTextSize(event.getDescription().length());
        sr.setMessageTime(formatDate("" + event.getDate()));
        sr.setSubscriberId("N_NBI_BST_1384542593000118299510");
        sr.setMessageContext(DBKeepAliveQueryHelper.MessageType.EVENT.getType());
        sr.setGroupMessage(false);
        if (event instanceof SimRemovedWithoutLogout) {
            String subject = SimRemovedWithoutLogout.getSubject(context);
            sr.setSubject(subject);
        }
        return sr;
    }

    public static BodyBase packSMS(Cursor cur, BodyBase sr, String myNumber) {
        BodyBase sr2 = new SMSMessageRecord();
        cur.getLong(cur.getColumnIndex("_id"));
        String from = cur.getString(cur.getColumnIndex("address_from"));
        String to = cur.getString(cur.getColumnIndex("address_to"));
        String date = cur.getString(cur.getColumnIndex("date"));
        String body = cur.getString(cur.getColumnIndex("body"));
        cur.getString(cur.getColumnIndex("status"));
        String uniqueId = cur.getString(cur.getColumnIndex("orig_id"));
        String direction = cur.getString(cur.getColumnIndex("direction"));
        String messageContext = getMessageContext(cur);
        boolean isGroup = (to == null || to.isEmpty() || !to.contains(";")) ? false : true;
        if (!TextUtils.isEmpty(direction) && direction.equalsIgnoreCase("out")) {
            ((SMSMessageRecord) sr2).setDirection("Out");
            ((SMSMessageRecord) sr2).setTo(isGroup ? to.split(";") : new String[]{to});
            ((SMSMessageRecord) sr2).setFrom(from);
        } else if (!TextUtils.isEmpty(direction) && direction.equalsIgnoreCase("in")) {
            ((SMSMessageRecord) sr2).setDirection("In");
            ((SMSMessageRecord) sr2).setFrom(from);
            ((SMSMessageRecord) sr2).setTo(new String[]{to});
        } else if (to != null && !to.isEmpty()) {
            ((SMSMessageRecord) sr2).setDirection("Out");
            ((SMSMessageRecord) sr2).setTo(isGroup ? to.split(";") : new String[]{to});
            ((SMSMessageRecord) sr2).setFrom(myNumber);
        } else {
            ((SMSMessageRecord) sr2).setDirection("In");
            ((SMSMessageRecord) sr2).setFrom(from);
            ((SMSMessageRecord) sr2).setTo(new String[]{myNumber});
        }
        ((SMSMessageRecord) sr2).setMessageId(uniqueId);
        ((SMSMessageRecord) sr2).setMsisdn(myNumber);
        if (body != null) {
            ((SMSMessageRecord) sr2).setTextContent(body);
            ((SMSMessageRecord) sr2).setTextSize(body.length());
        }
        ((SMSMessageRecord) sr2).setMessageTime(formatDate(date));
        ((SMSMessageRecord) sr2).setSubscriberId("N_NBI_BST_1384542593000118299510");
        ((SMSMessageRecord) sr2).setMessageContext(messageContext);
        ((SMSMessageRecord) sr2).setGroupMessage(isGroup);
        return sr2;
    }

    private static void packDb(Cursor cur, String myNumber, TelemessageArchiverMessage sr) {
        boolean isGroup;
        String[] ttoV;
        cur.getLong(cur.getColumnIndex("_id"));
        String from = cur.getString(cur.getColumnIndex("address_from"));
        String to = cur.getString(cur.getColumnIndex("address_to"));
        String date = cur.getString(cur.getColumnIndex("date"));
        String body = cur.getString(cur.getColumnIndex("body"));
        cur.getString(cur.getColumnIndex("status"));
        String uniqueId = cur.getString(cur.getColumnIndex("orig_id"));
        String direction = cur.getString(cur.getColumnIndex("direction"));
        int chatMode = cur.getInt(cur.getColumnIndex("chat_mode"));
        String subject = cur.getString(cur.getColumnIndex("subject"));
        String messageType = getMessageContext(cur);
        String nativeId = cur.getString(cur.getColumnIndex("native_message_id"));
        if (!TextUtils.isEmpty(subject)) {
            sr.setSubject(subject);
        }
        if (chatMode == DataGrabber.CHAT_MODE.chat.getId()) {
            isGroup = false;
        } else {
            isGroup = true;
        }
        if (isGroup) {
            sr.setTo(to.split(";"));
        } else if (to.split(";").length <= 1) {
            sr.setTo(new String[]{to});
        }
        sr.setFrom(from);
        if (direction.equalsIgnoreCase("out")) {
            sr.setDirection("Out");
        } else {
            sr.setDirection("In");
        }
        sr.setNativeId(nativeId);
        sr.setMessageId(uniqueId);
        sr.setMsisdn(myNumber);
        if (!TextUtils.isEmpty(body)) {
            sr.setTextContent(body);
            sr.setTextSize(body.length());
        }
        sr.setMessageTime(formatDate(date));
        sr.setSubscriberId("N_NBI_BST_1384542593000118299510");
        sr.setMessageContext(messageType);
        boolean isGroupOrChannel = chatMode == DataGrabber.CHAT_MODE.group.getId() || chatMode == DataGrabber.CHAT_MODE.broadcast.getId();
        sr.setGroupMessage(isGroupOrChannel);
        if (isGroupOrChannel) {
            String groupName = cur.getString(cur.getColumnIndex("chat_name"));
            String groupid = cur.getString(cur.getColumnIndex("chat_id"));
            sr.setDialogGroupName(groupName);
            sr.setDialogGroupId(groupid);
        }
        String fromN = cur.getString(cur.getColumnIndex("from_name"));
        String fromV = cur.getString(cur.getColumnIndex("from_value"));
        if (fromN != null) {
            fromN = fromN.trim();
        }
        if (fromV != null) {
            fromV = fromV.trim();
        }
        EnrichedContact f = new EnrichedContact();
        if (TextUtils.isEmpty(fromV)) {
            f.setValue(from);
        } else {
            f.setValue(fromV);
        }
        Contact contact = Contact.getContactFromString(fromN);
        ContactName cn = new ContactName();
        if (Contact.isEmpty(contact)) {
            cn.setFirstName("");
            cn.setLastName("");
        } else {
            cn.setFirstName(contact.firstName);
            cn.setLastName(contact.lastName);
        }
        f.setFullName(cn);
        sr.setFromEnriched(f);
        String toN = cur.getString(cur.getColumnIndex("to_name"));
        String toV = cur.getString(cur.getColumnIndex("to_value"));
        if (!TextUtils.isEmpty(toN)) {
            String[] ttoN = toN.split(";");
            if (TextUtils.isEmpty(toV)) {
                ttoV = new String[ttoN.length];
            } else {
                ttoV = toV.split(";");
            }
            EnrichedContact[] t = new EnrichedContact[ttoV.length];
            for (int i = 0; i < ttoV.length; i++) {
                String v = ttoV[i];
                String n = ttoN[i];
                if (v != null) {
                    v = v.trim();
                }
                if (n != null) {
                    n = n.trim();
                }
                EnrichedContact enrichedContact = new EnrichedContact();
                if (TextUtils.isEmpty(v)) {
                    enrichedContact.setValue(ArchiveRecipient.DEFAULT);
                } else {
                    enrichedContact.setValue(v);
                }
                Contact contact2 = Contact.getContactFromString(n);
                ContactName cnT = new ContactName();
                if (Contact.isEmpty(contact2)) {
                    cnT = null;
                } else {
                    cnT.setFirstName(contact2.firstName);
                    cnT.setLastName(contact2.lastName);
                }
                enrichedContact.setFullName(cnT);
                t[i] = enrichedContact;
            }
            sr.setToEnriched(t);
        }
    }

    public static BodyBase packENATextMessage(Cursor cur, String myNumber) {
        TelemessageArchiverMessage sr = new TelemessageArchiverMessage();
        packDb(cur, myNumber, sr);
        return sr;
    }

    public static BodyBase packENAforMMS(Context context, Cursor cur, ContentResolver resolver, TeleMessageMMSArchive sr, String myNumber) {
        packDb(cur, myNumber, sr);
        long id = cur.getLong(cur.getColumnIndex("_id"));
        String attchamnet = cur.getString(cur.getColumnIndex("_data"));
        String mmsAttachType = cur.getString(cur.getColumnIndex("mime"));
        String messageType = getMessageContext(cur);
        if (attchamnet != null && !attchamnet.isEmpty()) {
            String[] attName = attchamnet.split(";");
            String[] mime = mmsAttachType.split(";");
            Attachment[] att = new Attachment[attName.length];
            for (int i = 0; i < attName.length; i++) {
                Attachment t = new Attachment();
                att[i] = t;
                att[i].setContentType(mime[i]);
                att[i].setName(attName[i]);
                att[i].setUri(MessageContentProvider.CONTENT_URI.buildUpon().appendPath("media").appendPath(String.valueOf(id)).appendPath(attName[i]).build());
            }
            sr.setAttachment(att);
        }
        sr.setMessageContext(messageType);
        return sr;
    }

    public static BodyBase packMMS(Context context, Cursor cur, ContentResolver resolver, BodyBase sr, String myNumber) {
        boolean isGroup;
        long id = cur.getLong(cur.getColumnIndex("_id"));
        String from = cur.getString(cur.getColumnIndex("address_from"));
        String to = cur.getString(cur.getColumnIndex("address_to"));
        String date = cur.getString(cur.getColumnIndex("date"));
        String body = cur.getString(cur.getColumnIndex("body"));
        cur.getString(cur.getColumnIndex("status"));
        String attchamnet = cur.getString(cur.getColumnIndex("_data"));
        String direction = cur.getString(cur.getColumnIndex("direction"));
        String uniqueId = cur.getString(cur.getColumnIndex("orig_id"));
        String mmsAttachType = cur.getString(cur.getColumnIndex("mime"));
        String subject = cur.getString(cur.getColumnIndex("subject"));
        String messageType = getMessageContext(cur);
        if (to.contains(";")) {
            isGroup = to.split(";").length > 1;
        } else {
            isGroup = false;
        }
        if (direction.equalsIgnoreCase("Out")) {
            ((MMSMessageRecorder) sr).setDirection(direction);
            ((MMSMessageRecorder) sr).setTo(to.contains(";") ? to.split(";") : new String[]{to});
            ((MMSMessageRecorder) sr).setFrom(TextUtils.isEmpty(from) ? myNumber : from);
        } else {
            ((MMSMessageRecorder) sr).setDirection(direction);
            ((MMSMessageRecorder) sr).setFrom(from);
            ((MMSMessageRecorder) sr).setTo(to.contains(";") ? to.split(";") : new String[]{myNumber});
        }
        if (!TextUtils.isEmpty(subject)) {
            ((MMSMessageRecorder) sr).setSubject(subject);
        }
        ((MMSMessageRecorder) sr).setMessageId(uniqueId);
        ((MMSMessageRecorder) sr).setMsisdn(myNumber);
        if (!body.isEmpty()) {
            ((MMSMessageRecorder) sr).setTextContent(body);
            ((MMSMessageRecorder) sr).setTextSize(body.length());
        }
        ((MMSMessageRecorder) sr).setMessageTime(formatDate(date));
        ((MMSMessageRecorder) sr).setSubscriberId("N_NBI_BST_1384542593000118299510");
        if (attchamnet != null && !attchamnet.isEmpty()) {
            String[] attName = attchamnet.split(";");
            String[] mime = mmsAttachType.split(";");
            Attachment[] att = new Attachment[attName.length];
            for (int i = 0; i < attName.length; i++) {
                Attachment t = new Attachment();
                att[i] = t;
                att[i].setContentType(mime[i]);
                att[i].setName(attName[i]);
                InputStream stream = null;
                try {
                    InputStream stream2 = resolver.openInputStream(MessageContentProvider.CONTENT_URI.buildUpon().appendPath("media").appendPath(String.valueOf(id)).appendPath(attName[i]).build());
                    stream = UriUtils.getDecipheredInStream(context, stream2);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                byte[] bArr = new byte[0];
                try {
                    byte[] bytes = IOUtils.toByteArray(stream);
                    String b64 = Base64.encodeToString(bytes, 2);
                    att[i].setContent(b64);
                    att[i].setAttachSize(att[i].getContent().length());
                } catch (IOException e2) {
                    Log.e("packager", "packMMS", e2);
                }
            }
            ((MMSMessageRecorder) sr).setAttachment(att);
        }
        ((MMSMessageRecorder) sr).setSubscriberId("N_NBI_BST_1384542593000118299510");
        ((MMSMessageRecorder) sr).setGroupMessage(isGroup);
        ((MMSMessageRecorder) sr).setMessageContext(messageType);
        return sr;
    }

    public static BodyBase packVirtualMMS(Cursor cur, ContentResolver resolver, BodyBase sr, String myNumber, File zipFile, String start, String end) {
        String date = String.valueOf(System.currentTimeMillis());
        String attchamnet = zipFile.getName();
        ((MMSMessageRecorder) sr).setContentType("text/plain");
        ((MMSMessageRecorder) sr).setDirection("In");
        ((MMSMessageRecorder) sr).setTo(new String[]{myNumber});
        ((MMSMessageRecorder) sr).setFrom(myNumber);
        ((MMSMessageRecorder) sr).setMessageId(String.valueOf("0-" + UUID.randomUUID()));
        ((MMSMessageRecorder) sr).setMsisdn(myNumber);
        ((MMSMessageRecorder) sr).setContentType("text/plain");
        if (!"".isEmpty()) {
            ((MMSMessageRecorder) sr).setTextContent("");
            ((MMSMessageRecorder) sr).setTextSize("".length());
        }
        ((MMSMessageRecorder) sr).setMessageTime(formatDate(date));
        ((MMSMessageRecorder) sr).setSubscriberId("N_NBI_BST_1384542593000118299510");
        if (attchamnet != null && !attchamnet.isEmpty()) {
            String[] attName = attchamnet.split(";");
            Attachment[] att = new Attachment[attName.length];
            for (int i = 0; i < attName.length; i++) {
                Attachment t = new Attachment();
                att[i] = t;
                att[i].setContentType(new String(NetworkConstance.SEND_LOGS_REQUEST_FILE_TYPE));
                att[i].setName(attName[i]);
                InputStream stream = null;
                try {
                    stream = new FileInputStream(zipFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                byte[] bArr = new byte[0];
                try {
                    byte[] bytes = IOUtils.toByteArray(stream);
                    att[i].setContent(Base64.encodeToString(bytes, 2));
                    att[i].setAttachSize(att[i].getContent().length());
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            ((MMSMessageRecorder) sr).setAttachment(att);
        }
        ((MMSMessageRecorder) sr).setSubscriberId("N_NBI_BST_1384542593000118299510");
        ((MMSMessageRecorder) sr).setMessageContext(FlavorSettings.getInstance().getMultimediaMessageContext().getType());
        ((MMSMessageRecorder) sr).setGroupMessage(false);
        ((MMSMessageRecorder) sr).setSubject(String.format("Historic message archiving from %s to %s", start, end));
        return sr;
    }

    public static BodyBase packMMS(BodyBase sr) {
        return null;
    }

    public static BodyBase packCallLog(Cursor cur, ContentResolver resolver, Context context, BodyBase sr, String myNumber) {
        String[] ttoN;
        long id = cur.getLong(cur.getColumnIndex("_id"));
        String from = cur.getString(cur.getColumnIndex("address_from"));
        String to = cur.getString(cur.getColumnIndex("address_to"));
        String date = cur.getString(cur.getColumnIndex("date"));
        String duration = cur.getString(cur.getColumnIndex("duration"));
        cur.getString(cur.getColumnIndex("status"));
        String attchamnet = cur.getString(cur.getColumnIndex("_data"));
        cur.getString(cur.getColumnIndex("mime"));
        String uniqueId = cur.getString(cur.getColumnIndex("orig_id"));
        int numberOfRetries = cur.getInt(cur.getColumnIndex("comp_retries"));
        int callType = cur.getInt(cur.getColumnIndex("call_type"));
        int callMode = cur.getInt(cur.getColumnIndex("mode"));
        String callContentBody = cur.getString(cur.getColumnIndex("body"));
        String callSubject = cur.getString(cur.getColumnIndex("subject"));
        String messageContext = getMessageContext(cur);
        int aggregateIndex = cur.getInt(cur.getColumnIndex("multiple_index"));
        cur.getInt(cur.getColumnIndex("extra_data"));
        String direction = cur.getString(cur.getColumnIndex("direction"));
        String endTime = Long.toString(Long.parseLong(date) + (Long.parseLong(duration) * 1000));
        int intDuration = Integer.valueOf(duration).intValue();
        StringBuilder body = null;
        String multiple = "";
        boolean isMultipleCalls = false;
        boolean isIn = true;
        if (aggregateIndex > 0) {
            Cursor c = resolver.query(MessageContentProvider.CONTENT_URI, null, "multiple_index=" + aggregateIndex + " AND extra_data=1", null, "date ASC");
            isMultipleCalls = true;
            if (c != null) {
                c.moveToFirst();
                if (c.getCount() > 1) {
                    body = new StringBuilder();
                    boolean once = true;
                    do {
                        String from2 = c.getString(c.getColumnIndex("address_from"));
                        String to2 = c.getString(c.getColumnIndex("address_to"));
                        String date2 = c.getString(c.getColumnIndex("date"));
                        String duration2 = c.getString(c.getColumnIndex("duration"));
                        String date22 = NetworkManager.formatter().format(formatDate(date2));
                        intDuration = Integer.valueOf(duration2).intValue();
                        String duration22 = getDurationString(Integer.valueOf(duration2).intValue());
                        String direction2 = c.getString(c.getColumnIndex("direction"));
                        if (!TextUtils.isEmpty(direction2) && direction2.equalsIgnoreCase("out")) {
                            body.append(to2 + " " + date22 + " " + duration22 + " " + (StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.MULTIPLE_OUTGOING_TYPE : context.getString(R.string.multiple_outgoing_type)) + " from: " + from2 + "\n ");
                            isIn = false;
                            multiple = multiple + to2 + ", ";
                        } else {
                            body.append(from2 + " " + date22 + " " + duration22 + " " + (StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.MULTIPLE_INCOMING_TYPE : context.getString(R.string.multiple_incoming_type)) + " to: " + to2 + "\n ");
                            multiple = multiple + from2 + ", ";
                            if (once) {
                                multiple = multiple + to2 + ", ";
                                once = false;
                            }
                        }
                    } while (c.moveToNext());
                    multiple = multiple.substring(0, multiple.lastIndexOf(","));
                }
                c.close();
            }
        }
        if (isMultipleCalls) {
            String[] toto = multiple.split(",");
            ((CallLogMessageRecorder) sr).setDirection((isIn && toto.length == 1) ? "In" : "Out");
            ((CallLogMessageRecorder) sr).setTo(toto);
            ((CallLogMessageRecorder) sr).setFrom(myNumber);
            ((CallLogMessageRecorder) sr).callInfo.setRecipientNum((to == null || to.isEmpty()) ? myNumber : to);
        } else if (!TextUtils.isEmpty(direction) && direction.equalsIgnoreCase("out")) {
            ((CallLogMessageRecorder) sr).setDirection("Out");
            ((CallLogMessageRecorder) sr).setTo(new String[]{to});
            ((CallLogMessageRecorder) sr).setFrom(from);
            ((CallLogMessageRecorder) sr).callInfo.setRecipientNum(to);
        } else {
            ((CallLogMessageRecorder) sr).setDirection("In");
            ((CallLogMessageRecorder) sr).setFrom(from);
            ((CallLogMessageRecorder) sr).setTo(new String[]{to});
            ((CallLogMessageRecorder) sr).callInfo.setRecipientNum(to);
        }
        if (callContentBody != null && !callContentBody.isEmpty() && !isMultipleCalls) {
            body = new StringBuilder(callContentBody);
        }
        ((CallLogMessageRecorder) sr).setMsisdn(myNumber);
        if (body != null) {
            ((CallLogMessageRecorder) sr).setTextContent(body.toString());
        }
        ((CallLogMessageRecorder) sr).setTextSize(body != null ? String.valueOf(body.toString().length()) : String.valueOf(("Duration: " + duration).length()));
        ((CallLogMessageRecorder) sr).setMessageTime(formatDate(date));
        ((CallLogMessageRecorder) sr).setSubscriberId("N_NBI_BST_1384542593000118299510");
        ((CallLogMessageRecorder) sr).setMessageContext(messageContext);
        ((CallLogMessageRecorder) sr).setGroupMessage(false);
        ((CallLogMessageRecorder) sr).setMessageId(uniqueId);
        ((CallLogMessageRecorder) sr).callInfo.setCallID(null);
        ((CallLogMessageRecorder) sr).callInfo.setCallStatus(Integer.toString(callType));
        ((CallLogMessageRecorder) sr).callInfo.setStartTime(formatDate(date));
        ((CallLogMessageRecorder) sr).callInfo.setEndTime(formatDate(endTime));
        String subjectTo = ((CallLogMessageRecorder) sr).getTo()[0];
        String subjectFrom = ((CallLogMessageRecorder) sr).getFrom();
        String callString = " voice call";
        if (callMode == 1) {
            callString = " video call";
        }
        if (callType == 2 || callType == 5) {
            callString = " unanswered" + callString;
        }
        String call_log_subject = String.format((StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.ARCHIVER_MESSAGE_TITLE : context.getString(R.string.archiver_message_title)) + callString + " from %s to %s\n", subjectFrom, subjectTo);
        String currentSubject = StringUtils.capitalize(call_log_subject.trim());
        if (!isMultipleCalls && callSubject != null && !callSubject.isEmpty()) {
            currentSubject = callSubject;
        }
        ((CallLogMessageRecorder) sr).setSubject(isMultipleCalls ? "Calls with multiple participants" : currentSubject);
        if (attchamnet != null && !attchamnet.isEmpty()) {
            Log.d("Packager", "callLog has attachment");
            boolean isNew = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isNewProt", false);
            String[] attName = attchamnet.split(";");
            Attachment[] att = new Attachment[attName.length];
            for (int i = 0; i < attName.length; i++) {
                att[i] = new Attachment();
                if (isNew) {
                    att[i].setContentType(new String("audio/3gp"));
                    att[i].setName(attName[i] + ".3gp");
                } else if (numberOfRetries > 1) {
                    att[i].setContentType(new String("audio/wav"));
                    att[i].setName(attName[i] + ".wav");
                } else {
                    att[i].setContentType(new String("audio/mpeg"));
                    att[i].setName(attName[i] + ".mp3");
                }
                Uri uri = MessageContentProvider.CONTENT_URI.buildUpon().appendPath("media").appendPath(String.valueOf(id)).appendPath(attName[i]).build();
                att[i].setUri(uri);
                ((CallLogMessageRecorder) sr).setAttachment(att);
            }
        } else {
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("recording", false) && intDuration > 0) {
                Log.d("Packager", "empty recording");
                CommonUtils.addFailedRecordingsEvent(context, EventAbsObj.EventType.FailedRecordingsEvent, StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.FAILED_RECORDING_EVENT_TEXT : context.getString(R.string.failed_recording_event_text));
            }
            if (AudioSettingsManager.getInstance(context).getAudioSettings().getndk_in().equalsIgnoreCase("default_app") && intDuration > 0 && AudioSettingsManager.getInstance(context).getAudioSettings().getIsSupported() && PreferenceManager.getDefaultSharedPreferences(context).getBoolean("recording", false)) {
                CommonUtils.addEvent(context, EventAbsObj.EventType.AutoRecordingIsOff);
                CommonUtils.showGeneralNotification(context, StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.NOTIFICATION_AUTO_RECORDING_IS_OFF : context.getString(R.string.notification_auto_recording_is_off), StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.NOTIFICATION_AUTO_RECORDING_IS_OFF_URL : context.getString(R.string.notification_auto_recording_is_off_url));
            }
        }
        String fromN = cur.getString(cur.getColumnIndex("from_name"));
        String fromV = cur.getString(cur.getColumnIndex("from_value"));
        if (fromN != null) {
            fromN = fromN.trim();
        }
        if (fromV != null) {
            fromV = fromV.trim();
        }
        EnrichedContact f = new EnrichedContact();
        if (TextUtils.isEmpty(fromV)) {
            f.setValue(from);
        } else {
            f.setValue(fromV);
        }
        Contact contact = Contact.getContactFromString(fromN);
        ContactName cn = new ContactName();
        if (Contact.isEmpty(contact)) {
            cn.setFirstName(null);
            cn.setLastName(null);
        } else {
            cn.setFirstName(contact.firstName);
            cn.setLastName(contact.lastName);
        }
        f.setFullName(cn);
        ((CallLogMessageRecorder) sr).setFromEnriched(f);
        String toN = cur.getString(cur.getColumnIndex("to_name"));
        String toV = cur.getString(cur.getColumnIndex("to_value"));
        if (!TextUtils.isEmpty(toV)) {
            String[] ttoV = toV.split(";");
            if (TextUtils.isEmpty(toN)) {
                ttoN = new String[ttoV.length];
            } else {
                ttoN = toN.split(";");
            }
            EnrichedContact[] t = new EnrichedContact[ttoV.length];
            for (int i2 = 0; i2 < ttoV.length; i2++) {
                String v = ttoV[i2];
                String n = ttoN[i2];
                if (v != null) {
                    v = v.trim();
                }
                if (n != null) {
                    n = n.trim();
                }
                EnrichedContact enrichedContact = new EnrichedContact();
                if (TextUtils.isEmpty(v)) {
                    enrichedContact.setValue(null);
                } else {
                    enrichedContact.setValue(v);
                }
                Contact c2 = Contact.getContactFromString(n);
                ContactName cnT = new ContactName();
                if (Contact.isEmpty(c2)) {
                    cnT = null;
                } else {
                    cnT.setFirstName(c2.firstName);
                    cnT.setLastName(c2.lastName);
                }
                enrichedContact.setFullName(cnT);
                t[i2] = enrichedContact;
            }
            ((CallLogMessageRecorder) sr).setToEnriched(t);
        }
        return sr;
    }

    private static String getDurationString(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        return twoDigitString(hours) + ":" + twoDigitString(minutes) + ":" + twoDigitString(seconds % 60);
    }

    private static String twoDigitString(int number) {
        if (number == 0) {
            return "00";
        }
        if (number / 10 == 0) {
            return "0" + number;
        }
        return String.valueOf(number);
    }

    private static Date formatDate(String date) {
        return new Date(Long.parseLong(date));
    }

    private static long calculateEndPoint(Cursor cursor) {
        String date0 = cursor.getString(cursor.getColumnIndex("date"));
        String duration0 = cursor.getString(cursor.getColumnIndex("duration"));
        long ret = Long.valueOf(date0).longValue() + (1000 * Long.valueOf(duration0).longValue());
        int index = cursor.getPosition();
        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String duration = cursor.getString(cursor.getColumnIndex("duration"));
            if (Long.valueOf(date).longValue() > ret) {
                break;
            }
            ret = Math.max(ret, Long.valueOf(date).longValue() + (1000 * Long.valueOf(duration).longValue()));
        }
        cursor.moveToPosition(index);
        return ret;
    }

    public static List<HeaderObj> getAllHeaders(Context context, String msgId) {
        String[] condition = {msgId};
        Cursor cursor = context.getContentResolver().query(MessageContentProvider.CONTENT_URI_HEADERS, null, "message_id = ?", condition, null);
        List<HeaderObj> list = new ArrayList<>();
        while (cursor != null) {
            try {
                try {
                    if (!cursor.moveToNext() || cursor.isAfterLast()) {
                        break;
                    }
                    String key = cursor.getString(cursor.getColumnIndex(DBHeadersTable.HeadersEntry.COLUMN_NAME_KEY));
                    String value = cursor.getString(cursor.getColumnIndex(DBHeadersTable.HeadersEntry.COLUMN_NAME_VALUE));
                    HeaderObj header = new HeaderObj(key, value, msgId);
                    list.add(header);
                } catch (Exception e) {
                    Log.e("PACKAGER", "getAllHeaders", e);
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return list;
    }

    private static String getMessageContext(Cursor cursor) {
        String messageType = getCursorString(cursor, "messageType");
        return TextUtils.isEmpty(messageType) ? DBKeepAliveQueryHelper.MessageType.APP_MESSAGE.getType() : messageType;
    }

    private static String getCursorString(Cursor cursor, String name) {
        int index = cursor.getColumnIndex(name);
        if (index < 0) {
            return null;
        }
        return cursor.getString(index);
    }
}
