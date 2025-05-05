package com.tm.authenticatorsdk.socgen.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.tm.authenticatorsdk.R;
import com.tm.authenticatorsdk.socgen.AuthenticatorAPI;
import com.tm.authenticatorsdk.socgen.AuthenticatorSDK;
import com.tm.authenticatorsdk.socgen.MessageEvent;
import com.tm.authenticatorsdk.socgen.NetworkConnectivityListener;
import com.tm.authenticatorsdk.socgen.RetrieveCredentials.RetrieveCredentialsReq;
import com.tm.authenticatorsdk.socgen.RetrieveCredentials.UserResponse;
import com.tm.authenticatorsdk.socgen.SMSIncomingReceiver;
import com.tm.logger.Log;
import com.tm.utils.ApplicationInterface;
import com.tm.utils.Definitions;
import com.tm.utils.GetAppSettingsEvent;
import com.tm.utils.Util;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/UI/ActivationActivity.class */
public class ActivationActivity extends Activity {
    private static final int PROGRESS_DIALOG_KEY = 100;
    private static final int PROGRESS_SEND_AS_VOICE_DIALOG_KEY = 101;
    private static final int SHOW_ALERT_DIALOG = 301;
    private static final int SHOW_PROGRESS_BAR = 302;
    private static final int REMOVE_PROGRESS_BAR = 303;
    public static final int EVENT_DATA_STATE_CHANGED = 42717;
    private static final String TAG = "TMActivationActivity";
    private EditText _editText;
    private SharedPreferences prefs;
    private NetworkConnectivityListener mConnectivityListener;
    Button activateButton;
    Button sendAsVoice;
    private TextView _countDown;
    private View _errorLayout;
    private boolean hasConnection;
    private boolean _usedSendAsVoice;
    private static final String PRESS_CALL_ME_IN_KEY = "PRESS_CALL_ME_IN_KEY";
    private static final String COUNT_DOWN_STARTED_TIME = "COUNT_DOWN_STARTED_TIME";
    private static final String COUNT_DOWN_STARTED = "COUNT_DOWN_STARTED";
    private Handler _uiHandler = new Handler() { // from class: com.tm.authenticatorsdk.socgen.UI.ActivationActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ActivationActivity.SHOW_ALERT_DIALOG /* 301 */:
                    String messageBody = (String) msg.obj;
                    int index = messageBody.indexOf(64);
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivationActivity.this);
                    if (index != -1) {
                        String title = messageBody.substring(0, index);
                        builder.setTitle(title);
                        messageBody = messageBody.substring(index + 1, messageBody.length());
                    }
                    builder.setMessage(messageBody);
                    builder.setPositiveButton(17039379, (DialogInterface.OnClickListener) null);
                    builder.show();
                    return;
                case ActivationActivity.SHOW_PROGRESS_BAR /* 302 */:
                    ActivationActivity.this.showDialog(ActivationActivity.PROGRESS_DIALOG_KEY);
                    return;
                case ActivationActivity.REMOVE_PROGRESS_BAR /* 303 */:
                    ActivationActivity.this.removeDialog(ActivationActivity.PROGRESS_DIALOG_KEY);
                    return;
                case 42717:
                    Log.d(ActivationActivity.TAG, "EVENT_DATA_STATE_CHANGED");
                    ActivationActivity.this.hasConnection = ActivationActivity.isWifiOr3GConnection(ActivationActivity.this.getBaseContext());
                    if (ActivationActivity.this.hasConnection) {
                        ActivationActivity.this._editText.setText("");
                        ActivationActivity.this._errorLayout.setVisibility(4);
                        if (!ActivationActivity.this._usedSendAsVoice) {
                            ActivationActivity.this.sendAsVoice.setEnabled(true);
                            ActivationActivity.this.sendAsVoice.setTextColor(ActivationActivity.this.getResources().getColor(R.color.link_color));
                            return;
                        }
                        ActivationActivity.this.sendAsVoice.setEnabled(false);
                        ActivationActivity.this.sendAsVoice.setTextColor(ActivationActivity.this.getResources().getColor(R.color.ColorPrimaryDark));
                        return;
                    }
                    ActivationActivity.this.activateButton.setEnabled(false);
                    ActivationActivity.this.sendAsVoice.setEnabled(false);
                    ActivationActivity.this._errorLayout.setVisibility(0);
                    ActivationActivity.this.sendAsVoice.setTextColor(ActivationActivity.this.getResources().getColor(R.color.date_status_color));
                    ActivationActivity.this.activateButton.setTextColor(ActivationActivity.this.getResources().getColor(R.color.call_me_color));
                    return;
                default:
                    return;
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/UI/ActivationActivity$PressCallMeIn.class */
    public enum PressCallMeIn {
        TWO_MIN,
        ONE_HOUR,
        DAY
    }

