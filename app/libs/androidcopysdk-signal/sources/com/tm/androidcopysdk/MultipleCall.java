package com.tm.androidcopysdk;

import android.content.Context;
import android.net.Uri;
import com.tm.androidcopysdk.database.MessageContentProvider;
import java.util.ArrayList;
import java.util.List;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/MultipleCall.class */
public class MultipleCall extends CallObj {
    List<CallObj> list;

    public MultipleCall(String id, String number, String date, String type, String duration, String myNum) {
        super(id, number, date, type, duration, "", myNum);
        this.list = new ArrayList();
    }

    public MultipleCall(String id, String number, String date, String type, String duration, int aggregateIndex, int extraData, String myNumber) {
        super(id, number, date, type, duration, myNumber, aggregateIndex, extraData);
        this.list = new ArrayList();
        this.aggregateIndex = aggregateIndex;
        this.extraData = extraData;
    }

    public void addCalls(CallObj callObj) {
        this.list.add(callObj);
    }

    public void addList(List<CallObj> list) {
        this.list.addAll(list);
    }

    public List<CallObj> getList() {
        return this.list;
    }

    @Override // com.tm.androidcopysdk.CallObj
    public String getLastDate() {
        String dateToSave = null;
        for (CallObj callObj : this.list) {
            String date = callObj.getDate();
            if (dateToSave == null) {
                dateToSave = date;
            } else if (Long.valueOf(dateToSave).longValue() < Long.valueOf(date).longValue()) {
                dateToSave = date;
            }
        }
        return dateToSave;
    }

    @Override // com.tm.androidcopysdk.CallObj
    public int extraData() {
        return 2;
    }

    @Override // com.tm.androidcopysdk.CallObj
    public Uri writeToDB(Context context, MessageContentProvider.MessageDeliveryStatus status, String uri) {
        for (CallObj callObj : this.list) {
            callObj.writeToDB(context, this.aggregateIndex, MessageContentProvider.MessageDeliveryStatus.WaitingToMultipleParticipants, null);
        }
        return super.writeToDB(context, this.aggregateIndex, status, uri);
    }

    @Override // com.tm.androidcopysdk.CallObj
    public String toString() {
        String ret = "MultipleCall: ";
        for (int i = 0; i < this.list.size(); i++) {
            ret = ret + " __ " + this.list.get(i).toString();
        }
        return ret;
    }
}
