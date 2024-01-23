package org.tm.archive.payments.preferences.addmoney

import androidx.annotation.MainThread
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.keyvalue.SignalStore
import org.signal.core.util.Result as SignalResult

internal class PaymentsAddMoneyRepository {
  @MainThread
  fun getWalletAddress(): Single<SignalResult<AddressAndUri, Error>> {
    if (!SignalStore.paymentsValues().mobileCoinPaymentsEnabled()) {
      return Single.just(SignalResult.failure(Error.PAYMENTS_NOT_ENABLED))
    }

    return Single.fromCallable<SignalResult<AddressAndUri, Error>> {
      val publicAddress = ApplicationDependencies.getPayments().wallet.mobileCoinPublicAddress
      val paymentAddressBase58 = publicAddress.paymentAddressBase58
      val paymentAddressUri = publicAddress.paymentAddressUri
      SignalResult.success(AddressAndUri(paymentAddressBase58, paymentAddressUri))
    }
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.io())
  }

  internal enum class Error {
    PAYMENTS_NOT_ENABLED
  }
}
