/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.tm.archive.backup.v2.stream

import org.tm.archive.backup.v2.proto.Frame

interface BackupExportWriter : AutoCloseable {
  fun write(frame: Frame)
}
