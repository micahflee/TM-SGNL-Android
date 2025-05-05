package com.tm.androidcopysdk;

import android.content.Context;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.tm.androidcopysdk.Models.AudioSettings;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.androidcopysdk.utils.PrefManagerConstants;
import com.tm.logger.Log;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/AudioSettingsManager.class */
public class AudioSettingsManager {
    private static final String TAG = "AudioSettingsManager";
    private static AudioSettingsManager audioSettingsManager;
    private static final String AUDIO_SETTINGS = "AUDIO_SETTINGS";
    private static final String FIREBASE_AUDIO_SETTINGS = "FIREBASE_AUDIO_SETTINGS";
    private AudioSettings audioSettings;

    private AudioSettingsManager() {
    }

    public static AudioSettingsManager getInstance(Context context) {
        audioSettingsManager = null;
        if (audioSettingsManager == null) {
            audioSettingsManager = new AudioSettingsManager();
            audioSettingsManager.checkPreferencesForAudioSettings(context);
        }
        return audioSettingsManager;
    }

    public void resetInstance() {
        this.audioSettings = null;
    }

    public void setAudioSourcesPrefs(Context context, AudioSettings audioSettings, String SHARED_PREFS_KEY) {
        Log.d(TAG, "setAudioSourcesPrefs, SHARED_PREFS_KEY = " + SHARED_PREFS_KEY + ", audioSettings = " + audioSettings.toString());
        Gson gson = new Gson();
        String json = gson.toJson(audioSettings);
        PrefManager.setStringPref(context, SHARED_PREFS_KEY, json);
        this.audioSettings = audioSettings;
    }

    public AudioSettings getAudioSettings() {
        return this.audioSettings;
    }

    public AudioSettings getAudioSettingsFromPref(Context context) throws NullPointerException {
        if (this.audioSettings == null) {
            Gson gson = new Gson();
            String firebaseJson = PrefManager.getStringPref(context, FIREBASE_AUDIO_SETTINGS);
            String json = PrefManager.getStringPref(context, AUDIO_SETTINGS);
            if (!TextUtils.isEmpty(firebaseJson)) {
                this.audioSettings = (AudioSettings) gson.fromJson(firebaseJson, AudioSettings.class);
                return this.audioSettings;
            } else if (!TextUtils.isEmpty(json)) {
                this.audioSettings = (AudioSettings) gson.fromJson(json, AudioSettings.class);
                return this.audioSettings;
            } else {
                return null;
            }
        }
        String callType = PreferenceManager.getDefaultSharedPreferences(context).getString(PrefManagerConstants.SHARED_PREFERENCE_CALL_TYPE_KEY_KEY, "0");
        if (!callType.equalsIgnoreCase("0")) {
            this.audioSettings.setAudioSourceInc(callType);
            this.audioSettings.setAudioSourceOut(callType);
        }
        return this.audioSettings;
    }

    public void clearAudioSettingsManagerPreferences(Context context) {
        PrefManager.setStringPref(context, AUDIO_SETTINGS, "");
    }

    public void clearFirebaseAudioSettingsManagerPreferences(Context context) {
        PrefManager.setStringPref(context, FIREBASE_AUDIO_SETTINGS, "");
    }

    public void checkPreferencesForAudioSettings(Context context) {
        AudioSettings audioSettings = getAudioSettingsFromPref(context);
        if (audioSettings == null || !audioSettings.getApi().contains(String.valueOf(Build.VERSION.SDK_INT))) {
            updateAudioSettingsFromAssets(context);
        }
    }

    /* JADX WARN: Type inference failed for: r0v4, types: [com.tm.androidcopysdk.AudioSettingsManager$1] */
    private void updateAudioSettingsFromAssets(Context context) {
        JsonParser parser = new JsonParser();
        JsonArray obj = parser.parse(readJSONFromAsset(context)).getAsJsonArray();
        Type listType = new TypeToken<List<AudioSettings>>() { // from class: com.tm.androidcopysdk.AudioSettingsManager.1
        }.getType();
        List<AudioSettings> audioSettingsList = (List) new Gson().fromJson(obj, listType);
        updateAudioSettings(context, audioSettingsList, false);
    }

