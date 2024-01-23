package org.tm.archive.service.webrtc;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.signal.ringrtc.CallId;
import org.signal.ringrtc.CallManager;
import org.signal.ringrtc.GroupCall;
import org.tm.archive.database.CallTable;
import org.tm.archive.groups.GroupId;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.recipients.RecipientId;
import org.tm.archive.ringrtc.CameraEventListener;
import org.tm.archive.ringrtc.RemotePeer;
import org.tm.archive.service.webrtc.state.WebRtcServiceState;
import org.tm.archive.util.AppForegroundObserver;
import org.tm.archive.webrtc.audio.AudioManagerCommand;
import org.tm.archive.webrtc.audio.SignalAudioManager;
import org.tm.archive.webrtc.locks.LockManager;
import org.whispersystems.signalservice.api.messages.calls.SignalServiceCallMessage;
import org.whispersystems.signalservice.internal.push.SyncMessage;

import java.util.Collection;
import java.util.UUID;

/**
 * Serves as the bridge between the action processing framework as the WebRTC service. Attempts
 * to minimize direct access to various managers by providing a simple proxy to them. Due to the
 * heavy use of {@link CallManager} throughout, it was exempted from the rule.
 */
public class WebRtcInteractor {

  private final Context                        context;
  private final SignalCallManager              signalCallManager;
  private final LockManager                    lockManager;
  private final CameraEventListener            cameraEventListener;
  private final GroupCall.Observer             groupCallObserver;
  private final AppForegroundObserver.Listener foregroundListener;

  public WebRtcInteractor(@NonNull Context context,
                          @NonNull SignalCallManager signalCallManager,
                          @NonNull LockManager lockManager,
                          @NonNull CameraEventListener cameraEventListener,
                          @NonNull GroupCall.Observer groupCallObserver,
                          @NonNull AppForegroundObserver.Listener foregroundListener)
  {
    this.context             = context;
    this.signalCallManager   = signalCallManager;
    this.lockManager         = lockManager;
    this.cameraEventListener = cameraEventListener;
    this.groupCallObserver   = groupCallObserver;
    this.foregroundListener  = foregroundListener;
  }

  @NonNull Context getContext() {
    return context;
  }

  @NonNull CameraEventListener getCameraEventListener() {
    return cameraEventListener;
  }

  @NonNull CallManager getCallManager() {
    return signalCallManager.getRingRtcCallManager();
  }

  @NonNull GroupCall.Observer getGroupCallObserver() {
    return groupCallObserver;
  }

  @NonNull AppForegroundObserver.Listener getForegroundListener() {
    return foregroundListener;
  }

  void updatePhoneState(@NonNull LockManager.PhoneState phoneState) {
    lockManager.updatePhoneState(phoneState);
  }

  void postStateUpdate(@NonNull WebRtcServiceState state) {
    signalCallManager.postStateUpdate(state);
  }

  void sendCallMessage(@NonNull RemotePeer remotePeer, @NonNull SignalServiceCallMessage callMessage) {
    signalCallManager.sendCallMessage(remotePeer, callMessage);
  }

  void sendGroupCallMessage(@NonNull Recipient recipient, @Nullable String groupCallEraId, @Nullable CallId callId, boolean isIncoming, boolean isJoinEvent) {
    signalCallManager.sendGroupCallUpdateMessage(recipient, groupCallEraId, callId, isIncoming, isJoinEvent);
  }

  void updateGroupCallUpdateMessage(@NonNull RecipientId groupId, @Nullable String groupCallEraId, @NonNull Collection<UUID> joinedMembers, boolean isCallFull) {
    signalCallManager.updateGroupCallUpdateMessage(groupId, groupCallEraId, joinedMembers, isCallFull);
  }

  void setCallInProgressNotification(int type, @NonNull RemotePeer remotePeer, boolean isVideoCall) {
    WebRtcCallService.update(context, type, remotePeer.getRecipient().getId(), isVideoCall);
  }

  void setCallInProgressNotification(int type, @NonNull Recipient recipient, boolean isVideoCall) {
    WebRtcCallService.update(context, type, recipient.getId(), isVideoCall);
  }

  void retrieveTurnServers(@NonNull RemotePeer remotePeer) {
    signalCallManager.retrieveTurnServers(remotePeer);
  }

  void stopForegroundService() {
    WebRtcCallService.stop(context);
  }

  void insertMissedCall(@NonNull RemotePeer remotePeer, long timestamp, boolean isVideoOffer) {
    insertMissedCall(remotePeer, timestamp, isVideoOffer, CallTable.Event.MISSED);
  }

  void insertMissedCall(@NonNull RemotePeer remotePeer, long timestamp, boolean isVideoOffer, @NonNull CallTable.Event missedEvent) {
    signalCallManager.insertMissedCall(remotePeer, timestamp, isVideoOffer, missedEvent);
  }

