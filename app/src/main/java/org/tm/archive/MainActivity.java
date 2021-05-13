package org.tm.archive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tm.androidcopysdk.utils.PrefManager;

import org.archiver.ArchivePreferenceConstants;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.util.AppStartup;
import org.tm.archive.util.CachedInflater;
import org.tm.archive.util.CommunicationActions;
import org.tm.archive.util.DynamicNoActionBarTheme;
import org.tm.archive.util.DynamicTheme;

public class MainActivity extends PassphraseRequiredActivity {

  public static final int RESULT_CONFIG_CHANGED = Activity.RESULT_FIRST_USER + 901;

  private final DynamicTheme  dynamicTheme = new DynamicNoActionBarTheme();
  private final MainNavigator navigator    = new MainNavigator(this);

  public static @NonNull Intent clearTop(@NonNull Context context) {
    Intent intent = new Intent(context, MainActivity.class);

    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK  |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);

    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState, boolean ready) {
    AppStartup.getInstance().onCriticalRenderEventStart();
    super.onCreate(savedInstanceState, ready);
    setContentView(R.layout.main_activity);

    navigator.onCreate(savedInstanceState);

    handleGroupLinkInIntent(getIntent());
    handleProxyInIntent(getIntent());

    CachedInflater.from(this).clear();
  }

  @Override
  public Intent getIntent() {
    return super.getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                      Intent.FLAG_ACTIVITY_NEW_TASK  |
                                      Intent.FLAG_ACTIVITY_SINGLE_TOP);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    handleGroupLinkInIntent(intent);
    handleProxyInIntent(intent);
  }

  @Override
  protected void onPreCreate() {
    super.onPreCreate();
    dynamicTheme.onCreate(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    dynamicTheme.onResume(this);

    notifyMessageIfNeeded();

  }

  private void notifyMessageIfNeeded() {
    boolean isAlreadyRestarted = PrefManager.getBooleanPref(this, ArchivePreferenceConstants.PREF_KEY_MAIN_ACTIVITY_RESTART, false);

    if(!isAlreadyRestarted){
      PrefManager.setBooleanPref(this, ArchivePreferenceConstants.PREF_KEY_MAIN_ACTIVITY_RESTART, true);

      final Handler handler = new Handler(Looper.getMainLooper());
      handler.postDelayed(new Runnable() {
        @Override
        public void run() {
          notifyMessage();
        }
      }, 4000);
    }
  }


  public synchronized void notifyMessage(){
    synchronized (ApplicationDependencies.getIncomingMessageObserver()) {
      ApplicationDependencies.getIncomingMessageObserver().notifyAll();
    }
  }

  @Override
  public void onBackPressed() {
    if (!navigator.onBackPressed()) {
      super.onBackPressed();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == MainNavigator.REQUEST_CONFIG_CHANGES && resultCode == RESULT_CONFIG_CHANGED) {
      recreate();
    }
  }

  public @NonNull MainNavigator getNavigator() {
    return navigator;
  }

  private void handleGroupLinkInIntent(Intent intent) {
    Uri data = intent.getData();
    if (data != null) {
      CommunicationActions.handlePotentialGroupLinkUrl(this, data.toString());
    }
  }

  private void handleProxyInIntent(Intent intent) {
    Uri data = intent.getData();
    if (data != null) {
      CommunicationActions.handlePotentialProxyLinkUrl(this, data.toString());
    }
  }
}
