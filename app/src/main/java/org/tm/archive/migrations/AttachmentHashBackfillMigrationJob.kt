/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.tm.archive.migrations

import org.signal.core.util.logging.Log
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.jobmanager.Job
import org.tm.archive.jobs.AttachmentHashBackfillJob
import java.lang.Exception

/**
 * Kicks off the attachment hash backfill process by enqueueing a [AttachmentHashBackfillJob].
 */
internal class AttachmentHashBackfillMigrationJob(parameters: Parameters = Parameters.Builder().build()) : MigrationJob(parameters) {

  companion object {
    val TAG = Log.tag(AttachmentHashBackfillMigrationJob::class.java)
    const val KEY = "AttachmentHashBackfillMigrationJob"
  }

  override fun getFactoryKey(): String = KEY

  override fun isUiBlocking(): Boolean = false

  override fun performMigration() {
    ApplicationDependencies.getJobManager().add(AttachmentHashBackfillJob())
  }

  override fun shouldRetry(e: Exception): Boolean = false

  class Factory : Job.Factory<AttachmentHashBackfillMigrationJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): AttachmentHashBackfillMigrationJob {
      return AttachmentHashBackfillMigrationJob(parameters)
    }
  }
}
