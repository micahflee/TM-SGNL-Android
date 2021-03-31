package org.tm.archive.jobs;

import androidx.annotation.NonNull;

import com.google.android.mms.pdu_alt.GenericPdu;
import com.google.android.mms.pdu_alt.NotificationInd;
import com.google.android.mms.pdu_alt.PduHeaders;
import com.google.android.mms.pdu_alt.PduParser;

import org.signal.core.util.logging.Log;
import org.tm.archive.database.DatabaseFactory;
import org.tm.archive.database.MessageDatabase;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.jobmanager.Data;
import org.tm.archive.jobmanager.Job;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.util.Base64;
import org.tm.archive.util.Util;
import org.whispersystems.libsignal.util.Pair;

import java.io.IOException;

public class MmsReceiveJob extends BaseJob {

  public static final String KEY = "MmsReceiveJob";

  private static final String TAG = MmsReceiveJob.class.getSimpleName();

  private static final String KEY_DATA            = "data";
  private static final String KEY_SUBSCRIPTION_ID = "subscription_id";

  private byte[] data;
  private int    subscriptionId;

  public MmsReceiveJob(byte[] data, int subscriptionId) {
    this(new Job.Parameters.Builder().setMaxAttempts(25).build(), data, subscriptionId);
  }

  private MmsReceiveJob(@NonNull Job.Parameters parameters, byte[] data, int subscriptionId) {
    super(parameters);

    this.data           = data;
    this.subscriptionId = subscriptionId;
  }

  @Override
  public @NonNull Data serialize() {
    return new Data.Builder().putString(KEY_DATA, Base64.encodeBytes(data))
                             .putInt(KEY_SUBSCRIPTION_ID, subscriptionId)
                             .build();
  }

  @Override
  public @NonNull String getFactoryKey() {
    return KEY;
  }

  @Override
  public void onRun() {
    if (data == null) {
      Log.w(TAG, "Received NULL pdu, ignoring...");
      return;
    }

    PduParser  parser = new PduParser(data);
    GenericPdu pdu    = null;

    try {
      pdu = parser.parse();
    } catch (RuntimeException e) {
      Log.w(TAG, e);
    }

    if (isNotification(pdu) && !isBlocked(pdu)) {
      MessageDatabase  database           = DatabaseFactory.getMmsDatabase(context);
      Pair<Long, Long> messageAndThreadId = database.insertMessageInbox((NotificationInd)pdu, subscriptionId);

      Log.i(TAG, "Inserted received MMS notification...");

      ApplicationDependencies.getJobManager().add(new MmsDownloadJob(messageAndThreadId.first(),
                                                                     messageAndThreadId.second(),
                                                                     true));
    } else if (isNotification(pdu)) {
      Log.w(TAG, "*** Received blocked MMS, ignoring...");
    }
  }

  @Override
  public void onFailure() {
    // TODO
  }

  @Override
  public boolean onShouldRetry(@NonNull Exception exception) {
    return false;
  }

  private boolean isBlocked(GenericPdu pdu) {
    if (pdu.getFrom() != null && pdu.getFrom().getTextString() != null) {
      Recipient recipients = Recipient.external(context, Util.toIsoString(pdu.getFrom().getTextString()));
      return recipients.isBlocked();
    }

    return false;
  }

  private boolean isNotification(GenericPdu pdu) {
    return pdu != null && pdu.getMessageType() == PduHeaders.MESSAGE_TYPE_NOTIFICATION_IND;
  }

  public static final class Factory implements Job.Factory<MmsReceiveJob> {
    @Override
    public @NonNull MmsReceiveJob create(@NonNull Parameters parameters, @NonNull Data data) {
      try {
        return new MmsReceiveJob(parameters, Base64.decode(data.getString(KEY_DATA)), data.getInt(KEY_SUBSCRIPTION_ID));
      } catch (IOException e) {
        throw new AssertionError(e);
      }
    }
  }
}
