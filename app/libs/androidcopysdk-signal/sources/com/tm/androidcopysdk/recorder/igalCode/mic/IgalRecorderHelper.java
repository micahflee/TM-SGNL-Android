package com.tm.androidcopysdk.recorder.igalCode.mic;

import android.content.Context;
import androidx.core.content.ContextCompat;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/igalCode/mic/IgalRecorderHelper.class */
public class IgalRecorderHelper {
    private static IgalRecorderHelper ourInstance = new IgalRecorderHelper();

    public static IgalRecorderHelper getInstance() {
        return ourInstance;
    }

    private IgalRecorderHelper() {
    }

    public boolean startFixCallRecorder(Context context, int audioSessionId) {
        if (isPermissionAudioSettingsModify(context)) {
            IgalCallRecorderFromMicHelper.getInstance().initialize();
            IgalCallRecorderFromMicHelper.getInstance().startRec(audioSessionId);
            return true;
        }
        return false;
    }

    public void stopFixCallRecorder() {
        IgalCallRecorderFromMicHelper.getInstance().stopRec();
    }

    private boolean isPermissionAudioSettingsModify(Context context) {
        int permissionCheck = ContextCompat.checkSelfPermission(context, "android.permission.MODIFY_AUDIO_SETTINGS");
        boolean result = false;
        switch (permissionCheck) {
            case -1:
                result = false;
                break;
            case 0:
                result = true;
                break;
        }
        return result;
    }
}
