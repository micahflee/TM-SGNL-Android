package org.tm.archive.components.webrtc;

import android.content.Context;
import android.media.AudioManager;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.core.util.Consumer;

import org.signal.core.util.concurrent.SignalExecutors;
import org.tm.archive.database.DatabaseFactory;
import org.tm.archive.database.GroupDatabase;
import org.tm.archive.database.IdentityDatabase;
import org.tm.archive.database.identity.IdentityRecordList;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.util.ServiceUtil;

import java.util.Collections;
import java.util.List;

class WebRtcCallRepository {

  private final Context      context;
  private final AudioManager audioManager;

  WebRtcCallRepository(@NonNull Context context) {
    this.context      = context;
    this.audioManager = ServiceUtil.getAudioManager(ApplicationDependencies.getApplication());
  }

  @NonNull WebRtcAudioOutput getAudioOutput() {
    if (audioManager.isBluetoothScoOn()) {
      return WebRtcAudioOutput.HEADSET;
    } else if (audioManager.isSpeakerphoneOn()) {
      return WebRtcAudioOutput.SPEAKER;
    } else {
      return WebRtcAudioOutput.HANDSET;
    }
  }

  @WorkerThread
  void getIdentityRecords(@NonNull Recipient recipient, @NonNull Consumer<IdentityRecordList> consumer) {
    SignalExecutors.BOUNDED.execute(() -> {
      IdentityDatabase identityDatabase = DatabaseFactory.getIdentityDatabase(context);
      List<Recipient>  recipients;

      if (recipient.isGroup()) {
        recipients = DatabaseFactory.getGroupDatabase(context).getGroupMembers(recipient.requireGroupId(), GroupDatabase.MemberSet.FULL_MEMBERS_EXCLUDING_SELF);
      } else {
        recipients = Collections.singletonList(recipient);
      }

      consumer.accept(identityDatabase.getIdentities(recipients));
    });
  }
}
