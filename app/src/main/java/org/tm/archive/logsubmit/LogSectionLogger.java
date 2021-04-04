package org.tm.archive.logsubmit;

import android.content.Context;

import androidx.annotation.NonNull;

import org.tm.archive.ApplicationContext;

public class LogSectionLogger implements LogSection {

  @Override
  public @NonNull String getTitle() {
    return "LOGGER";
  }

  @Override
  public @NonNull CharSequence getContent(@NonNull Context context) {
    CharSequence logs = ApplicationContext.getInstance(context).getPersistentLogger().getLogs();
    return logs != null ? logs : "Unable to retrieve logs.";
  }
}
