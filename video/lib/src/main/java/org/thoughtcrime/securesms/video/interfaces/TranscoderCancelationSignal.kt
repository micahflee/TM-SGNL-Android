package org.tm.archive.video.interfaces

fun interface TranscoderCancelationSignal {
  fun isCanceled(): Boolean
}
