package com.tm.authenticatorsdk.socgen;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.tm.authenticatorsdk.socgen.UI.SelfSignupActivity;
import com.tm.authenticatorsdk.socgen.UI.SignupActivity;
import com.tm.logger.Log;
import com.tm.utils.Util;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/AuthenticatorSDK.class */
public class AuthenticatorSDK {
    static AuthenticatorSDK mInstance;
    private Context mContext;
    private AuthenticationListener mListener;
    private boolean supportSelfSignIn = false;
    public FLAVOR_SETTINGS flavor = FLAVOR_SETTINGS.DEFAULT;

    public static AuthenticatorSDK getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AuthenticatorSDK();
            mInstance.mContext = context;
        }
        return mInstance;
    }

    public static AuthenticatorSDK getInstance(Context context, String flavorName) {
        AuthenticatorSDK ret = getInstance(context);
        if (TextUtils.isEmpty(flavorName)) {
            ret.flavor = FLAVOR_SETTINGS.DEFAULT;
        } else if (flavorName.equalsIgnoreCase("Android archiver")) {
            ret.flavor = FLAVOR_SETTINGS.AndroidArchiver;
        } else if (flavorName.toLowerCase().contains("capture mobile android")) {
            ret.flavor = FLAVOR_SETTINGS.SmarshArvhicer;
        }
        return ret;
    }

    public void init(AuthenticationListener listener) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        this.mListener = listener;
    }

    public void show(String registerName, String token, AuthenticationListener listener) {
        this.supportSelfSignIn = true;
        this.mListener = listener;
    }

    public void show(AuthenticationListener listener) {
        Log.d("AuthenticatorSDK", "show started");
        this.supportSelfSignIn = false;
        this.mListener = listener;
        if (Util.isSelfSignup((Application) this.mContext.getApplicationContext())) {
            this.mContext.startActivity(new Intent(this.mContext, SelfSignupActivity.class));
        } else {
            this.mContext.startActivity(new Intent(this.mContext, SignupActivity.class));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {
        if (this.mListener != null) {
            if (event.resultcode == 0) {
                this.mListener.onSuccess(event.username, event.password);
            } else {
                this.mListener.onFailure(event.resultcode, event.resultdescription);
            }
        }
    }

    /* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/AuthenticatorSDK$FLAVOR_SETTINGS.class */
    public enum FLAVOR_SETTINGS {
        AndroidArchiver("Android Archiver", "911", "600701", "support@telemessage.com"),
        SmarshArvhicer("Capture Mobile Android", "533", "600161", "CaptureMobileSupport@Smarsh.com"),
        DEFAULT("Android Archiver", "911", "600701", "support@telemessage.com");
        
        private final String appName;
        private final String userType;
        private final String appId;
        private final String support;

        FLAVOR_SETTINGS(String appName, String userType, String appId, String support) {
            this.appName = appName;
            this.userType = userType;
            this.appId = appId;
            this.support = support;
        }

        public String getAppName() {
            return this.appName;
        }

        public String getUserType() {
            return this.userType;
        }

        public String getAppId() {
            return this.appId;
        }

        public String getSupport() {
            return this.support;
        }
    }
}
