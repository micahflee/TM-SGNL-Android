package org.tm.archive.safety

import android.content.DialogInterface
import android.view.View
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.material.button.MaterialButton
import org.signal.core.util.DimensionUnit
import org.signal.core.util.concurrent.LifecycleDisposable
import org.signal.core.util.logging.Log
import org.tm.archive.R
import org.tm.archive.components.WrapperDialogFragment
import org.tm.archive.components.menu.ActionItem
import org.tm.archive.components.settings.DSLConfiguration
import org.tm.archive.components.settings.DSLSettingsAdapter
import org.tm.archive.components.settings.DSLSettingsBottomSheetFragment
import org.tm.archive.components.settings.DSLSettingsText
import org.tm.archive.components.settings.configure
import org.tm.archive.components.settings.models.SplashImage
import org.tm.archive.conversation.ui.error.SafetyNumberChangeRepository
import org.tm.archive.conversation.ui.error.TrustAndVerifyResult
import org.tm.archive.crypto.IdentityKeyParcelable
import org.tm.archive.database.IdentityTable
import org.tm.archive.safety.review.SafetyNumberReviewConnectionsFragment
import org.tm.archive.util.fragments.findListener
import org.tm.archive.util.visible
import org.tm.archive.verify.VerifyIdentityFragment

/**
 * Displays a bottom sheet containing information about safety number changes and allows the user to
 * address these changes.
 */
class SafetyNumberBottomSheetFragment : DSLSettingsBottomSheetFragment(layoutId = R.layout.safety_number_bottom_sheet), WrapperDialogFragment.WrapperDialogFragmentCallback {

  private lateinit var sendAnyway: MaterialButton

  override val peekHeightPercentage: Float = 1f

  @get:MainThread
  private val args: SafetyNumberBottomSheetArgs by lazy(LazyThreadSafetyMode.NONE) {
    SafetyNumberBottomSheet.getArgsFromBundle(requireArguments())
  }

  private val viewModel: SafetyNumberBottomSheetViewModel by viewModels(factoryProducer = {
    SafetyNumberBottomSheetViewModel.Factory(
      args,
      SafetyNumberChangeRepository(requireContext())
    )
  })

  private val lifecycleDisposable = LifecycleDisposable()

  override fun bindAdapter(adapter: DSLSettingsAdapter) {
    val reviewConnections: View = requireView().findViewById(R.id.review_connections)
    sendAnyway = requireView().findViewById(R.id.send_anyway)

    reviewConnections.setOnClickListener {
      viewModel.setDone()
      SafetyNumberReviewConnectionsFragment.show(childFragmentManager)
    }

    sendAnyway.setOnClickListener {
      sendAnyway.isEnabled = false
      lifecycleDisposable += viewModel.trustAndVerify().subscribe { trustAndVerifyResult ->
        when (trustAndVerifyResult.result) {
          TrustAndVerifyResult.Result.TRUST_AND_VERIFY -> {
            findListener<SafetyNumberBottomSheet.Callbacks>()?.sendAnywayAfterSafetyNumberChangedInBottomSheet(viewModel.destinationSnapshot)
          }
          TrustAndVerifyResult.Result.TRUST_VERIFY_AND_RESEND -> {
            findListener<SafetyNumberBottomSheet.Callbacks>()?.onMessageResentAfterSafetyNumberChangeInBottomSheet()
          }
          TrustAndVerifyResult.Result.UNKNOWN -> {
            Log.w(TAG, "Unknown Result")
          }
        }

        dismissAllowingStateLoss()
      }
    }

    SplashImage.register(adapter)
    SafetyNumberRecipientRowItem.register(adapter)
    lifecycleDisposable.bindTo(viewLifecycleOwner)

    lifecycleDisposable += viewModel.state.subscribe { state ->
      reviewConnections.visible = state.hasLargeNumberOfUntrustedRecipients

      if (state.isCheckupComplete()) {
        sendAnyway.setText(R.string.conversation_activity__send)
      }

      adapter.submitList(getConfiguration(state).toMappingModelList())
    }
  }

  override fun onDismiss(dialog: DialogInterface) {
    super.onDismiss(dialog)
    if (sendAnyway.isEnabled) {
      findListener<SafetyNumberBottomSheet.Callbacks>()?.onCanceled()
    }
  }

  override fun onWrapperDialogFragmentDismissed() = Unit

