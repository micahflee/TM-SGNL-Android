package org.tm.archive.testing

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import org.tm.archive.SignalInstrumentationApplicationContext

/**
 * Custom runner that replaces application with [SignalInstrumentationApplicationContext].
 */
@Suppress("unused")
class SignalTestRunner : AndroidJUnitRunner() {
  override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
    return super.newApplication(cl, SignalInstrumentationApplicationContext::class.java.name, context)
  }
}
