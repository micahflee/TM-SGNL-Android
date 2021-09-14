package org.tm.archive.payments.preferences.model;

import androidx.annotation.NonNull;

import org.tm.archive.util.MappingModel;

public class NoRecentActivity implements MappingModel<NoRecentActivity> {
  @Override
  public boolean areItemsTheSame(@NonNull NoRecentActivity newItem) {
    return true;
  }

  @Override
  public boolean areContentsTheSame(@NonNull NoRecentActivity newItem) {
    return true;
  }
}
