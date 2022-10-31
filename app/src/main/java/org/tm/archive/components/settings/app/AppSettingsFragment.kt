package org.tm.archive.components.settings.app

import android.app.AlertDialog
import android.app.Dialog
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tm.androidcopysdk.AndroidCopySDK
import com.tm.androidcopysdk.ISendLogCallback
import com.tm.androidcopysdk.utils.PrefManager
import org.archiver.ArchivePreferenceConstants
import org.selfAuthentication.ProgressDialog
import org.signal.glide.Log
import org.tm.archive.R
import org.tm.archive.badges.BadgeImageView
import org.tm.archive.components.AvatarImageView
import org.tm.archive.components.settings.DSLConfiguration
import org.tm.archive.components.settings.DSLSettingsFragment
import org.tm.archive.components.settings.DSLSettingsIcon
import org.tm.archive.components.settings.DSLSettingsText
import org.tm.archive.components.settings.PreferenceModel
import org.tm.archive.components.settings.PreferenceViewHolder
import org.tm.archive.components.settings.configure
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.phonenumbers.PhoneNumberFormatter
import org.tm.archive.recipients.Recipient
import org.tm.archive.util.FeatureFlags
import org.tm.archive.util.PlayServicesUtil
import org.tm.archive.util.Util
import org.tm.archive.util.adapter.mapping.LayoutFactory
import org.tm.archive.util.adapter.mapping.MappingAdapter
import org.tm.archive.util.adapter.mapping.MappingViewHolder
import org.tm.archive.util.navigation.safeNavigate

