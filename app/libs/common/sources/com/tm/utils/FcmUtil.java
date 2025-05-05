package com.tm.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tm.logger.Log;
import org.jetbrains.annotations.NotNull;
/* loaded from: input.aar:classes.jar:com/tm/utils/FcmUtil.class */
public class FcmUtil {
    private static String TAG = "FcmUtil";
    public static String FCM_TOKEN_KEY = "FCM_TOKEN_KEY";
    public static String install_identifier = "install_identifier";

    /* loaded from: input.aar:classes.jar:com/tm/utils/FcmUtil$FcmTokenCallBack.class */
    public interface FcmTokenCallBack {
        void getFcmTokenCallBack(ApplicationInterface applicationInterface, String str);
    }

    public static void getFcmToken(final ApplicationInterface applicationInterface, final FcmTokenCallBack fcmTokenCallBack, final Context context) {
        Log.d("lior", "start getFcmToken");
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() { // from class: com.tm.utils.FcmUtil.1
            public void onComplete(@NonNull @NotNull Task<String> task) {
                Log.d("lior", "onComplete getFcmToken");
                if (!task.isSuccessful()) {
                    Log.d(FcmUtil.TAG, "getInstanceId failed", task.getException());
                    return;
                }
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putString(FcmUtil.FCM_TOKEN_KEY, (String) task.getResult()).apply();
                Log.d(FcmUtil.TAG, "getInstanceId success");
                fcmTokenCallBack.getFcmTokenCallBack(applicationInterface, (String) task.getResult());
            }
        });
    }

    public static boolean getInstallId(final Context context) {
        String installId = PreferenceManager.getDefaultSharedPreferences(context).getString(install_identifier, "");
        Log.d(TAG, "getInstallId : installId = " + installId);
        if (TextUtils.isEmpty(installId)) {
            FirebaseInstallations.getInstance().getId().addOnCompleteListener(new OnCompleteListener<String>() { // from class: com.tm.utils.FcmUtil.2
                public void onComplete(@NonNull Task<String> task) {
                    if (task.isSuccessful()) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                        editor.putString(FcmUtil.install_identifier, (String) task.getResult()).apply();
                        Log.d("Installations", "Installation ID: " + ((String) task.getResult()));
                        return;
                    }
                    Log.e("Installations", "Unable to get Installation ID");
                }
            });
        }
        return TextUtils.isEmpty(installId);
    }
}