  void insertReceivedCall(@NonNull RemotePeer remotePeer, boolean isVideoOffer) {
    signalCallManager.insertReceivedCall(remotePeer, isVideoOffer);
  }

  boolean startWebRtcCallActivityIfPossible() {
    return signalCallManager.startCallCardActivityIfPossible();
  }

  void registerPowerButtonReceiver() {
    WebRtcCallService.changePowerButtonReceiver(context, true);
  }

  void unregisterPowerButtonReceiver() {
    WebRtcCallService.changePowerButtonReceiver(context, false);
  }

  void silenceIncomingRinger() {
    WebRtcCallService.sendAudioManagerCommand(context, new AudioManagerCommand.SilenceIncomingRinger());
  }

  void initializeAudioForCall() {
    WebRtcCallService.sendAudioManagerCommand(context, new AudioManagerCommand.Initialize());
  }

  void startIncomingRinger(@Nullable Uri ringtoneUri, boolean vibrate) {
    WebRtcCallService.sendAudioManagerCommand(context, new AudioManagerCommand.StartIncomingRinger(ringtoneUri, vibrate));
  }

  void startOutgoingRinger() {
    WebRtcCallService.sendAudioManagerCommand(context, new AudioManagerCommand.StartOutgoingRinger());
  }

  void stopAudio(boolean playDisconnect) {
    WebRtcCallService.sendAudioManagerCommand(context, new AudioManagerCommand.Stop(playDisconnect));
  }

  void startAudioCommunication() {
    WebRtcCallService.sendAudioManagerCommand(context, new AudioManagerCommand.Start());
  }

  public void setUserAudioDevice(@Nullable RecipientId recipientId, @NonNull SignalAudioManager.ChosenAudioDeviceIdentifier userDevice) {
    if (userDevice.isLegacy()) {
      WebRtcCallService.sendAudioManagerCommand(context, new AudioManagerCommand.SetUserDevice(recipientId, userDevice.getDesiredAudioDeviceLegacy().ordinal(), false));
    } else {
      WebRtcCallService.sendAudioManagerCommand(context, new AudioManagerCommand.SetUserDevice(recipientId, userDevice.getDesiredAudioDevice31(), true));
    }
  }

  public void setDefaultAudioDevice(@NonNull RecipientId recipientId, @NonNull SignalAudioManager.AudioDevice userDevice, boolean clearUserEarpieceSelection) {
    WebRtcCallService.sendAudioManagerCommand(context, new AudioManagerCommand.SetDefaultDevice(recipientId, userDevice, clearUserEarpieceSelection));
  }

  public void playStateChangeUp() {
    WebRtcCallService.sendAudioManagerCommand(context, new AudioManagerCommand.PlayStateChangeUp());
  }

  void peekGroupCallForRingingCheck(@NonNull GroupCallRingCheckInfo groupCallRingCheckInfo) {
    signalCallManager.peekGroupCallForRingingCheck(groupCallRingCheckInfo);
  }

  public void activateCall(RecipientId recipientId) {
    AndroidTelecomUtil.activateCall(recipientId);
  }

  public void terminateCall(RecipientId recipientId) {
    AndroidTelecomUtil.terminateCall(recipientId);
  }

  public boolean addNewIncomingCall(RecipientId recipientId, long callId, boolean remoteVideoOffer) {
    return AndroidTelecomUtil.addIncomingCall(recipientId, callId, remoteVideoOffer);
  }

  public void rejectIncomingCall(RecipientId recipientId) {
    AndroidTelecomUtil.reject(recipientId);
  }

  public boolean addNewOutgoingCall(RecipientId recipientId, long callId, boolean isVideoCall) {
    return AndroidTelecomUtil.addOutgoingCall(recipientId, callId, isVideoCall);
  }

  public void requestGroupMembershipProof(GroupId.V2 groupId, int groupCallHashCode) {
    signalCallManager.requestGroupMembershipToken(groupId, groupCallHashCode);
  }

  public void sendAcceptedCallEventSyncMessage(@NonNull RemotePeer remotePeer, boolean isOutgoing, boolean isVideoCall) {
    signalCallManager.sendAcceptedCallEventSyncMessage(remotePeer, isOutgoing, isVideoCall);
  }

  public void sendNotAcceptedCallEventSyncMessage(@NonNull RemotePeer remotePeer, boolean isOutgoing, boolean isVideoCall) {
    signalCallManager.sendNotAcceptedCallEventSyncMessage(remotePeer, isOutgoing, isVideoCall);
  }

  public void sendGroupCallNotAcceptedCallEventSyncMessage(@NonNull RemotePeer remotePeer, boolean isOutgoing) {
    signalCallManager.sendGroupCallNotAcceptedCallEventSyncMessage(remotePeer, isOutgoing);
  }
}
