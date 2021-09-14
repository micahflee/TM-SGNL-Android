package org.tm.archive.migrations;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import org.signal.core.util.logging.Log;
import org.tm.archive.MainActivity;
import org.tm.archive.NewConversationActivity;
import org.tm.archive.R;
import org.tm.archive.database.DatabaseFactory;
import org.tm.archive.database.ThreadDatabase;
import org.tm.archive.jobmanager.Data;
import org.tm.archive.jobmanager.Job;
import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.notifications.NotificationChannels;
import org.tm.archive.notifications.NotificationIds;
import org.tm.archive.recipients.RecipientId;
import org.tm.archive.util.SetUtil;
import org.tm.archive.util.TextSecurePreferences;

import java.util.List;
import java.util.Set;

/**
 * Show a user that contacts are newly available. Only for users that recently installed.
 */
public class UserNotificationMigrationJob extends MigrationJob {

  private static final String TAG = Log.tag(UserNotificationMigrationJob.class);

  public static final String KEY =  "UserNotificationMigration";

  UserNotificationMigrationJob() {
    this(new Parameters.Builder().build());
  }

  private UserNotificationMigrationJob(Parameters parameters) {
    super(parameters);
  }

  @Override
  public @NonNull String getFactoryKey() {
    return KEY;
  }

  @Override
  boolean isUiBlocking() {
    return false;
  }

  @Override
  void performMigration() {
    if (!TextSecurePreferences.isPushRegistered(context)      ||
        TextSecurePreferences.getLocalNumber(context) == null ||
        TextSecurePreferences.getLocalUuid(context) == null)
    {
      Log.w(TAG, "Not registered! Skipping.");
      return;
    }

    if (!SignalStore.settings().isNotifyWhenContactJoinsSignal()) {
      Log.w(TAG, "New contact notifications disabled! Skipping.");
      return;
    }

    if (TextSecurePreferences.getFirstInstallVersion(context) < 759) {
      Log.w(TAG, "Install is older than v5.0.8. Skipping.");
      return;
    }

    ThreadDatabase threadDatabase = DatabaseFactory.getThreadDatabase(context);

    int threadCount = threadDatabase.getUnarchivedConversationListCount() +
                      threadDatabase.getArchivedConversationListCount();

    if (threadCount >= 3) {
      Log.w(TAG, "Already have 3 or more threads. Skipping.");
      return;
    }

    List<RecipientId> registered               = DatabaseFactory.getRecipientDatabase(context).getRegistered();
    List<RecipientId> systemContacts           = DatabaseFactory.getRecipientDatabase(context).getSystemContacts();
    Set<RecipientId>  registeredSystemContacts = SetUtil.intersection(registered, systemContacts);
    Set<RecipientId>  threadRecipients         = threadDatabase.getAllThreadRecipients();

    if (threadRecipients.containsAll(registeredSystemContacts)) {
      Log.w(TAG, "Threads already exist for all relevant contacts. Skipping.");
      return;
    }

    String message = context.getResources().getQuantityString(R.plurals.UserNotificationMigrationJob_d_contacts_are_on_signal,
                                                              registeredSystemContacts.size(),
                                                              registeredSystemContacts.size());

    Intent        mainActivityIntent    = new Intent(context, MainActivity.class);
    Intent        newConversationIntent = new Intent(context, NewConversationActivity.class);
    PendingIntent pendingIntent         = TaskStackBuilder.create(context)
                                                          .addNextIntent(mainActivityIntent)
                                                          .addNextIntent(newConversationIntent)
                                                          .getPendingIntent(0, 0);

    Notification notification = new NotificationCompat.Builder(context, NotificationChannels.getMessagesChannel(context))
                                                      .setSmallIcon(R.drawable.ic_notification)
                                                      .setContentText(message)
                                                      .setContentIntent(pendingIntent)
                                                      .build();

    try {
      NotificationManagerCompat.from(context)
                               .notify(NotificationIds.USER_NOTIFICATION_MIGRATION, notification);
    } catch (Throwable t) {
      Log.w(TAG, "Failed to notify!", t);
    }
  }

  @Override
  boolean shouldRetry(@NonNull Exception e) {
    return false;
  }

  public static final class Factory implements Job.Factory<UserNotificationMigrationJob> {

    @Override
    public @NonNull UserNotificationMigrationJob create(@NonNull Parameters parameters, @NonNull Data data) {
      return new UserNotificationMigrationJob(parameters);
    }
  }
}
