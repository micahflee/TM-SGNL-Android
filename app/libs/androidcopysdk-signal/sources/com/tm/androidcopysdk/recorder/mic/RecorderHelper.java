package com.tm.androidcopysdk.recorder.mic;

import android.content.Context;
import androidx.core.content.ContextCompat;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/mic/RecorderHelper.class */
public class RecorderHelper {
    private static RecorderHelper ourInstance = new RecorderHelper();

    public static RecorderHelper getInstance() {
        return ourInstance;
    }

    private RecorderHelper() {
    }

    public boolean startFixCallRecorder7(Context context) {
        if (isPermissionAudioSettingsModify(context)) {
            CallRecorderFromMicHelper.getInstance().initialize();
            CallRecorderFromMicHelper.getInstance().startRec7();
            return true;
        }
        return false;
    }

    public boolean startFixCallRecorder(Context context, int audioSessionId) {
        if (isPermissionAudioSettingsModify(context)) {
            CallRecorderFromMicHelper.getInstance().initialize();
            CallRecorderFromMicHelper.getInstance().startRec(audioSessionId);
            return true;
        }
        return false;
    }

    public void stopFixCallRecorder(boolean isOut) {
        CallRecorderFromMicHelper.getInstance().stopRec(isOut);
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
