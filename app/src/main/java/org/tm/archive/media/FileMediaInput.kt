/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */
package org.tm.archive.media

import android.media.MediaExtractor
import org.tm.archive.video.interfaces.MediaInput
import java.io.File
import java.io.IOException

/**
 * A media input source that the system reads directly from the file.
 */
class FileMediaInput(private val file: File) : MediaInput {
  @Throws(IOException::class)
  override fun createExtractor(): MediaExtractor {
    val extractor = MediaExtractor()
    extractor.setDataSource(file.absolutePath)
    return extractor
  }

  override fun hasSameInput(other: MediaInput): Boolean {
    return other is FileMediaInput && other.file == this.file
  }

  override fun close() {}
}
