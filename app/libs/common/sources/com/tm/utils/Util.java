package com.tm.utils;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.PersistableBundle;
import androidx.annotation.RequiresApi;
import androidx.core.app.JobIntentService;
import com.tm.logger.Log;
import java.util.Map;
/* loaded from: input.aar:classes.jar:com/tm/utils/Util.class */
public class Util {
    @RequiresApi(api = 21)
    public static void handleAppSettings(Application app, String username, String password) {
        UtilsInterface utilsInterface = getUtilsInterface(app);
        if (utilsInterface != null) {
            Intent service = new Intent(app, utilsInterface.getWorkerIntentClass());
            service.putExtra(Definitions.adUserName, username);
            service.putExtra(Definitions.adPassword, password);
            PersistableBundle bundle = new PersistableBundle();
            bundle.putString(Definitions.adUserName, username);
            bundle.putString(Definitions.adPassword, password);
            ComponentName serviceComponent = new ComponentName(app.getBaseContext(), utilsInterface.getWorkerIntentClass());
            JobInfo.Builder builder = new JobInfo.Builder(999, serviceComponent);
            builder.setExtras(bundle);
            builder.setMinimumLatency(1000L);
            builder.setOverrideDeadline(Definitions.ipRetryInterval);
            builder.setRequiredNetworkType(1);
            JobScheduler jobScheduler = (JobScheduler) app.getBaseContext().getSystemService(JobScheduler.class);
            jobScheduler.schedule(builder.build());
        }
    }

    public static void handleKeepAlive(Application app) {
        UtilsInterface utilsInterface = getUtilsInterface(app);
        if (utilsInterface != null) {
            Intent service = new Intent(app, utilsInterface.getKeepAliveIntentClass());
            app.startService(service);
            JobIntentService.enqueueWork(app, utilsInterface.getKeepAliveIntentClass(), 2009, service);
        }
    }

    public static Class getMainActivityClass(Application app) {
        UtilsInterface utilsInterface = getUtilsInterface(app);
        if (utilsInterface != null) {
            return utilsInterface.getMainActivityClass();
        }
        return null;
    }

    public static void signOut(Application app) {
        UtilsInterface utilsInterface = getUtilsInterface(app);
        if (utilsInterface != null) {
            utilsInterface.signOut(app);
        }
    }

    public static String getBitRate(Application app) {
        UtilsInterface utilsInterface = getUtilsInterface(app);
        if (utilsInterface != null) {
            return utilsInterface.getBitRate();
        }
        return null;
    }

    public static boolean isPlayVersion(Application app) {
        UtilsInterface utilsInterface = getUtilsInterface(app);
        if (utilsInterface != null) {
            return utilsInterface.isPlayVersion();
        }
        return true;
    }

    public static boolean isDefaultRecordingApp(Application app) {
        UtilsInterface utilsInterface = getUtilsInterface(app);
        if (utilsInterface != null) {
            return utilsInterface.isDefaultRecordingApp(app);
        }
        return true;
    }

    public static boolean supportSettings(Application app) {
        UtilsInterface utilsInterface = getUtilsInterface(app);
        if (utilsInterface != null) {
            return utilsInterface.supportSettings();
        }
        return true;
    }

    public static boolean supportGetAppSettings(Application app) {
        UtilsInterface utilsInterface = getUtilsInterface(app);
        if (utilsInterface != null) {
            return utilsInterface.supportGetAppSettings();
        }
        return true;
    }

    private static UtilsInterface getUtilsInterface(Application app) {
        if (app instanceof ApplicationInterface) {
            ApplicationInterface a = (ApplicationInterface) app;
            return a.getInterface();
        }
        return null;
    }

    public static boolean isSelfSignup(Application app) {
        UtilsInterface utilsInterface = getUtilsInterface(app);
        if (utilsInterface != null) {
            return utilsInterface.isSelfSignup();
        }
        return false;
    }

    public static Map<String, String> getMdmSettings(Application app) {
        UtilsInterface utilsInterface = getUtilsInterface(app);
        if (utilsInterface != null) {
            return utilsInterface.getMdmSettings(app.getBaseContext());
        }
        return null;
    }

    public static String encodeHexString(Application app, byte[] content) {
        UtilsInterface utilsInterface = getUtilsInterface(app);
        if (utilsInterface != null) {
            return utilsInterface.encodeHexString(content);
        }
        return null;
    }

    public static boolean isSocgenVersion(Application app) {
        UtilsInterface utilsInterface = getUtilsInterface(app);
        if (utilsInterface != null) {
            return utilsInterface.isSocgenVersion();
        }
        return false;
    }

    public static void updateMdmPhoneNumberIfNeed(Application app, String username, String password) {
        UtilsInterface utilsInterface = getUtilsInterface(app);
        if (utilsInterface != null) {
            utilsInterface.updateMdmPhoneNumberIfNeed(app.getBaseContext(), username, password);
        }
    }

    public static void updateMdmFirstNameLastNameIfNeed(Application app, String username, String password) {
        UtilsInterface utilsInterface = getUtilsInterface(app);
        if (utilsInterface != null) {
            utilsInterface.updateMdmFirstNameLastNameIfNeed(app.getBaseContext(), username, password);
        }
    }

    public static void initPushFcmListener(Application app) {
        UtilsInterface utilsInterface = getUtilsInterface(app);
        if (utilsInterface != null) {
            utilsInterface.initPushFcmListener(app.getBaseContext());
        }
    }

    public static void startBattery(Application app) {
        Log.d("Util", "startBattery");
        UtilsInterface utilsInterface = getUtilsInterface(app);
        if (utilsInterface != null) {
            utilsInterface.startBattery(app.getBaseContext());
        }
    }
}
