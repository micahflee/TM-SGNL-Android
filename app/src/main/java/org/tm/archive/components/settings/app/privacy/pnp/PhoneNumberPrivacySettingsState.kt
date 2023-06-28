package org.tm.archive.components.settings.app.privacy.pnp

import org.tm.archive.keyvalue.PhoneNumberPrivacyValues

data class PhoneNumberPrivacySettingsState(
  val seeMyPhoneNumber: PhoneNumberPrivacyValues.PhoneNumberSharingMode,
  val findMeByPhoneNumber: PhoneNumberPrivacyValues.PhoneNumberListingMode
)
