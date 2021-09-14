package org.tm.archive.components.settings.app.privacy.expire

import android.content.Context
import androidx.annotation.WorkerThread
import org.signal.core.util.concurrent.SignalExecutors
import org.signal.core.util.logging.Log
import org.tm.archive.database.DatabaseFactory
import org.tm.archive.database.ThreadDatabase
import org.tm.archive.groups.GroupChangeException
import org.tm.archive.groups.GroupManager
import org.tm.archive.mms.OutgoingExpirationUpdateMessage
import org.tm.archive.recipients.Recipient
import org.tm.archive.recipients.RecipientId
import org.tm.archive.sms.MessageSender
import java.io.IOException

private val TAG: String = Log.tag(ExpireTimerSettingsRepository::class.java)

/**
 * Provide operations to set expire timer for individuals and groups.
 */
class ExpireTimerSettingsRepository(val context: Context) {

  fun setExpiration(recipientId: RecipientId, newExpirationTime: Int, consumer: (Result<Int>) -> Unit) {
    SignalExecutors.BOUNDED.execute {
      val recipient = Recipient.resolved(recipientId)
      if (recipient.groupId.isPresent && recipient.groupId.get().isPush) {
        try {
          GroupManager.updateGroupTimer(context, recipient.groupId.get().requirePush(), newExpirationTime)
          consumer.invoke(Result.success(newExpirationTime))
        } catch (e: GroupChangeException) {
          Log.w(TAG, e)
          consumer.invoke(Result.failure(e))
        } catch (e: IOException) {
          Log.w(TAG, e)
          consumer.invoke(Result.failure(e))
        }
      } else {
        DatabaseFactory.getRecipientDatabase(context).setExpireMessages(recipientId, newExpirationTime)
        val outgoingMessage = OutgoingExpirationUpdateMessage(Recipient.resolved(recipientId), System.currentTimeMillis(), newExpirationTime * 1000L)
        MessageSender.send(context, outgoingMessage, getThreadId(recipientId), false, null, null)
        consumer.invoke(Result.success(newExpirationTime))
      }
    }
  }

  @WorkerThread
  private fun getThreadId(recipientId: RecipientId): Long {
    val threadDatabase: ThreadDatabase = DatabaseFactory.getThreadDatabase(context)
    val recipient: Recipient = Recipient.resolved(recipientId)
    return threadDatabase.getOrCreateThreadIdFor(recipient)
  }
}
