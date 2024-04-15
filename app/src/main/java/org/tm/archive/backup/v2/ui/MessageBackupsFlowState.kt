/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.tm.archive.backup.v2.ui

import org.tm.archive.components.settings.app.subscription.donate.gateway.GatewayResponse
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.lock.v2.PinKeyboardType

data class MessageBackupsFlowState(
  val selectedMessageBackupsType: MessageBackupsType? = null,
  val availableBackupsTypes: List<MessageBackupsType> = emptyList(),
  val selectedPaymentGateway: GatewayResponse.Gateway? = null,
  val availablePaymentGateways: List<GatewayResponse.Gateway> = emptyList(),
  val pin: String = "",
  val pinKeyboardType: PinKeyboardType = SignalStore.pinValues().keyboardType
)
