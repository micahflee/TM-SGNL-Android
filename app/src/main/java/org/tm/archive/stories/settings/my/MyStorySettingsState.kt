package org.tm.archive.stories.settings.my

data class MyStorySettingsState(
  val myStoryPrivacyState: MyStoryPrivacyState = MyStoryPrivacyState(),
  val areRepliesAndReactionsEnabled: Boolean = false
)
