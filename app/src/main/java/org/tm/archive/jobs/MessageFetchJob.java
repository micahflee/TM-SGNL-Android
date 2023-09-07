package org.tm.archive.jobs;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.signal.core.util.logging.Log;
import org.tm.archive.R;
import org.tm.archive.jobmanager.Job;
import org.tm.archive.jobmanager.impl.NetworkConstraint;
import org.tm.archive.messages.WebSocketDrainer;
import org.tm.archive.notifications.NotificationChannels;
import org.tm.archive.service.DelayedNotificationController;
import org.tm.archive.service.GenericForegroundService;
import org.tm.archive.service.NotificationController;
import org.whispersystems.signalservice.api.push.exceptions.PushNetworkException;

import java.io.IOException;

/**
 * Fetches messages from the service, posting a foreground service if possible.
 */
public final class MessageFetchJob extends BaseJob {

    public static final String KEY = "PushNotificationReceiveJob";

    private static final String TAG = Log.tag(MessageFetchJob.class);

    public MessageFetchJob() {
        this(new Job.Parameters.Builder()
                .addConstraint(NetworkConstraint.KEY)
                .setQueue("__notification_received")
                .setMaxAttempts(3)
                .setMaxInstancesForFactory(1)
                .build());
    }

    private MessageFetchJob(Job.Parameters parameters) {
        super(parameters);
    }

    @Override
    public @Nullable byte[] serialize() {
        return null;
    }

    @Override
    public @NonNull String getFactoryKey() {
        return KEY;
    }

    @Override
    public void onRun() throws IOException {
        boolean success;

        if (Build.VERSION.SDK_INT < 31) {
            try (DelayedNotificationController unused = GenericForegroundService.startForegroundTaskDelayed(context, context.getString(R.string.BackgroundMessageRetriever_checking_for_messages), 300, R.drawable.ic_signal_refresh)) {
                success = WebSocketDrainer.blockUntilDrainedAndProcessed();
            }
        } else {
            try {
                try (NotificationController unused = GenericForegroundService.startForegroundTask(context, context.getString(R.string.BackgroundMessageRetriever_checking_for_messages), NotificationChannels.getInstance().OTHER, R.drawable.ic_signal_refresh)) {
                    success = WebSocketDrainer.blockUntilDrainedAndProcessed();
                }
            } catch (UnableToStartException e) {
                Log.w(TAG, "Failed to start foreground service. Running in the background.");
                success = WebSocketDrainer.blockUntilDrainedAndProcessed();
            }
        }

        if (success) {
            Log.i(TAG, "Successfully pulled messages.");
        } else {
            throw new PushNetworkException("Failed to pull messages.");
        }
    }

    @Override
    public boolean onShouldRetry(@NonNull Exception e) {
        Log.w(TAG, e);
        return e instanceof PushNetworkException;
    }

    @Override
    public void onFailure() {
    }

    public static final class Factory implements Job.Factory<MessageFetchJob> {
        @Override
        public @NonNull MessageFetchJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
            return new MessageFetchJob(parameters);
        }
    }
}
