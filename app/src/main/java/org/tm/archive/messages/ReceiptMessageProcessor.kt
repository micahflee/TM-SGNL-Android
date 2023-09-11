package org.tm.archive.messages

import android.annotation.SuppressLint
import android.content.Context
import org.tm.archive.database.SignalDatabase
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.jobs.PushProcessEarlyMessagesJob
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.messages.MessageContentProcessor.Companion.log
import org.tm.archive.messages.MessageContentProcessor.Companion.warn
import org.tm.archive.recipients.Recipient
import org.tm.archive.recipients.RecipientId
import org.tm.archive.util.EarlyMessageCacheEntry
import org.tm.archive.util.TextSecurePreferences
import org.whispersystems.signalservice.api.crypto.EnvelopeMetadata
import org.whispersystems.signalservice.internal.push.SignalServiceProtos
import org.whispersystems.signalservice.internal.push.SignalServiceProtos.Envelope
import org.whispersystems.signalservice.internal.push.SignalServiceProtos.ReceiptMessage

object ReceiptMessageProcessor {
  fun process(context: Context, senderRecipient: Recipient, envelope: Envelope, content: SignalServiceProtos.Content, metadata: EnvelopeMetadata, earlyMessageCacheEntry: EarlyMessageCacheEntry?) {
    val receiptMessage = content.receiptMessage

    when (receiptMessage.type) {
      ReceiptMessage.Type.DELIVERY -> handleDeliveryReceipt(envelope, metadata, receiptMessage, senderRecipient.id)
      ReceiptMessage.Type.READ -> handleReadReceipt(context, senderRecipient.id, envelope, metadata, receiptMessage, earlyMessageCacheEntry)
      ReceiptMessage.Type.VIEWED -> handleViewedReceipt(context, envelope, metadata, receiptMessage, senderRecipient.id, earlyMessageCacheEntry)
      else -> warn(envelope.timestamp, "Unknown recipient message type ${receiptMessage.type}")
    }
  }

  @SuppressLint("DefaultLocale")
  private fun handleDeliveryReceipt(
    envelope: Envelope,
    metadata: EnvelopeMetadata,
    deliveryReceipt: ReceiptMessage,
    senderRecipientId: RecipientId
  ) {
    log(envelope.timestamp, "Processing delivery receipts. Sender: $senderRecipientId, Device: ${metadata.sourceDeviceId}, Timestamps: ${deliveryReceipt.timestampList.joinToString(", ")}")

    val missingTargetTimestamps: Set<Long> = SignalDatabase.messages.incrementDeliveryReceiptCounts(deliveryReceipt.timestampList, senderRecipientId, envelope.timestamp)

    for (targetTimestamp in missingTargetTimestamps) {
      warn(envelope.timestamp, "[handleDeliveryReceipt] Could not find matching message! targetTimestamp: $targetTimestamp, receiptAuthor: $senderRecipientId")
      // Early delivery receipts are special-cased in the database methods
    }

    if (missingTargetTimestamps.isNotEmpty()) {
      PushProcessEarlyMessagesJob.enqueue()
    }

    SignalDatabase.pendingPniSignatureMessages.acknowledgeReceipts(senderRecipientId, deliveryReceipt.timestampList, metadata.sourceDeviceId)
    SignalDatabase.messageLog.deleteEntriesForRecipient(deliveryReceipt.timestampList, senderRecipientId, metadata.sourceDeviceId)
  }

  @SuppressLint("DefaultLocale")
  private fun handleReadReceipt(
    context: Context,
    senderRecipientId: RecipientId,
    envelope: Envelope,
    metadata: EnvelopeMetadata,
    readReceipt: ReceiptMessage,
    earlyMessageCacheEntry: EarlyMessageCacheEntry?
  ) {
    if (!TextSecurePreferences.isReadReceiptsEnabled(context)) {
      log(envelope.timestamp, "Ignoring read receipts for IDs: " + readReceipt.timestampList.joinToString(", "))
      return
    }

    log(envelope.timestamp, "Processing read receipts. Sender: $senderRecipientId, Device: ${metadata.sourceDeviceId}, Timestamps: ${readReceipt.timestampList.joinToString(", ")}")

    val missingTargetTimestamps: Set<Long> = SignalDatabase.messages.incrementReadReceiptCounts(readReceipt.timestampList, senderRecipientId, envelope.timestamp)

    if (missingTargetTimestamps.isNotEmpty()) {
      val selfId = Recipient.self().id

      for (targetTimestamp in missingTargetTimestamps) {
        warn(envelope.timestamp, "[handleReadReceipt] Could not find matching message! targetTimestamp: $targetTimestamp, receiptAuthor: $senderRecipientId | Receipt, so associating with message from self ($selfId)")
        if (earlyMessageCacheEntry != null) {
          ApplicationDependencies.getEarlyMessageCache().store(selfId, targetTimestamp, earlyMessageCacheEntry)
        }
      }
    }

    if (missingTargetTimestamps.isNotEmpty() && earlyMessageCacheEntry != null) {
      PushProcessEarlyMessagesJob.enqueue()
    }
  }

  private fun handleViewedReceipt(
    context: Context,
    envelope: Envelope,
    metadata: EnvelopeMetadata,
    viewedReceipt: ReceiptMessage,
    senderRecipientId: RecipientId,
    earlyMessageCacheEntry: EarlyMessageCacheEntry?
  ) {
    val readReceipts = TextSecurePreferences.isReadReceiptsEnabled(context)
    val storyViewedReceipts = SignalStore.storyValues().viewedReceiptsEnabled

    if (!readReceipts && !storyViewedReceipts) {
      log(envelope.timestamp, "Ignoring viewed receipts for IDs: ${viewedReceipt.timestampList.joinToString(", ")}")
      return
    }

    log(envelope.timestamp, "Processing viewed receipts. Sender: $senderRecipientId, Device: ${metadata.sourceDeviceId}, Only Stories: ${!readReceipts}, Timestamps: ${viewedReceipt.timestampList.joinToString(", ")}")

    val missingTargetTimestamps: Set<Long> = if (readReceipts && storyViewedReceipts) {
      SignalDatabase.messages.incrementViewedReceiptCounts(viewedReceipt.timestampList, senderRecipientId, envelope.timestamp)
    } else if (readReceipts) {
      SignalDatabase.messages.incrementViewedNonStoryReceiptCounts(viewedReceipt.timestampList, senderRecipientId, envelope.timestamp)
    } else {
      SignalDatabase.messages.incrementViewedStoryReceiptCounts(viewedReceipt.timestampList, senderRecipientId, envelope.timestamp)
    }

    val foundTargetTimestamps: Set<Long> = viewedReceipt.timestampList.toSet() - missingTargetTimestamps.toSet()
    SignalDatabase.messages.updateViewedStories(foundTargetTimestamps)

    if (missingTargetTimestamps.isNotEmpty()) {
      val selfId = Recipient.self().id

      for (targetTimestamp in missingTargetTimestamps) {
        warn(envelope.timestamp, "[handleViewedReceipt] Could not find matching message! targetTimestamp: $targetTimestamp, receiptAuthor: $senderRecipientId | Receipt so associating with message from self ($selfId)")
        if (earlyMessageCacheEntry != null) {
          ApplicationDependencies.getEarlyMessageCache().store(selfId, targetTimestamp, earlyMessageCacheEntry)
        }
      }
    }

    if (missingTargetTimestamps.isNotEmpty() && earlyMessageCacheEntry != null) {
      PushProcessEarlyMessagesJob.enqueue()
    }
  }
}
