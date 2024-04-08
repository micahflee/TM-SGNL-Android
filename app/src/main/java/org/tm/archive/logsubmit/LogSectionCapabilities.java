package org.tm.archive.logsubmit;

import android.content.Context;

import androidx.annotation.NonNull;

import org.tm.archive.AppCapabilities;
import org.tm.archive.database.SignalDatabase;
import org.tm.archive.database.model.RecipientRecord;
import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.recipients.Recipient;
import org.whispersystems.signalservice.api.account.AccountAttributes;

public final class LogSectionCapabilities implements LogSection {

  @Override
  public @NonNull String getTitle() {
    return "CAPABILITIES";
  }

  @Override
  public @NonNull CharSequence getContent(@NonNull Context context) {
    if (!SignalStore.account().isRegistered()) {
      return "Unregistered";
    }

    if (SignalStore.account().getE164() == null || SignalStore.account().getAci() == null) {
      return "Self not yet available!";
    }

    Recipient self = Recipient.self();

    AccountAttributes.Capabilities localCapabilities  = AppCapabilities.getCapabilities(false);
    RecipientRecord.Capabilities   globalCapabilities = SignalDatabase.recipients().getCapabilities(self.getId());

    StringBuilder builder = new StringBuilder().append("-- Local").append("\n")
                                               .append("PNP/PNI: ").append(localCapabilities.getPni()).append("\n")
                                               .append("\n")
                                               .append("-- Global").append("\n");

    if (globalCapabilities != null) {
      builder.append("PNP/PNI: ").append(globalCapabilities.getPnpCapability()).append("\n");
    } else {
      builder.append("Self not found!");
    }

    return builder;
  }
}
