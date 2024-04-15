/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.tm.archive.recipients.ui.findby

import org.tm.archive.recipients.RecipientId

sealed interface FindByResult {
  data class Success(val recipientId: RecipientId) : FindByResult
  object InvalidEntry : FindByResult
  data class NotFound(val recipientId: RecipientId = RecipientId.UNKNOWN) : FindByResult
  object NetworkError : FindByResult
}
