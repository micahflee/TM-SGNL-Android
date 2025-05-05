package com.tm.androidcopysdk;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.text.TextUtils;
import androidx.core.app.ActivityCompat;
import com.tm.androidcopysdk.Models.AudioSettings;
import com.tm.androidcopysdk.recorder.igalCode.IgalCallRecorderService;
import com.tm.androidcopysdk.utils.PrefManagerConstants;
import com.tm.androidcopysdk.utils.StartConverEvent;
import com.tm.logger.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.greenrobot.eventbus.EventBus;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/CallListener.class */
public class CallListener extends PhoneStateListener {
    private Context contextNow;
    private AudioManager audioManager;
    public static long startCall = System.currentTimeMillis();
    private final String TAG = "CallListener";
    private String mFileName = null;
    private boolean inWork = false;
    private boolean isOut = true;
    private boolean saveTime = true;
    private MediaRecorder recorder = null;

    public CallListener(Context context) {
        this.contextNow = context;
    }

    private String generateFileName() {
        String fileName = this.contextNow.getFilesDir().getAbsolutePath() + "/" + String.valueOf(System.currentTimeMillis() + 350);
        Log.d("CallListener", " generateFileName: " + fileName);
        return fileName;
    }

    @Override // android.telephony.PhoneStateListener
    public void onCallStateChanged(int state, String s) {
        Log.d("CallListener", "onCallStateChanged state = " + state);
        if (state != 0) {
            if (this.saveTime) {
                startCall = System.currentTimeMillis() - 100;
                Log.d("CallListener", "onCallStateChanged startCall = " + startCall);
                this.saveTime = false;
            }
        } else {
            this.saveTime = true;
        }
        String callType = PreferenceManager.getDefaultSharedPreferences(this.contextNow).getString(PrefManagerConstants.SHARED_PREFERENCE_CALL_TYPE_KEY_KEY, "0");
        boolean autoSpeaker = PreferenceManager.getDefaultSharedPreferences(this.contextNow).getBoolean("auto_speaker_key", false);
        boolean enableCallRecording = PreferenceManager.getDefaultSharedPreferences(this.contextNow).getBoolean("recording", false);
        if (callType.equals("1") && autoSpeaker && enableCallRecording) {
            this.audioManager = (AudioManager) this.contextNow.getSystemService("audio");
            Log.d("CallListener", "headphones " + String.valueOf(this.audioManager.isWiredHeadsetOn()));
            this.audioManager.setMode(2);
            this.audioManager.setSpeakerphoneOn(true);
            this.audioManager.setStreamVolume(0, this.audioManager.getStreamMaxVolume(0), 0);
        }
        switch (state) {
            case 0:
                this.isOut = true;
                this.inWork = false;
                Boolean isRecording = Boolean.valueOf(PreferenceManager.getDefaultSharedPreferences(this.contextNow).getBoolean("recording", false));
                if (isRecording.booleanValue()) {
                    EventBus.getDefault().post(new StartConverEvent(TextUtils.isEmpty(this.mFileName) ? this.mFileName : new String(this.mFileName)));
                } else {
                    Log.d("CallListener", "asking to archive a voice call settings off");
                    GetMessagesService.startJobIntentService(this.contextNow, "com.tm.androidcopysdk.action.getCallLog");
                }
                this.mFileName = null;
                return;
            case 1:
                this.isOut = false;
                if (this.mFileName == null) {
                    this.mFileName = generateFileName();
                    return;
                }
                return;
            case 2:
                Log.d("CallListener", "CALL_STATE_OFFHOOK  call state s = " + (s == null ? "null caller" : s));
                if (!this.inWork) {
                    this.inWork = true;
                    int recordCallLogsPermission = ActivityCompat.checkSelfPermission(this.contextNow, "android.permission.RECORD_AUDIO");
                    Boolean isRecording2 = Boolean.valueOf(PreferenceManager.getDefaultSharedPreferences(this.contextNow).getBoolean("recording", false));
                    boolean defaultRecordedFiles = AudioSettingsManager.getInstance(this.contextNow).getAudioSettings().getndk_in().equalsIgnoreCase("default_app");
                    if (isRecording2.booleanValue() && recordCallLogsPermission == 0 && !defaultRecordedFiles) {
                        AudioSettings audioSettings = AudioSettingsManager.getInstance(this.contextNow).getAudioSettingsFromPref(this.contextNow);
                        Log.d("CallListener", "NDK chosen= " + (audioSettings.getndk_in().equalsIgnoreCase("igal_wav") ? "IgalCallRecorderService" : "CallRecorderService"));
                        Intent intent = new Intent(this.contextNow, audioSettings.getndk_in().equalsIgnoreCase("igal_wav") ? IgalCallRecorderService.class : CallRecorderService.class);
                        intent.setAction("START_RECORD");
                        intent.putExtra("phone", s);
                        intent.putExtra("isOut", this.isOut);
                        if (this.mFileName == null) {
                            this.mFileName = generateFileName();
                        }
                        Log.d("CallListener", "filename is:" + this.mFileName);
                        intent.putExtra("filename", this.mFileName);
                        this.contextNow.startService(intent);
                        return;
                    }
                    return;
                }
                return;
            default:
                return;
        }
    }

    private void copyRecordToExternalStorage(Context context, String filename) {
        try {
            File sdcardFile = new File(Environment.getExternalStorageDirectory(), "record.3gp");
            sdcardFile.createNewFile();
            InputStream inputStream = new FileInputStream(filename);
            OutputStream outputStream = new FileOutputStream(sdcardFile);
            FileInputStream stream = new FileInputStream(this.mFileName);
            byte[] byteArray = new byte[stream.available()];
            stream.read(byteArray);
            outputStream.write(byteArray, 0, byteArray.length);
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            Log.e("CallListener", "Exception" + e.toString());
        }
    }
}
