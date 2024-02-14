package org.archiver

import android.app.Application
import com.tm.logger.Log


class SignalLoggerAdapter(application: Application) : org.signal.core.util.logging.Log.Logger() {

  init {
    Log.createInstance(application.applicationContext)
  }

  override fun v(tag: String, message: String?, t: Throwable?, keepLonger: Boolean) {
    Log.v(tag, message, t)
  }

  override fun d(tag: String, message: String?, t: Throwable?, keepLonger: Boolean) {
    Log.d(tag, message, t)
  }

  override fun i(tag: String, message: String?, t: Throwable?, keepLonger: Boolean) {
    Log.i(tag, message, t)
  }

  override fun w(tag: String, message: String?, t: Throwable?, keepLonger: Boolean) {
    Log.w(tag, message, t)
  }

  override fun e(tag: String, message: String?, t: Throwable?, keepLonger: Boolean) {
    Log.e(tag, message, t)
  }

  override fun flush() {}
}