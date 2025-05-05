package com.tm.androidcopysdk.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/TeleMessageMMSArchive.class */
public class TeleMessageMMSArchive extends TelemessageArchiverMessage {
    List<Attachment> attachment = new ArrayList();

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

    @Override // com.tm.androidcopysdk.network.TelemessageArchiverMessage
    public String toString() {
        String list = "";
        for (Attachment a : this.attachment) {
            if (a != null) {
                list = a.toString() + "\n";
            }
        }
        return super.toString() + " attachment size:" + this.attachment.size() + "\n" + list;
    }
}
