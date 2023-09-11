package org.tm.archive.conversation.ui.inlinequery

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import org.tm.archive.components.mention.MentionAnnotation
import org.tm.archive.database.MentionUtil
import org.tm.archive.recipients.Recipient

/**
 * Encapsulate how to replace a query with a user selected result.
 */
sealed class InlineQueryReplacement(@get:JvmName("isKeywordSearch") val keywordSearch: Boolean = false) {
  abstract fun toCharSequence(context: Context): CharSequence

  class Emoji(private val emoji: String, keywordSearch: Boolean) : InlineQueryReplacement(keywordSearch) {
    override fun toCharSequence(context: Context): CharSequence {
      return emoji
    }
  }

  class Mention(private val recipient: Recipient, keywordSearch: Boolean) : InlineQueryReplacement(keywordSearch) {
    override fun toCharSequence(context: Context): CharSequence {
      val builder = SpannableStringBuilder().apply {
        append(MentionUtil.MENTION_STARTER)
        append(recipient.getDisplayName(context))
        append(" ")
      }

      builder.setSpan(MentionAnnotation.mentionAnnotationForRecipientId(recipient.id), 0, builder.length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

      return builder
    }
  }
}
