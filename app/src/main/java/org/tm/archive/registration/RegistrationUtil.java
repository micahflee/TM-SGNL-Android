package org.tm.archive.registration;

import org.signal.core.util.logging.Log;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.jobs.DirectoryRefreshJob;
import org.tm.archive.jobs.StorageSyncJob;
import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.recipients.Recipient;

public final class RegistrationUtil {

  private static final String TAG = Log.tag(RegistrationUtil.class);

  private RegistrationUtil() {}

  /**
   * There's several events where a registration may or may not be considered complete based on what
   * path a user has taken. This will only truly mark registration as complete if all of the
   * requirements are met.
   */
  public static void maybeMarkRegistrationComplete() {
    if (!SignalStore.registrationValues().isRegistrationComplete() &&
        SignalStore.account().isRegistered()                       &&
        !Recipient.self().getProfileName().isEmpty()               &&
        (SignalStore.svr().hasPin() || SignalStore.svr().hasOptedOut()))
    {
      Log.i(TAG, "Marking registration completed.", new Throwable());
      SignalStore.registrationValues().setRegistrationComplete();
      ApplicationDependencies.getJobManager().startChain(new StorageSyncJob())
                                             .then(new DirectoryRefreshJob(false))
                                             .enqueue();
    } else if (!SignalStore.registrationValues().isRegistrationComplete()) {
      Log.i(TAG, "Registration is not yet complete.", new Throwable());
    }
  }
}
