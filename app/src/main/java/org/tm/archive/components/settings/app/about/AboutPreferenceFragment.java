package org.tm.archive.components.settings.app.about;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.tm.archive.BuildConfig;
import org.tm.archive.R;
//**TM_SA**//Add this class
public class AboutPreferenceFragment extends Fragment {
  private static final String TAG = AboutPreferenceFragment.class.getSimpleName();

  private TextView fullAppName;
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
    //getApplicationPreferencesActivity().getSupportActionBar().setTitle(R.string.ManageProfileFragment_about);
  }


  private void initResources(View view){

    fullAppName = view.findViewById(R.id.full_app_name);
    version = view.findViewById(R.id.version);
    terms = view.findViewById(R.id.terms);
    center = view.findViewById(R.id.privacy_center);


    version.setText("version" + " " + getVersionString(getActivity()));

    setString(fullAppName, R.string.settings_about_title, null);
    setString(terms, R.string.terms_about, null);
    setString(center, R.string.privacy_center_about, null);

  }


  private void setString(TextView view, int strId,String str){
    String text = strId>0 ? getString(strId) : str;
    view.setText(Html.fromHtml(text));
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