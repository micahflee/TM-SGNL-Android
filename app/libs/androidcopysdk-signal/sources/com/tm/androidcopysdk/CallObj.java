package com.tm.androidcopysdk;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import com.tm.androidcopysdk.database.DBKeepAliveQueryHelper;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.utils.Contact;
import com.tm.androidcopysdk.utils.FlavorSettings;
import com.tm.androidcopysdk.utils.PrefManager;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/CallObj.class */
public class CallObj {
    public static final int MISSED_CALL_OUTGOING_TYPE = 8;
    public static final int REJECTED_TYPE_OUTGOING_TYPE = 9;
    String id;
    String number;
    String date;
    String type;
    String duration;
    String lastModified;
    int callMode;
    int aggregateIndex;
    int extraData;
    String myNumber;
    Contact name;

    public CallObj(String id, String number, String date, String type, String duration, String lastModified, String myNumber) {
        this.id = null;
        this.number = null;
        this.date = null;
        this.type = null;
        this.duration = null;
        this.lastModified = "";
        this.callMode = 0;
        this.aggregateIndex = 0;
        this.extraData = 0;
        this.myNumber = null;
        this.name = null;
        this.id = id;
        this.number = number;
        this.date = date;
        this.type = type;
        this.duration = duration;
        this.lastModified = lastModified;
        this.myNumber = myNumber;
    }

    public CallObj(String id, String number, String date, String type, String duration, String lastModified, String myNumber, int callMode) {
        this.id = null;
        this.number = null;
        this.date = null;
        this.type = null;
        this.duration = null;
        this.lastModified = "";
        this.callMode = 0;
        this.aggregateIndex = 0;
        this.extraData = 0;
        this.myNumber = null;
        this.name = null;
        this.id = id;
        this.number = number;
        this.date = date;
        this.type = type;
        this.duration = duration;
        this.lastModified = lastModified;
        this.myNumber = myNumber;
        this.callMode = callMode;
    }

    public CallObj(String id, String number, String date, String type, String duration, String myNumber, int aggregateIndex, int extraData) {
        this.id = null;
        this.number = null;
        this.date = null;
        this.type = null;
        this.duration = null;
        this.lastModified = "";
        this.callMode = 0;
        this.aggregateIndex = 0;
        this.extraData = 0;
        this.myNumber = null;
        this.name = null;
        this.id = id;
        this.number = number;
        this.date = date;
        this.type = type;
        this.duration = duration;
        this.aggregateIndex = aggregateIndex;
        this.extraData = extraData;
        this.myNumber = myNumber;
    }

    public String getLastModified() {
        return this.lastModified;
    }

    public String getId() {
        return this.id;
    }

    public String getNumber() {
        return this.number;
    }

    public String getDate() {
        return this.date;
    }

    public String getType() {
        return this.type;
    }

    public String getDuration() {
        return this.duration;
    }

    public int getAggregateIndex() {
        return this.aggregateIndex;
    }

    public void setAggregateIndex(int aggregateIndex) {
        this.aggregateIndex = aggregateIndex;
    }

    public int getExtraData() {
        return this.extraData;
    }

    public void setExtraData(int extraData) {
        this.extraData = extraData;
    }

    public String getLastDate() {
        return this.date;
    }

    public int extraData() {
        return 1;
    }

    public Uri writeToDB(Context context, MessageContentProvider.MessageDeliveryStatus status, String uri) {
        return writeToDB(context, 0, status, uri);
    }

    public Uri writeToDB(Context context, int aggregateIndex, MessageContentProvider.MessageDeliveryStatus status, String uri) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("duration", this.duration);
        contentValues.put("date", this.date);
        boolean mobileNumberNeedToConvertToGlobal = FlavorSettings.getInstance().isMobileNumberNeedToConvertToGlobal();
        if (TextUtils.isEmpty(this.number)) {
            this.number = CommonUtils.resolvePhoneNumberOrName(this.number, this.name);
        } else if (!TextUtils.isEmpty(this.number) && mobileNumberNeedToConvertToGlobal) {
            this.number = CommonUtils.formatNumberWithoutPlus(CommonUtils.getInternationalNumber(context, this.number));
        }
        if (!TextUtils.isEmpty(this.myNumber) && mobileNumberNeedToConvertToGlobal) {
            this.myNumber = CommonUtils.formatNumberWithoutPlus(CommonUtils.getInternationalNumber(context, this.myNumber));
        }
        if (aggregateIndex > 0 && (Integer.valueOf(this.type).intValue() == 2 || Integer.valueOf(this.type).intValue() == 1)) {
            boolean hasCall = (TextUtils.isEmpty(this.duration) || this.duration.equalsIgnoreCase("0")) ? false : true;
            if (hasCall) {
                contentValues.put("multiple_index", Integer.valueOf(aggregateIndex));
                contentValues.put("extra_data", Integer.valueOf(this.extraData));
            }
        }
        contentValues.put("orig_id", CommonUtils.getUniqueId(this.id));
        String lastType = DataGrabber.getChangedType(this.type);
        contentValues.put("call_type", lastType);
        contentValues.put("mode", Integer.valueOf(this.callMode));
        String myName = PrefManager.getStringPref(context, "pref_my_name_contact", "");
        Contact contact = Contact.getContactFromString(myName);
        if (Integer.valueOf(this.type).intValue() == 2 || Integer.parseInt(this.type) == 8 || Integer.parseInt(this.type) == 9) {
            contentValues.put("address_to", this.number);
            contentValues.put("address_from", this.myNumber);
            contentValues.put("direction", "Out");
            if (!Contact.isEmpty(this.name)) {
                contentValues.put("to_name", this.name.toString() + ";");
            }
            contentValues.put("to_value", this.number + ";");
            if (!Contact.isEmpty(contact)) {
                contentValues.put("from_name", contact.toString());
            }
            contentValues.put("from_value", this.myNumber);
        }
        if (Integer.valueOf(this.type).intValue() == 1 || (Integer.parseInt(this.type) >= 3 && Integer.parseInt(this.type) < 8)) {
            contentValues.put("address_from", this.number);
            contentValues.put("address_to", this.myNumber);
            contentValues.put("direction", "In");
            if (!Contact.isEmpty(this.name)) {
                contentValues.put("from_name", this.name.toString());
            }
            contentValues.put("from_value", this.number);
            if (!Contact.isEmpty(contact)) {
                contentValues.put("to_name", contact.toString() + ";");
            }
            contentValues.put("to_value", this.myNumber + ";");
        }
        contentValues.put("type", String.valueOf(MessageType.CallLog));
        contentValues.put("status", status.name());
        DBKeepAliveQueryHelper.MessageType messageContext = DBKeepAliveQueryHelper.MessageType.CALL_LOG;
        if (!TextUtils.isEmpty(uri)) {
            contentValues.put("_data", uri);
            messageContext = this.callMode == 0 ? DBKeepAliveQueryHelper.MessageType.VOICE_CALL : DBKeepAliveQueryHelper.MessageType.VIDEO_CALL;
        }
        contentValues.put("messageType", messageContext.getType());
        return context.getContentResolver().insert(MessageContentProvider.CONTENT_URI, contentValues);
    }

    public Contact getName() {
        return this.name;
    }

    public void setName(Contact name) {
        this.name = name;
    }

    public String toString() {
        return "type:" + this.type + " number:" + this.number + " date: " + this.date + " duration:" + this.duration;
    }
}
