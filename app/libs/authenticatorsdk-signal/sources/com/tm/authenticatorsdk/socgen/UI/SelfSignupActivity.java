package com.tm.authenticatorsdk.socgen.UI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.tm.authenticatorsdk.R;
import com.tm.authenticatorsdk.socgen.signup.SignUpManager;
import com.tm.logger.Log;
import com.tm.utils.RemoveProgressFromSignupManagerEvent;
import com.tm.utils.Util;
import java.util.Map;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/UI/SelfSignupActivity.class */
public class SelfSignupActivity extends SignUpBase {
    Map<String, String> mdmSettings;
    public static final String SELF_SIGNUP_WORKER_TAG = "SELF_SIGNUP_WORKER_TAG";

    @Override // com.tm.authenticatorsdk.socgen.UI.SignUpBase, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.self_sign_in);
        this._userNameET = (EditText) findViewById(R.id.UserNameET);
        this._errorLayout = findViewById(R.id.errorLayout);
        this.mdmSettings = Util.getMdmSettings(getApplication());
        String email = this.mdmSettings.get("Email");
        this._userNameET.setText(email);
        prepareSignUp();
        this.sendLogs = (TextView) findViewById(R.id.sendLogs);
        this.sendLogs.setOnClickListener(new View.OnClickListener() { // from class: com.tm.authenticatorsdk.socgen.UI.SelfSignupActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SelfSignupActivity.this.sendLogs();
            }
        });
    }

    @Override // com.tm.authenticatorsdk.socgen.UI.SignUpBase
    public void performSignUp() {
        Log.d("network", "started api call");
        this._uiHandler.sendEmptyMessage(302);
        SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        ed.putInt(SignUpManager.INITIAL_DELAY_SHAREDPREFERENCE_KEY, 0);
        ed.commit();
        SignUpManager.startSelfSignupWorker(1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RemoveProgressFromSignupManagerEvent event) {
        this._uiHandler.sendEmptyMessage(303);
    }
}
