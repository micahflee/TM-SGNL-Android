package org.tm.archive.service.webrtc;

import androidx.annotation.NonNull;

import org.signal.core.util.logging.Log;
import org.signal.ringrtc.CallException;
import org.signal.ringrtc.CallManager;
import org.tm.archive.components.webrtc.EglBaseWrapper;
import org.tm.archive.database.SignalDatabase;
import org.tm.archive.events.WebRtcViewModel;
import org.tm.archive.groups.GroupId;
import org.tm.archive.notifications.profiles.NotificationProfile;
import org.tm.archive.notifications.profiles.NotificationProfiles;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.ringrtc.RemotePeer;
import org.tm.archive.service.webrtc.state.WebRtcServiceState;
import org.whispersystems.signalservice.api.messages.calls.OfferMessage;

import java.util.UUID;

/**
 * Action handler for when the system is at rest. Mainly responsible
 * for starting pre-call state, starting an outgoing call, or receiving an
 * incoming call.
 */
public class IdleActionProcessor extends WebRtcActionProcessor {

  private static final String TAG = Log.tag(IdleActionProcessor.class);

  private final BeginCallActionProcessorDelegate beginCallDelegate;

  public IdleActionProcessor(@NonNull WebRtcInteractor webRtcInteractor) {
    super(webRtcInteractor, TAG);
    beginCallDelegate = new BeginCallActionProcessorDelegate(webRtcInteractor, TAG);
  }

  protected @NonNull WebRtcServiceState handleStartIncomingCall(@NonNull WebRtcServiceState currentState, @NonNull RemotePeer remotePeer, @NonNull OfferMessage.Type offerType) {
    Log.i(TAG, "handleStartIncomingCall():");

    currentState = WebRtcVideoUtil.initializeVideo(context, webRtcInteractor.getCameraEventListener(), currentState, remotePeer.getCallId().longValue());
    return beginCallDelegate.handleStartIncomingCall(currentState, remotePeer, offerType);
  }

  @Override
  protected @NonNull WebRtcServiceState handleOutgoingCall(@NonNull WebRtcServiceState currentState,
                                                           @NonNull RemotePeer remotePeer,
                                                           @NonNull OfferMessage.Type offerType)
  {
    Log.i(TAG, "handleOutgoingCall():");

    Recipient recipient = Recipient.resolved(remotePeer.getId());
    if (recipient.isGroup()) {
      Log.w(TAG, "Aborting attempt to start 1:1 call for group recipient: " + remotePeer.getId());
      return currentState;
    }

    currentState = WebRtcVideoUtil.initializeVideo(context, webRtcInteractor.getCameraEventListener(), currentState, EglBaseWrapper.OUTGOING_PLACEHOLDER);
    return beginCallDelegate.handleOutgoingCall(currentState, remotePeer, offerType);
  }

  @Override
  protected @NonNull WebRtcServiceState handlePreJoinCall(@NonNull WebRtcServiceState currentState, @NonNull RemotePeer remotePeer) {
    Log.i(TAG, "handlePreJoinCall():");

    boolean               isGroupCall = remotePeer.getRecipient().isPushV2Group();
    WebRtcActionProcessor processor   = isGroupCall ? new GroupPreJoinActionProcessor(webRtcInteractor)
                                                    : new PreJoinActionProcessor(webRtcInteractor);

    currentState = WebRtcVideoUtil.initializeVanityCamera(WebRtcVideoUtil.initializeVideo(context,
                                                                                          webRtcInteractor.getCameraEventListener(),
                                                                                          currentState,
                                                                                          isGroupCall ? RemotePeer.GROUP_CALL_ID.longValue()
                                                                                                      : EglBaseWrapper.OUTGOING_PLACEHOLDER));

    currentState = currentState.builder()
                               .actionProcessor(processor)
                               .changeCallInfoState()
                               .callState(WebRtcViewModel.State.CALL_PRE_JOIN)
                               .callRecipient(remotePeer.getRecipient())
                               .build();

    return isGroupCall ? currentState.getActionProcessor().handlePreJoinCall(currentState, remotePeer)
                       : currentState;
  }

