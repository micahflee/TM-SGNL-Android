package com.tm.androidcopysdk.network;

import java.util.Date;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/SMSMessageRecord.class */
public class SMSMessageRecord extends BodyBase {
    String subscriberId;
    String msisdn;
    String ban;
    String messageId;
    String messageContext;
    Date messageTime;
    String from;
    String[] to;
    boolean groupMessage;
    String contentType;
    String textContent;
    int textSize;
    String direction;
    String subject;

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
}
