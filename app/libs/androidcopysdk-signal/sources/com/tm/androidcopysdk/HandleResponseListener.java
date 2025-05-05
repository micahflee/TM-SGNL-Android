package com.tm.androidcopysdk;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/HandleResponseListener.class */
public interface HandleResponseListener {
    void backoffRetryOnFailedMessages();

    void reportOnSuccess();

    void reportOnFailure();
}
