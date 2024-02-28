/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.logger

import android.app.Application
import com.tm.logger.Log


class SignalLoggerAdapter(application: Application) : org.signal.core.util.logging.Log.Logger() {

  init {
    Log.createInstance(application.applicationContext)
  }

  override fun v(tag: String, message: String?, t: Throwable?, keepLonger: Boolean) {
    Log.v(TAG, getMessage(tag, message), t)
  }

  override fun d(tag: String, message: String?, t: Throwable?, keepLonger: Boolean) {
    Log.d(TAG, getMessage(tag, message), t)
  }

  override fun i(tag: String, message: String?, t: Throwable?, keepLonger: Boolean) {
    Log.i(TAG, getMessage(tag, message), t)
  }

  override fun w(tag: String, message: String?, t: Throwable?, keepLonger: Boolean) {
    Log.w(TAG, getMessage(tag, message), t)
  }

  override fun e(tag: String, message: String?, t: Throwable?, keepLonger: Boolean) {
    Log.e(TAG, getMessage(tag, message), t)
  }

  override fun flush() {}

  private fun getMessage(tag: String, message: String?) = "$tag: $message"

  companion object {
    private const val TAG = "TMSignalLogger"
  }
}
