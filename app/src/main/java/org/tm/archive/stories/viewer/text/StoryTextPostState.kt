package org.tm.archive.stories.viewer.text

import android.graphics.Typeface
import org.tm.archive.database.model.databaseprotos.StoryTextPost
import org.tm.archive.linkpreview.LinkPreview

data class StoryTextPostState(
  val storyTextPost: StoryTextPost? = null,
  val linkPreview: LinkPreview? = null,
  val loadState: LoadState = LoadState.INIT,
  val typeface: Typeface? = null
) {
  enum class LoadState {
    INIT,
    LOADED,
    FAILED
  }
}
