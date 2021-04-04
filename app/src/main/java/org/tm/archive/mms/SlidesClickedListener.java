package org.tm.archive.mms;

import android.view.View;

import java.util.List;

public interface SlidesClickedListener {
  void onClick(View v, List<Slide> slides);
}
