package com.tm.androidcopysdk.compress;

import java.io.File;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/compress/IConvertCallback.class */
public interface IConvertCallback {
    void onSuccess(File file);

    void onFailure(Exception exc);
}
