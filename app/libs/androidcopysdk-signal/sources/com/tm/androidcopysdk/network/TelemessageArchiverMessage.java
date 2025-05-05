package com.tm.androidcopysdk.network;

import java.util.Date;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/TelemessageArchiverMessage.class */
public class TelemessageArchiverMessage extends BodyBase {
    String subscriberId;
    String msisdn;
    String ban;
    String messageId;
    String messageContext;
    Date messageTime;
    String from;
    String[] to;
    boolean groupMessage;
    String direction;
    String subject;
    String contentType;
    String textContent;
    int textSize;
    String dialogGroupName;
    String dialogGroupId;
    EnrichedContact fromEnriched;
    EnrichedContact[] toEnriched;
    String nativeId = "";
    boolean enterpriseSMSNumber;

    public String getSubscriberId() {
        return this.subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getMsisdn() {
        return this.msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @Override // com.tm.androidcopysdk.network.BodyBase
    public String getMessageId() {
        return this.messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageContext() {
        return this.messageContext;
    }

    public void setMessageContext(String messageContext) {
        this.messageContext = messageContext;
    }

    public Date getMessageTime() {
        return this.messageTime;
    }

    public void setMessageTime(Date messageTime) {
        this.messageTime = messageTime;
    }

    public String getFrom() {
        return this.from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String[] getTo() {
        return this.to;
    }

    public void setTo(String[] to) {
        this.to = to;
    }

    public boolean getGroupMessage() {
        return this.groupMessage;
    }

    public void setGroupMessage(boolean groupMessage) {
        this.groupMessage = groupMessage;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getTextContent() {
        return this.textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public int getTextSize() {
        return this.textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public String getDirection() {
        return this.direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isGroupMessage() {
        return this.groupMessage;
    }

    public String getDialogGroupName() {
        return this.dialogGroupName;
    }

    public void setDialogGroupName(String dialogGroupName) {
        this.dialogGroupName = dialogGroupName;
    }

    public String getDialogGroupId() {
        return this.dialogGroupId;
    }

    public void setDialogGroupId(String dialogGroupId) {
        this.dialogGroupId = dialogGroupId;
    }

    public EnrichedContact getFromEnriched() {
        return this.fromEnriched;
    }

    public void setFromEnriched(EnrichedContact fromEnriched) {
        this.fromEnriched = fromEnriched;
    }

    public EnrichedContact[] getToEnriched() {
        return this.toEnriched;
    }

    public void setToEnriched(EnrichedContact[] toEnriched) {
        this.toEnriched = toEnriched;
    }

    public boolean getEnterpriseSMSNumber() {
        return false;
    }

    public void setEnterpriseSMSNumber(boolean enterpriseSMSNumber) {
        this.enterpriseSMSNumber = enterpriseSMSNumber;
    }

    private String getToString() {
        String[] strArr;
        String ret = "";
        if (this.to != null && this.to.length > 0) {
            for (String s : this.to) {
                ret = ret + s + "::";
            }
        }
        return ret;
    }

    public String getNativeId() {
        return this.nativeId;
    }

    public void setNativeId(String nativeId) {
        if (nativeId == null) {
            this.nativeId = "";
        } else {
            this.nativeId = nativeId;
        }
    }

    public String toString() {
        return "<id> " + this.messageId + " <nativeId> " + this.nativeId + " <getMessageTime> " + getMessageTime().toString() + "<subject> " + this.subject + " <from> " + this.from + " <to> " + getToString();
    }
}
