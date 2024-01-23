package org.tm.archive.conversation

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.signal.core.util.StreamUtil
import org.signal.core.util.concurrent.LifecycleDisposable
import org.signal.core.util.concurrent.SimpleTask
import org.signal.core.util.dp
import org.signal.core.util.logging.Log
import org.tm.archive.R
import org.tm.archive.components.FixedRoundedCornerBottomSheetDialogFragment
import org.tm.archive.components.SignalProgressDialog
import org.tm.archive.components.menu.ActionItem
import org.tm.archive.components.menu.SignalContextMenu
import org.tm.archive.components.recyclerview.SmoothScrollingLinearLayoutManager
import org.tm.archive.conversation.colors.Colorizer
import org.tm.archive.conversation.colors.RecyclerViewColorizer
import org.tm.archive.conversation.mutiselect.MultiselectPart
import org.tm.archive.conversation.mutiselect.MultiselectPart.Attachments
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.model.MessageRecord
import org.tm.archive.database.model.MmsMessageRecord
import org.tm.archive.giph.mp4.GiphyMp4ItemDecoration
import org.tm.archive.giph.mp4.GiphyMp4PlaybackController
import org.tm.archive.giph.mp4.GiphyMp4PlaybackPolicy
import org.tm.archive.giph.mp4.GiphyMp4ProjectionPlayerHolder
import org.tm.archive.giph.mp4.GiphyMp4ProjectionRecycler
import org.tm.archive.groups.GroupId
import org.tm.archive.groups.GroupMigrationMembershipChange
import org.tm.archive.mms.GlideApp
import org.tm.archive.mms.PartAuthority
import org.tm.archive.mms.TextSlide
import org.tm.archive.recipients.Recipient
import org.tm.archive.recipients.RecipientId
import org.tm.archive.util.BottomSheetUtil
import org.tm.archive.util.BottomSheetUtil.requireCoordinatorLayout
import org.tm.archive.util.StickyHeaderDecoration
import org.tm.archive.util.Util
import org.tm.archive.util.fragments.requireListener
import org.tm.archive.util.hasTextSlide
import org.tm.archive.util.requireTextSlide
import java.io.IOException
import java.util.Locale

/**
 * Bottom sheet dialog to view all scheduled messages within a given thread.
 */
class ScheduledMessagesBottomSheet : FixedRoundedCornerBottomSheetDialogFragment(), ScheduleMessageTimePickerBottomSheet.RescheduleCallback {

  override val peekHeightPercentage: Float = 0.66f
  override val themeResId: Int = R.style.Widget_Signal_FixedRoundedCorners_Messages

  private var firstRender: Boolean = true
  private var deleteDialog: AlertDialog? = null

  private lateinit var messageAdapter: ConversationAdapter
  private lateinit var callback: ConversationBottomSheetCallback

  private val viewModel: ScheduledMessagesViewModel by viewModels(
    factoryProducer = {
      val threadId = requireArguments().getLong(KEY_THREAD_ID)
      ScheduledMessagesViewModel.Factory(threadId)
    }
  )

