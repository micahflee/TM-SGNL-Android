package com.tm.androidcopysdk.network;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/MMSMessageRecorder.class */
public class MMSMessageRecorder extends BodyBase {
    String subscriberId;
    String msisdn;
    String ban;
    String subject;
    String messageId;
    String messageContext;
    Date messageTime;
    String from;
    String[] to;
    boolean groupMessage;
    String dialogGroupName;
    String contentType;
    String textContent;
    int textSize;
    String direction;
    List<Attachment> attachment = new ArrayList(3);

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

    public Attachment[] getAttachment() {
        return (Attachment[]) this.attachment.toArray(new Attachment[this.attachment.size()]);
    }

    public void setAttachment(Attachment[] attachment_in) {
        this.attachment = Arrays.asList(attachment_in);
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

    public String toString() {
        String sTextContent = TextUtils.isEmpty(this.textContent) ? " empty" : this.textContent;
        String sTo = this.to.toString();
        return this.subscriberId + " ," + this.msisdn + " ," + this.ban + " ," + this.subject + " ," + this.messageId + " ," + this.messageContext + " ," + this.messageTime.toString() + " ," + this.from + " ," + sTo + " ," + this.contentType + " ," + this.direction + " ," + sTextContent;
    }
}
