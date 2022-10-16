package org.tm.archive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.tm.androidcopysdk.utils.PrefManager;

import org.archiver.ArchivePreferenceConstants;
import org.tm.archive.components.voice.VoiceNoteMediaController;
import org.tm.archive.components.voice.VoiceNoteMediaControllerOwner;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.devicetransfer.olddevice.OldDeviceTransferLockedDialog;
import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.stories.Stories;
import org.tm.archive.stories.tabs.ConversationListTabRepository;
import org.tm.archive.stories.tabs.ConversationListTabsViewModel;
import org.tm.archive.util.AppStartup;
import org.tm.archive.util.CachedInflater;
import org.tm.archive.util.CommunicationActions;
import org.tm.archive.util.DynamicNoActionBarTheme;
import org.tm.archive.util.DynamicTheme;
import org.tm.archive.util.SplashScreenUtil;
import org.tm.archive.util.WindowUtil;

public class MainActivity extends PassphraseRequiredActivity implements VoiceNoteMediaControllerOwner {

  public static final int RESULT_CONFIG_CHANGED = Activity.RESULT_FIRST_USER + 901;

  private final DynamicTheme  dynamicTheme = new DynamicNoActionBarTheme();
  private final MainNavigator navigator    = new MainNavigator(this);

  private VoiceNoteMediaController      mediaController;
  private ConversationListTabsViewModel conversationListTabsViewModel;

  public static @NonNull Intent clearTop(@NonNull Context context) {
    Intent intent = new Intent(context, MainActivity.class);

    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);

    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState, boolean ready) {
    AppStartup.getInstance().onCriticalRenderEventStart();
    super.onCreate(savedInstanceState, ready);

    setContentView(R.layout.main_activity);

    mediaController = new VoiceNoteMediaController(this);

    ConversationListTabRepository         repository = new ConversationListTabRepository();
    ConversationListTabsViewModel.Factory factory    = new ConversationListTabsViewModel.Factory(repository);

    handleGroupLinkInIntent(getIntent());
    handleProxyInIntent(getIntent());
    handleSignalMeIntent(getIntent());

    CachedInflater.from(this).clear();

    conversationListTabsViewModel = new ViewModelProvider(this, factory).get(ConversationListTabsViewModel.class);
    updateTabVisibility();
  }

  @Override
  public Intent getIntent() {
    return super.getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                      Intent.FLAG_ACTIVITY_NEW_TASK |
                                      Intent.FLAG_ACTIVITY_SINGLE_TOP);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    handleGroupLinkInIntent(intent);
    handleProxyInIntent(intent);
    handleSignalMeIntent(intent);
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
    if (SignalStore.misc().isOldDeviceTransferLocked()) {
      OldDeviceTransferLockedDialog.show(getSupportFragmentManager());
    }

    updateTabVisibility();

    //**TM_SA**// start
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

  //**TM_SA**// End

  @Override
  protected void onStop() {
    super.onStop();
    SplashScreenUtil.setSplashScreenThemeIfNecessary(this, SignalStore.settings().getTheme());
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

  private void updateTabVisibility() {
    if (Stories.isFeatureEnabled()) {
      findViewById(R.id.conversation_list_tabs).setVisibility(View.VISIBLE);
      WindowUtil.setNavigationBarColor(this, ContextCompat.getColor(this, R.color.signal_colorSurface2));
    } else {
      findViewById(R.id.conversation_list_tabs).setVisibility(View.GONE);
      WindowUtil.setNavigationBarColor(this, ContextCompat.getColor(this, R.color.signal_colorBackground));
      conversationListTabsViewModel.onChatsSelected();
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

  private void handleSignalMeIntent(Intent intent) {
    Uri data = intent.getData();
    if (data != null) {
      CommunicationActions.handlePotentialSignalMeUrl(this, data.toString());
    }
  }

  @Override
  public @NonNull VoiceNoteMediaController getVoiceNoteMediaController() {
    return mediaController;
  }
}
