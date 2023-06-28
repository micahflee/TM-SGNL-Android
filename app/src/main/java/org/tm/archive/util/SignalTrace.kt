package org.tm.archive.util

import org.tm.archive.BuildConfig
import androidx.tracing.Trace as AndroidTrace

object SignalTrace {
  @JvmStatic
  fun beginSection(methodName: String) {
    if (!BuildConfig.TRACING_ENABLED) {
      return
    }
    AndroidTrace.beginSection(methodName)
  }

  @JvmStatic
  fun endSection() {
    if (!BuildConfig.TRACING_ENABLED) {
      return
    }
    AndroidTrace.endSection()
  }
}
