package com.tm.androidcopysdk.events;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.tm.androidcopysdk.R;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.events.EventAbsObj;
import com.tm.androidcopysdk.utils.DualSimUtils;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.androidcopysdk.utils.StringsFlavorsUtils;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/SimRemovedWithoutLogout.class */
public class SimRemovedWithoutLogout extends EventAbsObj {
    public static String getSubject(Context context) {
        String user = PrefManager.getStringPref(context, "pref_my_name");
        String myNumber = null;
        if (DualSimUtils.checkForDualSimDevice(context) && DualSimUtils.dualSimIsOn(context)) {
            String firstM = DualSimUtils.getPhoneBySlot(context, 0);
            String secondM = DualSimUtils.getPhoneBySlot(context, 1);
            if (!TextUtils.isEmpty(firstM)) {
                myNumber = firstM;
            }
            if (!TextUtils.isEmpty(secondM)) {
                myNumber = !TextUtils.isEmpty(myNumber) ? myNumber + " , " + secondM : secondM;
            }
        } else {
            myNumber = PreferenceManager.getDefaultSharedPreferences(context).getString("phonenumber", "999999999");
            if (!myNumber.contains("+") && !myNumber.contains("@")) {
                myNumber = "+" + myNumber;
            }
        }
        return context.getString(R.string.sim_removed_subject, user, myNumber);
    }

    public SimRemovedWithoutLogout(Context context) {
        super(EventAbsObj.EventType.SimRemovedWithoutLogout, StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.EVENTS_DUAL_SIM_CARD_BACK : context.getString(R.string.events_dual_sim_card_back), MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered, System.currentTimeMillis());
        String myNumber = null;
        if (DualSimUtils.checkForDualSimDevice(context) && DualSimUtils.dualSimIsOn(context)) {
            String firstM = DualSimUtils.getPhoneBySlot(context, 0);
            String secondM = DualSimUtils.getPhoneBySlot(context, 1);
            myNumber = TextUtils.isEmpty(firstM) ? myNumber : firstM;
            if (!TextUtils.isEmpty(secondM)) {
                myNumber = !TextUtils.isEmpty(myNumber) ? myNumber + " , " + secondM : secondM;
            }
        } else {
            myNumber = PreferenceManager.getDefaultSharedPreferences(context).getString("phonenumber", "999999999");
            if (!myNumber.contains("+") && !myNumber.contains("@")) {
                myNumber = "+" + myNumber;
            }
        }
        this.description = getSubject(context) + "\n\n" + context.getString(R.string.sim_removed_body, myNumber);
    }
}
