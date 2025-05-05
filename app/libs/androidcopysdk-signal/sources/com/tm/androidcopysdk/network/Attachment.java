package com.tm.androidcopysdk.network;

import android.net.Uri;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/Attachment.class */
public class Attachment {
    Uri uri;
    String name;
    String contentType;
    String content;
    int attachSize;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Uri getUri() {
        return this.uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public int getAttachSize() {
        return this.attachSize;
    }

    public void setAttachSize(int attachSize) {
        this.attachSize = attachSize;
    }

    public String toString() {
        String suri = this.uri != null ? this.uri.toString() : " null";
        return "Uri=" + suri + " name:" + this.name + ", contentType:" + this.contentType;
    }
}
