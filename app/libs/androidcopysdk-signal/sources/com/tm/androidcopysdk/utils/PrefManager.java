package com.tm.androidcopysdk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/utils/PrefManager.class */
public class PrefManager {
    private static String _SkinType = null;
    private static Boolean _sharedpreferencesTypeDefault = true;

    public static void setStringPref(Context context, int key, String value) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor settings = myPrefs.edit();
            settings.putString(context.getString(key), value);
            if (!settings.commit()) {
                _sharedpreferencesTypeDefault = false;
                SharedPreferences myPrefs1 = context.getSharedPreferences(context.getPackageName(), 4);
                SharedPreferences.Editor settings1 = myPrefs1.edit();
                settings1.putString(context.getString(key), value);
                settings1.commit();
                return;
            }
            return;
        }
        SharedPreferences myPrefs12 = context.getSharedPreferences(context.getPackageName(), 4);
        SharedPreferences.Editor settings12 = myPrefs12.edit();
        settings12.putString(context.getString(key), value);
        settings12.commit();
    }

    public static void setStringPref(Context context, String key, String value) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor settings = myPrefs.edit();
            settings.putString(key, value);
            if (!settings.commit()) {
                _sharedpreferencesTypeDefault = false;
                SharedPreferences myPrefs1 = context.getSharedPreferences(context.getPackageName(), 4);
                SharedPreferences.Editor settings1 = myPrefs1.edit();
                settings1.putString(key, value);
                settings1.commit();
                return;
            }
            return;
        }
        SharedPreferences myPrefs12 = context.getSharedPreferences(context.getPackageName(), 4);
        SharedPreferences.Editor settings12 = myPrefs12.edit();
        settings12.putString(key, value);
        settings12.commit();
    }

    public static void setBooleanPref(Context context, String key, boolean value) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor settings = myPrefs.edit();
            settings.putBoolean(key, value);
            if (!settings.commit()) {
                _sharedpreferencesTypeDefault = false;
                SharedPreferences myPrefs1 = context.getSharedPreferences(context.getPackageName(), 4);
                SharedPreferences.Editor settings1 = myPrefs1.edit();
                settings1.putBoolean(key, value);
                settings1.commit();
                return;
            }
            return;
        }
        SharedPreferences myPrefs12 = context.getSharedPreferences(context.getPackageName(), 4);
        SharedPreferences.Editor settings12 = myPrefs12.edit();
        settings12.putBoolean(key, value);
        settings12.commit();
    }

    public static void setBooleanPref(Context context, int key, boolean value) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor settings = myPrefs.edit();
            settings.putBoolean(context.getString(key), value);
            if (!settings.commit()) {
                _sharedpreferencesTypeDefault = false;
                SharedPreferences myPrefs1 = context.getSharedPreferences(context.getPackageName(), 4);
                SharedPreferences.Editor settings1 = myPrefs1.edit();
                settings1.putBoolean(context.getString(key), value);
                settings1.commit();
                return;
            }
            return;
        }
        SharedPreferences myPrefs12 = context.getSharedPreferences(context.getPackageName(), 4);
        SharedPreferences.Editor settings12 = myPrefs12.edit();
        settings12.putBoolean(context.getString(key), value);
        settings12.commit();
    }

    public static void removePref(Context context, int key) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor settings = myPrefs.edit();
            settings.remove(context.getString(key));
            if (!settings.commit()) {
                _sharedpreferencesTypeDefault = false;
                SharedPreferences myPrefs1 = context.getSharedPreferences(context.getPackageName(), 4);
                SharedPreferences.Editor settings1 = myPrefs1.edit();
                settings1.remove(context.getString(key));
                settings1.commit();
                return;
            }
            return;
        }
        SharedPreferences myPrefs12 = context.getSharedPreferences(context.getPackageName(), 4);
        SharedPreferences.Editor settings12 = myPrefs12.edit();
        settings12.remove(context.getString(key));
        settings12.commit();
    }

    public static void removePref(Context context, String key) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor settings = myPrefs.edit();
            settings.remove(key);
            if (!settings.commit()) {
                _sharedpreferencesTypeDefault = false;
                SharedPreferences myPrefs1 = context.getSharedPreferences(context.getPackageName(), 4);
                SharedPreferences.Editor settings1 = myPrefs1.edit();
                settings1.remove(key);
                settings1.commit();
                return;
            }
            return;
        }
        SharedPreferences myPrefs12 = context.getSharedPreferences(context.getPackageName(), 4);
        SharedPreferences.Editor settings12 = myPrefs12.edit();
        settings12.remove(key);
        settings12.commit();
    }

    public static void setIntPref(Context context, String key, int value) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor settings = myPrefs.edit();
            settings.putInt(key, value);
            if (!settings.commit()) {
                _sharedpreferencesTypeDefault = false;
                SharedPreferences myPrefs1 = context.getSharedPreferences(context.getPackageName(), 4);
                SharedPreferences.Editor settings1 = myPrefs1.edit();
                settings1.putInt(key, value);
                settings1.commit();
                return;
            }
            return;
        }
        SharedPreferences myPrefs12 = context.getSharedPreferences(context.getPackageName(), 4);
        SharedPreferences.Editor settings12 = myPrefs12.edit();
        settings12.putInt(key, value);
        settings12.commit();
    }

    public static void setIntPref(Context context, int key, int value) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor settings = myPrefs.edit();
            settings.putInt(context.getString(key), value);
            if (!settings.commit()) {
                _sharedpreferencesTypeDefault = false;
                SharedPreferences myPrefs1 = context.getSharedPreferences(context.getPackageName(), 4);
                SharedPreferences.Editor settings1 = myPrefs1.edit();
                settings1.putInt(context.getString(key), value);
                settings1.commit();
                return;
            }
            return;
        }
        SharedPreferences myPrefs12 = context.getSharedPreferences(context.getPackageName(), 4);
        SharedPreferences.Editor settings12 = myPrefs12.edit();
        settings12.putInt(context.getString(key), value);
        settings12.commit();
    }

    public static void setLongPref(Context context, String key, long value) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor settings = myPrefs.edit();
            settings.putLong(key, value);
            if (!settings.commit()) {
                _sharedpreferencesTypeDefault = false;
                SharedPreferences myPrefs1 = context.getSharedPreferences(context.getPackageName(), 4);
                SharedPreferences.Editor settings1 = myPrefs1.edit();
                settings1.putLong(key, value);
                settings1.commit();
                return;
            }
            return;
        }
        SharedPreferences myPrefs12 = context.getSharedPreferences(context.getPackageName(), 4);
        SharedPreferences.Editor settings12 = myPrefs12.edit();
        settings12.putLong(key, value);
        settings12.commit();
    }

    public static void setLongPref(Context context, int key, long value) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor settings = myPrefs.edit();
            settings.putLong(context.getString(key), value);
            if (!settings.commit()) {
                _sharedpreferencesTypeDefault = false;
                SharedPreferences myPrefs1 = context.getSharedPreferences(context.getPackageName(), 4);
                SharedPreferences.Editor settings1 = myPrefs1.edit();
                settings1.putLong(context.getString(key), value);
                settings1.commit();
                return;
            }
            return;
        }
        SharedPreferences myPrefs12 = context.getSharedPreferences(context.getPackageName(), 4);
        SharedPreferences.Editor settings12 = myPrefs12.edit();
        settings12.putLong(context.getString(key), value);
        settings12.commit();
    }

    public static String getStringPref(Context context, int key) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            return myPrefs.getString(context.getString(key), "");
        }
        SharedPreferences myPrefs2 = context.getSharedPreferences(context.getPackageName(), 4);
        return myPrefs2.getString(context.getString(key), "");
    }

    public static boolean getBooleanPref(Context context, String key, boolean defualt) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            return myPrefs.getBoolean(key, defualt);
        }
        SharedPreferences myPrefs2 = context.getSharedPreferences(context.getPackageName(), 4);
        return myPrefs2.getBoolean(key, defualt);
    }

    public static String getStringPref(Context context, String key) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            return myPrefs.getString(key, "");
        }
        SharedPreferences myPrefs2 = context.getSharedPreferences(context.getPackageName(), 4);
        return myPrefs2.getString(key, "");
    }

    public static String getStringPref(Context context, int key, String defualtValue) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            return myPrefs.getString(context.getString(key), defualtValue);
        }
        SharedPreferences myPrefs2 = context.getSharedPreferences(context.getPackageName(), 4);
        return myPrefs2.getString(context.getString(key), defualtValue);
    }

    public static String getStringPref(Context context, String key, String defualtValue) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            return myPrefs.getString(key, defualtValue);
        }
        SharedPreferences myPrefs2 = context.getSharedPreferences(context.getPackageName(), 4);
        return myPrefs2.getString(key, defualtValue);
    }

    public static boolean getBooleanPref(Context context, String key) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            return myPrefs.getBoolean(key, false);
        }
        SharedPreferences myPrefs2 = context.getSharedPreferences(context.getPackageName(), 4);
        return myPrefs2.getBoolean(key, false);
    }

    public static boolean getBooleanPref(Context context, int key) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            return myPrefs.getBoolean(context.getString(key), false);
        }
        SharedPreferences myPrefs2 = context.getSharedPreferences(context.getPackageName(), 4);
        return myPrefs2.getBoolean(context.getString(key), false);
    }

    public static boolean getBooleanPref(Context context, int key, boolean defualt) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            return myPrefs.getBoolean(context.getString(key), defualt);
        }
        SharedPreferences myPrefs2 = context.getSharedPreferences(context.getPackageName(), 4);
        return myPrefs2.getBoolean(context.getString(key), defualt);
    }

    public static int getIntPref(Context context, int key) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            return myPrefs.getInt(context.getString(key), -1);
        }
        SharedPreferences myPrefs2 = context.getSharedPreferences(context.getPackageName(), 4);
        return myPrefs2.getInt(context.getString(key), -1);
    }

    public static int getIntPref(Context context, String key, int defualt) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            return myPrefs.getInt(key, defualt);
        }
        SharedPreferences myPrefs2 = context.getSharedPreferences(context.getPackageName(), 4);
        return myPrefs2.getInt(key, defualt);
    }

    public static int getIntPref(Context context, int key, int defualt) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            return myPrefs.getInt(context.getString(key), defualt);
        }
        SharedPreferences myPrefs2 = context.getSharedPreferences(context.getPackageName(), 4);
        return myPrefs2.getInt(context.getString(key), defualt);
    }

    public static long getLongPref(Context context, String key) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            return myPrefs.getLong(key, -1L);
        }
        SharedPreferences myPrefs2 = context.getSharedPreferences(context.getPackageName(), 4);
        return myPrefs2.getLong(key, -1L);
    }

    public static long getLongPref(Context context, int key) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            return myPrefs.getLong(context.getString(key), -1L);
        }
        SharedPreferences myPrefs2 = context.getSharedPreferences(context.getPackageName(), 4);
        return myPrefs2.getLong(context.getString(key), -1L);
    }

    public static long getLongPref(Context context, int key, long defualt) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            return myPrefs.getLong(context.getString(key), defualt);
        }
        SharedPreferences myPrefs2 = context.getSharedPreferences(context.getPackageName(), 4);
        return myPrefs2.getLong(context.getString(key), defualt);
    }

    public static long getLongPref(Context context, String key, long defualt) {
        if (_sharedpreferencesTypeDefault.booleanValue()) {
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            return myPrefs.getLong(key, defualt);
        }
        SharedPreferences myPrefs2 = context.getSharedPreferences(context.getPackageName(), 4);
        return myPrefs2.getLong(key, defualt);
    }
}
