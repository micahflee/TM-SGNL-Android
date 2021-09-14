package org.tm.archive.util;

import androidx.annotation.NonNull;

import org.signal.core.util.logging.Log;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.keyvalue.SignalStore;

import java.util.concurrent.TimeUnit;

public class SignalUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

  private static final String TAG = Log.tag(SignalUncaughtExceptionHandler.class);

  private final Thread.UncaughtExceptionHandler originalHandler;

  public SignalUncaughtExceptionHandler(@NonNull Thread.UncaughtExceptionHandler originalHandler) {
    this.originalHandler = originalHandler;
  }

  @Override
  public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
    Log.e(TAG, "", e, true);
    SignalStore.blockUntilAllWritesFinished();
    Log.blockUntilAllWritesFinished();
    ApplicationDependencies.getJobManager().flush();
    originalHandler.uncaughtException(t, e);
  }
}
