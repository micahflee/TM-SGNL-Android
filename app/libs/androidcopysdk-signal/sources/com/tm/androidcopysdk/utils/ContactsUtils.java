package com.tm.androidcopysdk.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;
import com.tm.androidcopysdk.CommonUtils;
import com.tm.androidcopysdk.R;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/utils/ContactsUtils.class */
public class ContactsUtils {
    public static Contact getContactName(Context context, String phoneNumber) {
        String contactName = "";
        if (CommonUtils.checkPermission(context, "android.permission.READ_CONTACTS") && getPrefValue(context)) {
            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
            Cursor cursor = cr.query(uri, new String[]{"display_name"}, null, null, null);
            if (cursor == null) {
                return null;
            }
            while (cursor.moveToNext() && TextUtils.isEmpty(contactName)) {
                contactName = cursor.getString(cursor.getColumnIndex("display_name"));
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        String[] names = contactName.trim().split(" ");
        Contact contact = new Contact();
        if (names.length > 1) {
            contact.firstName = names[0];
            String last = "";
            for (int i = 1; i < names.length; i++) {
                last = last + names[i] + " ";
            }
            contact.lastName = last.trim();
        } else if (names.length == 1) {
            contact.firstName = names[0];
            contact.lastName = "";
        } else {
            contact.firstName = "";
            contact.lastName = "";
        }
        return contact;
    }

    public static void turnContactPermissionFlagOnce(Context context) {
        PrefManager.setBooleanPref(context, "contactPermissionFlagOnce", true);
    }

    public static boolean contactPermissionFlagOnce(Context context) {
        return PrefManager.getBooleanPref(context, "contactPermissionFlagOnce", false);
    }

    public static void setPref(Context context, boolean value) {
        PrefManager.setBooleanPref(context, "contacts_pref_key", value);
    }

    public static boolean getPrefValue(Context context) {
        return PrefManager.getBooleanPref(context, "contacts_pref_key", false);
    }

    public static void setPrefDialog(Context context, boolean value) {
        PrefManager.setBooleanPref(context, "contacts_pref_dialog_key", value);
    }

    public static boolean getPrefDialogValue(Context context) {
        return PrefManager.getBooleanPref(context, "contacts_pref_dialog_key", false);
    }

    public static void showDialog(Activity activity, DialogInterface.OnClickListener positiveButtonListener, DialogInterface.OnClickListener negativeButtonListener) {
        setPrefDialog(activity, true);
        View view = activity.getLayoutInflater().inflate(R.layout.privacy_policy_popup, (ViewGroup) null);
        TextView tv = (TextView) view.findViewById(R.id.textView1);
        tv.setText(StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.PRIVACY_POLICY_DIALOG_MESSAGE : activity.getString(R.string.privacy_policy_dialog_message));
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false).setView(view).setPositiveButton("Yes", positiveButtonListener).setNegativeButton("No", negativeButtonListener);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static boolean canAskPermissions(Activity context) {
        if (!contactPermissionFlagOnce(context) || ActivityCompat.shouldShowRequestPermissionRationale(context, "android.permission.READ_PHONE_STATE")) {
            return true;
        }
        return false;
    }
}