class AppSettingsFragment : DSLSettingsFragment(R.string.text_secure_normal__menu_settings),
  ISendLogCallback { //**TM_SA**// add ISendLogCallback

  private val viewModel: AppSettingsViewModel by viewModels()

  lateinit var mProgressDialog : Dialog //**TM_SA**//

  override fun bindAdapter(adapter: MappingAdapter) {
    adapter.registerFactory(BioPreference::class.java, LayoutFactory(::BioPreferenceViewHolder, R.layout.bio_preference_item))
    adapter.registerFactory(PaymentsPreference::class.java, LayoutFactory(::PaymentsPreferenceViewHolder, R.layout.dsl_payments_preference))
    adapter.registerFactory(SubscriptionPreference::class.java, LayoutFactory(::SubscriptionPreferenceViewHolder, R.layout.dsl_preference_item))

    viewModel.state.observe(viewLifecycleOwner) { state ->
      adapter.submitList(getConfiguration(state).toMappingModelList())
    }
  }

  override fun onResume() {
    super.onResume()
    viewModel.refreshExpiredGiftBadge()
  }

  private fun getConfiguration(state: AppSettingsState): DSLConfiguration {
    return configure {

      customPref(
        BioPreference(state.self) {
          findNavController().safeNavigate(R.id.action_appSettingsFragment_to_manageProfileActivity)
        }
      )

      clickPref(
        title = DSLSettingsText.from(R.string.AccountSettingsFragment__account),
        icon = DSLSettingsIcon.from(R.drawable.ic_profile_circle_24),
        onClick = {
          findNavController().safeNavigate(R.id.action_appSettingsFragment_to_accountSettingsFragment)
        }
      )

      clickPref(
        title = DSLSettingsText.from(R.string.preferences__linked_devices),
        icon = DSLSettingsIcon.from(R.drawable.ic_linked_devices_24),
        onClick = {
          findNavController().safeNavigate(R.id.action_appSettingsFragment_to_deviceActivity)
        }
      )



      dividerPref()

      clickPref(
        title = DSLSettingsText.from(R.string.preferences__appearance),
        icon = DSLSettingsIcon.from(R.drawable.ic_appearance_24),
        onClick = {
          findNavController().safeNavigate(R.id.action_appSettingsFragment_to_appearanceSettingsFragment)
        }
      )

      clickPref(
        title = DSLSettingsText.from(R.string.preferences_chats__chats),
        icon = DSLSettingsIcon.from(R.drawable.ic_chat_message_24),
        onClick = {
          findNavController().safeNavigate(R.id.action_appSettingsFragment_to_chatsSettingsFragment)
        }
      )

      clickPref(
        title = DSLSettingsText.from(R.string.preferences__notifications),
        icon = DSLSettingsIcon.from(R.drawable.ic_bell_24),
        onClick = {
          findNavController().safeNavigate(R.id.action_appSettingsFragment_to_notificationsSettingsFragment)
        }
      )

      clickPref(
        title = DSLSettingsText.from(R.string.preferences__privacy),
        icon = DSLSettingsIcon.from(R.drawable.ic_lock_24),
        onClick = {
          findNavController().safeNavigate(R.id.action_appSettingsFragment_to_privacySettingsFragment)
        }
      )

      clickPref(
        title = DSLSettingsText.from(R.string.preferences__data_and_storage),
        icon = DSLSettingsIcon.from(R.drawable.ic_archive_24dp),
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
        icon = DSLSettingsIcon.from(R.drawable.ic_invite_24),
        onClick = {
          findNavController().safeNavigate(R.id.action_appSettingsFragment_to_inviteActivity)
        }
      )

      //**TM_SA**// Start - Comment all the Signal mention and put our about and sending logs logic.
      if (false/*PlayServicesUtil.getPlayServicesStatus(requireContext()) == PlayServicesUtil.PlayServicesStatus.SUCCESS*/) {
        /*clickPref(
          title = DSLSettingsText.from(R.string.preferences__donate_to_signal),
          icon = DSLSettingsIcon.from(R.drawable.ic_heart_24),
          iconEnd = if (state.hasExpiredGiftBadge) DSLSettingsIcon.from(R.drawable.ic_info_solid_24, R.color.signal_accent_primary) else null,
          onClick = {
            findNavController().safeNavigate(AppSettingsFragmentDirections.actionAppSettingsFragmentToManageDonationsFragment())
          },
          onLongClick = this@AppSettingsFragment::copySubscriberIdToClipboard
        )*/
      } else {
        /*externalLinkPref(
          title = DSLSettingsText.from(R.string.preferences__donate_to_signal),
          icon = DSLSettingsIcon.from(R.drawable.ic_heart_24),
          linkId = R.string.donate_url
        )*/

        clickPref(
          title = DSLSettingsText.from(R.string.preferences__send_logs_to_telemessage),
          icon = DSLSettingsIcon.from(R.drawable.ic_settings_logs_icon),
          onClick = {
            doSendLogsClicked()
          }
        )

        clickPref(
          title = DSLSettingsText.from(R.string.EditAboutFragment_about),
          icon = DSLSettingsIcon.from(R.drawable.ic_about_icon),
          onClick = {
            findNavController().safeNavigate(R.id.action_appSettingsFragment_to_tmAboutSettings)
          }
        )

        //**TM_SA**// End
      }

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

  //**TM_SA**// start


  override fun sendLogSucceed() {
    mProgressDialog.hide()
    Log.d("E#E#@E@#", "sendLogSucceed")
  }

  override fun sendLogFailure() {
    mProgressDialog.hide()
    Log.d("E#E#@E@#", "sendLogFailure")
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
        ArchivePreferenceConstants.GENERATE_TOK_NAME,
        ArchivePreferenceConstants.GENERATE_TOK_PASS
      )
    }
    builder.setNegativeButton(R.string.CommunicationActions_cancel, null)
    builder.show()

  }

  //**TM_SA**// End

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

  private class BioPreference(val recipient: Recipient, val onClick: () -> Unit) : PreferenceModel<BioPreference>() {
    override fun areContentsTheSame(newItem: BioPreference): Boolean {
      return super.areContentsTheSame(newItem) && recipient.hasSameContent(newItem.recipient)
    }

    override fun areItemsTheSame(newItem: BioPreference): Boolean {
      return recipient == newItem.recipient
    }
  }

  private class BioPreferenceViewHolder(itemView: View) : PreferenceViewHolder<BioPreference>(itemView) {

    private val avatarView: AvatarImageView = itemView.findViewById(R.id.icon)
    private val aboutView: TextView = itemView.findViewById(R.id.about)
    private val badgeView: BadgeImageView = itemView.findViewById(R.id.badge)

    override fun bind(model: BioPreference) {
      super.bind(model)

      itemView.setOnClickListener { model.onClick() }

      titleView.text = model.recipient.profileName.toString()
      summaryView.text = PhoneNumberFormatter.prettyPrint(model.recipient.requireE164())
      avatarView.setRecipient(Recipient.self())
      badgeView.setBadgeFromRecipient(Recipient.self())

      titleView.visibility = View.VISIBLE
      summaryView.visibility = View.VISIBLE
      avatarView.visibility = View.VISIBLE

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
