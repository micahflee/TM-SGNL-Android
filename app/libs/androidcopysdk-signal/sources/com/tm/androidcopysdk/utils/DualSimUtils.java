package com.tm.androidcopysdk.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import com.github.underscore.lodash.$;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.tm.androidcopysdk.R;
import com.tm.androidcopysdk.SimChangedReceiver;
import com.tm.logger.Log;
import com.tm.utils.DualSimRefreshEvent;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/utils/DualSimUtils.class */
public class DualSimUtils {
    public static final String TAG = "DualSimUtils";
    static DualSimUtils mInstance;
    private Activity context;
    private String savePhone;
    private String saveCountry;
    private int saveSlot;

    public DualSimUtils(Activity context) {
        this.context = context;
    }

    public void setActivity(Activity activity) {
        this.context = activity;
    }

    public static boolean checkForDualSimDevice(Context context) {
        if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") == 0) {
            SubscriptionManager subscriptionManager = SubscriptionManager.from(context.getApplicationContext());
            List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();
            return subsInfoList != null && subsInfoList.size() > 1;
        }
        return false;
    }

    public static String getNumberBySubId(Context context, int subId) {
        int slot;
        String ret = PreferenceManager.getDefaultSharedPreferences(context).getString("phonenumber", "999999999");
        SubscriptionManager subscriptionManager = SubscriptionManager.from(context.getApplicationContext());
        List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        Iterator<SubscriptionInfo> it = subsInfoList.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            SubscriptionInfo sub = it.next();
            if (sub.getSubscriptionId() == subId && (slot = sub.getSimSlotIndex()) > -1) {
                ret = getPhoneBySlot(context, slot);
                break;
            }
        }
        return ret;
    }

    public static boolean dualSimIsOn(Context context) {
        return PrefManager.getBooleanPref(context, "dual_sim_on", false);
    }

    public boolean dualSimShowPopup() {
        return PrefManager.getBooleanPref((Context) this.context, "dual_show_popup", true);
    }

    public boolean dualSimWasStarted() {
        return PrefManager.getBooleanPref((Context) this.context, "dual_was_started", false);
    }

    public static void dualSimOff(Context context) {
        PrefManager.setBooleanPref(context, "dual_sim_on", false);
        PrefManager.setBooleanPref(context, "dual_show_popup", true);
        PrefManager.setBooleanPref(context, "dual_was_started", false);
    }

    public void turnOff() {
        showSlotDialog(true);
    }

    public void showDualSimDialog() {
        PrefManager.setBooleanPref((Context) this.context, "dual_show_popup", true);
        PrefManager.setBooleanPref((Context) this.context, "dual_was_started", true);
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this.context).create();
        LayoutInflater inflater = this.context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dual_sim_dialog, (ViewGroup) null);
        dialogBuilder.setCancelable(false);
        final RadioGroup group = (RadioGroup) dialogView.findViewById(R.id.myRadioGroup);
        final EditText countryCode = (EditText) dialogView.findViewById(R.id.sim_countrt_code_edit);
        countryCode.setText(getCountryCode());
        final EditText phone = (EditText) dialogView.findViewById(R.id.sim_phone_edit);
        Button cancel = (Button) dialogView.findViewById(R.id.cancel);
        final Button ok = (Button) dialogView.findViewById(R.id.ok_button);
        cancel.setOnClickListener(new View.OnClickListener() { // from class: com.tm.androidcopysdk.utils.DualSimUtils.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Log.d(DualSimUtils.TAG, "showDualSimDialog cancel");
                DualSimUtils.this.saveCountry = null;
                DualSimUtils.this.savePhone = null;
                dialogBuilder.dismiss();
                DualSimUtils.this.showSlotDialog(false);
            }
        });
        ok.setOnClickListener(new View.OnClickListener() { // from class: com.tm.androidcopysdk.utils.DualSimUtils.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Log.d(DualSimUtils.TAG, "showDualSimDialog ok");
                if (!countryCode.getText().toString().trim().equalsIgnoreCase(DualSimUtils.this.getCountryCode())) {
                    DualSimUtils.this.savePhone = phone.getText().toString().trim();
                    DualSimUtils.this.saveCountry = countryCode.getText().toString().trim();
                    int selectedId = group.getCheckedRadioButtonId();
                    DualSimUtils.this.saveSlot = selectedId == R.id.slot1 ? 0 : 1;
                    DualSimUtils.this.showCountryErrorDialog();
                } else {
                    PhoneValidateResponse response = DualSimUtils.isPhoneNumberValidate(phone.getText().toString().trim(), countryCode.getText().toString().trim());
                    if (response.isValid()) {
                        PrefManager.setBooleanPref((Context) DualSimUtils.this.context, "dual_show_popup", false);
                        int selectedId2 = group.getCheckedRadioButtonId();
                        int slot = selectedId2 == R.id.slot1 ? 0 : 1;
                        DualSimUtils.this.saveSimInfo(slot, response.getCode() + response.getPhone());
                        DualSimUtils.this.saveCountry = null;
                        DualSimUtils.this.saveCountry = null;
                        DualSimUtils.this.saveSlot = -1;
                        EventBus.getDefault().post(new DualSimRefreshEvent());
                    } else {
                        DualSimUtils.this.showDualSimDialog();
                        DualSimUtils.this.saveCountry = null;
                        DualSimUtils.this.saveCountry = null;
                        DualSimUtils.this.saveSlot = -1;
                        Toast.makeText(DualSimUtils.this.context, "wrong format number", 1).show();
                    }
                }
                dialogBuilder.dismiss();
            }
        });
        TextWatcher phoneTextWatcher = new TextWatcher() { // from class: com.tm.androidcopysdk.utils.DualSimUtils.3
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    ok.setEnabled(false);
                } else if (s.toString().length() > 0 && countryCode.getText().length() > 0) {
                    ok.setEnabled(true);
                } else {
                    ok.setEnabled(false);
                }
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };
        phone.addTextChangedListener(phoneTextWatcher);
        TextWatcher countryTextWatcher = new TextWatcher() { // from class: com.tm.androidcopysdk.utils.DualSimUtils.4
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    ok.setEnabled(false);
                } else if (s.toString().length() > 0 && phone.getText().length() > 0) {
                    ok.setEnabled(true);
                } else {
                    ok.setEnabled(false);
                }
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };
        countryCode.addTextChangedListener(countryTextWatcher);
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showSlotDialog(final boolean fromSettings) {
        final AlertDialog dialogBuilder2 = new AlertDialog.Builder(this.context).create();
        LayoutInflater inflater = this.context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dual_sim_dialog, (ViewGroup) null);
        dialogBuilder2.setCancelable(false);
        TextView message = (TextView) dialogView.findViewById(R.id.part1);
        TextView message2 = (TextView) dialogView.findViewById(R.id.part2);
        TextView message3 = (TextView) dialogView.findViewById(R.id.part3);
        TextView subTitle1 = (TextView) dialogView.findViewById(R.id.edit_title);
        TextView subTitle2 = (TextView) dialogView.findViewById(R.id.edit_title_2);
        subTitle1.setVisibility(8);
        subTitle2.setVisibility(8);
        message3.setVisibility(8);
        final RadioGroup group = (RadioGroup) dialogView.findViewById(R.id.myRadioGroup);
        EditText countryCode = (EditText) dialogView.findViewById(R.id.sim_countrt_code_edit);
        countryCode.setVisibility(8);
        EditText phone = (EditText) dialogView.findViewById(R.id.sim_phone_edit);
        phone.setVisibility(8);
        Button cancel = (Button) dialogView.findViewById(R.id.cancel);
        Button ok = (Button) dialogView.findViewById(R.id.ok_button);
        ok.setEnabled(true);
        cancel.setEnabled(true);
        cancel.setOnClickListener(new View.OnClickListener() { // from class: com.tm.androidcopysdk.utils.DualSimUtils.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Log.d(DualSimUtils.TAG, "showSlotDialog cancel");
                if (fromSettings) {
                    EventBus.getDefault().post(new DualSimRefreshEvent());
                } else {
                    DualSimUtils.this.showDualSimDialog();
                }
                dialogBuilder2.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() { // from class: com.tm.androidcopysdk.utils.DualSimUtils.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (fromSettings) {
                    Log.d(DualSimUtils.TAG, "showSlotDialog ok");
                    DualSimUtils.this.saveSlot = PrefManager.getIntPref(DualSimUtils.this.context, "sim_main_slot", -1);
                    DualSimUtils.this.saveSimInfo(DualSimUtils.this.saveSlot, DualSimUtils.this.getPhoneNumber());
                    PrefManager.setBooleanPref((Context) DualSimUtils.this.context, "dual_show_popup", false);
                    if (ActivityCompat.checkSelfPermission(DualSimUtils.this.context, "android.permission.READ_PHONE_STATE") == 0) {
                        TelephonyManager telephonyManager = (TelephonyManager) DualSimUtils.this.context.getSystemService("phone");
                        String simSerialNumber = null;
                        if (telephonyManager != null) {
                            try {
                                simSerialNumber = telephonyManager.getSimSerialNumber();
                            } catch (Exception e) {
                                Log.d("android 10", "does not meet the requirements to access device identifiers", e);
                            }
                        }
                        if (simSerialNumber != null && simSerialNumber.length() > 0) {
                            DualSimUtils.this.setSimSerialNumber(simSerialNumber);
                        }
                    }
                } else {
                    Log.d(DualSimUtils.TAG, "showSlotDialog ok");
                    int selectedId = group.getCheckedRadioButtonId();
                    DualSimUtils.this.saveSlot = selectedId == R.id.slot1 ? 0 : 1;
                    DualSimUtils.this.saveSimInfo(DualSimUtils.this.saveSlot, DualSimUtils.this.getPhoneNumber());
                }
                DualSimUtils.this.saveCountry = null;
                DualSimUtils.this.saveCountry = null;
                DualSimUtils.this.saveSlot = -1;
                PrefManager.setBooleanPref((Context) DualSimUtils.this.context, "dual_show_popup", false);
                dialogBuilder2.dismiss();
                EventBus.getDefault().post(new DualSimRefreshEvent());
            }
        });
        if (fromSettings) {
            message.setText("Are you sure you want to disable your second SIM number " + PrefManager.getStringPref(this.context, "sim_second_phone") + " archiving ?");
            message2.setVisibility(8);
            group.setVisibility(8);
        } else {
            message.setText("You selected to archive only one of the SIM card numbers " + getPhoneNumber());
            message2.setText("Set the location of the SIM");
        }
        dialogBuilder2.setView(dialogView);
        dialogBuilder2.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showCountryErrorDialog() {
        new AlertDialog.Builder(this.context).setMessage(StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.COUNTRY_CODE_ERROR_DIALOG : this.context.getString(R.string.country_code_error_dialog)).setCancelable(false).setPositiveButton("ON", new DialogInterface.OnClickListener() { // from class: com.tm.androidcopysdk.utils.DualSimUtils.8
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                PhoneValidateResponse response = DualSimUtils.isPhoneNumberValidate(DualSimUtils.this.savePhone, DualSimUtils.this.saveCountry);
                if (response.isValid()) {
                    PrefManager.setBooleanPref((Context) DualSimUtils.this.context, "dual_show_popup", false);
                    DualSimUtils.this.saveSimInfo(DualSimUtils.this.saveSlot, response.getCode() + response.getPhone());
                    PrefManager.setBooleanPref((Context) DualSimUtils.this.context, "dual_show_popup", false);
                    DualSimUtils.this.saveCountry = null;
                    DualSimUtils.this.saveCountry = null;
                    DualSimUtils.this.saveSlot = -1;
                } else {
                    DualSimUtils.this.showDualSimDialog();
                    DualSimUtils.this.saveCountry = null;
                    DualSimUtils.this.saveCountry = null;
                    DualSimUtils.this.saveSlot = -1;
                    Toast.makeText(DualSimUtils.this.context, "wrong format number", 1).show();
                }
                dialog.dismiss();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // from class: com.tm.androidcopysdk.utils.DualSimUtils.7
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                DualSimUtils.this.showDualSimDialog();
                DualSimUtils.this.saveCountry = null;
                DualSimUtils.this.saveCountry = null;
                DualSimUtils.this.saveSlot = -1;
            }
        }).create().show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getCountryCode() {
        String countryCode = "";
        TelephonyManager tm = (TelephonyManager) this.context.getSystemService("phone");
        try {
            countryCode = tm.getSimCountryIso();
        } catch (Exception e) {
            Log.d(TAG, "exception", e);
        }
        String xml = "";
        try {
            Resources res = this.context.getResources();
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

    /* JADX INFO: Access modifiers changed from: private */
    public void setSimSerialNumber(String simSerialNumber) {
        Log.d("setSimSerialNumber", "setSimSerialNumber: " + simSerialNumber);
        SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(this.context).edit();
        if (simSerialNumber != null && simSerialNumber.length() > 0) {
            ed.putString(SimChangedReceiver.CORRECT_SIM_SERIAL_NUMBER_KEY, simSerialNumber);
        }
        ed.commit();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveSimInfo(int slotIndex, String phoneNumber) {
        boolean saveAll;
        Log.d(TAG, "saveSimInfo: " + slotIndex + " , " + phoneNumber);
        PhoneNumberUtil.MatchType matchType = PhoneNumberUtil.getInstance().isNumberMatch(getPhoneNumber(), phoneNumber);
        SubscriptionManager subscriptionManager = SubscriptionManager.from(this.context.getApplicationContext());
        List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        Log.d(TAG, "Current list = " + subsInfoList);
        if (matchType == PhoneNumberUtil.MatchType.NSN_MATCH || matchType == PhoneNumberUtil.MatchType.EXACT_MATCH) {
            PrefManager.setBooleanPref((Context) this.context, "dual_sim_on", false);
            saveAll = false;
        } else {
            PrefManager.setBooleanPref((Context) this.context, "dual_sim_on", true);
            saveAll = true;
        }
        for (SubscriptionInfo subscriptionInfo : subsInfoList) {
            if (subscriptionInfo.getSimSlotIndex() == slotIndex) {
                if (saveAll) {
                    PrefManager.setStringPref(this.context, "sim_second_id", subscriptionInfo.getIccId());
                    Log.d(TAG, "sim_second_id:" + subscriptionInfo.getIccId());
                    PrefManager.setStringPref(this.context, "sim_second_phone", phoneNumber);
                    Log.d(TAG, "sim_second_phone:" + phoneNumber);
                    PrefManager.setIntPref(this.context, "sim_second_slot", slotIndex);
                    Log.d(TAG, "sim_second_slot:" + slotIndex);
                } else {
                    PrefManager.setStringPref(this.context, "sim_main_id", subscriptionInfo.getIccId());
                    PrefManager.setStringPref(this.context, "sim_main_phone", getPhoneNumber());
                    PrefManager.setIntPref(this.context, "sim_main_slot", slotIndex);
                    setSimSerialNumber(subscriptionInfo.getIccId());
                }
            } else if (saveAll) {
                PrefManager.setStringPref(this.context, "sim_main_id", subscriptionInfo.getIccId());
                Log.d(TAG, "sim_main_id:" + subscriptionInfo.getIccId());
                PrefManager.setStringPref(this.context, "sim_main_phone", getPhoneNumber());
                Log.d(TAG, "sim_main_phone:" + getPhoneNumber());
                PrefManager.setIntPref(this.context, "sim_main_slot", subscriptionInfo.getSimSlotIndex());
                Log.d(TAG, "sim_main_slot:" + subscriptionInfo.getSimSlotIndex());
                setSimSerialNumber(subscriptionInfo.getIccId());
            }
        }
    }

    public static boolean checkCurrentState(Context context) {
        boolean ret = true;
        Log.d(TAG, "checkCurrentState");
        if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") != 0) {
            return false;
        }
        int count = TextUtils.isEmpty(PrefManager.getStringPref(context, "sim_second_id", "")) ? 1 : 2;
        int sum = 0;
        SubscriptionManager subscriptionManager = SubscriptionManager.from(context.getApplicationContext());
        List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        Log.d(TAG, "Current list = " + subsInfoList);
        for (SubscriptionInfo subscriptionInfo : subsInfoList) {
            int mainSlot = PrefManager.getIntPref(context, "sim_main_slot", -1);
            if (subscriptionInfo.getSimSlotIndex() == mainSlot) {
                String old = PrefManager.getStringPref(context, "sim_main_id", "");
                ret = old.equalsIgnoreCase(subscriptionInfo.getIccId());
                Log.d(TAG, "mainSlot = " + mainSlot + " currentState = " + ret);
                sum++;
                if (!ret) {
                    break;
                }
            } else {
                String old2 = PrefManager.getStringPref(context, "sim_second_id", "");
                ret = old2.equalsIgnoreCase(subscriptionInfo.getIccId());
                Log.d(TAG, "sim_second_id = " + subscriptionInfo.getSimSlotIndex() + " currentState = " + ret);
                sum++;
                if (!ret) {
                    break;
                }
            }
        }
        Log.d(TAG, "checkCurrentState return " + ret);
        if (sum != count) {
            ret = false;
        }
        if (sum == 1) {
            Log.d(TAG, "sum ==1 , turn on dual sim state");
            dualSimOff(context);
            EventBus.getDefault().post(new DualSimRefreshEvent());
        }
        return ret;
    }

    private void savePhoneNumber(String phoneNumber) {
        SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(this.context).edit();
        ed.putString("phonenumber", phoneNumber);
        ed.commit();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getPhoneNumber() {
        return PreferenceManager.getDefaultSharedPreferences(this.context).getString("phonenumber", "999999999");
    }

    public static PhoneValidateResponse isPhoneNumberValidate(String mobNumber, String countryCode) {
        PhoneValidateResponse phoneNumberValidate = new PhoneValidateResponse();
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        PhoneNumberUtil.PhoneNumberType isMobile = null;
        boolean isValid = false;
        try {
            String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode));
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(mobNumber, isoCode);
            if (phoneNumber != null) {
                Log.d(TAG, "isPhoneNumberValidate: " + phoneNumber.toString());
            } else {
                Log.d(TAG, "isPhoneNumberValidate: phoneNumber is NULL");
            }
            isValid = phoneNumberUtil.isValidNumber(phoneNumber);
            isMobile = phoneNumberUtil.getNumberType(phoneNumber);
            phoneNumberValidate.setCode(String.valueOf(phoneNumber.getCountryCode()));
            phoneNumberValidate.setPhone(String.valueOf(phoneNumber.getNationalNumber()));
            phoneNumberValidate.setValid(false);
        } catch (NumberParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e2) {
            e2.printStackTrace();
        } catch (NumberFormatException e3) {
            e3.printStackTrace();
        }
        if (isValid && (PhoneNumberUtil.PhoneNumberType.MOBILE == isMobile || PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE == isMobile)) {
            phoneNumberValidate.setValid(true);
            Log.d(TAG, "isValid = true");
        }
        return phoneNumberValidate;
    }

    public String getSummary() {
        return "+" + PrefManager.getStringPref(this.context, "sim_second_phone", "") + "\nSlot #" + (PrefManager.getIntPref(this.context, "sim_second_slot", -1) + 1);
    }

    public static String getDescriptionForDualSim(Context context) {
        String ret = "";
        Log.d(TAG, "checkCurrentState");
        if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") != 0) {
            return "";
        }
        SubscriptionManager subscriptionManager = SubscriptionManager.from(context.getApplicationContext());
        List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        Log.d(TAG, "Current list = " + subsInfoList);
        if (subsInfoList.size() == 0) {
            int mainSlot = PrefManager.getIntPref(context, "sim_main_slot", -1);
            if (mainSlot != -1) {
                String old = PrefManager.getStringPref(context, "sim_main_id", "");
                if (!TextUtils.isEmpty(old)) {
                    ret = ret + "SIM Card was on slot " + (mainSlot + 1) + ", number " + PrefManager.getStringPref(context, "sim_main_phone", "") + " was removed\n";
                }
            }
            String old2 = PrefManager.getStringPref(context, "sim_second_id", "");
            if (!TextUtils.isEmpty(old2)) {
                int secSlot = PrefManager.getIntPref(context, "sim_second_slot", -1);
                ret = ret + "SIM Card was on slot " + (secSlot + 1) + ", number " + PrefManager.getStringPref(context, "sim_second_phone", "") + " was removed\n";
            }
        } else if (subsInfoList.size() == 1) {
            for (SubscriptionInfo subscriptionInfo : subsInfoList) {
                int mainSlot2 = PrefManager.getIntPref(context, "sim_main_slot", -1);
                if (subscriptionInfo.getSimSlotIndex() == mainSlot2) {
                    int secSlot2 = PrefManager.getIntPref(context, "sim_second_slot", -1);
                    String old3 = PrefManager.getStringPref(context, "sim_second_id", "");
                    if (!old3.equalsIgnoreCase(subscriptionInfo.getIccId())) {
                        ret = ret + "SIM Card was on slot " + (secSlot2 + 1) + ", number " + PrefManager.getStringPref(context, "sim_second_phone", "") + " was removed\n";
                    }
                } else {
                    String old4 = PrefManager.getStringPref(context, "sim_main_id", "");
                    if (!old4.equalsIgnoreCase(subscriptionInfo.getIccId())) {
                        ret = ret + "SIM Card was on slot " + (mainSlot2 + 1) + ", number " + PrefManager.getStringPref(context, "sim_main_phone", "") + " was removed\n";
                    }
                }
            }
        } else {
            for (SubscriptionInfo subscriptionInfo2 : subsInfoList) {
                int mainSlot3 = PrefManager.getIntPref(context, "sim_main_slot", -1);
                if (subscriptionInfo2.getSimSlotIndex() == mainSlot3) {
                    String old5 = PrefManager.getStringPref(context, "sim_main_id", "");
                    if (!old5.equalsIgnoreCase(subscriptionInfo2.getIccId())) {
                        ret = ret + "SIM Card was on slot " + (mainSlot3 + 1) + ", number " + PrefManager.getStringPref(context, "sim_main_phone", "") + " was removed\n";
                    }
                } else {
                    String old6 = PrefManager.getStringPref(context, "sim_second_id", "");
                    if (!old6.equalsIgnoreCase(subscriptionInfo2.getIccId())) {
                        ret = ret + "SIM Card was on slot " + (subscriptionInfo2.getSimSlotIndex() + 1) + ", number " + PrefManager.getStringPref(context, "sim_second_phone", "") + " was removed\n";
                    }
                }
            }
        }
        return ret;
    }

    public static String getPhoneBySlot(Context context, int slot) {
        String ret;
        int main = PrefManager.getIntPref(context, "sim_main_slot", -1);
        if (main == slot) {
            ret = PrefManager.getStringPref(context, "sim_main_phone");
        } else {
            ret = PrefManager.getStringPref(context, "sim_second_phone");
        }
        return ret;
    }

    public static String getPhoneByid(Context context, String id, String account) {
        String ret;
        String main = PrefManager.getStringPref(context, "sim_main_id");
        Log.d(TAG, "getPhoneByid");
        if (!TextUtils.isEmpty(main)) {
            Log.d(TAG, "getPhoneByid main id = " + main);
            if (main.contains(id) || id.contains(main)) {
                ret = PrefManager.getStringPref(context, "sim_main_phone");
            } else {
                ret = PrefManager.getStringPref(context, "sim_second_phone");
            }
            return ret;
        }
        Log.d(TAG, "getPhoneByid account = " + account);
        String mob1 = PrefManager.getStringPref(context, "sim_main_phone");
        if (!TextUtils.isEmpty(account)) {
            String mob2 = PrefManager.getStringPref(context, "sim_second_phone");
            String mob12 = (TextUtils.isEmpty(mob1) || !mob1.startsWith("+")) ? "+" + mob1 : mob1;
            String mob22 = (TextUtils.isEmpty(mob2) || !mob2.startsWith("+")) ? "+" + mob2 : mob2;
            String account2 = account.startsWith("+") ? account : "+" + account;
            Log.d(TAG, "getPhoneByid account = " + account2);
            if (!TextUtils.isEmpty(mob12) && PhoneNumberUtils.compare(context, account2, mob12)) {
                Log.d(TAG, "getPhoneByid account = " + account2 + " return " + mob12);
                return mob12;
            } else if (!TextUtils.isEmpty(mob22) && PhoneNumberUtils.compare(context, account2, mob22)) {
                Log.d(TAG, "getPhoneByid account = " + account2 + " return " + mob22);
                return mob22;
            } else {
                Log.d(TAG, "getPhoneByid is empty");
                return mob12;
            }
        }
        Log.d(TAG, "getPhoneByid return main");
        return mob1;
    }

    public static boolean isSimIDActive(Context context, String id) {
        if (TextUtils.isEmpty(id)) {
            return false;
        }
        String main = PrefManager.getStringPref(context, "sim_main_id");
        return main.contains(id) || id.contains(main);
    }

    public static boolean isSimSlotActive(Context context, int slot) {
        int main = PrefManager.getIntPref(context, "sim_main_slot", -1);
        return main == slot;
    }

    public static String getAllNumbers(Context context) {
        return "+" + PrefManager.getStringPref(context, "sim_main_phone") + "\n+" + PrefManager.getStringPref(context, "sim_second_phone");
    }
}