  private fun getConfiguration(state: SafetyNumberBottomSheetState): DSLConfiguration {
    return configure {
      customPref(
        SplashImage.Model(
          R.drawable.ic_safety_number_24,
          R.color.signal_colorOnSurface
        )
      )

      textPref(
        title = DSLSettingsText.from(
          when {
            state.isCheckupComplete() && state.hasLargeNumberOfUntrustedRecipients -> R.string.SafetyNumberBottomSheetFragment__safety_number_checkup_complete
            state.hasLargeNumberOfUntrustedRecipients -> R.string.SafetyNumberBottomSheetFragment__safety_number_checkup
            else -> R.string.SafetyNumberBottomSheetFragment__safety_number_changes
          },
          DSLSettingsText.TextAppearanceModifier(R.style.Signal_Text_TitleLarge),
          DSLSettingsText.CenterModifier
        )
      )

      textPref(
        title = DSLSettingsText.from(
          when {
            state.isCheckupComplete() && state.hasLargeNumberOfUntrustedRecipients -> getString(R.string.SafetyNumberBottomSheetFragment__all_connections_have_been_reviewed)
            state.hasLargeNumberOfUntrustedRecipients -> resources.getQuantityString(R.plurals.SafetyNumberBottomSheetFragment__you_have_d_connections_plural, args.untrustedRecipients.size, args.untrustedRecipients.size)
            else -> getString(R.string.SafetyNumberBottomSheetFragment__the_following_people)
          },
          DSLSettingsText.TextAppearanceModifier(R.style.Signal_Text_BodyLarge),
          DSLSettingsText.CenterModifier
        )
      )

      if (state.isEmpty()) {
        space(DimensionUnit.DP.toPixels(48f).toInt())

        noPadTextPref(
          title = DSLSettingsText.from(
            R.string.SafetyNumberBottomSheetFragment__no_more_recipients_to_show,
            DSLSettingsText.TextAppearanceModifier(R.style.Signal_Text_BodyLarge),
            DSLSettingsText.CenterModifier,
            DSLSettingsText.ColorModifier(ContextCompat.getColor(requireContext(), R.color.signal_colorOnSurfaceVariant))
          )
        )

        space(DimensionUnit.DP.toPixels(48f).toInt())
      }

      if (!state.hasLargeNumberOfUntrustedRecipients) {
        state.destinationToRecipientMap.values.flatten().distinct().forEach {
          customPref(
            SafetyNumberRecipientRowItem.Model(
              recipient = it.recipient,
              isVerified = it.identityRecord.verifiedStatus == IdentityTable.VerifiedStatus.VERIFIED,
              distributionListMembershipCount = it.distributionListMembershipCount,
              groupMembershipCount = it.groupMembershipCount,
              getContextMenuActions = { model ->
                val actions = mutableListOf<ActionItem>()

                actions.add(
                  ActionItem(
                    iconRes = R.drawable.ic_safety_number_24,
                    title = getString(R.string.SafetyNumberBottomSheetFragment__verify_safety_number),
                    tintRes = R.color.signal_colorOnSurface,
                    action = {
                      lifecycleDisposable += viewModel.getIdentityRecord(model.recipient.id).subscribe { record ->
                        VerifyIdentityFragment.createDialog(
                          model.recipient.id,
                          IdentityKeyParcelable(record.identityKey),
                          false
                        ).show(childFragmentManager, null)
                      }
                    }
                  )
                )

                if (model.distributionListMembershipCount > 0) {
                  actions.add(
                    ActionItem(
                      iconRes = R.drawable.ic_circle_x_24,
                      title = getString(R.string.SafetyNumberBottomSheetFragment__remove_from_story),
                      tintRes = R.color.signal_colorOnSurface,
                      action = {
                        viewModel.removeRecipientFromSelectedStories(model.recipient.id)
                      }
                    )
                  )
                }

                if (model.distributionListMembershipCount == 0 && model.groupMembershipCount == 0) {
                  actions.add(
                    ActionItem(
                      iconRes = R.drawable.ic_circle_x_24,
                      title = getString(R.string.SafetyNumberReviewConnectionsFragment__remove),
                      tintRes = R.color.signal_colorOnSurface,
                      action = {
                        viewModel.removeDestination(model.recipient.id)
                      }
                    )
                  )
                }

                actions
              }
            )
          )
        }
      }
    }
  }

  companion object {
    private val TAG = Log.tag(SafetyNumberBottomSheetFragment::class.java)
  }
}
