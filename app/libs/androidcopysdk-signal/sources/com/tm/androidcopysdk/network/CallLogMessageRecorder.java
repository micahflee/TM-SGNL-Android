package com.tm.androidcopysdk.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/CallLogMessageRecorder.class */
public class CallLogMessageRecorder extends BodyBase {
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
    String textSize;
    String direction;
    String subject;
    int extraData;
    EnrichedContact fromEnriched;
    EnrichedContact[] toEnriched;
    public static final String subscriberId_key = "subscriberId";
    public static final String msisdn_key = "msisdn";
    public static final String ban_key = "ban";
    public static final String messageId_key = "messageId";
    public static final String messageContext_key = "messageContext";
    public static final String messageTime_key = "messageTime";
    public static final String from_key = "from";
    public static final String to_key = "to";
    public static final String groupMessage_key = "groupMessage";
    public static final String dialogGroupName_key = "dialogGroupName";
    public static final String dialogGroupName_id = "dialogGroupId";
    public static final String contentType_key = "contentType";
    public static final String textContent_key = "textContent";
    public static final String textSize_key = "textSize";
    public static final String direction_key = "direction";
    public static final String subject_key = "subject";
    public static final String attachment_key = "attachment";
    public static final String callinfo_call_id_key = "callID";
    public static final String callinfo_recipient_num_key = "recipientNum";
    public static final String callinfo_call_status_key = "callStatus";
    public static final String callinfo_start_time_key = "startTime";
    public static final String callinfo_end_time_key = "endTime";
    public static final String callinfo_key = "callInfo";
    List<Attachment> attachment = new ArrayList();
    public CallInfo callInfo = new CallInfo();

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

    public String getBan() {
        return this.ban;
    }

    public void setBan(String ban) {
        this.ban = ban;
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

    public String getTextSize() {
        return this.textSize;
    }

    public void setTextSize(String textSize) {
        this.textSize = textSize;
    }

    public String getDirection() {
        return this.direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public boolean isGroupMessage() {
        return this.groupMessage;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Attachment[] getAttachment() {
        if (this.attachment != null) {
            return (Attachment[]) this.attachment.toArray(new Attachment[this.attachment.size()]);
        }
        return null;
    }

    public void setAttachment(Attachment[] attachment_in) {
        if (attachment_in != null) {
            this.attachment = Arrays.asList(attachment_in);
        } else {
            this.attachment = null;
        }
    }

    public int getExtraData() {
        return this.extraData;
    }

    public void setExtraData(int extraData) {
        this.extraData = extraData;
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

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/CallLogMessageRecorder$CallInfo.class */
    public class CallInfo {
        String callID = "";
        String recipientNum = "";
        String callStatus = "";
        Date startTime;
        Date endTime;

        public CallInfo() {
        }

        public String getCallID() {
            return this.callID;
        }

        public void setCallID(String callID) {
            this.callID = callID;
        }

        public String getRecipientNum() {
            return this.recipientNum;
        }

        public void setRecipientNum(String recipientNum) {
            this.recipientNum = recipientNum;
        }

        public String getCallStatus() {
            return this.callStatus;
        }

        public void setCallStatus(String callStatus) {
            this.callStatus = callStatus;
        }

        public Date getStartTime() {
            return this.startTime;
        }

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }

        public Date getEndTime() {
            return this.endTime;
        }

        public void setEndTime(Date endTime) {
            this.endTime = endTime;
        }

        public String toString() {
            return "<id> " + this.callID + " <recipientNum> " + this.recipientNum + " <startTime> " + this.startTime + " <endTime> " + this.endTime + " <callStatus>" + this.callStatus;
        }
    }

    private String getToString() {
        String[] strArr;
        String ret = "";
        for (String s : this.to) {
            ret = ret + s + "::";
        }
        return ret;
    }

    public String attachmentToString() {
        String list = "";
        if (this.attachment != null) {
            for (Attachment a : this.attachment) {
                if (a != null) {
                    list = a.toString() + "\n";
                }
            }
        }
        return list;
    }

    public String toString() {
        return "<id> " + (this.messageId != null ? this.messageId : "null") + " <getMessageTime> " + getMessageTime().toString() + "<subject> " + (this.subject != null ? this.subject : "null") + "<callinfo> " + this.callInfo.toString() + " <from> " + (this.from != null ? this.from : "null") + " <to> " + getToString() + " <attachment>" + attachmentToString();
    }
}
