package org.archiver.model

enum class CallRtcMode(val code: Int) {
  Voice(0),
  Video(1),

  ;

  companion object {
    fun fromIsVideo(isVideo: Boolean) = if (isVideo) Video else Voice
  }
}