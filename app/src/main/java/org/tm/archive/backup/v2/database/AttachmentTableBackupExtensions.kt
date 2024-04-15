/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.tm.archive.backup.v2.database

import org.signal.core.util.deleteAll
import org.tm.archive.database.AttachmentTable

fun AttachmentTable.clearAllDataForBackupRestore() {
  writableDatabase.deleteAll(AttachmentTable.TABLE_NAME)
}
