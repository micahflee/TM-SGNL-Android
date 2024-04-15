package org.tm.archive.shakereport;

import android.app.Application;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.signal.core.util.ShakeDetector;
import org.signal.core.util.ThreadUtil;
import org.signal.core.util.logging.Log;
import org.tm.archive.R;
import org.tm.archive.conversation.mutiselect.forward.MultiselectForwardFragment;
import org.tm.archive.conversation.mutiselect.forward.MultiselectForwardFragmentArgs;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.logsubmit.SubmitDebugLogRepository;
import org.tm.archive.sharing.MultiShareArgs;
import org.tm.archive.util.ServiceUtil;
import org.tm.archive.util.views.SimpleProgressDialog;

import java.lang.ref.WeakReference;
import java.util.Collections;

/**
 * A class that will detect a shake and then prompts the user to submit a debuglog. Basically a
 * shortcut to submit a debuglog from anywhere.
 */
public final class ShakeToReport implements ShakeDetector.Listener {

  private static final String TAG = Log.tag(ShakeToReport.class);

  private final Application   application;
  private final ShakeDetector detector;

  private WeakReference<AppCompatActivity> weakActivity;

  public ShakeToReport(@NonNull Application application) {
    this.application  = application;
    this.detector     = new ShakeDetector(this);
    this.weakActivity = new WeakReference<>(null);
  }

  public void enable() {
    if (!SignalStore.internalValues().shakeToReport()) return;

    detector.start(ServiceUtil.getSensorManager(application));
  }

  public void disable() {
    if (!SignalStore.internalValues().shakeToReport()) return;

    detector.stop();
  }

  public void registerActivity(@NonNull AppCompatActivity activity) {
    if (!SignalStore.internalValues().shakeToReport()) return;

    this.weakActivity = new WeakReference<>(activity);
  }

  @Override
  public void onShakeDetected() {
    if (!SignalStore.internalValues().shakeToReport()) return;

    AppCompatActivity activity = weakActivity.get();
    if (activity == null) {
      Log.w(TAG, "No registered activity!");
      return;
    }

    if (activity.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
      disable();

      new MaterialAlertDialogBuilder(activity)
          .setTitle(R.string.ShakeToReport_shake_detected)
          .setMessage(R.string.ShakeToReport_submit_debug_log)
          .setNegativeButton(android.R.string.cancel, (d, i) -> {
            d.dismiss();
            enableIfVisible();
          })
          .setPositiveButton(R.string.ShakeToReport_submit, (d, i) -> {
            d.dismiss();
            submitLog(activity);
          })
          .show();
    }
  }

  private void submitLog(@NonNull AppCompatActivity activity) {
    AlertDialog              spinner = SimpleProgressDialog.show(activity);
    SubmitDebugLogRepository repo    = new SubmitDebugLogRepository();

    Log.i(TAG, "Submitting log...");

    repo.buildAndSubmitLog(url -> {
      Log.i(TAG, "Logs uploaded!");

      ThreadUtil.runOnMain(() -> {
        spinner.dismiss();

        if (url.isPresent()) {
          showPostSubmitDialog(activity, url.get());
        } else {
          Toast.makeText(activity, R.string.ShakeToReport_failed_to_submit, Toast.LENGTH_SHORT).show();
          enableIfVisible();
        }
      });
    });
  }

  private void showPostSubmitDialog(@NonNull AppCompatActivity activity, @NonNull String url) {
    AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
        .setTitle(R.string.ShakeToReport_success)
        .setMessage(url)
        .setNegativeButton(android.R.string.cancel, (d, i) -> {
          d.dismiss();
          enableIfVisible();
        })
        .setPositiveButton(R.string.ShakeToReport_share, (d, i) -> {
          d.dismiss();
          enableIfVisible();

          MultiselectForwardFragment.showFullScreen(
              activity.getSupportFragmentManager(),
              new MultiselectForwardFragmentArgs(
                  Collections.singletonList(new MultiShareArgs.Builder()
                                                .withDraftText(url)
                                                .build()),
                  R.string.MultiselectForwardFragment__share_with
              )
          );
        })
        .show();

    ((TextView) dialog.findViewById(android.R.id.message)).setTextIsSelectable(true);
  }

  private void enableIfVisible() {
    if (ApplicationDependencies.getAppForegroundObserver().isForegrounded()) {
      enable();
    }
  }
}
