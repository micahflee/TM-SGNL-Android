package org.tm.archive.util;

import androidx.annotation.StyleRes;

import org.tm.archive.R;

public class DynamicRegistrationTheme extends DynamicTheme {

  protected @StyleRes int getTheme() {
    return R.style.Signal_DayNight_Registration;
  }
}
