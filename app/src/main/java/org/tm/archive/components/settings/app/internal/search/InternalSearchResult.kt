/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.tm.archive.components.settings.app.internal.search

import org.tm.archive.groups.GroupId
import org.tm.archive.recipients.RecipientId

data class InternalSearchResult(
  val name: String,
  val id: RecipientId,
  val aci: String? = null,
  val pni: String? = null,
  val groupId: GroupId? = null
)
