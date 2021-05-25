package org.tm.archive.preferences;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;

import com.google.firebase.iid.FirebaseInstanceId;
import com.tm.logger.Log;

import org.tm.archive.ApplicationPreferencesActivity;
import org.tm.archive.R;
import org.tm.archive.BuildConfig;
import org.tm.archive.contacts.ContactAccessor;
import org.tm.archive.contacts.ContactIdentityManager;
import org.tm.archive.delete.DeleteAccountFragment;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.logsubmit.SubmitDebugLogActivity;
import org.tm.archive.phonenumbers.PhoneNumberFormatter;
import org.tm.archive.registration.RegistrationNavigationActivity;
import org.tm.archive.util.DynamicTheme;
import org.tm.archive.util.FeatureFlags;
import org.tm.archive.util.TextSecurePreferences;
import org.tm.archive.util.task.ProgressDialogAsyncTask;
import org.whispersystems.libsignal.util.guava.Optional;
import org.whispersystems.signalservice.api.SignalServiceAccountManager;
import org.whispersystems.signalservice.api.push.exceptions.AuthorizationFailedException;

import java.io.IOException;

public class AboutPreferenceFragment extends Fragment {
  private static final String TAG = AboutPreferenceFragment.class.getSimpleName();

  private TextView version;
  private TextView doNotSell;
  private TextView policy;
  private TextView center;
  private TextView terms;
  private LinearLayout contactUs;
  private LinearLayout issueR;
  protected Handler mHandler = new Handler();
  private final String LOG_NAME = "log.zip";


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.settings_about_custom_layout, container, false);
    //name = (TextView)view.view.findViewById(R.id.Name);
    //location = (TextView)view.view.findViewById(R.id.Location);
    initResources(view);
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    getApplicationPreferencesActivity().getSupportActionBar().setTitle(R.string.ManageProfileFragment_about);


  }

  private @NonNull ApplicationPreferencesActivity getApplicationPreferencesActivity() {
    return (ApplicationPreferencesActivity) requireActivity();
  }


  private void initResources(View view){

    version = view.findViewById(R.id.version);
    terms = view.findViewById(R.id.terms);
    center = view.findViewById(R.id.privacy_center);


    version.setText("version" + " " + getVersionString(getActivity()));

    setString(terms,R.string.terms_about,null);
    setString(center,R.string.privacy_center_about,null);

  }


  private void setString(TextView view, int strId,String str){
    String text = strId>0 ? getString(strId) : str;
    view.setText(Html.fromHtml(text));
    view.setLinkTextColor(getResources().getColorStateList(R.color.amber_A700));
    view.setMovementMethod(LinkMovementMethod.getInstance());
//        stripUnderlines(view);
  }


  public static String getVersionString(Context context){
    String versionName;

    //  versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
      versionName = BuildConfig.signal_teleMessage_version;

    return versionName;
  }



}