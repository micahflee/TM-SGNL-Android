package org.tm.archive.payments.preferences.model;

import androidx.annotation.NonNull;

import org.tm.archive.util.MappingModel;

public class InProgress implements MappingModel<InProgress> {
  @Override
  public boolean areItemsTheSame(@NonNull InProgress newItem) {
    return true;
  }

  @Override
  public boolean areContentsTheSame(@NonNull InProgress newItem) {
    return true;
  }
}
