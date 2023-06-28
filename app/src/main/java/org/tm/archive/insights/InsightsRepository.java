package org.tm.archive.insights;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import com.annimon.stream.Stream;

import org.tm.archive.R;
import org.tm.archive.contacts.avatars.GeneratedContactPhoto;
import org.tm.archive.contacts.avatars.ProfileContactPhoto;
import org.tm.archive.database.MessageTable;
import org.tm.archive.database.RecipientTable;
import org.tm.archive.database.SignalDatabase;
import org.tm.archive.mms.OutgoingMessage;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.recipients.RecipientId;
import org.tm.archive.sms.MessageSender;
import org.tm.archive.util.Util;
import org.signal.core.util.concurrent.SimpleTask;

import java.util.List;
import java.util.Optional;

public class InsightsRepository implements InsightsDashboardViewModel.Repository, InsightsModalViewModel.Repository {

  private final Context context;

  public InsightsRepository(Context context) {
    this.context = context.getApplicationContext();
  }

  @Override
  public void getInsightsData(@NonNull Consumer<InsightsData> insightsDataConsumer) {
    SimpleTask.run(() -> {
      MessageTable messageTable = SignalDatabase.messages();
      int          insecure     = messageTable.getInsecureMessageCountForInsights();
      int          secure       = messageTable.getSecureMessageCountForInsights();

      if (insecure + secure == 0) {
        return new InsightsData(false, 0);
      } else {
        return new InsightsData(true, Util.clamp((int) Math.ceil((insecure * 100f) / (insecure + secure)), 0, 100));
      }
    }, insightsDataConsumer::accept);
  }

  @Override
  public void getInsecureRecipients(@NonNull Consumer<List<Recipient>> insecureRecipientsConsumer) {
    SimpleTask.run(() -> {
      RecipientTable    recipientTable         = SignalDatabase.recipients();
      List<RecipientId> unregisteredRecipients = recipientTable.getUninvitedRecipientsForInsights();

      return Stream.of(unregisteredRecipients)
                   .map(Recipient::resolved)
                   .toList();
    },
    insecureRecipientsConsumer::accept);
  }

  @Override
  public void getUserAvatar(@NonNull Consumer<InsightsUserAvatar> avatarConsumer) {
    SimpleTask.run(() -> {
      Recipient self = Recipient.self().resolve();
      String    name = Optional.of(self.getDisplayName(context)).orElse("");

      return new InsightsUserAvatar(new ProfileContactPhoto(self),
                                    self.getAvatarColor(),
                                    new GeneratedContactPhoto(name, R.drawable.ic_profile_outline_40));
    }, avatarConsumer::accept);
  }

  @Override
  public void sendSmsInvite(@NonNull Recipient recipient, Runnable onSmsMessageSent) {
    SimpleTask.run(() -> {
      Recipient resolved       = recipient.resolve();
      int       subscriptionId = resolved.getDefaultSubscriptionId().orElse(-1);
      String    message        = context.getString(R.string.InviteActivity_lets_switch_to_signal, context.getString(R.string.install_url));

      MessageSender.send(context, OutgoingMessage.sms(resolved, message, subscriptionId), -1L, MessageSender.SendType.SMS, null, null);

      RecipientTable database = SignalDatabase.recipients();
      database.setHasSentInvite(recipient.getId());

      return null;
    }, v -> onSmsMessageSent.run());
  }
}
