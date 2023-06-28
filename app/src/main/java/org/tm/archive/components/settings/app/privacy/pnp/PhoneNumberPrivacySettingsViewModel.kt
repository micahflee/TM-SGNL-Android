package org.tm.archive.components.settings.app.privacy.pnp

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import org.tm.archive.database.SignalDatabase
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.jobs.RefreshAttributesJob
import org.tm.archive.jobs.RefreshOwnProfileJob
import org.tm.archive.keyvalue.PhoneNumberPrivacyValues.PhoneNumberListingMode
import org.tm.archive.keyvalue.PhoneNumberPrivacyValues.PhoneNumberSharingMode
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.recipients.Recipient
import org.tm.archive.storage.StorageSyncHelper

class PhoneNumberPrivacySettingsViewModel : ViewModel() {

  private val _state = mutableStateOf(
    PhoneNumberPrivacySettingsState(
      seeMyPhoneNumber = SignalStore.phoneNumberPrivacy().phoneNumberSharingMode,
      findMeByPhoneNumber = SignalStore.phoneNumberPrivacy().phoneNumberListingMode
    )
  )

  val state: State<PhoneNumberPrivacySettingsState> = _state

  fun setNobodyCanSeeMyNumber() {
    setPhoneNumberSharingMode(PhoneNumberSharingMode.NOBODY)
  }

  fun setEveryoneCanSeeMyNumber() {
    setPhoneNumberSharingMode(PhoneNumberSharingMode.EVERYONE)
    setPhoneNumberListingMode(PhoneNumberListingMode.LISTED)
  }

  fun setNobodyCanFindMeByMyNumber() {
    setPhoneNumberListingMode(PhoneNumberListingMode.UNLISTED)
  }

  fun setEveryoneCanFindMeByMyNumber() {
    setPhoneNumberListingMode(PhoneNumberListingMode.LISTED)
  }

  private fun setPhoneNumberSharingMode(phoneNumberSharingMode: PhoneNumberSharingMode) {
    SignalStore.phoneNumberPrivacy().phoneNumberSharingMode = phoneNumberSharingMode
    SignalDatabase.recipients.markNeedsSync(Recipient.self().id)
    StorageSyncHelper.scheduleSyncForDataChange()
    refresh()
  }

  private fun setPhoneNumberListingMode(phoneNumberListingMode: PhoneNumberListingMode) {
    SignalStore.phoneNumberPrivacy().phoneNumberListingMode = phoneNumberListingMode
    StorageSyncHelper.scheduleSyncForDataChange()
    ApplicationDependencies.getJobManager().startChain(RefreshAttributesJob()).then(RefreshOwnProfileJob()).enqueue()
    refresh()
  }

  fun refresh() {
    _state.value = PhoneNumberPrivacySettingsState(
      seeMyPhoneNumber = SignalStore.phoneNumberPrivacy().phoneNumberSharingMode,
      findMeByPhoneNumber = SignalStore.phoneNumberPrivacy().phoneNumberListingMode
    )
  }
}
