package org.tm.archive.payments.backup;

import androidx.annotation.NonNull;

import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.payments.Mnemonic;

public final class PaymentsRecoveryRepository {
  public @NonNull Mnemonic getMnemonic() {
    return SignalStore.paymentsValues().getPaymentsMnemonic();
  }
}