    public void updateAudioSettings(Context context, List<AudioSettings> audioSettingsList, boolean isFirebaseSettings) {
        Log.d(TAG, "updateAudioSettings");
        String sharedAudioSettingsKey = isFirebaseSettings ? FIREBASE_AUDIO_SETTINGS : AUDIO_SETTINGS;
        List<AudioSettings> manufacturerAudioSettingsList = new ArrayList<>();
        List<AudioSettings> manufacturerAndModelAudioSettingsList = new ArrayList<>();
        List<AudioSettings> manufacturerAndModelandAPIAudioSettingsList = new ArrayList<>();
        List<AudioSettings> manufacturerAndDefaultudioSettingsList = new ArrayList<>();
        for (AudioSettings audioSettings : audioSettingsList) {
            if (Build.MANUFACTURER.equalsIgnoreCase(audioSettings.getManufacturer())) {
                manufacturerAudioSettingsList.add(audioSettings);
            }
        }
        if (manufacturerAudioSettingsList.size() > 0) {
            for (AudioSettings manufacturerAudioSettings : manufacturerAudioSettingsList) {
                if (manufacturerAudioSettings.getModel().toLowerCase().contains(Build.MODEL.toLowerCase())) {
                    manufacturerAudioSettings.setModel(Build.MODEL);
                    manufacturerAndModelAudioSettingsList.add(manufacturerAudioSettings);
                }
            }
            if (manufacturerAndModelAudioSettingsList.size() > 0) {
                for (AudioSettings apiAndManufacturerAndModelAudioSettingsList : manufacturerAndModelAudioSettingsList) {
                    if (apiAndManufacturerAndModelAudioSettingsList.getApi().contains(String.valueOf(Build.VERSION.SDK_INT))) {
                        apiAndManufacturerAndModelAudioSettingsList.setApi(String.valueOf(Build.VERSION.SDK_INT));
                        setAudioSourcesPrefs(context, apiAndManufacturerAndModelAudioSettingsList, sharedAudioSettingsKey);
                        manufacturerAndModelandAPIAudioSettingsList.add(apiAndManufacturerAndModelAudioSettingsList);
                    }
                }
                if (manufacturerAndModelandAPIAudioSettingsList.size() <= 0) {
                    setAudioSettingsForDefaultManifAndAPI(context, manufacturerAudioSettingsList, manufacturerAndDefaultudioSettingsList, audioSettingsList, sharedAudioSettingsKey);
                    return;
                }
                return;
            }
            setAudioSettingsForDefaultManifAndAPI(context, manufacturerAudioSettingsList, manufacturerAndDefaultudioSettingsList, audioSettingsList, sharedAudioSettingsKey);
            return;
        }
        setAudioSettingsForAPI(context, audioSettingsList, sharedAudioSettingsKey);
    }

    private void setAudioSettingsForDefaultManifAndAPI(Context context, List<AudioSettings> manufacturerAudioSettingsList, List<AudioSettings> manufacturerAndDefaultudioSettingsList, List<AudioSettings> audioSettingsList, String sharedPrefsKey) {
        for (AudioSettings defaultAudioSettings : manufacturerAudioSettingsList) {
            if (defaultAudioSettings.getModel().contains("default") && defaultAudioSettings.getApi().contains(String.valueOf(Build.VERSION.SDK_INT))) {
                defaultAudioSettings.setApi(String.valueOf(Build.VERSION.SDK_INT));
                manufacturerAndDefaultudioSettingsList.add(defaultAudioSettings);
            }
        }
        if (manufacturerAndDefaultudioSettingsList.size() > 0) {
            setAudioSourcesPrefs(context, manufacturerAndDefaultudioSettingsList.get(0), sharedPrefsKey);
        } else {
            setAudioSettingsForAPI(context, audioSettingsList, sharedPrefsKey);
        }
    }

    private void setAudioSettingsForAPI(Context context, List<AudioSettings> audioSettingsList, String SHARED_PREFS_KEY) {
        for (AudioSettings audioSettings : audioSettingsList) {
            if (audioSettings.getManufacturer().equals("default") && audioSettings.getApi().contains(String.valueOf(Build.VERSION.SDK_INT))) {
                audioSettings.setApi(String.valueOf(Build.VERSION.SDK_INT));
                setAudioSourcesPrefs(context, audioSettings, SHARED_PREFS_KEY);
            }
        }
    }

    public String readJSONFromAsset(Context context) {
        try {
            InputStream is = context.getAssets().open("ConfigExt.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            return json;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
