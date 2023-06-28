package org.tm.archive.components.settings.app

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tm.androidcopysdk.AndroidCopySDK
import com.tm.androidcopysdk.ISendLogCallback
import com.tm.androidcopysdk.utils.PrefManager
import org.archiver.ArchivePreferenceConstants
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.selfAuthentication.ProgressDialog
import org.signal.glide.Log
import org.tm.archive.R
import org.tm.archive.badges.BadgeImageView
import org.tm.archive.components.AvatarImageView
import org.tm.archive.components.emoji.EmojiTextView
import org.tm.archive.components.reminder.ExpiredBuildReminder
import org.tm.archive.components.reminder.Reminder
import org.tm.archive.components.reminder.ReminderView
import org.tm.archive.components.reminder.UnauthorizedReminder
import org.tm.archive.components.settings.DSLConfiguration
import org.tm.archive.components.settings.DSLSettingsFragment
import org.tm.archive.components.settings.DSLSettingsIcon
import org.tm.archive.components.settings.DSLSettingsText
import org.tm.archive.components.settings.PreferenceModel
import org.tm.archive.components.settings.PreferenceViewHolder
import org.tm.archive.components.settings.configure
import org.tm.archive.events.ReminderUpdateEvent
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.phonenumbers.PhoneNumberFormatter
import org.tm.archive.recipients.Recipient
import org.tm.archive.registration.RegistrationNavigationActivity
import org.tm.archive.util.FeatureFlags
import org.tm.archive.util.PlayStoreUtil
import org.tm.archive.util.Util
import org.tm.archive.util.ViewUtil
import org.tm.archive.util.adapter.mapping.LayoutFactory
import org.tm.archive.util.adapter.mapping.MappingAdapter
import org.tm.archive.util.adapter.mapping.MappingViewHolder
import org.tm.archive.util.navigation.safeNavigate
import org.tm.archive.util.views.Stub

