package org.tm.archive.logsubmit;

import androidx.annotation.NonNull;

public interface LogLine {

  long getId();
  @NonNull String getText();
  @NonNull Style getStyle();
  @NonNull Placeholder getPlaceholderType();

  enum Style {
    NONE, VERBOSE, DEBUG, INFO, WARNING, ERROR
  }

  enum Placeholder {
    NONE, TRACE
  }
}
