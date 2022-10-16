package org.tm.archive

import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.dependencies.ApplicationDependencyProvider
import org.tm.archive.dependencies.InstrumentationApplicationDependencyProvider

/**
 * Application context for running instrumentation tests (aka androidTests).
 */
class SignalInstrumentationApplicationContext : ApplicationContext() {
  override fun initializeAppDependencies() {
    val default = ApplicationDependencyProvider(this)
    ApplicationDependencies.init(this, InstrumentationApplicationDependencyProvider(this, default))
  }
}