class AppSettingsFragment : DSLSettingsFragment(
  titleId = R.string.text_secure_normal__menu_settings,
  layoutId = R.layout.dsl_settings_fragment_with_reminder
) ,
  ISendLogCallback { //**TM_SA**// add ISendLogCallback{

  private val viewModel: AppSettingsViewModel by viewModels()

  private lateinit var reminderView: Stub<ReminderView>

  lateinit var mProgressDialog : Dialog //**TM_SA**//

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    reminderView = ViewUtil.findStubById(view, R.id.reminder_stub)

    updateReminders()
  }

  override fun bindAdapter(adapter: MappingAdapter) {
    adapter.registerFactory(BioPreference::class.java, LayoutFactory(::BioPreferenceViewHolder, R.layout.bio_preference_item))
    adapter.registerFactory(PaymentsPreference::class.java, LayoutFactory(::PaymentsPreferenceViewHolder, R.layout.dsl_payments_preference))
    adapter.registerFactory(SubscriptionPreference::class.java, LayoutFactory(::SubscriptionPreferenceViewHolder, R.layout.dsl_preference_item))

    viewModel.state.observe(viewLifecycleOwner) { state ->
      adapter.submitList(getConfiguration(state).toMappingModelList())
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onEvent(event: ReminderUpdateEvent?) {
    updateReminders()
  }

  private fun updateReminders() {
    if (ExpiredBuildReminder.isEligible()) {
      showReminder(ExpiredBuildReminder(context))
    } else if (UnauthorizedReminder.isEligible(context)) {
      showReminder(UnauthorizedReminder(context))
    } else {
      hideReminders()
    }
    viewModel.refreshDeprecatedOrUnregistered()
  }

  private fun showReminder(reminder: Reminder) {
    if (!reminderView.resolved()) {
      reminderView.get().addOnLayoutChangeListener { _, _, top, _, bottom, _, _, _, _ ->
        recyclerView?.setPadding(0, bottom - top, 0, 0)
      }
      recyclerView?.clipToPadding = false
    }
    reminderView.get().showReminder(reminder)
    reminderView.get().setOnActionClickListener { reminderActionId: Int -> this.handleReminderAction(reminderActionId) }
  }

  private fun hideReminders() {
    if (reminderView.resolved()) {
      reminderView.get().hide()
      recyclerView?.clipToPadding = true
    }
  }

  private fun handleReminderAction(@IdRes reminderActionId: Int) {
    when (reminderActionId) {
      R.id.reminder_action_update_now -> {
        PlayStoreUtil.openPlayStoreOrOurApkDownloadPage(requireContext())
      }
      R.id.reminder_action_re_register -> {
        startActivity(RegistrationNavigationActivity.newIntentForReRegistration(requireContext()))
      }
    }
  }

  override fun onResume() {
    super.onResume()
    viewModel.refreshExpiredGiftBadge()
    EventBus.getDefault().register(this)
  }

  override fun onPause() {
    super.onPause()
    EventBus.getDefault().unregister(this)
  }

  private fun getConfiguration(state: AppSettingsState): DSLConfiguration {
    return configure {
      customPref(
        BioPreference(
          recipient = state.self,
          onRowClicked = {
            findNavController().safeNavigate(R.id.action_appSettingsFragment_to_manageProfileActivity)
          },
          onQrButtonClicked = {
            if (Recipient.self().getUsername().isPresent()) {
              findNavController().safeNavigate(R.id.action_appSettingsFragment_to_usernameLinkSettingsFragment)
            } else {
              findNavController().safeNavigate(R.id.action_appSettingsFragment_to_usernameEducationFragment)
            }
          }
        )
      )

      clickPref(
        title = DSLSettingsText.from(R.string.AccountSettingsFragment__account),
        icon = DSLSettingsIcon.from(R.drawable.symbol_person_circle_24),
        onClick = {
          findNavController().safeNavigate(R.id.action_appSettingsFragment_to_accountSettingsFragment)
        }
      )

      clickPref(
        title = DSLSettingsText.from(R.string.preferences__linked_devices),
        icon = DSLSettingsIcon.from(R.drawable.symbol_devices_24),
        onClick = {
          findNavController().safeNavigate(R.id.action_appSettingsFragment_to_deviceActivity)
        },
        isEnabled = state.isDeprecatedOrUnregistered()
      )

      //**TM_SA**// Start - Comment all the Signal mention and put our about and sending logs logic.
      if (false/*state.allowUserToGoToDonationManagementScreen*/) {
       /* clickPref(
          title = DSLSettingsText.from(R.string.preferences__donate_to_signal),
          icon = DSLSettingsIcon.from(R.drawable.symbol_heart_24),
          iconEnd = if (state.hasExpiredGiftBadge) DSLSettingsIcon.from(R.drawable.symbol_info_fill_24, R.color.signal_accent_primary) else null,
          onClick = {
            findNavController().safeNavigate(AppSettingsFragmentDirections.actionAppSettingsFragmentToManageDonationsFragment())
          },
          onLongClick = this@AppSettingsFragment::copySubscriberIdToClipboard
        )*/
      } else {
       /* externalLinkPref(
          title = DSLSettingsText.from(R.string.preferences__donate_to_signal),
          icon = DSLSettingsIcon.from(R.drawable.symbol_heart_24),
          linkId = R.string.donate_url
        )*/

        clickPref(
          title = DSLSettingsText.from(R.string.preferences__send_logs_to_telemessage),
          icon = DSLSettingsIcon.from(R.drawable.ic_settings_logs_icon),
          onClick = {
            doSendLogsClicked()
          })

        clickPref(
          title = DSLSettingsText.from(R.string.EditAboutFragment_about),
          icon = DSLSettingsIcon.from(R.drawable.ic_about_icon),
          onClick = {
            findNavController().safeNavigate(R.id.action_appSettingsFragment_to_tmAboutSettings)
          }
        )

        //**TM_SA**// End
      }

      dividerPref()

      clickPref(
        title = DSLSettingsText.from(R.string.preferences__appearance),
        icon = DSLSettingsIcon.from(R.drawable.symbol_appearance_24),
        onClick = {
          findNavController().safeNavigate(R.id.action_appSettingsFragment_to_appearanceSettingsFragment)
        }
      )

      clickPref(
        title = DSLSettingsText.from(R.string.preferences_chats__chats),
        icon = DSLSettingsIcon.from(R.drawable.symbol_chat_24),
        onClick = {
          findNavController().safeNavigate(R.id.action_appSettingsFragment_to_chatsSettingsFragment)
        },
        isEnabled = state.isDeprecatedOrUnregistered()
      )

      clickPref(
        title = DSLSettingsText.from(R.string.preferences__stories),
        icon = DSLSettingsIcon.from(R.drawable.symbol_stories_24),
        onClick = {
          findNavController().safeNavigate(AppSettingsFragmentDirections.actionAppSettingsFragmentToStoryPrivacySettings(R.string.preferences__stories))
        },
        isEnabled = state.isDeprecatedOrUnregistered()
      )

      clickPref(
        title = DSLSettingsText.from(R.string.preferences__notifications),
        icon = DSLSettingsIcon.from(R.drawable.symbol_bell_24),
        onClick = {
          findNavController().safeNavigate(R.id.action_appSettingsFragment_to_notificationsSettingsFragment)
        },
        isEnabled = state.isDeprecatedOrUnregistered()
      )

      clickPref(
        title = DSLSettingsText.from(R.string.preferences__privacy),
        icon = DSLSettingsIcon.from(R.drawable.symbol_lock_24),
        onClick = {
          findNavController().safeNavigate(R.id.action_appSettingsFragment_to_privacySettingsFragment)
        },
        isEnabled = state.isDeprecatedOrUnregistered()
      )

      clickPref(
        title = DSLSettingsText.from(R.string.preferences__data_and_storage),
        icon = DSLSettingsIcon.from(R.drawable.symbol_data_24),
        onClick = {
          findNavController().safeNavigate(R.id.action_appSettingsFragment_to_dataAndStorageSettingsFragment)
        }
      )

      dividerPref()

      if (SignalStore.paymentsValues().paymentsAvailability.showPaymentsMenu()) {
        customPref(
          PaymentsPreference(
            unreadCount = state.unreadPaymentsCount
          ) {
            findNavController().safeNavigate(R.id.action_appSettingsFragment_to_paymentsActivity)
          }
        )

        dividerPref()
      }

      //**TM_SA**// Mark this part
      /*clickPref(
        title = DSLSettingsText.from(R.string.preferences__help),
        icon = DSLSettingsIcon.from(R.drawable.ic_help_24),
        onClick = {
          findNavController().safeNavigate(R.id.action_appSettingsFragment_to_helpSettingsFragment)
        }
      )*/
      //**TM_SA**//

      clickPref(
        title = DSLSettingsText.from(R.string.AppSettingsFragment__invite_your_friends),
        icon = DSLSettingsIcon.from(R.drawable.symbol_invite_24),
        onClick = {
          findNavController().safeNavigate(R.id.action_appSettingsFragment_to_inviteActivity)
        }
      )

      if (FeatureFlags.internalUser()) {
        dividerPref()

        clickPref(
          title = DSLSettingsText.from(R.string.preferences__internal_preferences),
          onClick = {
            findNavController().safeNavigate(R.id.action_appSettingsFragment_to_internalSettingsFragment)
          }
        )
      }
    }
  }


  //**TM_SA**// start


  override fun sendLogSucceed() {
    mProgressDialog.hide()
    com.tm.logger.Log.d("sendLog", "sendLogSucceed")
  }

  override fun sendLogFailure() {
    mProgressDialog.hide()
    com.tm.logger.Log.d("sendLog", "sendLogFailure")
  }


  private fun doSendLogsClicked() {

    val builder = AlertDialog.Builder(context)

    mProgressDialog = ProgressDialog.progressDialog(requireContext())

    builder.setTitle(R.string.issue_report_list_title)
    builder.setMessage(getString(R.string.issue_report_list_summery) + "?")

    builder.setPositiveButton(R.string.ShareActivity__send) { dialog, which ->

      mProgressDialog.show()

      AndroidCopySDK.getInstance(context).sentLogs(
        activity,
        this,
        PrefManager.getStringPref(context, ArchivePreferenceConstants.PREF_KEY_DEVICE_PHONE_NUMBER, ""),
        "Signal Archiver logs",
        PrefManager.getStringPref(context, ArchivePreferenceConstants.PREF_KEY_DEVICE_NAME, ""),
        "",
        "",
        "",
        "",
        ArchivePreferenceConstants.GENERATE_TOK_NAME,
        ArchivePreferenceConstants.GENERATE_TOK_PASS
      )
    }
    builder.setNegativeButton(R.string.CommunicationActions_cancel, null)
    builder.show()

  }

  //**TM_SA**// End

  private fun copySubscriberIdToClipboard(): Boolean {
    val subscriber = SignalStore.donationsValues().getSubscriber()
    return if (subscriber == null) {
      false
    } else {
      Toast.makeText(requireContext(), R.string.AppSettingsFragment__copied_subscriber_id_to_clipboard, Toast.LENGTH_LONG).show()
      Util.copyToClipboard(requireContext(), subscriber.subscriberId.serialize())
      true
    }
  }

  private class SubscriptionPreference(
    override val title: DSLSettingsText,
    override val summary: DSLSettingsText? = null,
    override val icon: DSLSettingsIcon? = null,
    override val isEnabled: Boolean = true,
    val isActive: Boolean = false,
    val onClick: (Boolean) -> Unit,
    val onLongClick: () -> Boolean
  ) : PreferenceModel<SubscriptionPreference>() {
    override fun areItemsTheSame(newItem: SubscriptionPreference): Boolean {
      return true
    }

    override fun areContentsTheSame(newItem: SubscriptionPreference): Boolean {
      return super.areContentsTheSame(newItem) && isActive == newItem.isActive
    }
  }

  private class SubscriptionPreferenceViewHolder(itemView: View) : PreferenceViewHolder<SubscriptionPreference>(itemView) {
    override fun bind(model: SubscriptionPreference) {
      super.bind(model)
      itemView.setOnClickListener { model.onClick(model.isActive) }
      itemView.setOnLongClickListener { model.onLongClick() }
    }
  }

  private class BioPreference(val recipient: Recipient, val onRowClicked: () -> Unit, val onQrButtonClicked: () -> Unit) : PreferenceModel<BioPreference>() {
    override fun areContentsTheSame(newItem: BioPreference): Boolean {
      return super.areContentsTheSame(newItem) && recipient.hasSameContent(newItem.recipient)
    }

    override fun areItemsTheSame(newItem: BioPreference): Boolean {
      return recipient == newItem.recipient
    }
  }

  private class BioPreferenceViewHolder(itemView: View) : PreferenceViewHolder<BioPreference>(itemView) {

    private val avatarView: AvatarImageView = itemView.findViewById(R.id.icon)
    private val aboutView: EmojiTextView = itemView.findViewById(R.id.about)
    private val badgeView: BadgeImageView = itemView.findViewById(R.id.badge)
    private val qrButton: View = itemView.findViewById(R.id.qr_button)

    init {
      aboutView.setOverflowText(" ")
    }

    override fun bind(model: BioPreference) {
      super.bind(model)

      itemView.setOnClickListener { model.onRowClicked() }

      titleView.text = model.recipient.profileName.toString()
      summaryView.text = PhoneNumberFormatter.prettyPrint(model.recipient.requireE164())
      avatarView.setRecipient(Recipient.self())
      badgeView.setBadgeFromRecipient(Recipient.self())

      titleView.visibility = View.VISIBLE
      summaryView.visibility = View.VISIBLE
      avatarView.visibility = View.VISIBLE

      if (FeatureFlags.usernames()) {
        qrButton.visibility = View.VISIBLE
        qrButton.isClickable = true
        qrButton.setOnClickListener { model.onQrButtonClicked() }
      } else {
        qrButton.visibility = View.GONE
      }

      if (model.recipient.combinedAboutAndEmoji != null) {
        aboutView.text = model.recipient.combinedAboutAndEmoji
        aboutView.visibility = View.VISIBLE
      } else {
        aboutView.visibility = View.GONE
      }
    }
  }

  private class PaymentsPreference(val unreadCount: Int, val onClick: () -> Unit) : PreferenceModel<PaymentsPreference>() {
    override fun areContentsTheSame(newItem: PaymentsPreference): Boolean {
      return super.areContentsTheSame(newItem) && unreadCount == newItem.unreadCount
    }

    override fun areItemsTheSame(newItem: PaymentsPreference): Boolean {
      return true
    }
  }

  private class PaymentsPreferenceViewHolder(itemView: View) : MappingViewHolder<PaymentsPreference>(itemView) {

    private val unreadCountView: TextView = itemView.findViewById(R.id.unread_indicator)

    override fun bind(model: PaymentsPreference) {
      unreadCountView.text = model.unreadCount.toString()
      unreadCountView.visibility = if (model.unreadCount > 0) View.VISIBLE else View.GONE

      itemView.setOnClickListener {
        model.onClick()
      }
    }
  }
}
