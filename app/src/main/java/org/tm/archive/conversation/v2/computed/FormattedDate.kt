/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.tm.archive.conversation.v2.computed

data class FormattedDate(
  val isRelative: Boolean,
  val isNow: Boolean,
  val value: String
)