  @Override
  protected @NonNull WebRtcServiceState handleGroupCallRingUpdate(@NonNull WebRtcServiceState currentState,
                                                                  @NonNull RemotePeer remotePeerGroup,
                                                                  @NonNull GroupId.V2 groupId,
                                                                  long ringId,
                                                                  @NonNull UUID uuid,
                                                                  @NonNull CallManager.RingUpdate ringUpdate)
  {
    Log.i(TAG, "handleGroupCallRingUpdate(): recipient: " + remotePeerGroup.getId() + " ring: " + ringId + " update: " + ringUpdate);

    if (ringUpdate != CallManager.RingUpdate.REQUESTED) {
      SignalDatabase.groupCallRings().insertOrUpdateGroupRing(ringId, System.currentTimeMillis(), ringUpdate);
      return currentState;
    } else if (SignalDatabase.groupCallRings().isCancelled(ringId)) {
      try {
        Log.i(TAG, "Incoming ring request for already cancelled ring: " + ringId);
        webRtcInteractor.getCallManager().cancelGroupRing(groupId.getDecodedId(), ringId, null);
      } catch (CallException e) {
        Log.w(TAG, "Error while trying to cancel ring: " + ringId, e);
      }
      return currentState;
    }

    NotificationProfile activeProfile = NotificationProfiles.getActiveProfile(SignalDatabase.notificationProfiles().getProfiles());
    if (activeProfile != null && !(activeProfile.isRecipientAllowed(remotePeerGroup.getId()) || activeProfile.getAllowAllCalls())) {
      try {
        Log.i(TAG, "Incoming ring request for profile restricted recipient");
        SignalDatabase.groupCallRings().insertOrUpdateGroupRing(ringId, System.currentTimeMillis(), CallManager.RingUpdate.EXPIRED_REQUEST);
        webRtcInteractor.getCallManager().cancelGroupRing(groupId.getDecodedId(), ringId, CallManager.RingCancelReason.DeclinedByUser);
      } catch (CallException e) {
        Log.w(TAG, "Error while trying to cancel ring: " + ringId, e);
      }
      return currentState;
    }

    webRtcInteractor.peekGroupCallForRingingCheck(new GroupCallRingCheckInfo(remotePeerGroup.getId(), groupId, ringId, uuid, ringUpdate));

    return currentState;
  }

  @Override
  protected @NonNull WebRtcServiceState handleReceivedGroupCallPeekForRingingCheck(@NonNull WebRtcServiceState currentState, @NonNull GroupCallRingCheckInfo info, long deviceCount) {
    Log.i(tag, "handleReceivedGroupCallPeekForRingingCheck(): recipient: " + info.getRecipientId() + " ring: " + info.getRingId() + " deviceCount: " + deviceCount);

    if (SignalDatabase.groupCallRings().isCancelled(info.getRingId())) {
      try {
        Log.i(TAG, "Ring was cancelled while getting peek info ring: " + info.getRingId());
        webRtcInteractor.getCallManager().cancelGroupRing(info.getGroupId().getDecodedId(), info.getRingId(), null);
      } catch (CallException e) {
        Log.w(TAG, "Error while trying to cancel ring: " + info.getRingId(), e);
      }
      return currentState;
    }

    if (deviceCount == 0) {
      Log.i(TAG, "No one in the group call, mark as expired and do not ring");
      SignalDatabase.groupCallRings().insertOrUpdateGroupRing(info.getRingId(), System.currentTimeMillis(), CallManager.RingUpdate.EXPIRED_REQUEST);
      return currentState;
    }

    currentState = currentState.builder()
                               .actionProcessor(new IncomingGroupCallActionProcessor(webRtcInteractor))
                               .build();

    return currentState.getActionProcessor().handleGroupCallRingUpdate(currentState, new RemotePeer(info.getRecipientId()), info.getGroupId(), info.getRingId(), info.getRingerUuid(), info.getRingUpdate());
  }
}
