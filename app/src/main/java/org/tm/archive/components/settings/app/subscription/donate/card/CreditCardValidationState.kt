package org.tm.archive.components.settings.app.subscription.donate.card

data class CreditCardValidationState(
  val type: CreditCardType,
  val numberValidity: CreditCardNumberValidator.Validity,
  val expirationValidity: CreditCardExpirationValidator.Validity,
  val codeValidity: CreditCardCodeValidator.Validity
) {
  val isValid: Boolean =
    numberValidity == CreditCardNumberValidator.Validity.FULLY_VALID &&
      expirationValidity == CreditCardExpirationValidator.Validity.FULLY_VALID &&
      codeValidity == CreditCardCodeValidator.Validity.FULLY_VALID
}
