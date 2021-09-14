package org.tm.archive.payments.preferences.addmoney;

import android.net.Uri;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.payments.MobileCoinPublicAddress;
import org.tm.archive.util.AsynchronousCallback;

final class PaymentsAddMoneyRepository {

  @MainThread
  void getWalletAddress(@NonNull AsynchronousCallback.MainThread<AddressAndUri, Error> callback) {
    if (!SignalStore.paymentsValues().mobileCoinPaymentsEnabled()) {
      callback.onError(Error.PAYMENTS_NOT_ENABLED);
    }

    MobileCoinPublicAddress publicAddress        = ApplicationDependencies.getPayments().getWallet().getMobileCoinPublicAddress();
    String                  paymentAddressBase58 = publicAddress.getPaymentAddressBase58();
    Uri                     paymentAddressUri    = publicAddress.getPaymentAddressUri();

    callback.onComplete(new AddressAndUri(paymentAddressBase58, paymentAddressUri));
  }

  enum Error {
    PAYMENTS_NOT_ENABLED
  }

}
