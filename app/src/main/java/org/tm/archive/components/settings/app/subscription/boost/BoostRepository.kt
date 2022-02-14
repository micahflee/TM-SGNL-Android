package org.tm.archive.components.settings.app.subscription.boost

import io.reactivex.rxjava3.core.Single
import org.signal.core.util.money.FiatMoney
import org.tm.archive.badges.Badges
import org.tm.archive.badges.models.Badge
import org.tm.archive.util.PlatformCurrencyUtil
import org.whispersystems.signalservice.api.profiles.SignalServiceProfile
import org.whispersystems.signalservice.api.services.DonationsService
import org.whispersystems.signalservice.internal.ServiceResponse
import java.math.BigDecimal
import java.util.Currency
import java.util.Locale

class BoostRepository(private val donationsService: DonationsService) {

  fun getBoosts(): Single<Map<Currency, List<Boost>>> {
    return donationsService.boostAmounts
      .flatMap(ServiceResponse<Map<String, List<BigDecimal>>>::flattenResult)
      .map { result ->
        result
          .filter { PlatformCurrencyUtil.getAvailableCurrencyCodes().contains(it.key) }
          .mapKeys { (code, _) -> Currency.getInstance(code) }
          .mapValues { (currency, prices) -> prices.map { Boost(FiatMoney(it, currency)) } }
      }
  }

  fun getBoostBadge(): Single<Badge> {
    return donationsService.getBoostBadge(Locale.getDefault())
      .flatMap(ServiceResponse<SignalServiceProfile.Badge>::flattenResult)
      .map(Badges::fromServiceBadge)
  }
}