  private val disposables: LifecycleDisposable = LifecycleDisposable()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val view = inflater.inflate(R.layout.scheduled_messages_bottom_sheet, container, false)
    disposables.bindTo(viewLifecycleOwner)
    return view
  }

  @SuppressLint("WrongThread")
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val conversationRecipientId = RecipientId.from(arguments?.getString(KEY_CONVERSATION_RECIPIENT_ID, null) ?: throw IllegalArgumentException())
    val conversationRecipient = Recipient.resolved(conversationRecipientId)

    callback = requireListener()

    val colorizer = Colorizer()

    messageAdapter = ConversationAdapter(requireContext(), viewLifecycleOwner, GlideApp.with(this), Locale.getDefault(), ConversationAdapterListener(), conversationRecipient.hasWallpaper(), colorizer).apply {
      setCondensedMode(ConversationItemDisplayMode.Condensed(scheduleMessageMode = true))
    }

    val list: RecyclerView = view.findViewById<RecyclerView>(R.id.scheduled_list).apply {
      layoutManager = SmoothScrollingLinearLayoutManager(requireContext(), true)
      adapter = messageAdapter
      itemAnimator = null

      doOnNextLayout {
        // Adding this without waiting for a layout pass would result in an indeterminate amount of padding added to the top of the view
        addItemDecoration(StickyHeaderDecoration(messageAdapter, false, false, ConversationAdapter.HEADER_TYPE_INLINE_DATE))
      }
    }

    val recyclerViewColorizer = RecyclerViewColorizer(list)

    disposables += viewModel.getMessages(requireContext()).subscribe { messages ->
      if (messages.isEmpty()) {
        deleteDialog?.dismiss()
        dismissAllowingStateLoss()
      }

      messageAdapter.submitList(messages) {
        if (firstRender) {
          (list.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(messages.size - 1, 100)

          firstRender = false
        } else if (!list.canScrollVertically(1)) {
          list.layoutManager?.scrollToPosition(0)
        }
      }
      recyclerViewColorizer.setChatColors(conversationRecipient.chatColors)
    }

    initializeGiphyMp4(view.findViewById(R.id.video_container) as ViewGroup, list)
  }

  private fun initializeGiphyMp4(videoContainer: ViewGroup, list: RecyclerView): GiphyMp4ProjectionRecycler {
    val maxPlayback = GiphyMp4PlaybackPolicy.maxSimultaneousPlaybackInConversation()
    val holders = GiphyMp4ProjectionPlayerHolder.injectVideoViews(
      requireContext(),
      viewLifecycleOwner.lifecycle,
      videoContainer,
      maxPlayback
    )
    val callback = GiphyMp4ProjectionRecycler(holders)

    GiphyMp4PlaybackController.attach(list, callback, maxPlayback)
    list.addItemDecoration(GiphyMp4ItemDecoration(callback) {}, 0)

    return callback
  }

  private fun showScheduledMessageContextMenu(view: View, conversationMessage: ConversationMessage) {
    SignalContextMenu.Builder(view, requireCoordinatorLayout())
      .offsetX(12.dp)
      .offsetY(12.dp)
      .preferredVerticalPosition(SignalContextMenu.VerticalPosition.ABOVE)
      .show(getMenuActionItems(conversationMessage))
  }

  private fun getMenuActionItems(message: ConversationMessage): List<ActionItem> {
    val canCopy = message.multiselectCollection.toSet().any { it !is Attachments && message.messageRecord.body.isNotEmpty() }
    val items: MutableList<ActionItem> = ArrayList()
    items.add(ActionItem(R.drawable.symbol_trash_24, resources.getString(R.string.conversation_selection__menu_delete), action = { handleDeleteMessage(message.messageRecord) }))
    if (canCopy) {
      items.add(ActionItem(R.drawable.symbol_copy_android_24, resources.getString(R.string.conversation_selection__menu_copy), action = { handleCopyMessage(message) }))
    }
    items.add(ActionItem(R.drawable.symbol_send_24, resources.getString(R.string.ScheduledMessagesBottomSheet_menu_send_now), action = { handleSendMessageNow(message.messageRecord) }))
    items.add(ActionItem(R.drawable.symbol_calendar_24, resources.getString(R.string.ScheduledMessagesBottomSheet_menu_reschedule), action = { handleRescheduleMessage(message.messageRecord) }))
    return items
  }

  private fun handleRescheduleMessage(messageRecord: MessageRecord) {
    ScheduleMessageTimePickerBottomSheet.showReschedule(childFragmentManager, messageRecord.id, (messageRecord as MmsMessageRecord).scheduledDate)
  }

  private fun handleSendMessageNow(messageRecord: MessageRecord) {
    viewModel.rescheduleMessage(messageRecord.id, System.currentTimeMillis())
  }

  private fun handleDeleteMessage(messageRecord: MessageRecord) {
    deleteDialog?.dismiss()
    deleteDialog = buildDeleteScheduledMessageConfirmationDialog(messageRecord)
      .setOnDismissListener { deleteDialog = null }
      .show()
  }

  private fun handleCopyMessage(message: ConversationMessage) {
    SimpleTask.run(
      viewLifecycleOwner.lifecycle,
      { getMessageText(message) },
      { bodies: CharSequence? ->
        if (!Util.isEmpty(bodies)) {
          Util.copyToClipboard(requireContext(), bodies!!)
        }
      }
    )
  }

  private fun buildDeleteScheduledMessageConfirmationDialog(messageRecord: MessageRecord): AlertDialog.Builder {
    return MaterialAlertDialogBuilder(requireContext())
      .setTitle(resources.getString(R.string.ScheduledMessagesBottomSheet_delete_dialog_message))
      .setCancelable(true)
      .setPositiveButton(R.string.ScheduledMessagesBottomSheet_delete_dialog_action) { _: DialogInterface?, _: Int ->
        deleteMessage(messageRecord.id)
      }
      .setNegativeButton(android.R.string.cancel, null)
  }

  private fun getMessageText(message: ConversationMessage): CharSequence {
    if (message.messageRecord.hasTextSlide()) {
      val textSlide: TextSlide = message.messageRecord.requireTextSlide()
      if (textSlide.uri == null) {
        return message.getDisplayBody(requireContext())
      }
      try {
        PartAuthority.getAttachmentStream(requireContext(), textSlide.uri!!).use { stream ->
          val body = StreamUtil.readFullyAsString(stream)
          return ConversationMessage.ConversationMessageFactory.createWithUnresolvedData(requireContext(), message.messageRecord, body, message.threadRecipient)
            .getDisplayBody(requireContext())
        }
      } catch (e: IOException) {
        Log.w(TAG, "Failed to read text slide data.")
      }
    }
    return ConversationMessage.ConversationMessageFactory.createWithUnresolvedData(requireContext(), message.messageRecord, message.threadRecipient).getDisplayBody(requireContext())
  }

  private fun deleteMessage(messageId: Long) {
    context?.let { context ->
      val progressDialog = SignalProgressDialog.show(
        context = context,
        message = resources.getString(R.string.ScheduledMessagesBottomSheet_deleting_progress_message),
        indeterminate = true
      )
      SimpleTask.run({
        SignalDatabase.messages.deleteScheduledMessage(messageId)
      }, {
        progressDialog.dismiss()
      })
    }
  }

  override fun onReschedule(scheduledTime: Long, messageId: Long) {
    viewModel.rescheduleMessage(messageId, scheduledTime)
  }

  private inner class ConversationAdapterListener : ConversationAdapter.ItemClickListener by callback.getConversationAdapterListener() {
    override fun onQuoteClicked(messageRecord: MmsMessageRecord) {
      dismissAllowingStateLoss()
      callback.getConversationAdapterListener().onQuoteClicked(messageRecord)
    }

    override fun onScheduledIndicatorClicked(view: View, conversationMessage: ConversationMessage) {
      showScheduledMessageContextMenu(view, conversationMessage)
    }

    override fun onGroupMemberClicked(recipientId: RecipientId, groupId: GroupId) {
      dismissAllowingStateLoss()
      callback.getConversationAdapterListener().onGroupMemberClicked(recipientId, groupId)
    }

    override fun onItemClick(item: MultiselectPart) = Unit
    override fun onItemLongClick(itemView: View, item: MultiselectPart) = Unit
    override fun onQuotedIndicatorClicked(messageRecord: MessageRecord) = Unit
    override fun onReactionClicked(multiselectPart: MultiselectPart, messageId: Long, isMms: Boolean) = Unit
    override fun onMessageWithRecaptchaNeededClicked(messageRecord: MessageRecord) = Unit
    override fun onGroupMigrationLearnMoreClicked(membershipChange: GroupMigrationMembershipChange) = Unit
    override fun onChatSessionRefreshLearnMoreClicked() = Unit
    override fun onBadDecryptLearnMoreClicked(author: RecipientId) = Unit
    override fun onSafetyNumberLearnMoreClicked(recipient: Recipient) = Unit
    override fun onJoinGroupCallClicked() = Unit
    override fun onInviteFriendsToGroupClicked(groupId: GroupId.V2) = Unit
    override fun onEnableCallNotificationsClicked() = Unit
    override fun onCallToAction(action: String) = Unit
    override fun onDonateClicked() = Unit
    override fun onRecipientNameClicked(target: RecipientId) = Unit
    override fun onViewGiftBadgeClicked(messageRecord: MessageRecord) = Unit
    override fun onActivatePaymentsClicked() = Unit
    override fun onSendPaymentClicked(recipientId: RecipientId) = Unit
    override fun onEditedIndicatorClicked(messageRecord: MessageRecord) = Unit
  }

  companion object {
    private val TAG = Log.tag(ScheduledMessagesBottomSheet::class.java)

    private const val KEY_THREAD_ID = "thread_id"
    private const val KEY_CONVERSATION_RECIPIENT_ID = "conversation_recipient_id"

    @JvmStatic
    fun show(fragmentManager: FragmentManager, threadId: Long, conversationRecipientId: RecipientId) {
      val args = Bundle().apply {
        putLong(KEY_THREAD_ID, threadId)
        putString(KEY_CONVERSATION_RECIPIENT_ID, conversationRecipientId.serialize())
      }

      val fragment = ScheduledMessagesBottomSheet().apply {
        arguments = args
      }

      fragment.show(fragmentManager, BottomSheetUtil.STANDARD_BOTTOM_SHEET_FRAGMENT_TAG)
    }
  }
}
