package org.tm.archive.migrations

import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.SignalDatabase.Companion.recipients
import org.tm.archive.jobmanager.Job
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.recipients.Recipient
import org.tm.archive.storage.StorageSyncHelper

/**
 * Added to initialize whether the user has seen the onboarding story
 */
internal class StoryReadStateMigrationJob(
  parameters: Parameters = Parameters.Builder().build()
) : MigrationJob(parameters) {

  companion object {
    const val KEY = "StoryReadStateMigrationJob"
  }

  override fun getFactoryKey(): String = KEY

  override fun isUiBlocking(): Boolean = false

  override fun performMigration() {
    if (!SignalStore.storyValues().hasUserOnboardingStoryReadBeenSet()) {
      SignalStore.storyValues().userHasReadOnboardingStory = SignalStore.storyValues().userHasReadOnboardingStory
      SignalDatabase.messages.markOnboardingStoryRead()

      if (SignalStore.account().isRegistered) {
        recipients.markNeedsSync(Recipient.self().id)
        StorageSyncHelper.scheduleSyncForDataChange()
      }
    }
  }

  override fun shouldRetry(e: Exception): Boolean = false

  class Factory : Job.Factory<StoryReadStateMigrationJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): StoryReadStateMigrationJob {
      return StoryReadStateMigrationJob(parameters)
    }
  }
}
