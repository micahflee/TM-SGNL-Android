package org.tm.archive.jobs;

import androidx.annotation.NonNull;

import com.annimon.stream.Stream;

import org.signal.core.util.logging.Log;
import org.tm.archive.BuildConfig;
import org.tm.archive.TextSecureExpiredException;
import org.tm.archive.attachments.Attachment;
import org.tm.archive.contactshare.Contact;
import org.tm.archive.database.AttachmentDatabase;
import org.tm.archive.database.DatabaseFactory;
import org.tm.archive.jobmanager.Job;
import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.linkpreview.LinkPreview;
import org.tm.archive.mms.OutgoingMediaMessage;
import org.tm.archive.util.Util;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;

public abstract class SendJob extends BaseJob {

  @SuppressWarnings("unused")
  private final static String TAG = SendJob.class.getSimpleName();

  public SendJob(Job.Parameters parameters) {
    super(parameters);
  }

  @Override
  public final void onRun() throws Exception {
    if (SignalStore.misc().isClientDeprecated()) {
      throw new TextSecureExpiredException(String.format("TextSecure expired (build %d, now %d)",
                                                         BuildConfig.BUILD_TIMESTAMP,
                                                         System.currentTimeMillis()));
    }

    Log.i(TAG, "Starting message send attempt");
    onSend();
    Log.i(TAG, "Message send completed");
  }

  protected abstract void onSend() throws Exception;

  protected void markAttachmentsUploaded(long messageId, @NonNull OutgoingMediaMessage message) {
    List<Attachment> attachments = new LinkedList<>();

    attachments.addAll(message.getAttachments());
    attachments.addAll(Stream.of(message.getLinkPreviews()).map(lp -> lp.getThumbnail().orNull()).withoutNulls().toList());
    attachments.addAll(Stream.of(message.getSharedContacts()).map(Contact::getAvatarAttachment).withoutNulls().toList());

    if (message.getOutgoingQuote() != null) {
      attachments.addAll(message.getOutgoingQuote().getAttachments());
    }

    AttachmentDatabase database = DatabaseFactory.getAttachmentDatabase(context);

    for (Attachment attachment : attachments) {
      database.markAttachmentUploaded(messageId, attachment);
    }
  }
}
