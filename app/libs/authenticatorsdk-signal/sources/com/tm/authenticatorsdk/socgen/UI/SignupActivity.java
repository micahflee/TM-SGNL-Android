package com.tm.authenticatorsdk.socgen.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import com.github.underscore.lodash.$;
import com.tm.INetworkProvider;
import com.tm.authenticatorsdk.AppSignatureHelper;
import com.tm.authenticatorsdk.R;
import com.tm.authenticatorsdk.socgen.AuthenticatorAPI;
import com.tm.authenticatorsdk.socgen.AuthenticatorSDK;
import com.tm.authenticatorsdk.socgen.ErrorEvent;
import com.tm.authenticatorsdk.socgen.IReadPinFromSmsInterface;
import com.tm.authenticatorsdk.socgen.MessageEvent;
import com.tm.authenticatorsdk.socgen.NetworkConnectivityListener;
import com.tm.authenticatorsdk.socgen.PinCodeEvent;
import com.tm.authenticatorsdk.socgen.PinSmsHandler;
import com.tm.authenticatorsdk.socgen.RetrieveCredentials.RetrieveCredentialsReq;
import com.tm.authenticatorsdk.socgen.RetrieveCredentials.UserResponse;
import com.tm.authenticatorsdk.socgen.RetrieveOneTimePin.Response;
import com.tm.authenticatorsdk.socgen.RetrieveOneTimePin.RetrieveOneTimePINExtReq;
import com.tm.authenticatorsdk.socgen.signup.SignupRequest;
import com.tm.authenticatorsdk.socgen.signup.SignupResponse;
import com.tm.authenticatorsdk.socgen.signup.UserFields;
import com.tm.logger.Log;
import com.tm.utils.Definitions;
import com.tm.utils.Util;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.net.ssl.SSLException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/UI/SignupActivity.class */
public class SignupActivity extends SignUpBase implements IReadPinFromSmsInterface {
    protected EditText _countryCodeET;
    protected Button activateButton;
    protected TextView appNameView;
    TextWatcher _userNameTextWatcher = new TextWatcher() { // from class: com.tm.authenticatorsdk.socgen.UI.SignupActivity.6
        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable s) {
            if (s.toString().length() == 0) {
                SignupActivity.this.disableButton();
            } else if (s.toString().length() > 0) {
                SignupActivity.this.enableButton();
                SignupActivity.this._errorLayout.setVisibility(4);
            } else if (s.toString().length() > 0) {
                SignupActivity.this.disableButton();
                SignupActivity.this._errorLayout.setVisibility(0);
            }
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    @Override // com.tm.authenticatorsdk.socgen.UI.SignUpBase, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.isDebuggable = 0 != (getApplicationInfo().flags & 2);
        setContentView(R.layout.sign_in_activity_zep);
        this._errorLayout = findViewById(R.id.errorLayout);
        this.sendLogs = (TextView) findViewById(R.id.sendLogs);
        this.sendLogs.setOnClickListener(new View.OnClickListener() { // from class: com.tm.authenticatorsdk.socgen.UI.SignupActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SignupActivity.this.sendLogs();
            }
        });
        this._userNameET = (EditText) findViewById(R.id.UserNameET);
        this._userNameET.requestFocus();
        this._countryCodeET = (EditText) findViewById(R.id.CountryCodeET);
        this._countryCodeET.setEnabled(true);
        this.appNameView = (TextView) findViewById(R.id.android_archiver);
        this.appNameView.setText(AuthenticatorSDK.getInstance(this).flavor.getAppName());
        if ("" != 0) {
            this._countryCodeET.setText("");
        }
        this._userNameET.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.tm.authenticatorsdk.socgen.UI.SignupActivity.2
            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == 6) {
                    if (SignupActivity.this._userNameET.getText().length() != 0) {
                        SignupActivity.this.prepareSignUp();
                        return true;
                    }
                    return true;
                }
                return false;
            }
        });
        TextView termsAndConditions = (TextView) findViewById(R.id.Terms_and_Policy);
        TextView howSignup = (TextView) findViewById(R.id.howsignup);
        if (AuthenticatorSDK.getInstance(this).flavor == AuthenticatorSDK.FLAVOR_SETTINGS.DEFAULT || AuthenticatorSDK.getInstance(this).flavor == AuthenticatorSDK.FLAVOR_SETTINGS.AndroidArchiver) {
            termsAndConditions.setText(Html.fromHtml(getString(R.string.validating_mobile_number)));
            howSignup.setText(Html.fromHtml(getString(R.string.how_do_i_register)));
        } else {
            termsAndConditions.setText(Html.fromHtml(getString(R.string.validating_mobile_number_smarsh)));
            howSignup.setText(Html.fromHtml(getString(R.string.how_do_i_register_smarsh)));
        }
        termsAndConditions.setLinkTextColor(getResources().getColorStateList(R.color.link_color));
        termsAndConditions.setMovementMethod(LinkMovementMethod.getInstance());
        stripUnderlines(termsAndConditions);
        howSignup.setText(Html.fromHtml(getString(R.string.how_do_i_register)));
        howSignup.setLinkTextColor(getResources().getColorStateList(R.color.link_color));
        howSignup.setMovementMethod(LinkMovementMethod.getInstance());
        stripUnderlines(howSignup);
        this.activateButton = (Button) findViewById(R.id.SignUnButton);
        if (this._userNameET.getText().length() == 0) {
            disableButton();
        }
        this.activateButton.setOnClickListener(new View.OnClickListener() { // from class: com.tm.authenticatorsdk.socgen.UI.SignupActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View paramView) {
                SignupActivity.this.prepareSignUp();
            }
        });
        this._userNameET.addTextChangedListener(this._userNameTextWatcher);
        this.mConnectivityListener = new NetworkConnectivityListener();
        this.mConnectivityListener.registerHandler(this._uiHandler, 42717);
        this.mConnectivityListener.startListening(this);
        if (!Util.isPlayVersion(getApplication())) {
            handlePermissions();
        }
        readDevicePhoneNumber();
    }

    @Override // com.tm.authenticatorsdk.socgen.UI.SignUpBase
    public void readDevicePhoneNumber() {
        String line1Number = null;
        if (ActivityCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") == 0) {
            TelephonyManager telMgr = (TelephonyManager) getSystemService("phone");
            try {
                line1Number = telMgr.getLine1Number();
            } catch (Exception e) {
                Log.d("SignInActivity", " no permission", e);
            }
        }
        String phoneNumber = line1Number == null ? "" : line1Number;
        String countryCode = getCountryCode(this);
        if (!TextUtils.isEmpty(phoneNumber)) {
            if (phoneNumber.startsWith("+")) {
                phoneNumber = phoneNumber.substring(countryCode.length() + 1);
            } else if (phoneNumber.startsWith(countryCode)) {
                phoneNumber = phoneNumber.substring(countryCode.length());
            }
        }
        if (countryCode != null) {
            SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(this).edit();
            ed.putString("country", countryCode);
            ed.commit();
        }
        if (countryCode != null) {
            this._countryCodeET.setText("+" + countryCode);
        } else {
            this._countryCodeET.setText(getString(R.string.signup_no_sim));
        }
        this._userNameET.setText(phoneNumber);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.tm.authenticatorsdk.socgen.UI.SignUpBase, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // com.tm.authenticatorsdk.socgen.UI.SignUpBase
    protected void disableButton() {
        this.activateButton.setEnabled(false);
    }

    @Override // com.tm.authenticatorsdk.socgen.UI.SignUpBase
    protected void enableButton() {
        this.activateButton.setEnabled(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @NonNull
    public AuthenticatorAPI getService() {
        INetworkProvider networkProvider = getApplication().networkProvider();
        return (AuthenticatorAPI) networkProvider.service(Definitions.getBase(), AuthenticatorAPI.class);
    }

    @Override // com.tm.authenticatorsdk.socgen.UI.SignUpBase
    public void performSignUp() {
        String user;
        Log.d("network", "started api call");
        SignupRequest req = new SignupRequest();
        UserFields app = new UserFields();
        UserFields mob = new UserFields();
        UserFields un = new UserFields();
        UserFields type = new UserFields();
        app.setValues(AuthenticatorSDK.getInstance(this).flavor.getAppId());
        app.setName("APP");
        String user2 = this._countryCodeET.getText().toString().replace("+", "");
        if (this._userNameET.getText().toString().startsWith("0")) {
            user = user2 + this._userNameET.getText().toString().substring(1);
        } else {
            user = user2 + this._userNameET.getText().toString();
        }
        mob.setValues(user);
        mob.setName("MOBILE");
        un.setValues(user);
        un.setName("USERNAME");
        type.setValues(AuthenticatorSDK.getInstance(this).flavor.getUserType());
        type.setName("USER_TYPE");
        UserFields[] fields = {app, mob, un, type};
        req.setFields(fields);
        ArrayList<UserFields[]> field = new ArrayList<>(1);
        field.add(fields);
        this._uiHandler.sendEmptyMessage(302);
        this.mSub = getService().Signup(field).concatMap(new Func1<List<SignupResponse>, Observable<List<Response>>>() { // from class: com.tm.authenticatorsdk.socgen.UI.SignupActivity.5
            public Observable<List<Response>> call(List<SignupResponse> signupResponses) {
                String user3;
                SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(SignupActivity.this).edit();
                String user4 = SignupActivity.this._countryCodeET.getText().toString().replace("+", "");
                if (SignupActivity.this._userNameET.getText().toString().startsWith("0")) {
                    user3 = user4 + SignupActivity.this._userNameET.getText().toString().substring(1);
                } else {
                    user3 = user4 + SignupActivity.this._userNameET.getText().toString();
                }
                ed.putString("phonenumber", user3);
                ed.commit();
                Log.d("signup", "getResultCode: " + signupResponses.get(0).getResultCode() + ", chosenUrl:" + SignupActivity.this.chosenUrl);
                if (signupResponses.get(0).getResultCode() == 0) {
                }
                if (signupResponses.get(0).getResultCode() == 22) {
                    RetrieveOneTimePINExtReq pin = new RetrieveOneTimePINExtReq();
                    pin.setRetrieveMode(2);
                    pin.setType(-1);
                    pin.setValue(user3);
                    AppSignatureHelper appSignatureHelper = new AppSignatureHelper(SignupActivity.this);
                    pin.setHashOTP(appSignatureHelper.getAppSignatures().get(0));
                    Object[] re = {pin.getValue(), Integer.valueOf(pin.getType()), Integer.valueOf(pin.getRetrieveMode()), pin.getHashOTP()};
                    SignupActivity.this.pinSmsHandler = new PinSmsHandler(SignupActivity.this, "SignInActivity", SignupActivity.this);
                    SignupActivity.this.pinSmsHandler.startListening();
                    return SignupActivity.this.getService().RetrieveOneTime(re).delay(35L, TimeUnit.SECONDS);
                }
                int result = Integer.valueOf(signupResponses.get(0).getResultCode()).intValue();
                SignupActivity.this._uiHandler.sendEmptyMessage(303);
                EventBus.getDefault().post(new MessageEvent(result, signupResponses.get(0).getResultDescription()));
                return Observable.empty();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<Response>>() { // from class: com.tm.authenticatorsdk.socgen.UI.SignupActivity.4
            public void onCompleted() {
                Log.d("signup", "done");
            }

            public void onError(Throwable e) {
                Log.d("signup", "onError" + e.getMessage());
                SignupActivity.this._uiHandler.sendEmptyMessage(303);
                Definitions.setBaseUrl("https://rest.telemessage.com");
                if ((e instanceof SSLException) || (e instanceof TimeoutException) || (e instanceof ConnectException) || (e instanceof SocketTimeoutException) || (e instanceof SocketTimeoutException)) {
                    EventBus.getDefault().post(new MessageEvent(-3, e.getMessage()));
                } else {
                    EventBus.getDefault().post(new MessageEvent(-1, e.getMessage()));
                }
            }

            public void onNext(List<Response> userResponses) {
                Log.d("signup", "onNext" + userResponses.get(0).toString());
                SignupActivity.this._uiHandler.sendEmptyMessage(303);
                SignupActivity.this.startActivity(new Intent(SignupActivity.this.getApplicationContext(), ActivationActivity.class));
            }
        });
    }

    @Override // com.tm.authenticatorsdk.socgen.UI.SignUpBase
    protected String getPhoneNumber() {
        String user;
        String user2 = this._countryCodeET.getText().toString().replace("+", "");
        if (this._userNameET.getText().toString().startsWith("0")) {
            user = user2 + this._userNameET.getText().toString().substring(1);
        } else {
            user = user2 + this._userNameET.getText().toString();
        }
        return user;
    }

    public String getCountryCode(Context context) {
        String countryCode = "";
        TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
        try {
            countryCode = tm.getSimCountryIso();
        } catch (Exception e) {
            Log.d("SignInActivity", "-=-=-=-=-=-=-=-=-=", e);
        }
        String xml = "";
        try {
            Resources res = getResources();
            InputStream in_s = res.openRawResource(R.raw.country_code);
            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            xml = new String(b);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        Map<String, String> newMap = (Map) $.fromXml(xml);
        return newMap.get(countryCode.toUpperCase());
    }

    @Override // com.tm.authenticatorsdk.socgen.UI.SignUpBase, android.app.Activity
    public void onResume() {
        super.onResume();
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("activated_aa", false)) {
            finish();
        }
        Definitions.setBaseUrl("https://rest.telemessage.com");
    }

    @Override // com.tm.authenticatorsdk.socgen.UI.SignUpBase, android.app.Activity
    public void onPause() {
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PinCodeEvent event) {
        this.mSub.unsubscribe();
        RetrieveCredentialsReq cred = new RetrieveCredentialsReq();
        cred.setOneTimePIN(event.getPin());
        cred.setRetrieveMode(1);
        cred.setSearchBy(0);
        cred.setValue(this._countryCodeET.getText().toString() + this._userNameET.getText().toString());
        Object[] re = {cred.getValue(), Integer.valueOf(cred.getSearchBy()), Integer.valueOf(cred.getRetrieveMode()), cred.getOneTimePIN()};
        getService().RetrieveCredentials(re).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<UserResponse>>() { // from class: com.tm.authenticatorsdk.socgen.UI.SignupActivity.7
            public void onCompleted() {
                Log.d("activation", "done");
            }

            public void onError(Throwable e) {
                Log.d("signup", e.getMessage());
                SignupActivity.this._uiHandler.sendEmptyMessage(303);
                if (!e.getMessage().toLowerCase().contains("given job id")) {
                    EventBus.getDefault().post(new MessageEvent(-1, e.getMessage()));
                }
            }

            public void onNext(List<UserResponse> userResponses) {
                Log.d("signup", userResponses.get(0).toString());
                if (userResponses.get(0).getResultCode() == 0) {
                    Util.handleAppSettings(SignupActivity.this.getApplication(), userResponses.get(0).getFields()[1].getValue(), userResponses.get(0).getFields()[0].getValue());
                    Util.handleKeepAlive(SignupActivity.this.getApplication());
                    return;
                }
                SignupActivity.this._uiHandler.sendEmptyMessage(303);
                int resultCode = Integer.valueOf(userResponses.get(0).getResultCode()).intValue();
                EventBus.getDefault().post(new MessageEvent(resultCode, userResponses.get(0).getResultDescription()));
            }
        });
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
        EventBus.getDefault().post(new MessageEvent(-1, "User Cancelled"));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ErrorEvent event) {
        Log.d("ErrorMessageSignUp", "ErrorMessageSignUpActivity");
        this.sendLogs.setVisibility(0);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        String messageBody = event.getMsg();
        String title = event.getTitle();
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(messageBody);
        alertDialogBuilder.setPositiveButton("OK", (DialogInterface.OnClickListener) null);
        alertDialogBuilder.create().show();
    }

    @Override // com.tm.authenticatorsdk.socgen.IReadPinFromSmsInterface
    public void fillPinValue(String pinValue) {
        Log.d("SignInActivity", "fillPinValue: " + pinValue);
        EventBus.getDefault().post(new PinCodeEvent(pinValue));
    }
}
