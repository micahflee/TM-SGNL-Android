package org.tm.archive.profiles.edit.pnp

import io.reactivex.rxjava3.core.Completable
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.jobs.ProfileUploadJob
import org.tm.archive.jobs.RefreshAttributesJob
import org.tm.archive.keyvalue.PhoneNumberPrivacyValues
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.storage.StorageSyncHelper

/**
 * Manages the current phone-number listing state.
 */
class WhoCanFindMeByPhoneNumberRepository {

  fun getCurrentState(): WhoCanFindMeByPhoneNumberState {
    return when (SignalStore.phoneNumberPrivacy().phoneNumberListingMode) {
      PhoneNumberPrivacyValues.PhoneNumberListingMode.LISTED -> WhoCanFindMeByPhoneNumberState.EVERYONE
      PhoneNumberPrivacyValues.PhoneNumberListingMode.UNLISTED -> WhoCanFindMeByPhoneNumberState.NOBODY
    }
  }

  fun onSave(whoCanFindMeByPhoneNumberState: WhoCanFindMeByPhoneNumberState): Completable {
    return Completable.fromAction {
      when (whoCanFindMeByPhoneNumberState) {
        WhoCanFindMeByPhoneNumberState.EVERYONE -> {
          SignalStore.phoneNumberPrivacy().phoneNumberListingMode = PhoneNumberPrivacyValues.PhoneNumberListingMode.LISTED
        }
        WhoCanFindMeByPhoneNumberState.NOBODY -> {
          SignalStore.phoneNumberPrivacy().phoneNumberSharingMode = PhoneNumberPrivacyValues.PhoneNumberSharingMode.NOBODY
          SignalStore.phoneNumberPrivacy().phoneNumberListingMode = PhoneNumberPrivacyValues.PhoneNumberListingMode.UNLISTED
        }
      }

      ApplicationDependencies.getJobManager().add(RefreshAttributesJob())
      StorageSyncHelper.scheduleSyncForDataChange()
      ApplicationDependencies.getJobManager().add(ProfileUploadJob())
    }
  }
}