    protected void stripUnderlines(TextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = (URLSpan[]) s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            s.setSpan(new URLSpanNoUnderline(span.getURL()), start, end, 0);
        }
        textView.setText(s);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/UI/ActivationActivity$URLSpanNoUnderline.class */
    public class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }

        @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.hasConnection = isWifiOr3GConnection(getApplicationContext());
        getIntent();
        EventBus.getDefault().register(this);
        setContentView(R.layout.activation_zep);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.sendAsVoice = (Button) findViewById(R.id.SendAsVoiceActivateButton);
        this._errorLayout = findViewById(R.id.errorLayout);
        this._editText = (EditText) findViewById(R.id.insert_activation_code);
        this._countDown = (TextView) findViewById(R.id.call_me_count_down);
        TextWatcher _activationTextWatcher = new TextWatcher() { // from class: com.tm.authenticatorsdk.socgen.UI.ActivationActivity.2
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
                if (s.toString().length() < 4) {
                    ActivationActivity.this.disableActivation();
                } else if (s.toString().length() >= 4 && ActivationActivity.isWifiOr3GConnection(ActivationActivity.this.getBaseContext())) {
                    ActivationActivity.this.enableActivation();
                    ActivationActivity.this._errorLayout.setVisibility(4);
                } else if (!ActivationActivity.isWifiOr3GConnection(ActivationActivity.this.getBaseContext())) {
                    ActivationActivity.this.disableActivation();
                    ActivationActivity.this._errorLayout.setVisibility(0);
                }
            }
        };
        this._editText.addTextChangedListener(_activationTextWatcher);
        this.activateButton = (Button) findViewById(R.id.SignInActivateButton);
        this.activateButton.setOnClickListener(new View.OnClickListener() { // from class: com.tm.authenticatorsdk.socgen.UI.ActivationActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View paramView) {
                ActivationActivity.this._uiHandler.sendEmptyMessage(ActivationActivity.SHOW_PROGRESS_BAR);
                RetrieveCredentialsReq cred = new RetrieveCredentialsReq();
                cred.setOneTimePIN(ActivationActivity.this._editText.getText().toString());
                cred.setRetrieveMode(1);
                cred.setSearchBy(0);
                cred.setValue(PreferenceManager.getDefaultSharedPreferences(ActivationActivity.this).getString("phonenumber", ""));
                Object[] re = {cred.getValue(), Integer.valueOf(cred.getSearchBy()), Integer.valueOf(cred.getRetrieveMode()), cred.getOneTimePIN()};
                ActivationActivity.this.getService().RetrieveCredentials(re).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<UserResponse>>() { // from class: com.tm.authenticatorsdk.socgen.UI.ActivationActivity.3.1
                    public void onCompleted() {
                        Log.d("activation", "done");
                    }

                    public void onError(Throwable e) {
                        Log.d("signup", e.getMessage());
                        ActivationActivity.this.removeDialog(ActivationActivity.PROGRESS_DIALOG_KEY);
                        EventBus.getDefault().post(new MessageEvent(-1, e.getMessage()));
                    }

                    public void onNext(List<UserResponse> userResponses) {
                        Log.d("signup", userResponses.get(0).toString());
                        if (userResponses.get(0).getResultCode() == 0) {
                            Util.handleAppSettings(ActivationActivity.this.getApplication(), userResponses.get(0).getFields()[1].getValue(), userResponses.get(0).getFields()[0].getValue());
                            Util.handleKeepAlive(ActivationActivity.this.getApplication());
                        } else if (userResponses.get(0).getResultCode() == 1) {
                            ActivationActivity.this.removeDialog(ActivationActivity.PROGRESS_DIALOG_KEY);
                            ActivationActivity.this.showAlerDialog("Error", ActivationActivity.this.getString(R.string.invalid_activation_code));
                        } else {
                            ActivationActivity.this.onBackPressed();
                            EventBus.getDefault().post(new MessageEvent(userResponses.get(0).getResultCode(), userResponses.get(0).getResultDescription()));
                        }
                    }
                });
            }
        });
        this._usedSendAsVoice = false;
        this.sendAsVoice.setText(Html.fromHtml(getString(R.string.call_me)));
        this.sendAsVoice.setLinkTextColor(getResources().getColorStateList(R.color.link_color));
        stripUnderlines(this.sendAsVoice);
        this.sendAsVoice.setOnClickListener(new View.OnClickListener() { // from class: com.tm.authenticatorsdk.socgen.UI.ActivationActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View paramView) {
                ActivationActivity.this.startCountDownTimerIfNeed(true);
                ActivationActivity.this.sendDidNotGetSmsEmailToSupport(PreferenceManager.getDefaultSharedPreferences(ActivationActivity.this).getString("phonenumber", ""));
            }
        });
        startCountDownTimerIfNeed(false);
        this.mConnectivityListener = new NetworkConnectivityListener();
        this.mConnectivityListener.registerHandler(this._uiHandler, 42717);
        this.mConnectivityListener.startListening(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AuthenticatorAPI getService() {
        ApplicationInterface applicationInterface = getApplication();
        return (AuthenticatorAPI) applicationInterface.networkProvider().service(Definitions.getBase(), AuthenticatorAPI.class);
    }

    public static boolean isWifiOr3GConnection(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService("connectivity");
        if (Build.VERSION.SDK_INT >= 21) {
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting() && (networkInfo.getType() == 0 || networkInfo.getType() == 1);
        }
        boolean isWifi = manager.getNetworkInfo(1).isConnectedOrConnecting();
        boolean is3g = manager.getNetworkInfo(0).isConnectedOrConnecting();
        return is3g || isWifi;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void disableActivation() {
        this.activateButton.setEnabled(false);
        this._editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_approve_disable, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableActivation() {
        this.activateButton.setEnabled(true);
        this._editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_approve_active, 0);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        this.mConnectivityListener.unregisterHandler(this._uiHandler);
        this.mConnectivityListener.stopListening();
        this.mConnectivityListener = null;
    }

    @Override // android.app.Activity
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case PROGRESS_DIALOG_KEY /* 100 */:
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage(getString(R.string.activating_progress_dialog));
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                return dialog;
            case PROGRESS_SEND_AS_VOICE_DIALOG_KEY /* 101 */:
                ProgressDialog dialog2 = new ProgressDialog(this);
                dialog2.setMessage("wait...");
                dialog2.setIndeterminate(true);
                dialog2.setCancelable(true);
                return dialog2;
            default:
                return null;
        }
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showAlerDialog(String title, String msgStr) {
        String messageBody = "";
        if (!TextUtils.isEmpty(title)) {
            messageBody = title + "@";
        }
        Message msg = new Message();
        msg.what = SHOW_ALERT_DIALOG;
        msg.obj = messageBody + msgStr;
        this._uiHandler.sendMessage(msg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GetAppSettingsEvent event) {
        removeDialog(PROGRESS_DIALOG_KEY);
        if (event.success) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
            PackageManager pm = getPackageManager();
            ComponentName componentName = new ComponentName(getBaseContext(), SMSIncomingReceiver.class);
            pm.setComponentEnabledSetting(componentName, 2, 1);
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

    /* JADX INFO: Access modifiers changed from: private */
    public void startCountDownTimerIfNeed(boolean callMeButtonClickeed) {
        long timeToFinish;
        String callMeInSharedPrefs = this.prefs.getString(PRESS_CALL_ME_IN_KEY, PressCallMeIn.TWO_MIN.toString());
        Long startedTime = Long.valueOf(this.prefs.getLong(COUNT_DOWN_STARTED_TIME, -1L));
        boolean started = this.prefs.getBoolean(COUNT_DOWN_STARTED, false);
        boolean needToStartAutomatically = false;
        switch (PressCallMeIn.valueOf(callMeInSharedPrefs)) {
            case TWO_MIN:
                needToStartAutomatically = true;
                timeToFinish = 120000;
                break;
            case ONE_HOUR:
                needToStartAutomatically = callMeButtonClickeed;
                timeToFinish = 3600000;
                break;
            case DAY:
                needToStartAutomatically = callMeButtonClickeed;
                timeToFinish = 86400000;
                break;
            default:
                timeToFinish = 120000;
                break;
        }
        if (started && startedTime.longValue() > -1 && System.currentTimeMillis() - startedTime.longValue() < timeToFinish) {
            startCountDownTimer(timeToFinish - (System.currentTimeMillis() - startedTime.longValue()), started);
        } else if (!started && needToStartAutomatically) {
            startCountDownTimer(timeToFinish, started);
        } else if (started && startedTime.longValue() > -1 && System.currentTimeMillis() - startedTime.longValue() > timeToFinish) {
            setPressCallMeIn(callMeInSharedPrefs);
            this._countDown.setVisibility(8);
            this.prefs.edit().putBoolean(COUNT_DOWN_STARTED, false).apply();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPressCallMeIn(String callMeInSharedPrefs) {
        switch (PressCallMeIn.valueOf(callMeInSharedPrefs)) {
            case TWO_MIN:
                this.prefs.edit().putString(PRESS_CALL_ME_IN_KEY, PressCallMeIn.ONE_HOUR.toString()).apply();
                return;
            case ONE_HOUR:
                this.prefs.edit().putString(PRESS_CALL_ME_IN_KEY, PressCallMeIn.DAY.toString()).apply();
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendDidNotGetSmsEmailToSupport(String phoneNumber) {
        String subject = getString(R.string.support_please_call_me__subject1, new Object[]{phoneNumber});
        String body = getString(R.string.support_please_call_me_body1);
        Intent email = new Intent("android.intent.action.SENDTO");
        String uriText = "mailto:" + Uri.encode(AuthenticatorSDK.getInstance(this).flavor.getSupport()) + "?subject=" + Uri.encode(subject) + "&body=" + Uri.encode(body);
        Uri uri = Uri.parse(uriText);
        email.setData(uri);
        startActivityForResult(email, 9895);
    }

    /* JADX WARN: Type inference failed for: r0v7, types: [com.tm.authenticatorsdk.socgen.UI.ActivationActivity$5] */
    private void startCountDownTimer(long count, boolean started) {
        if (!started) {
            this.prefs.edit().putLong(COUNT_DOWN_STARTED_TIME, System.currentTimeMillis()).apply();
            this.prefs.edit().putBoolean(COUNT_DOWN_STARTED, true).apply();
        }
        this._usedSendAsVoice = true;
        this._uiHandler.sendEmptyMessage(42717);
        if (this._countDown != null) {
            new CountDownTimer(count, 1000L) { // from class: com.tm.authenticatorsdk.socgen.UI.ActivationActivity.5
                @Override // android.os.CountDownTimer
                public void onTick(long millisUntilFinished) {
                    ActivationActivity.this._countDown.setVisibility(0);
                    ActivationActivity.this._countDown.setText(String.format("%02d:%02d:%02d", Long.valueOf(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))));
                }

                @Override // android.os.CountDownTimer
                public void onFinish() {
                    ActivationActivity.this._usedSendAsVoice = false;
                    ActivationActivity.this._uiHandler.sendEmptyMessage(42717);
                    ActivationActivity.this.prefs.edit().putBoolean(ActivationActivity.COUNT_DOWN_STARTED, false).apply();
                    String callMeInSharedPrefs = ActivationActivity.this.prefs.getString(ActivationActivity.PRESS_CALL_ME_IN_KEY, PressCallMeIn.TWO_MIN.toString());
                    ActivationActivity.this.setPressCallMeIn(callMeInSharedPrefs);
                }
            }.start();
        }
    }
}
