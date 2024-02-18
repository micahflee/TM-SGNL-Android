package org.tm.archive;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tm.androidcopysdk.network.appSettings.UpdateEvent;
import com.tm.androidcopysdk.network.appSettings.WorkerIntentService;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.logger.Log;

import org.archiver.ArchivePreferenceConstants;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.signal.core.util.concurrent.LifecycleDisposable;
import org.signal.donations.StripeApi;
import org.tm.archive.components.DebugLogsPromptDialogFragment;
import org.tm.archive.components.PromptBatterySaverDialogFragment;
import org.tm.archive.components.settings.app.AppSettingsActivity;
import org.tm.archive.components.voice.VoiceNoteMediaController;
import org.tm.archive.components.voice.VoiceNoteMediaControllerOwner;
import org.tm.archive.conversationlist.RelinkDevicesReminderBottomSheetFragment;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.devicetransfer.olddevice.OldDeviceExitActivity;
import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.net.DeviceTransferBlockingInterceptor;
import org.tm.archive.notifications.VitalsViewModel;
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
  private VitalsViewModel               vitalsViewModel;

  private Dialog dialog;

  private final LifecycleDisposable lifecycleDisposable = new LifecycleDisposable();

  private boolean onFirstRender = false;

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
    final View content = findViewById(android.R.id.content);
    content.getViewTreeObserver().addOnPreDrawListener(
        new ViewTreeObserver.OnPreDrawListener() {
          @Override
          public boolean onPreDraw() {
            // Use pre draw listener to delay drawing frames till conversation list is ready
            if (onFirstRender) {
              content.getViewTreeObserver().removeOnPreDrawListener(this);
              return true;
            } else {
              return false;
            }
          }
        });

    lifecycleDisposable.bindTo(this);

    mediaController = new VoiceNoteMediaController(this, true);

    ConversationListTabRepository         repository = new ConversationListTabRepository();
    ConversationListTabsViewModel.Factory factory    = new ConversationListTabsViewModel.Factory(repository);

    handleDeeplinkIntent(getIntent());

    CachedInflater.from(this).clear();

    conversationListTabsViewModel = new ViewModelProvider(this, factory).get(ConversationListTabsViewModel.class);
    updateTabVisibility();

    vitalsViewModel = new ViewModelProvider(this).get(VitalsViewModel.class);

    lifecycleDisposable.add(
        vitalsViewModel
            .getVitalsState()
            .subscribe(this::presentVitalsState)
    );

    WorkerIntentService.startJobIntentService(this, true);/*TM_SA*/
  }

  //**TM_SA**//Start
  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEvent(UpdateEvent event) {
    if (event == null) {
      return;
    }
    Log.d("MainActivity", "UpdateEvent -> onEvent: " + event.type);
    if (event.type == UpdateEvent.EVENTS_TYPE.suspension) {
      showDialog();
    } else if (event.type == UpdateEvent.EVENTS_TYPE.activated) {
      dialog.dismiss();
    }
  }

  private void showDialog() {
    dialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen); // Fullscreen theme
    dialog.setContentView(R.layout.fragment_registration_enter_phone_number);
    ConstraintLayout layout = dialog.findViewById(R.id.constraint_layout);
    layout.setBackgroundColor(getResources().getColor(R.color.white));
    org.tm.archive.util.views.CircularProgressMaterialButton button = dialog.findViewById(R.id.registerButton);
    button.setVisibility(View.GONE);
    dialog.setCancelable(false);
    dialog.show();
  }
  //**TM_SA**//End

  @SuppressLint("NewApi")
  private void presentVitalsState(VitalsViewModel.State state) {
    switch (state) {
      case NONE:
        break;
      case PROMPT_BATTERY_SAVER_DIALOG:
        PromptBatterySaverDialogFragment.show(getSupportFragmentManager());
        break;
      case PROMPT_DEBUGLOGS_FOR_NOTIFICATIONS:
        DebugLogsPromptDialogFragment.show(this, DebugLogsPromptDialogFragment.Purpose.NOTIFICATIONS);
      case PROMPT_DEBUGLOGS_FOR_CRASH:
        DebugLogsPromptDialogFragment.show(this, DebugLogsPromptDialogFragment.Purpose.CRASH);
        break;
    }
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
    handleDeeplinkIntent(intent);
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
      new MaterialAlertDialogBuilder(this)
          .setTitle(R.string.OldDeviceTransferLockedDialog__complete_registration_on_your_new_device)
          .setMessage(R.string.OldDeviceTransferLockedDialog__your_signal_account_has_been_transferred_to_your_new_device)
          .setPositiveButton(R.string.OldDeviceTransferLockedDialog__done, (d, w) -> OldDeviceExitActivity.exit(this))
          .setNegativeButton(R.string.OldDeviceTransferLockedDialog__cancel_and_activate_this_device, (d, w) -> {
            SignalStore.misc().clearOldDeviceTransferLocked();
            DeviceTransferBlockingInterceptor.getInstance().unblockNetwork();
          })
          .setCancelable(false)
          .show();
    }

    if (SignalStore.misc().getShouldShowLinkedDevicesReminder()) {
      SignalStore.misc().setShouldShowLinkedDevicesReminder(false);
      RelinkDevicesReminderBottomSheetFragment.show(getSupportFragmentManager());
    }

    updateTabVisibility();

    vitalsViewModel.checkSlowNotificationHeuristics();
    //**TM_SA**// start
    notifyMessageIfNeeded();

    if(!EventBus.getDefault().isRegistered(this)){
      EventBus.getDefault().register(this);
      Log.d("LaunchActivity", "registerBus");
    }

  }

  @Override protected void onDestroy() {
    super.onDestroy();
    //**TM_TA**// Start
    if(EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().unregister(this);
      Log.d("LaunchActivity", "unregisterBus");
    }

    //**TM_TA**// End
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
    findViewById(R.id.conversation_list_tabs).setVisibility(View.VISIBLE);
    WindowUtil.setNavigationBarColor(this, ContextCompat.getColor(this, R.color.signal_colorSurface2));
  }

  public @NonNull MainNavigator getNavigator() {
    return navigator;
  }

  private void handleDeeplinkIntent(Intent intent) {
    handleGroupLinkInIntent(intent);
    handleProxyInIntent(intent);
    handleSignalMeIntent(intent);
    handleCallLinkInIntent(intent);
    handleDonateReturnIntent(intent);
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

  private void handleCallLinkInIntent(Intent intent) {
    Uri data = intent.getData();
    if (data != null) {
      CommunicationActions.handlePotentialCallLinkUrl(this, data.toString());
    }
  }

  private void handleDonateReturnIntent(Intent intent) {
    Uri data = intent.getData();
    if (data != null && data.toString().startsWith(StripeApi.RETURN_URL_IDEAL)) {
      startActivity(AppSettingsActivity.manageSubscriptions(this));
    }
  }

  public void onFirstRender() {
    onFirstRender = true;
  }

  @Override
  public @NonNull VoiceNoteMediaController getVoiceNoteMediaController() {
    return mediaController;
  }
}
