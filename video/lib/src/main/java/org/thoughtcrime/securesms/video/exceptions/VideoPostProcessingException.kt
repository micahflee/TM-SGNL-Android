/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.tm.archive.video.exceptions

class VideoPostProcessingException : RuntimeException {
  internal constructor(message: String?) : super(message)
  internal constructor(message: String?, inner: Exception?) : super(message, inner)
}
