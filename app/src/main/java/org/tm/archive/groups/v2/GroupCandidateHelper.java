package org.tm.archive.groups.v2;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import org.signal.core.util.logging.Log;
import org.signal.libsignal.zkgroup.profiles.ExpiringProfileKeyCredential;
import org.tm.archive.database.RecipientTable;
import org.tm.archive.database.SignalDatabase;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.recipients.RecipientId;
import org.tm.archive.util.ProfileUtil;
import org.whispersystems.signalservice.api.SignalServiceAccountManager;
import org.whispersystems.signalservice.api.groupsv2.GroupCandidate;
import org.whispersystems.signalservice.api.push.ServiceId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GroupCandidateHelper {
  private final SignalServiceAccountManager signalServiceAccountManager;
  private final RecipientTable              recipientTable;

  public GroupCandidateHelper() {
    signalServiceAccountManager = ApplicationDependencies.getSignalServiceAccountManager();
    recipientTable              = SignalDatabase.recipients();
  }

  private static final String TAG = Log.tag(GroupCandidateHelper.class);

  /**
   * Given a recipient will create a {@link GroupCandidate} which may or may not have a profile key credential.
   * <p>
   * It will try to find missing profile key credentials from the server and persist locally.
   */
  @WorkerThread
  public @NonNull GroupCandidate recipientIdToCandidate(@NonNull RecipientId recipientId)
      throws IOException
  {
    final Recipient recipient = Recipient.resolved(recipientId);

    ServiceId serviceId = recipient.getServiceId().orElse(null);
    if (serviceId == null) {
      throw new AssertionError("Non UUID members should have need detected by now");
    }

    Optional<ExpiringProfileKeyCredential> expiringProfileKeyCredential = Optional.ofNullable(recipient.getExpiringProfileKeyCredential());
    GroupCandidate                         candidate                    = new GroupCandidate(serviceId, expiringProfileKeyCredential);

    if (!candidate.hasValidProfileKeyCredential()) {
      recipientTable.clearProfileKeyCredential(recipient.getId());

      Optional<ExpiringProfileKeyCredential> credential = ProfileUtil.updateExpiringProfileKeyCredential(recipient);
      if (credential.isPresent()) {
        candidate = candidate.withExpiringProfileKeyCredential(credential.get());
      } else {
        candidate = candidate.withoutExpiringProfileKeyCredential();
      }
    }

    return candidate;
  }

  @WorkerThread
  public @NonNull Set<GroupCandidate> recipientIdsToCandidates(@NonNull Collection<RecipientId> recipientIds)
      throws IOException
  {
    Set<GroupCandidate> result = new HashSet<>(recipientIds.size());

    for (RecipientId recipientId : recipientIds) {
      result.add(recipientIdToCandidate(recipientId));
    }

    return result;
  }

  @WorkerThread
  public @NonNull List<GroupCandidate> recipientIdsToCandidatesList(@NonNull Collection<RecipientId> recipientIds)
      throws IOException
  {
    List<GroupCandidate> result = new ArrayList<>(recipientIds.size());

    for (RecipientId recipientId : recipientIds) {
      result.add(recipientIdToCandidate(recipientId));
    }

    return result;
  }
}
