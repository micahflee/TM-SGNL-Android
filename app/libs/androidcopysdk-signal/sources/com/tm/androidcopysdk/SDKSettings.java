package com.tm.androidcopysdk;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import androidx.core.app.ActivityCompat;
import com.tm.logger.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/SDKSettings.class */
public class SDKSettings {
    public static final String TAG = "AppSettings";
    public static final String IDLE_EVENT_INTERVAL = "idle.event.interval";
    public static final String LOW_STORAGE_EVENT_INTERVAL_MILLIS = "low.storage.event.interval.millis";
    public static final String LOW_STORAGE_FREE_SPACE_THRESHOLD_MEGA = "low.storage.free.space.threshold.mega";
    public static final String GET_APP_SETTINGS_INTERVAL = "app.settings.interval.minute";
    public static final String GET_APP_SETTINGS_INTERVAL_SUSPEND = "app.settings.interval.day";
    public static final String GET_KEEP_ALIVE_INTERVAL = "app.keep_alive.interval.minute";
    public static final String ALARM_APP_INTERVAL = "app.alarm.interval.minute";
    public static final String FILE_NAME = "settingsData.txt";
    private Context _context;
    private HashMap<String, String> _settingData;

    public SDKSettings(Context context) {
        this._settingData = new HashMap<>();
        this._settingData = new HashMap<>();
        this._context = context;
        boolean isDebuggable = 0 != (context.getApplicationInfo().flags & 2);
        boolean hasPermission = ActivityCompat.checkSelfPermission(context, "android.permission.MANAGE_EXTERNAL_STORAGE") == 0;
        readFromFile(isDebuggable && hasPermission);
    }

    public boolean readData(BufferedReader br) {
        while (true) {
            try {
                String line = br.readLine();
                if (line != null) {
                    int idx = line.indexOf(",") + 1;
                    String key = line.substring(0, idx - 1);
                    String str = line.substring(idx);
                    this._settingData.put(key, str);
                } else {
                    return true;
                }
            } catch (IOException e) {
                Log.e(TAG, "Error reading from internal settings file", e);
                return false;
            }
        }
    }

    private void readFromFile(boolean isDebugable) {
        Log.d(TAG, "readFromFile :" + isDebugable);
        boolean internalFile = false;
        File sdcardSettings = Environment.getExternalStorageDirectory();
        File file = new File(sdcardSettings, FILE_NAME);
        if (file.exists() && isDebugable) {
            try {
                InputStream input = new BufferedInputStream(new FileInputStream(file));
                BufferedReader br = new BufferedReader(new InputStreamReader(input));
                internalFile = readData(br);
                if (this._settingData.isEmpty()) {
                    internalFile = false;
                }
            } catch (FileNotFoundException e) {
                internalFile = false;
            }
        }
        if (!internalFile) {
            InputStream ims = null;
            AssetManager assetManager = this._context.getAssets();
            try {
                ims = assetManager.open(FILE_NAME);
            } catch (IOException e2) {
                Log.e(TAG, "Error opening settings file", e2);
            }
            InputStreamReader isr = new InputStreamReader(ims);
            BufferedReader input2 = new BufferedReader(isr);
            while (true) {
                try {
                    String line = input2.readLine();
                    if (line != null) {
                        int idx = line.indexOf(",") + 1;
                        String key = line.substring(0, idx - 1);
                        String str = line.substring(idx);
                        this._settingData.put(key, str);
                    } else {
                        return;
                    }
                } catch (IOException e3) {
                    Log.e(TAG, "Error reading from settings file", e3);
                    return;
                }
            }
        }
    }

    public String getSetting(String settingName) {
        return this._settingData.get(settingName);
    }
}
