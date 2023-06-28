package org.tm.archive

import org.signal.core.util.concurrent.SignalExecutors
import org.signal.core.util.logging.AndroidLogger
import org.signal.core.util.logging.Log
import org.signal.libsignal.protocol.logging.SignalProtocolLoggerProvider
import org.tm.archive.database.LogDatabase
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.dependencies.ApplicationDependencyProvider
import org.tm.archive.dependencies.InstrumentationApplicationDependencyProvider
import org.tm.archive.logging.CustomSignalProtocolLogger
import org.tm.archive.logging.PersistentLogger
import org.tm.archive.testing.InMemoryLogger

/**
 * Application context for running instrumentation tests (aka androidTests).
 */
class SignalInstrumentationApplicationContext : ApplicationContext() {

  val inMemoryLogger: InMemoryLogger = InMemoryLogger()

  override fun initializeAppDependencies() {
    val default = ApplicationDependencyProvider(this)
    ApplicationDependencies.init(this, InstrumentationApplicationDependencyProvider(this, default))
    ApplicationDependencies.getDeadlockDetector().start()
  }

  override fun initializeLogging() {
    persistentLogger = PersistentLogger(this)

    Log.initialize({ true }, AndroidLogger(), persistentLogger, inMemoryLogger)

    SignalProtocolLoggerProvider.setProvider(CustomSignalProtocolLogger())

    SignalExecutors.UNBOUNDED.execute {
      Log.blockUntilAllWritesFinished()
      LogDatabase.getInstance(this).trimToSize()
    }
  }
}
