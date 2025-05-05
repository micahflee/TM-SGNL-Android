package com.tm.androidcopysdk.recorder;

import android.content.Context;
import com.tm.androidcopysdk.Models.AudioSettings;
import com.tm.logger.Log;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/RecorderFactory.class */
public class RecorderFactory {
    public static IAudioRecorder createAudioRecord(Context context, boolean isOut, AudioSettings audioSettings) {
        IAudioRecorder ret;
        Log.d("RecorderFactory", "createAudioRecord");
        String callType = isOut ? audioSettings.getAudioSourceOut() : audioSettings.getAudioSourceInc();
        int audioSource = Integer.parseInt(callType);
        String ndkType = isOut ? audioSettings.getndk_out() : audioSettings.getndk_in();
        if (ndkType.equalsIgnoreCase("native_audio")) {
            Log.d("RecorderFactory", "createAudioRecord choose: NativeWavRecorder");
            ret = NativeWavRecorder.getInstanse(context, audioSource);
        } else {
            Log.d("RecorderFactory", "createAudioRecord choose: WavAudioRecorder");
            ret = WavAudioRecorder.getInstanse(context, audioSource);
        }
        return ret;
    }
}
