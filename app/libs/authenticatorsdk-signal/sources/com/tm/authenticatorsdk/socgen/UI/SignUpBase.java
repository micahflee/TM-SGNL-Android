package com.tm.authenticatorsdk.socgen.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import com.tm.authenticatorsdk.BuildConfig;
import com.tm.authenticatorsdk.R;
import com.tm.authenticatorsdk.socgen.AuthenticatorSDK;
import com.tm.authenticatorsdk.socgen.MessageEvent;
import com.tm.authenticatorsdk.socgen.NetworkConnectivityListener;
import com.tm.authenticatorsdk.socgen.PinSmsHandler;
import com.tm.authenticatorsdk.socgen.SMSIncomingReceiver;
import com.tm.logger.Log;
import com.tm.utils.Definitions;
import com.tm.utils.GetAppSettingsEvent;
import com.tm.utils.Util;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import rx.Subscription;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/UI/SignUpBase.class */
public abstract class SignUpBase extends Activity {
    public PinSmsHandler pinSmsHandler;
    public Subscription mSub;
    public static final int EVENT_DATA_STATE_CHANGED = 42717;
    protected static final String TAG = "SignInActivity";
    protected static final int PROGRESS_DIALOG_KEY = 100;
    protected static final int PROGRESS_SYNC_DIALOG_KEY = 101;
    protected static final int SHOW_ALERT_DIALOG = 301;
    protected static final int SHOW_PROGRESS_BAR = 302;
    protected static final int REMOVE_PROGRESS_BAR = 303;
    protected static final int SHOW_ACTIVATION_DISCLAIMER = 306;
    protected static final int HANDLE_TIMEOUT = 307;
    protected static final int SHOW_SYNC_PROGRESS_BAR = 309;
    protected static final int TIME_OUT_IN_MILLIS = 20000;
    protected static final int NO_TIME_OUT = 500;
    public static final String TIMEOUT = "timeout";
    public static final String AUTHORITY = "com.android.contacts";
    public static final long SECONDS_PER_MINUTE = 60;
    public static final long SYNC_INTERVAL_IN_MINUTES = 180;
    public static final long SYNC_INTERVAL = 10800;
    public static final int NOT_ALLOWED_TO_USE_APP = 41;
    public static final int NON_SPRINT_NUMBER = 56;
    public static final int SECURE_USER_NOT_ALLOWED = 57;
    private static final int MY_PERMISSIONS_REQUEST = 1;
    boolean isDebuggable;
    protected SharedPreferences _prefs;
    protected AlertDialog levelDialog;
    protected boolean _isSharedPrefListenerRegistered;
    protected SharedPreferences.OnSharedPreferenceChangeListener _sharedPreferenceListener;
    protected String chosenUrl;
    protected String keeperUrl;
    protected NetworkConnectivityListener mConnectivityListener;
    protected EditText _userNameET;
    protected View _errorLayout;
    protected TextView sendLogs;
    protected BroadcastReceiver _contactSyncReceiver = null;
    protected Handler timeOutHandler = null;
    protected ProgressDialog _pd = null;
    protected Handler _uiHandler = new Handler() { // from class: com.tm.authenticatorsdk.socgen.UI.SignUpBase.3
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SignUpBase.SHOW_ALERT_DIALOG /* 301 */:
                    String messageBody = (String) msg.obj;
                    int index = messageBody.indexOf(64);
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpBase.this);
                    if (index != -1) {
                        String title = messageBody.substring(0, index);
                        builder.setTitle(title);
                        messageBody = messageBody.substring(index + SignUpBase.MY_PERMISSIONS_REQUEST, messageBody.length());
                    }
                    builder.setMessage(messageBody);
                    builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) null);
                    builder.show();
                    return;
                case SignUpBase.SHOW_PROGRESS_BAR /* 302 */:
                    if (!SignUpBase.this.isFinishing()) {
                        SignUpBase.this.showDialog(SignUpBase.PROGRESS_DIALOG_KEY);
                        return;
                    }
                    return;
                case SignUpBase.REMOVE_PROGRESS_BAR /* 303 */:
                    SignUpBase.this.removeDialog(SignUpBase.PROGRESS_DIALOG_KEY);
                    SignUpBase.this._pd = null;
                    return;
                case SignUpBase.HANDLE_TIMEOUT /* 307 */:
                    SignUpBase.this.removeDialog(SignUpBase.PROGRESS_DIALOG_KEY);
                    return;
                case 42717:
                    Log.d(SignUpBase.TAG, "EVENT_DATA_STATE_CHANGED");
                    boolean inConnection = SignUpBase.isWifiOr3GConnection(SignUpBase.this.getBaseContext());
                    if (!inConnection) {
                        SignUpBase.this.disableButton();
                        SignUpBase.this._errorLayout.setVisibility(0);
                        return;
                    }
                    if (SignUpBase.this._userNameET.getText().length() > 0) {
                        SignUpBase.this.enableButton();
                    }
                    SignUpBase.this._errorLayout.setVisibility(4);
                    return;
                default:
                    return;
            }
        }
    };

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.isDebuggable = 0 != (getApplicationInfo().flags & 2);
        EventBus.getDefault().register(this);
        this._prefs = PreferenceManager.getDefaultSharedPreferences(this);
        this._isSharedPrefListenerRegistered = false;
        this._sharedPreferenceListener = new SharedPreferences.OnSharedPreferenceChangeListener() { // from class: com.tm.authenticatorsdk.socgen.UI.SignUpBase.1
            @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                Log.d(SignUpBase.TAG, "SHARED PREFERENCES LISTENER !!!");
            }
        };
        this.mConnectivityListener = new NetworkConnectivityListener();
        this.mConnectivityListener.registerHandler(this._uiHandler, 42717);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void stripUnderlines(TextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = (URLSpan[]) s.getSpans(0, s.length(), URLSpan.class);
        int length = spans.length;
        for (int i = 0; i < length; i += MY_PERMISSIONS_REQUEST) {
            URLSpan span = spans[i];
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            s.setSpan(new URLSpanNoUnderline(span.getURL()), start, end, 0);
        }
        textView.setText(s);
    }

    /* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/UI/SignUpBase$URLSpanNoUnderline.class */
    protected class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }

        @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean handlePermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            int readSmsPermission = ActivityCompat.checkSelfPermission(this, "android.permission.READ_SMS");
            int readPhoneStatePermission = ActivityCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE");
            int receiveSmsPermission = ActivityCompat.checkSelfPermission(this, "android.permission.RECEIVE_SMS");
            int readCallLogsPermission = ActivityCompat.checkSelfPermission(this, "android.permission.READ_CALL_LOG");
            List<String> listPermissionsNeeded = new ArrayList<>();
            if (readSmsPermission != 0) {
                listPermissionsNeeded.add("android.permission.READ_SMS");
            }
            if (readCallLogsPermission != 0) {
                listPermissionsNeeded.add("android.permission.READ_CALL_LOG");
            }
            if (readPhoneStatePermission != 0) {
                listPermissionsNeeded.add("android.permission.READ_PHONE_STATE");
            }
            if (!Util.isPlayVersion(getApplication()) && receiveSmsPermission != 0) {
                listPermissionsNeeded.add("android.permission.RECEIVE_SMS");
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, (String[]) listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), (int) MY_PERMISSIONS_REQUEST);
                return false;
            }
            return true;
        }
        return true;
    }

    public void readDevicePhoneNumber() {
    }

    @Override // android.app.Activity
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST /* 1 */:
                Map<String, Integer> permissionsMap = new HashMap<>();
                permissionsMap.put("android.permission.READ_SMS", 0);
                permissionsMap.put("android.permission.READ_PHONE_STATE", 0);
                permissionsMap.put("android.permission.RECEIVE_SMS", 0);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i += MY_PERMISSIONS_REQUEST) {
                        permissionsMap.put(permissions[i], Integer.valueOf(grantResults[i]));
                    }
                    if (permissionsMap.get("android.permission.READ_SMS").intValue() == 0 && permissionsMap.get("android.permission.READ_PHONE_STATE").intValue() == 0) {
                        readDevicePhoneNumber();
                    } else {
                        handleContactsPermissionDeny();
                    }
                    if (permissionsMap.get("android.permission.READ_SMS").intValue() != 0) {
                        handleSmsPermissionDeny();
                        return;
                    }
                    return;
                }
                return;
            default:
                return;
        }
    }

    private void handleContactsPermissionGrant() {
    }

    private void handleContactsPermissionDeny() {
    }

    private void handleSmsPermissionDeny() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        this.mConnectivityListener.unregisterHandler(this._uiHandler);
        this.mConnectivityListener = null;
        if (this._contactSyncReceiver != null) {
            unregisterReceiver(this._contactSyncReceiver);
            this._contactSyncReceiver = null;
        }
    }

    protected void signInFromPopUp() {
        Definitions.setBaseUrl(this.chosenUrl);
        SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(this).edit();
        if (this.keeperUrl != null) {
            ed.putString("keeperUrl", this.keeperUrl);
        }
        if (!Definitions.getBase().contentEquals("https://rest.telemessage.com")) {
            ed.putString("baseurl", this.chosenUrl);
        }
        ed.commit();
        if (this.isDebuggable) {
            Toast.makeText(getBaseContext(), "url set to: " + this.chosenUrl, (int) MY_PERMISSIONS_REQUEST).show();
        }
        if (this.levelDialog != null && this.levelDialog.isShowing()) {
            this.levelDialog.dismiss();
        }
        this.chosenUrl = null;
        performSignUp();
    }

    protected void disableButton() {
    }

    protected void enableButton() {
    }

    public void performSignUp() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void prepareSignUp() {
        if (!this.isDebuggable || !Definitions.getBase().contentEquals("https://rest.telemessage.com")) {
            this.chosenUrl = "https://rest.telemessage.com";
            this.keeperUrl = Definitions.ArchiveUrl;
            signInFromPopUp();
        } else if (this.isDebuggable) {
            CharSequence[] items = {" Production ", "Production Keeper", " QA ", " QA Keeper", "Integration", "CRND", "CRND Keeper", "Integration Socgen", " Custom "};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select server");
            builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() { // from class: com.tm.authenticatorsdk.socgen.UI.SignUpBase.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int item) {
                    switch (item) {
                        case BuildConfig.DEBUG /* 0 */:
                            SignUpBase.this.chosenUrl = "https://rest.telemessage.com";
                            SignUpBase.this.keeperUrl = Definitions.ArchiveUrl;
                            SignUpBase.this.signInFromPopUp();
                            return;
                        case SignUpBase.MY_PERMISSIONS_REQUEST /* 1 */:
                            SignUpBase.this.chosenUrl = "https://rest.telemessage.com";
                            SignUpBase.this.keeperUrl = Definitions.ArchiveUrl;
                            SignUpBase.this.signInFromPopUp();
                            return;
                        case 2:
                            SignUpBase.this.chosenUrl = "https://qa.telemessage.com";
                            SignUpBase.this.keeperUrl = "https://api-gateway-qacharlie.devops.telemessage.co.il";
                            SignUpBase.this.signInFromPopUp();
                            return;
                        case 3:
                            SignUpBase.this.chosenUrl = "https://qa.telemessage.com";
                            SignUpBase.this.keeperUrl = "https://api-gateway-qacharlie.devops.telemessage.co.il";
                            SignUpBase.this.signInFromPopUp();
                            return;
                        case 4:
                            SignUpBase.this.chosenUrl = "https://integration.telemessage.co.il";
                            SignUpBase.this.keeperUrl = "https://api-gateway-integration.devops.telemessage.co.il";
                            SignUpBase.this.signInFromPopUp();
                            return;
                        case 5:
                            SignUpBase.this.chosenUrl = "https://rnd.telemessage.co.il";
                            SignUpBase.this.signInFromPopUp();
                            return;
                        case 6:
                            SignUpBase.this.chosenUrl = "https://rnd.telemessage.co.il";
                            SignUpBase.this.keeperUrl = "https://api-gateway-crnd.devops.telemessage.co.il";
                            SignUpBase.this.signInFromPopUp();
                            return;
                        case 7:
                            SignUpBase.this.chosenUrl = "http://integration.telemessage.co.il";
                            SignUpBase.this.keeperUrl = "https://api-gateway-integration.devops.telemessage.co.il";
                            SignUpBase.this.signInFromPopUp();
                            return;
                        case 8:
                            AlertDialog.Builder alert = new AlertDialog.Builder(SignUpBase.this);
                            alert.setTitle("Custom Url");
                            alert.setMessage("Type your Url");
                            final EditText input = new EditText(SignUpBase.this);
                            alert.setView(input);
                            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() { // from class: com.tm.authenticatorsdk.socgen.UI.SignUpBase.2.1
                                @Override // android.content.DialogInterface.OnClickListener
                                public void onClick(DialogInterface dialog2, int whichButton) {
                                    String value = input.getText().toString();
                                    SignUpBase.this.chosenUrl = value;
                                    SignUpBase.this.signInFromPopUp();
                                }
                            });
                            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // from class: com.tm.authenticatorsdk.socgen.UI.SignUpBase.2.2
                                @Override // android.content.DialogInterface.OnClickListener
                                public void onClick(DialogInterface dialog2, int whichButton) {
                                }
                            });
                            alert.show();
                            return;
                        default:
                            return;
                    }
                }
            });
            this.levelDialog = builder.create();
            this.levelDialog.show();
        }
    }

    protected String getPhoneNumber() {
        return "";
    }

    protected void startSignIn() {
        String phoneNumber = getPhoneNumber();
        if (TextUtils.isEmpty(phoneNumber)) {
            return;
        }
        int len = phoneNumber.length();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i += MY_PERMISSIONS_REQUEST) {
            char c = phoneNumber.charAt(i);
            if (Character.isDigit(c)) {
                builder.append(c);
            }
        }
        String str = "+" + builder.toString();
    }

    protected void showAlertDialog(String title, String msgStr) {
        String messageBody = "";
        if (!TextUtils.isEmpty(title)) {
            messageBody = title + "@";
        }
        Message msg = new Message();
        msg.what = SHOW_ALERT_DIALOG;
        msg.obj = messageBody + msgStr;
        this._uiHandler.sendMessage(msg);
    }

    @Override // android.app.Activity
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case PROGRESS_DIALOG_KEY /* 100 */:
                if (this._pd == null) {
                    this._pd = new ProgressDialog(this);
                }
                this._pd.setMessage(getString(R.string.sign_in_progress_dialog));
                this._pd.setIndeterminate(true);
                this._pd.setCancelable(false);
                return this._pd;
            case PROGRESS_SYNC_DIALOG_KEY /* 101 */:
            case SHOW_ACTIVATION_DISCLAIMER /* 306 */:
            default:
                return null;
        }
    }

    @Override // android.app.Activity
    public void onResume() {
        super.onResume();
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("activated_aa", false)) {
            finish();
        }
        this.mConnectivityListener.startListening(this);
        Definitions.setBaseUrl("https://rest.telemessage.com");
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        this.mConnectivityListener.stopListening();
    }

    public static boolean isWifiOr3GConnection(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService("connectivity");
        if (Build.VERSION.SDK_INT >= 21) {
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting() && (networkInfo.getType() == 0 || networkInfo.getType() == MY_PERMISSIONS_REQUEST);
        }
        boolean isWifi = manager.getNetworkInfo(MY_PERMISSIONS_REQUEST).isConnectedOrConnecting();
        boolean is3g = manager.getNetworkInfo(0).isConnectedOrConnecting();
        return is3g || isWifi;
    }

    @Override // android.app.Activity
    public void onPause() {
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(GetAppSettingsEvent event) {
        this._uiHandler.sendEmptyMessage(REMOVE_PROGRESS_BAR);
        if (event.success) {
            PackageManager pm = getPackageManager();
            ComponentName componentName = new ComponentName(getBaseContext(), SMSIncomingReceiver.class);
            pm.setComponentEnabledSetting(componentName, 2, MY_PERMISSIONS_REQUEST);
            SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
            ed.putString("username", event.username);
            ed.putString("password", event.password);
            ed.commit();
            MessageEvent event1 = new MessageEvent(0, null, event.username, event.password);
            EventBus.getDefault().post(event1);
            finish();
            return;
        }
        EventBus.getDefault().post(new MessageEvent(-1, "failed to get app settings"));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Type inference failed for: r0v7, types: [com.tm.authenticatorsdk.socgen.UI.SignUpBase$4] */
    public void sendLogs() {
        String storageState = Environment.getExternalStorageState();
        if ("shared".equals(storageState)) {
            Toast.makeText(this, R.string.please_unmount, (int) MY_PERMISSIONS_REQUEST).show();
            return;
        }
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.progress_dialog_msg));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        new AsyncTask<Void, Void, Void>() { // from class: com.tm.authenticatorsdk.socgen.UI.SignUpBase.4
            @Override // android.os.AsyncTask
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.show();
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Void doInBackground(Void... args) {
                Intent email = new Intent("android.intent.action.SEND");
                if (AuthenticatorSDK.getInstance(SignUpBase.this).flavor == AuthenticatorSDK.FLAVOR_SETTINGS.SmarshArvhicer) {
                    email.putExtra("android.intent.extra.EMAIL", new String[]{SignUpBase.this.getString(R.string.support_email_smarsh)});
                    email.putExtra("android.intent.extra.SUBJECT", SignUpBase.this.getString(R.string.contact_us_email_subject_samrsh));
                } else {
                    email.putExtra("android.intent.extra.EMAIL", new String[]{SignUpBase.this.getString(R.string.support_email)});
                    email.putExtra("android.intent.extra.SUBJECT", SignUpBase.this.getString(R.string.contact_us_email_subject));
                }
                email.setType("message/rfc822");
                File zipFile = (File) Log.getAttachmentsLog("log.zip", (String) null).get("zip");
                try {
                    Uri fileUri = FileProvider.getUriForFile(SignUpBase.this, SignUpBase.this.getPackageName() + ".authenticatorsdk.myfileprovider", zipFile);
                    List<ResolveInfo> resInfoList = SignUpBase.this.getPackageManager().queryIntentActivities(email, 65536);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        SignUpBase.this.grantUriPermission(packageName, fileUri, 3);
                    }
                    email.addFlags(SignUpBase.MY_PERMISSIONS_REQUEST);
                    email.putExtra("android.intent.extra.STREAM", fileUri);
                } catch (Exception e) {
                    Log.e(SignUpBase.TAG, "logs logs logs", e);
                }
                SignUpBase.this.startActivityForResult(Intent.createChooser(email, "Send Mail"), 22);
                return null;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Void aVoid) {
                super.onPostExecute((AnonymousClass4) aVoid);
                dialog.dismiss();
            }

            @Override // android.os.AsyncTask
            protected void onCancelled() {
                super.onCancelled();
                dialog.dismiss();
                Toast.makeText(SignUpBase.this, R.string.please_unmount, (int) SignUpBase.MY_PERMISSIONS_REQUEST).show();
            }
        }.execute(new Void[0]);
    }
}
