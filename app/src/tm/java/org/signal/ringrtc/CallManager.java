//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.signal.ringrtc;

import android.content.Context;
import android.os.Build;
import android.util.LongSparseArray;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.archiver.device.RecordedAudioToFileController;
import org.signal.ringrtc.CallLinkState.Restrictions;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.ContextUtils;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.NativeLibraryLoader;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SoftwareVideoDecoderFactory;
import org.webrtc.SoftwareVideoEncoderFactory;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoSink;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.Logging.Severity;
import org.webrtc.PeerConnection.AdapterType;
import org.webrtc.PeerConnection.BundlePolicy;
import org.webrtc.PeerConnection.ContinualGatheringPolicy;
import org.webrtc.PeerConnection.IceTransportsType;
import org.webrtc.PeerConnection.RtcpMuxPolicy;
import org.webrtc.PeerConnection.SdpSemantics;
import org.webrtc.PeerConnection.TcpCandidatePolicy;
import org.webrtc.PeerConnectionFactory.InitializationOptions;
import org.webrtc.audio.JavaAudioDeviceModule;

//*TM_SA*/copy and change this class
public class CallManager {
  @NonNull
  private static final String TAG = CallManager.class.getSimpleName();
  private static boolean isInitialized;
  private long nativeCallManager;
  @NonNull
  private Observer observer;
  @NonNull
  private LongSparseArray<GroupCall> groupCallByClientId;
  @NonNull
  private Requests<HttpResult<PeekInfo>> peekRequests;
  @NonNull
  private Requests<HttpResult<CallLinkState>> callLinkRequests;
  @Nullable
  private PeerConnectionFactory groupFactory;

  @Nullable
  private static RecordedAudioToFileController saveRecordedAudioToFile;/*TM_SA*/


  static final ExecutorService executor = Executors.newSingleThreadExecutor();/*TM_SA*/


  public static void initialize(Context applicationContext, Log.Logger logger, Map<String, String> fieldTrials) {
    try {
      android.util.Log.d("CallManager", "initialize");
      Log.initialize(logger);
      PeerConnectionFactory.InitializationOptions.Builder builder = InitializationOptions.builder(applicationContext).setNativeLibraryLoader(new NoOpLoader());
      BuildInfo buildInfo = ringrtcGetBuildInfo();
      Map<String, String> fieldTrialsWithDefaults = new HashMap();
      fieldTrialsWithDefaults.put("WebRTC-Audio-OpusSetSignalVoiceWithDtx", "Enabled");
      fieldTrialsWithDefaults.putAll(fieldTrials);
      String fieldTrialsString = buildFieldTrialsString(fieldTrialsWithDefaults);
      Log.i(TAG, "CallManager.initialize(): (" + (buildInfo.debug ? "debug" : "release") + " build, field trials = " + fieldTrialsString + ")");
      if (buildInfo.debug) {
        builder.setInjectableLogger(new WebRtcLogger(), Severity.LS_INFO);
      } else {
        builder.setInjectableLogger(new WebRtcLogger(), Severity.LS_WARNING);
      }

      builder.setFieldTrials(fieldTrialsString);
      PeerConnectionFactory.initialize(builder.createInitializationOptions());
      ringrtcInitialize();
      isInitialized = true;
      Log.i(TAG, "CallManager.initialize() returned");
    } catch (UnsatisfiedLinkError var7) {
      Log.w(TAG, "Unable to load ringrtc library", var7);
      throw new AssertionError("Unable to load ringrtc library", var7);
    } catch (CallException var8) {
      Log.w(TAG, "Unable to initialize ringrtc library", var8);
      throw new AssertionError("Unable to initialize ringrtc library", var8);
    }
  }

  private static void checkInitializeHasBeenCalled() {
    if (!isInitialized) {
      throw new IllegalStateException("CallManager.initialize has not been called");
    }
  }

  private static String buildFieldTrialsString(Map<String, String> fieldTrials) {
    StringBuilder builder = new StringBuilder();
    Iterator var2 = fieldTrials.entrySet().iterator();

    while(var2.hasNext()) {
      Map.Entry<String, String> entry = (Map.Entry)var2.next();
      builder.append((String)entry.getKey());
      builder.append('/');
      builder.append((String)entry.getValue());
      builder.append('/');
    }

    return builder.toString();
  }

  private PeerConnectionFactory createPeerConnectionFactory(@Nullable EglBase eglBase, AudioProcessingMethod audioProcessingMethod) {
    Set<String> HARDWARE_ENCODING_BLOCKLIST = new HashSet<String>() {
      {
        this.add("SM-G920F");
        this.add("SM-G920FD");
        this.add("SM-G920FQ");
        this.add("SM-G920I");
        this.add("SM-G920A");
        this.add("SM-G920T");
        this.add("SM-G930F");
        this.add("SM-G930FD");
        this.add("SM-G930W8");
        this.add("SM-G930S");
        this.add("SM-G930K");
        this.add("SM-G930L");
        this.add("SM-G935F");
        this.add("SM-G935FD");
        this.add("SM-G935W8");
        this.add("SM-G935S");
        this.add("SM-G935K");
        this.add("SM-G935L");
        this.add("SM-A320F");
        this.add("SM-A320FL");
        this.add("SM-A320F/DS");
        this.add("SM-A320Y/DS");
        this.add("SM-A320Y");
        this.add("SM-S901B");
      }
    };
    Object encoderFactory;
    if (eglBase != null && !HARDWARE_ENCODING_BLOCKLIST.contains(Build.MODEL)) {
      encoderFactory = new DefaultVideoEncoderFactory(eglBase.getEglBaseContext(), true, true);
    } else {
      encoderFactory = new SoftwareVideoEncoderFactory();
    }

    Object decoderFactory;
    if (eglBase == null) {
      decoderFactory = new SoftwareVideoDecoderFactory();
    } else {
      decoderFactory = new DefaultVideoDecoderFactory(eglBase.getEglBaseContext());
    }

    JavaAudioDeviceModule adm = createAudioDeviceModule(audioProcessingMethod);
    PeerConnectionFactory factory = PeerConnectionFactory.builder().setOptions(new PeerConnectionFactoryOptions()).setAudioDeviceModule(adm).setVideoEncoderFactory((VideoEncoderFactory)encoderFactory).setVideoDecoderFactory((VideoDecoderFactory)decoderFactory).createPeerConnectionFactory();
    adm.release();
    return factory;
  }

  static JavaAudioDeviceModule createAudioDeviceModule(AudioProcessingMethod audioProcessingMethod) {
    boolean useHardware;
    boolean useAecM;
    switch (audioProcessingMethod) {
      case ForceSoftwareAecM:
        useHardware = false;
        useAecM = true;
        break;
      case ForceSoftwareAec3:
        useHardware = false;
        useAecM = false;
        break;
      default:
        useHardware = true;
        useAecM = false;
    }

    Log.i(TAG, "createAudioDeviceModule(): useHardware: " + useHardware + " useAecM: " + useAecM);
    Context context = ContextUtils.getApplicationContext();/*TM_SA*/
    Log.d(TAG, "Enable recording of microphone input audio to file");/*TM_SA*/
    saveRecordedAudioToFile = new RecordedAudioToFileController(executor, context.getCacheDir().getAbsolutePath());/*TM_SA*/
    return JavaAudioDeviceModule.builder(context)
                                .setSamplesReadyCallback(saveRecordedAudioToFile) /*TM_SA*/
                                .setUseHardwareAcousticEchoCanceler(useHardware).setUseHardwareNoiseSuppressor(useHardware).setUseAecm(useAecM).createAudioDeviceModule();
  }

  private void checkCallManagerExists() {
    if (this.nativeCallManager == 0L) {
      throw new IllegalStateException("CallManager has been disposed");
    }
  }

  CallManager(@NonNull Observer observer) {
    Log.i(TAG, "CallManager():");
    this.observer = observer;
    this.nativeCallManager = 0L;
    this.groupCallByClientId = new LongSparseArray();
    this.peekRequests = new Requests();
    this.callLinkRequests = new Requests();
  }

  @Nullable
  public static CallManager createCallManager(@NonNull Observer observer) throws CallException {
    Log.i(TAG, "createCallManager():");
    checkInitializeHasBeenCalled();
    CallManager callManager = new CallManager(observer);
    long nativeCallManager = ringrtcCreateCallManager(callManager);
    if (nativeCallManager != 0L) {
      callManager.nativeCallManager = nativeCallManager;
      return callManager;
    } else {
      Log.w(TAG, "Unable to create Call Manager");
      return null;
    }
  }

  public void close() throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "close():");
    if (this.groupCallByClientId != null && this.groupCallByClientId.size() > 0) {
      Log.w(TAG, "Closing CallManager but groupCallByClientId still has objects");
    }

    if (this.groupFactory != null) {
      this.groupFactory.dispose();
    }
    this.ringrtcClose(this.nativeCallManager);
    this.nativeCallManager = 0L;
  }

  public void setSelfUuid(@NonNull UUID uuid) throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "setSelfUuid():");
    this.ringrtcSetSelfUuid(this.nativeCallManager, Util.getBytesFromUuid(uuid));
  }

  public void call(Remote remote, @NonNull CallMediaType callMediaType, @NonNull Integer localDeviceId) throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "call(): creating new call:");
    this.ringrtcCall(this.nativeCallManager, remote, callMediaType.ordinal(), localDeviceId);
  }

  public void proceed(@NonNull CallId callId, @NonNull Context context, @NonNull EglBase eglBase, AudioProcessingMethod audioProcessingMethod, @NonNull VideoSink localSink, @NonNull VideoSink remoteSink, @NonNull CameraControl camera, @NonNull List<PeerConnection.IceServer> iceServers, boolean hideIp, DataMode dataMode, @Nullable Integer audioLevelsIntervalMs, boolean enableCamera) throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "proceed(): callId: " + callId + ", hideIp: " + hideIp);
    Iterator var13 = iceServers.iterator();

    while(var13.hasNext()) {
      PeerConnection.IceServer iceServer = (PeerConnection.IceServer)var13.next();
      Iterator var15 = iceServer.urls.iterator();

      while(var15.hasNext()) {
        String url = (String)var15.next();
        Log.i(TAG, "  server: " + url);
      }
    }

    PeerConnectionFactory factory = this.createPeerConnectionFactory(eglBase, audioProcessingMethod);
    CallContext callContext = new CallContext(callId, context, factory, localSink, remoteSink, camera, iceServers, hideIp);
    callContext.setVideoEnabled(enableCamera);
    int audioLevelsIntervalMillis = audioLevelsIntervalMs == null ? 0 : audioLevelsIntervalMs;
    this.ringrtcProceed(this.nativeCallManager, callId.longValue(), callContext, dataMode.ordinal(), audioLevelsIntervalMillis);
  }

  public void drop(@NonNull CallId callId) throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "drop(): " + callId);
    this.ringrtcDrop(this.nativeCallManager, callId.longValue());
  }

  public void reset() throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "reset():");
    this.ringrtcReset(this.nativeCallManager);
  }

  public void messageSent(@NonNull CallId callId) throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "messageSent(): " + callId);
    this.ringrtcMessageSent(this.nativeCallManager, callId.longValue());
  }

  public void messageSendFailure(@NonNull CallId callId) throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "messageSendFailure(): " + callId);
    this.ringrtcMessageSendFailure(this.nativeCallManager, callId.longValue());
  }

  public void receivedOffer(CallId callId, Remote remote, Integer remoteDeviceId, @NonNull byte[] opaque, Long messageAgeSec, CallMediaType callMediaType, Integer localDeviceId, boolean isLocalDevicePrimary, @NonNull byte[] senderIdentityKey, @NonNull byte[] receiverIdentityKey) throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "receivedOffer(): id: " + callId.format(remoteDeviceId));
    this.ringrtcReceivedOffer(this.nativeCallManager, callId.longValue(), remote, remoteDeviceId, opaque, messageAgeSec, callMediaType.ordinal(), localDeviceId, isLocalDevicePrimary, senderIdentityKey, receiverIdentityKey);
  }

  public void receivedAnswer(CallId callId, Integer remoteDeviceId, @NonNull byte[] opaque, @NonNull byte[] senderIdentityKey, @NonNull byte[] receiverIdentityKey) throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "receivedAnswer(): id: " + callId.format(remoteDeviceId));
    this.ringrtcReceivedAnswer(this.nativeCallManager, callId.longValue(), remoteDeviceId, opaque, senderIdentityKey, receiverIdentityKey);
  }

  public void receivedIceCandidates(CallId callId, Integer remoteDeviceId, @NonNull List<byte[]> iceCandidates) throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "receivedIceCandidates(): id: " + callId.format(remoteDeviceId) + ", count: " + iceCandidates.size());
    this.ringrtcReceivedIceCandidates(this.nativeCallManager, callId.longValue(), remoteDeviceId, iceCandidates);
  }

  public void receivedHangup(CallId callId, Integer remoteDeviceId, HangupType hangupType, Integer deviceId) throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "receivedHangup(): id: " + callId.format(remoteDeviceId));
    this.ringrtcReceivedHangup(this.nativeCallManager, callId.longValue(), remoteDeviceId, hangupType.ordinal(), deviceId);
  }

  public void receivedBusy(CallId callId, Integer remoteDeviceId) throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "receivedBusy(): id: " + callId.format(remoteDeviceId));
    this.ringrtcReceivedBusy(this.nativeCallManager, callId.longValue(), remoteDeviceId);
  }

  public void receivedCallMessage(@NonNull UUID senderUuid, @NonNull Integer senderDeviceId, @NonNull Integer localDeviceId, @NonNull byte[] message, @NonNull Long messageAgeSec) throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "receivedCallMessage():");
    this.ringrtcReceivedCallMessage(this.nativeCallManager, Util.getBytesFromUuid(senderUuid), senderDeviceId, localDeviceId, message, messageAgeSec);
  }

  public void receivedHttpResponse(long requestId, int status, @NonNull byte[] body) throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "receivedHttpResponse(): requestId: " + requestId);
    this.ringrtcReceivedHttpResponse(this.nativeCallManager, requestId, status, body);
  }

  public void httpRequestFailed(long requestId) throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "httpRequestFailed(): requestId: " + requestId);
    this.ringrtcHttpRequestFailed(this.nativeCallManager, requestId);
  }

  public void acceptCall(@NonNull CallId callId) throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "accept(): " + callId);
    this.ringrtcAcceptCall(this.nativeCallManager, callId.longValue());
  }

  public void setAudioEnable(boolean enable) throws CallException {
    this.checkCallManagerExists();
    Connection connection = this.ringrtcGetActiveConnection(this.nativeCallManager);
    connection.setAudioEnabled(enable);
  }

  public void setVideoEnable(boolean enable) throws CallException {
    this.checkCallManagerExists();
    CallContext callContext = this.ringrtcGetActiveCallContext(this.nativeCallManager);
    callContext.setVideoEnabled(enable);
    this.ringrtcSetVideoEnable(this.nativeCallManager, enable);
  }

  public void updateDataMode(DataMode dataMode) throws CallException {
    this.checkCallManagerExists();
    this.ringrtcUpdateDataMode(this.nativeCallManager, dataMode.ordinal());
  }

  public void hangup() throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "hangup():");
    this.ringrtcHangup(this.nativeCallManager);
  }

  public void cancelGroupRing(@NonNull byte[] groupId, long ringId, @Nullable RingCancelReason reason) throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "cancelGroupRing():");
    int rawReason = reason != null ? reason.ordinal() : -1;
    this.ringrtcCancelGroupRing(this.nativeCallManager, groupId, ringId, rawReason);
  }

  public void readCallLink(@NonNull String sfuUrl, @NonNull byte[] authCredentialPresentation, @NonNull CallLinkRootKey linkRootKey, @NonNull ResponseHandler<HttpResult<CallLinkState>> handler) throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "readCallLink():");
    long requestId = this.callLinkRequests.add(handler);
    this.ringrtcReadCallLink(this.nativeCallManager, sfuUrl, authCredentialPresentation, linkRootKey.getKeyBytes(), requestId);
  }

  public void createCallLink(@NonNull String sfuUrl, @NonNull byte[] createCredentialPresentation, @NonNull CallLinkRootKey linkRootKey, @NonNull byte[] adminPasskey, @NonNull byte[] callLinkPublicParams, @NonNull ResponseHandler<HttpResult<CallLinkState>> handler) throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "createCallLink():");
    long requestId = this.callLinkRequests.add(handler);
    this.ringrtcCreateCallLink(this.nativeCallManager, sfuUrl, createCredentialPresentation, linkRootKey.getKeyBytes(), adminPasskey, callLinkPublicParams, requestId);
  }

  public void updateCallLinkName(@NonNull String sfuUrl, @NonNull byte[] authCredentialPresentation, @NonNull CallLinkRootKey linkRootKey, @NonNull byte[] adminPasskey, @NonNull String newName, @NonNull ResponseHandler<HttpResult<CallLinkState>> handler) throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "updateCallLinkName():");
    long requestId = this.callLinkRequests.add(handler);
    this.ringrtcUpdateCallLink(this.nativeCallManager, sfuUrl, authCredentialPresentation, linkRootKey.getKeyBytes(), adminPasskey, newName, -1, -1, requestId);
  }

  public void updateCallLinkRestrictions(@NonNull String sfuUrl, @NonNull byte[] authCredentialPresentation, @NonNull CallLinkRootKey linkRootKey, @NonNull byte[] adminPasskey, @NonNull CallLinkState.Restrictions restrictions, @NonNull ResponseHandler<HttpResult<CallLinkState>> handler) throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "updateCallLinkRestrictions():");
    if (restrictions == Restrictions.UNKNOWN) {
      throw new IllegalArgumentException("cannot set a call link's restrictions to UNKNOWN");
    } else {
      long requestId = this.callLinkRequests.add(handler);
      this.ringrtcUpdateCallLink(this.nativeCallManager, sfuUrl, authCredentialPresentation, linkRootKey.getKeyBytes(), adminPasskey, (String)null, restrictions.ordinal(), -1, requestId);
    }
  }

  public void updateCallLinkRevoked(@NonNull String sfuUrl, @NonNull byte[] authCredentialPresentation, @NonNull CallLinkRootKey linkRootKey, @NonNull byte[] adminPasskey, boolean revoked, @NonNull ResponseHandler<HttpResult<CallLinkState>> handler) throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "updateCallLinkRevoked():");
    long requestId = this.callLinkRequests.add(handler);
    this.ringrtcUpdateCallLink(this.nativeCallManager, sfuUrl, authCredentialPresentation, linkRootKey.getKeyBytes(), adminPasskey, (String)null, -1, revoked ? 1 : 0, requestId);
  }

  public void peekGroupCall(@NonNull String sfuUrl, @NonNull byte[] membershipProof, @NonNull Collection<GroupCall.GroupMemberInfo> groupMembers, @NonNull ResponseHandler<PeekInfo> handler) throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "peekGroupCall():");
    long requestId = this.peekRequests.add((result) -> {
      if (result.isSuccess()) {
        handler.handleResponse((PeekInfo)result.getValue());
      } else {
        handler.handleResponse(new PeekInfo(Collections.emptyList(), (UUID)null, (String)null, (Long)null, 0L, 0L, Collections.emptyList()));
      }

    });
    this.ringrtcPeekGroupCall(this.nativeCallManager, requestId, sfuUrl, membershipProof, Util.serializeFromGroupMemberInfo(groupMembers));
  }

  public void peekCallLinkCall(@NonNull String sfuUrl, @NonNull byte[] authCredentialPresentation, @NonNull CallLinkRootKey linkRootKey, @NonNull ResponseHandler<HttpResult<PeekInfo>> handler) throws CallException {
    this.checkCallManagerExists();
    Log.i(TAG, "peekCallLink():");
    long requestId = this.peekRequests.add(handler);
    this.ringrtcPeekCallLinkCall(this.nativeCallManager, requestId, sfuUrl, authCredentialPresentation, linkRootKey.getKeyBytes());
  }

  @Nullable
  public GroupCall createGroupCall(@NonNull byte[] groupId, @NonNull String sfuUrl, @NonNull byte[] hkdfExtraInfo, @Nullable Integer audioLevelsIntervalMs, AudioProcessingMethod audioProcessingMethod, @NonNull GroupCall.Observer observer) {
    this.checkCallManagerExists();
    if (this.groupFactory == null) {
      this.groupFactory = this.createPeerConnectionFactory((EglBase)null, audioProcessingMethod);
      if (this.groupFactory == null) {
        Log.e(TAG, "createPeerConnectionFactory failed");
        return null;
      }
    }

    GroupCall groupCall = GroupCall.create(this.nativeCallManager, groupId, sfuUrl, hkdfExtraInfo, audioLevelsIntervalMs, this.groupFactory, observer);
    if (groupCall != null) {
      this.groupCallByClientId.append(groupCall.clientId, groupCall);
    }

    return groupCall;
  }

  @Nullable
  public GroupCall createCallLinkCall(@NonNull String sfuUrl, @NonNull byte[] authCredentialPresentation, @NonNull CallLinkRootKey linkRootKey, @Nullable byte[] adminPasskey, @NonNull byte[] hkdfExtraInfo, @Nullable Integer audioLevelsIntervalMs, AudioProcessingMethod audioProcessingMethod, @NonNull GroupCall.Observer observer) {
    this.checkCallManagerExists();
    if (this.groupFactory == null) {
      this.groupFactory = this.createPeerConnectionFactory((EglBase)null, audioProcessingMethod);
      if (this.groupFactory == null) {
        Log.e(TAG, "createPeerConnectionFactory failed");
        return null;
      }
    }

    GroupCall groupCall = GroupCall.create(this.nativeCallManager, sfuUrl, authCredentialPresentation, linkRootKey, adminPasskey, hkdfExtraInfo, audioLevelsIntervalMs, this.groupFactory, observer);
    if (groupCall != null) {
      this.groupCallByClientId.append(groupCall.clientId, groupCall);
    }

    return groupCall;
  }

  @CalledByNative
  @Nullable
  private Connection createConnection(long nativeConnectionBorrowed, long nativeCallId, int remoteDeviceId, CallContext callContext, int audioJitterBufferMaxPackets, int audioJitterBufferMaxTargetDelayMs) {
    CallId callId = new CallId(nativeCallId);
    Log.i(TAG, "createConnection(): connectionId: " + callId.format(remoteDeviceId));
    MediaConstraints constraints = new MediaConstraints();
    PeerConnection.RTCConfiguration configuration = new PeerConnection.RTCConfiguration(callContext.iceServers);
    configuration.sdpSemantics = SdpSemantics.UNIFIED_PLAN;
    configuration.bundlePolicy = BundlePolicy.MAXBUNDLE;
    configuration.rtcpMuxPolicy = RtcpMuxPolicy.REQUIRE;
    configuration.tcpCandidatePolicy = TcpCandidatePolicy.DISABLED;
    configuration.continualGatheringPolicy = ContinualGatheringPolicy.GATHER_CONTINUALLY;
    if (callContext.hideIp) {
      configuration.iceTransportsType = IceTransportsType.RELAY;
    }

    configuration.audioJitterBufferMaxPackets = audioJitterBufferMaxPackets;
    configuration.audioJitterBufferMaxTargetDelayMs = audioJitterBufferMaxTargetDelayMs;
    PeerConnectionFactory factory = callContext.factory;
    CameraControl cameraControl = callContext.cameraControl;

    try {
      long nativePeerConnection = this.ringrtcCreatePeerConnection(factory.getNativeOwnedFactoryAndThreads(), nativeConnectionBorrowed, configuration, constraints);
      if (nativePeerConnection == 0L) {
        Log.w(TAG, "Unable to create native PeerConnection.");
        return null;
      } else {
        Connection connection = new Connection(new Connection.NativeFactory(nativePeerConnection, callId, remoteDeviceId));
        connection.setAudioPlayout(false);
        connection.setAudioRecording(false);
        MediaConstraints audioConstraints = new MediaConstraints();
        AudioSource audioSource = factory.createAudioSource(audioConstraints);
        AudioTrack audioTrack = factory.createAudioTrack("audio1", audioSource);
        audioTrack.setEnabled(false);
        connection.addTrack(audioTrack, Collections.singletonList("s"));
        if (callContext.videoTrack != null) {
          connection.addTrack(callContext.videoTrack, Collections.singletonList("s"));
        }

        connection.setAudioSource(audioSource, audioTrack);

        if (saveRecordedAudioToFile != null) { /*TM_SA*/
          if (saveRecordedAudioToFile.start("" + callId.longValue())) {
            Log.d(TAG, "Recording input audio to file is activated");
          }
        }/*TM_SA*/

        return connection;
      }
    } catch (CallException var20) {
      Log.w(TAG, "Unable to create Peer Connection with native call", var20);
      return null;
    }
  }

  @CalledByNative
  private void onConnectMedia(@NonNull CallContext callContext, @NonNull MediaStream mediaStream) {
    Log.i(TAG, "onConnectMedia(): mediaStream: " + mediaStream);
    if (mediaStream == null) {
      Log.w(TAG, "Remote media stream unavailable");
    } else if (mediaStream.audioTracks == null) {
      Log.w(TAG, "Remote media stream contains no audio tracks");
    } else {
      Iterator var3 = mediaStream.audioTracks.iterator();

      while(var3.hasNext()) {
        AudioTrack remoteAudioTrack = (AudioTrack)var3.next();
        Log.i(TAG, "onConnectMedia(): enabling audioTrack");
        remoteAudioTrack.setEnabled(true);
      }

      if (mediaStream.videoTracks == null) {
        Log.w(TAG, "Remote media stream contains no video tracks");
      } else {
        if (mediaStream.videoTracks.size() == 1) {
          Log.i(TAG, "onConnectMedia(): enabling videoTrack(0)");
          VideoTrack remoteVideoTrack = (VideoTrack)mediaStream.videoTracks.get(0);
          remoteVideoTrack.setEnabled(true);
          remoteVideoTrack.addSink(callContext.remoteSink);
        } else {
          Log.w(TAG, "onConnectMedia(): Media stream contains unexpected number of video tracks: " + mediaStream.videoTracks.size());
        }

      }
    }
  }

  @CalledByNative
  private void onCloseMedia(@NonNull CallContext callContext) {
    Log.i(TAG, "onCloseMedia():");
    callContext.setVideoEnabled(false);
  }

  @CalledByNative
  private void closeConnection(Connection connection) {
    Log.i(TAG, "closeConnection(): " + connection);
    connection.shutdown();
  }

  @CalledByNative
  private void closeCall(@NonNull CallContext callContext) {
    Log.i(TAG, "closeCall():");
    callContext.dispose();
  }

  @CalledByNative
  private void onStartCall(Remote remote, long callId, boolean isOutgoing, CallMediaType callMediaType) {
    Log.i(TAG, "onStartCall():");
    this.observer.onStartCall(remote, new CallId(callId), isOutgoing, callMediaType);
  }

  @CalledByNative
  private void onEvent(Remote remote, CallEvent event) {
    Log.i(TAG, "onEvent():");
    this.observer.onCallEvent(remote, event);
  }

  @CalledByNative
  private void onNetworkRouteChanged(Remote remote, int localNetworkAdapterType) {
    Log.i(TAG, "onNetworkRouteChange():");
    NetworkRoute networkRoute = new NetworkRoute(this.NetworkAdapterTypeFromRawValue(localNetworkAdapterType));
    this.observer.onNetworkRouteChanged(remote, networkRoute);
  }

  @CalledByNative
  private void onAudioLevels(Remote remote, int capturedLevel, int receivedLevel) {
    this.observer.onAudioLevels(remote, capturedLevel, receivedLevel);
  }

  @CalledByNative
  private void onLowBandwidthForVideo(Remote remote, boolean recovered) {
    this.observer.onLowBandwidthForVideo(remote, recovered);
  }

  @NonNull
  private PeerConnection.AdapterType NetworkAdapterTypeFromRawValue(int localNetworkAdapterType) {
    switch (localNetworkAdapterType) {
      case 0:
        return AdapterType.UNKNOWN;
      case 1:
        return AdapterType.ETHERNET;
      case 2:
        return AdapterType.WIFI;
      case 4:
        return AdapterType.CELLULAR;
      case 8:
        return AdapterType.VPN;
      case 16:
        return AdapterType.LOOPBACK;
      case 32:
        return AdapterType.ADAPTER_TYPE_ANY;
      case 64:
        return AdapterType.CELLULAR_2G;
      case 128:
        return AdapterType.CELLULAR_3G;
      case 256:
        return AdapterType.CELLULAR_4G;
      case 512:
        return AdapterType.CELLULAR_5G;
      default:
        return AdapterType.UNKNOWN;
    }
  }

  @CalledByNative
  private void onCallConcluded(Remote remote) {
    Log.i(TAG, "onCallConcluded():");
    if (saveRecordedAudioToFile != null) {//*TM_SA*/s
      Log.d(TAG, "Closing audio file for recorded input audio.");
      saveRecordedAudioToFile.stop();
//      saveRecordedAudioToFile = null;
    }//*TM_SA*/e
    this.observer.onCallConcluded(remote);
  }

  @CalledByNative
  private void onSendOffer(long callId, Remote remote, int remoteDeviceId, boolean broadcast, @NonNull byte[] opaque, CallMediaType callMediaType) {
    Log.i(TAG, "onSendOffer():");
    this.observer.onSendOffer(new CallId(callId), remote, remoteDeviceId, broadcast, opaque, callMediaType);
  }

  @CalledByNative
  private void onSendAnswer(long callId, Remote remote, int remoteDeviceId, boolean broadcast, @NonNull byte[] opaque) {
    Log.i(TAG, "onSendAnswer():");
    this.observer.onSendAnswer(new CallId(callId), remote, remoteDeviceId, broadcast, opaque);
  }

  @CalledByNative
  private void onSendIceCandidates(long callId, Remote remote, int remoteDeviceId, boolean broadcast, List<byte[]> iceCandidates) {
    Log.i(TAG, "onSendIceCandidates():");
    this.observer.onSendIceCandidates(new CallId(callId), remote, remoteDeviceId, broadcast, iceCandidates);
  }

  @CalledByNative
  private void onSendHangup(long callId, Remote remote, int remoteDeviceId, boolean broadcast, HangupType hangupType, int deviceId) {
    Log.i(TAG, "onSendHangup():");
    this.observer.onSendHangup(new CallId(callId), remote, remoteDeviceId, broadcast, hangupType, deviceId);
  }

  @CalledByNative
  private void onSendBusy(long callId, Remote remote, int remoteDeviceId, boolean broadcast) {
    Log.i(TAG, "onSendBusy():");
    this.observer.onSendBusy(new CallId(callId), remote, remoteDeviceId, broadcast);
  }

  @CalledByNative
  private void sendCallMessage(@NonNull byte[] recipientUuid, @NonNull byte[] message, int urgency) {
    Log.i(TAG, "sendCallMessage():");
    this.observer.onSendCallMessage(Util.getUuidFromBytes(recipientUuid), message, CallManager.CallMessageUrgency.values()[urgency]);
  }

  @CalledByNative
  private void sendCallMessageToGroup(@NonNull byte[] groupId, @NonNull byte[] message, int urgency) {
    Log.i(TAG, "sendCallMessageToGroup():");
    this.observer.onSendCallMessageToGroup(groupId, message, CallManager.CallMessageUrgency.values()[urgency]);
  }

  @CalledByNative
  private void sendHttpRequest(long requestId, String url, HttpMethod method, List<HttpHeader> headers, @Nullable byte[] body) {
    Log.i(TAG, "sendHttpRequest():");
    this.observer.onSendHttpRequest(requestId, url, method, headers, body);
  }

  @CalledByNative
  private boolean compareRemotes(Remote remote1, Remote remote2) {
    Log.i(TAG, "compareRemotes():");
    return remote1 != null ? remote1.recipientEquals(remote2) : false;
  }

  @CalledByNative
  private void groupCallRingUpdate(@NonNull byte[] groupId, long ringId, @NonNull byte[] sender, int state) {
    Log.i(TAG, "groupCallRingUpdate():");
    this.observer.onGroupCallRingUpdate(groupId, ringId, Util.getUuidFromBytes(sender), CallManager.RingUpdate.values()[state]);
  }

  @CalledByNative
  private void handlePeekResponse(long requestId, HttpResult<PeekInfo> info) {
    if (!this.peekRequests.resolve(requestId, info)) {
      Log.w(TAG, "Invalid requestId for handlePeekResponse: " + requestId);
    }

  }

  @CalledByNative
  private void handleCallLinkResponse(long requestId, HttpResult<CallLinkState> response) {
    if (!this.callLinkRequests.resolve(requestId, response)) {
      Log.w(TAG, "Invalid requestId for handleCallLinkResponse: " + requestId);
    }

  }

  @CalledByNative
  private void requestMembershipProof(long clientId) {
    Log.i(TAG, "requestMembershipProof():");
    GroupCall groupCall = (GroupCall)this.groupCallByClientId.get(clientId);
    if (groupCall == null) {
      Log.w(TAG, "groupCall not found by clientId: " + clientId);
    } else {
      groupCall.requestMembershipProof();
    }
  }

  @CalledByNative
  private void requestGroupMembers(long clientId) {
    Log.i(TAG, "requestGroupMembers():");
    GroupCall groupCall = (GroupCall)this.groupCallByClientId.get(clientId);
    if (groupCall == null) {
      Log.w(TAG, "groupCall not found by clientId: " + clientId);
    } else {
      groupCall.requestGroupMembers();
    }
  }

  @CalledByNative
  private void handleConnectionStateChanged(long clientId, GroupCall.ConnectionState connectionState) {
    Log.i(TAG, "handleConnectionStateChanged():");
    GroupCall groupCall = (GroupCall)this.groupCallByClientId.get(clientId);
    if (groupCall == null) {
      Log.w(TAG, "groupCall not found by clientId: " + clientId);
    } else {
      groupCall.handleConnectionStateChanged(connectionState);
    }
  }

  @CalledByNative
  private void handleNetworkRouteChanged(long clientId, int localNetworkAdapterType) {
    Log.i(TAG, "handleNetworkRouteChanged():");
    GroupCall groupCall = (GroupCall)this.groupCallByClientId.get(clientId);
    if (groupCall == null) {
      Log.w(TAG, "groupCall not found by clientId: " + clientId);
    } else {
      NetworkRoute networkRoute = new NetworkRoute(this.NetworkAdapterTypeFromRawValue(localNetworkAdapterType));
      groupCall.handleNetworkRouteChanged(networkRoute);
    }
  }

  @CalledByNative
  private void handleAudioLevels(long clientId, int capturedLevel, List<GroupCall.ReceivedAudioLevel> receivedLevels) {
    GroupCall groupCall = (GroupCall)this.groupCallByClientId.get(clientId);
    if (groupCall == null) {
      Log.w(TAG, "groupCall not found by clientId: " + clientId);
    } else {
      groupCall.handleAudioLevels(capturedLevel, receivedLevels);
    }
  }

  @CalledByNative
  private void handleLowBandwidthForVideo(long clientId, boolean recovered) {
    GroupCall groupCall = (GroupCall)this.groupCallByClientId.get(clientId);
    if (groupCall == null) {
      Log.w(TAG, "groupCall not found by clientId: " + clientId);
    } else {
      groupCall.handleLowBandwidthForVideo(recovered);
    }
  }

  @CalledByNative
  private void handleReactions(long clientId, List<GroupCall.Reaction> reactions) {
    GroupCall groupCall = (GroupCall)this.groupCallByClientId.get(clientId);
    if (groupCall == null) {
      Log.w(TAG, "groupCall not found by clientId: " + clientId);
    } else {
      groupCall.handleReactions(reactions);
    }
  }

  @CalledByNative
  private void handleRaisedHands(long clientId, List<Long> raisedHands) {
    GroupCall groupCall = (GroupCall)this.groupCallByClientId.get(clientId);
    if (groupCall == null) {
      Log.w(TAG, "groupCall not found by clientId: " + clientId);
    } else {
      groupCall.handleRaisedHands(raisedHands);
    }
  }

  @CalledByNative
  private void handleJoinStateChanged(long clientId, GroupCall.JoinState joinState, Long demuxId) {
    Log.i(TAG, "handleJoinStateChanged() -> joinState: " + joinState);
//    Log.i(TAG, "handleJoinStateChanged() -> clientId: " + clientId + ". demuxId: " + demuxId);
    GroupCall groupCall = (GroupCall)this.groupCallByClientId.get(clientId);
    if (groupCall == null) {
      Log.w(TAG, "groupCall not found by clientId: " + clientId);
    } else {
      groupCall.handleJoinStateChanged(joinState, demuxId);
    }
    if (saveRecordedAudioToFile != null) {//*TM_SA*/Start
      if (demuxId != null && joinState == GroupCall.JoinState.JOINED) {
        if (saveRecordedAudioToFile.start("" + demuxId)) {
          Log.d(TAG, "Recording input audio to file is activated");
        }
      }
      if (joinState == GroupCall.JoinState.NOT_JOINED) {
        Log.d(TAG, "Closing audio file for recorded input audio.");
        saveRecordedAudioToFile.stop();
//        saveRecordedAudioToFile = null;
      }
    }//*TM_SA*/End

  }

  @CalledByNative
  private void handleRemoteDevicesChanged(long clientId, List<GroupCall.RemoteDeviceState> remoteDeviceStates) {
    if (remoteDeviceStates != null) {
      Log.i(TAG, "handleRemoteDevicesChanged(): remoteDeviceStates.size = " + remoteDeviceStates.size());
    } else {
      Log.i(TAG, "handleRemoteDevicesChanged(): remoteDeviceStates is null!");
    }

    GroupCall groupCall = (GroupCall)this.groupCallByClientId.get(clientId);
    if (groupCall == null) {
      Log.w(TAG, "groupCall not found by clientId: " + clientId);
    } else {
      groupCall.handleRemoteDevicesChanged(remoteDeviceStates);
    }
  }

  @CalledByNative
  private void handleIncomingVideoTrack(long clientId, long remoteDemuxId, long nativeVideoTrackBorrowedRc) {
    Log.i(TAG, "handleIncomingVideoTrack():");
    GroupCall groupCall = (GroupCall)this.groupCallByClientId.get(clientId);
    if (groupCall == null) {
      Log.w(TAG, "groupCall not found by clientId: " + clientId);
    } else {
      groupCall.handleIncomingVideoTrack(remoteDemuxId, nativeVideoTrackBorrowedRc);
    }
  }

  @CalledByNative
  private void handlePeekChanged(long clientId, PeekInfo info) {
    GroupCall groupCall = (GroupCall)this.groupCallByClientId.get(clientId);
    if (groupCall == null) {
      Log.w(TAG, "groupCall not found by clientId: " + clientId);
    } else {
      groupCall.handlePeekChanged(info);
    }
  }

  @CalledByNative
  public void handleEnded(long clientId, GroupCall.GroupCallEndReason reason) { //*TM_SA*/ make it public
    Log.i(TAG, "handleEnded():");
    GroupCall groupCall = (GroupCall)this.groupCallByClientId.get(clientId);
    if (groupCall == null) {
      Log.w(TAG, "groupCall not found by clientId: " + clientId);
    } else {
      this.groupCallByClientId.delete(clientId);
      groupCall.handleEnded(reason);
    }
  }

  private static native BuildInfo ringrtcGetBuildInfo() throws CallException;

  private static native void ringrtcInitialize() throws CallException;

  private static native long ringrtcCreateCallManager(CallManager var0) throws CallException;

  private native void ringrtcSetSelfUuid(long var1, byte[] var3) throws CallException;

  private native long ringrtcCreatePeerConnection(long var1, long var3, PeerConnection.RTCConfiguration var5, MediaConstraints var6) throws CallException;

  private native void ringrtcCall(long var1, Remote var3, int var4, int var5) throws CallException;

  private native void ringrtcProceed(long var1, long var3, CallContext var5, int var6, int var7) throws CallException;

  private native void ringrtcMessageSent(long var1, long var3) throws CallException;

  private native void ringrtcMessageSendFailure(long var1, long var3) throws CallException;

  private native void ringrtcHangup(long var1) throws CallException;

  private native void ringrtcCancelGroupRing(long var1, byte[] var3, long var4, int var6) throws CallException;

  private native void ringrtcReceivedAnswer(long var1, long var3, int var5, byte[] var6, byte[] var7, byte[] var8) throws CallException;

  private native void ringrtcReceivedOffer(long var1, long var3, Remote var5, int var6, byte[] var7, long var8, int var10, int var11, boolean var12, byte[] var13, byte[] var14) throws CallException;

  private native void ringrtcReceivedIceCandidates(long var1, long var3, int var5, List<byte[]> var6) throws CallException;

  private native void ringrtcReceivedHangup(long var1, long var3, int var5, int var6, int var7) throws CallException;

  private native void ringrtcReceivedBusy(long var1, long var3, int var5) throws CallException;

  private native void ringrtcReceivedCallMessage(long var1, byte[] var3, int var4, int var5, byte[] var6, long var7) throws CallException;

  private native void ringrtcReceivedHttpResponse(long var1, long var3, int var5, byte[] var6) throws CallException;

  private native void ringrtcHttpRequestFailed(long var1, long var3) throws CallException;

  private native void ringrtcAcceptCall(long var1, long var3) throws CallException;

  private native Connection ringrtcGetActiveConnection(long var1) throws CallException;

  private native CallContext ringrtcGetActiveCallContext(long var1) throws CallException;

  private native void ringrtcSetVideoEnable(long var1, boolean var3) throws CallException;

  private native void ringrtcUpdateDataMode(long var1, int var3) throws CallException;

  private native void ringrtcDrop(long var1, long var3) throws CallException;

  private native void ringrtcReset(long var1) throws CallException;

  private native void ringrtcClose(long var1) throws CallException;

  private native void ringrtcPeekGroupCall(long var1, long var3, String var5, byte[] var6, byte[] var7) throws CallException;

  private native void ringrtcReadCallLink(long var1, String var3, byte[] var4, byte[] var5, long var6) throws CallException;

  private native void ringrtcCreateCallLink(long var1, String var3, byte[] var4, byte[] var5, byte[] var6, byte[] var7, long var8) throws CallException;

  private native void ringrtcUpdateCallLink(long var1, String var3, byte[] var4, byte[] var5, byte[] var6, String var7, int var8, int var9, long var10) throws CallException;

  private native void ringrtcPeekCallLinkCall(long var1, long var3, String var5, byte[] var6, byte[] var7) throws CallException;

  static {
    Log.d(TAG, "Loading ringrtc library");
    System.loadLibrary("ringrtc");
  }

  static class NoOpLoader implements NativeLibraryLoader {
    public NoOpLoader() {
    }

    public boolean load(String name) {
      return true;
    }
  }

  public static enum AudioProcessingMethod {
    Default,
    ForceHardware,
    ForceSoftwareAec3,
    ForceSoftwareAecM;

    private AudioProcessingMethod() {
    }
  }

  class PeerConnectionFactoryOptions extends PeerConnectionFactory.Options {
    public PeerConnectionFactoryOptions() {
      this.networkIgnoreMask = 16;
    }
  }

  public interface Observer {
    void onStartCall(Remote var1, CallId var2, Boolean var3, CallMediaType var4);

    void onCallEvent(Remote var1, CallEvent var2);

    void onNetworkRouteChanged(Remote var1, NetworkRoute var2);

    void onAudioLevels(Remote var1, int var2, int var3);

    void onLowBandwidthForVideo(Remote var1, boolean var2);

    void onCallConcluded(Remote var1);

    void onSendOffer(CallId var1, Remote var2, Integer var3, Boolean var4, @NonNull byte[] var5, CallMediaType var6);

    void onSendAnswer(CallId var1, Remote var2, Integer var3, Boolean var4, @NonNull byte[] var5);

    void onSendIceCandidates(CallId var1, Remote var2, Integer var3, Boolean var4, List<byte[]> var5);

    void onSendHangup(CallId var1, Remote var2, Integer var3, Boolean var4, HangupType var5, Integer var6);

    void onSendBusy(CallId var1, Remote var2, Integer var3, Boolean var4);

    void onSendCallMessage(@NonNull UUID var1, @NonNull byte[] var2, @NonNull CallMessageUrgency var3);

    void onSendCallMessageToGroup(@NonNull byte[] var1, @NonNull byte[] var2, @NonNull CallMessageUrgency var3);

    void onSendHttpRequest(long var1, @NonNull String var3, @NonNull HttpMethod var4, @Nullable List<HttpHeader> var5, @Nullable byte[] var6);

    void onGroupCallRingUpdate(@NonNull byte[] var1, long var2, @NonNull UUID var4, RingUpdate var5);
  }

  static class Requests<T> {
    private long nextId = 1L;
    @NonNull
    private LongSparseArray<ResponseHandler<T>> handlerById = new LongSparseArray();

    Requests() {
    }

    long add(ResponseHandler<T> handler) {
      long id = (long)(this.nextId++);
      this.handlerById.put(id, handler);
      return id;
    }

    boolean resolve(long id, T response) {
      ResponseHandler<T> handler = (ResponseHandler)this.handlerById.get(id);
      if (handler == null) {
        return false;
      } else {
        handler.handleResponse(response);
        this.handlerById.delete(id);
        return true;
      }
    }
  }

  public static enum CallMediaType {
    AUDIO_CALL,
    VIDEO_CALL;

    private CallMediaType() {
    }

    @CalledByNative
    static CallMediaType fromNativeIndex(int nativeIndex) {
      return values()[nativeIndex];
    }
  }

  static class CallContext {
    @NonNull
    private final String TAG = CallContext.class.getSimpleName();
    @NonNull
    public final CallId callId;
    @NonNull
    public final PeerConnectionFactory factory;
    @NonNull
    public final VideoSink remoteSink;
    @NonNull
    public final CameraControl cameraControl;
    @NonNull
    public final List<PeerConnection.IceServer> iceServers;
    public final boolean hideIp;
    @Nullable
    public final VideoSource videoSource;
    @Nullable
    public final VideoTrack videoTrack;

    public CallContext(@NonNull CallId callId, @NonNull Context context, @NonNull PeerConnectionFactory factory, @NonNull VideoSink localSink, @NonNull VideoSink remoteSink, @NonNull CameraControl camera, @NonNull List<PeerConnection.IceServer> iceServers, boolean hideIp) {
      Log.i(this.TAG, "ctor(): " + callId);
      this.callId = callId;
      this.factory = factory;
      this.remoteSink = remoteSink;
      this.cameraControl = camera;
      this.iceServers = iceServers;
      this.hideIp = hideIp;
      if (this.cameraControl.hasCapturer()) {
        this.videoSource = factory.createVideoSource(false);
        this.videoTrack = factory.createVideoTrack("video1", this.videoSource);
        this.videoTrack.setEnabled(false);
        this.cameraControl.initCapturer(this.videoSource.getCapturerObserver());
        this.videoTrack.addSink(localSink);
      } else {
        this.videoSource = null;
        this.videoTrack = null;
      }

    }

    void setVideoEnabled(boolean enable) {
      Log.i(this.TAG, "setVideoEnabled(): " + this.callId);
      if (this.videoTrack != null) {
        this.videoTrack.setEnabled(enable);
        this.cameraControl.setEnabled(enable);
      }

    }

    void dispose() {
      Log.i(this.TAG, "dispose(): " + this.callId);
      if (this.cameraControl != null) {
        this.cameraControl.setEnabled(false);
      }

      if (this.videoSource != null) {
        this.videoSource.dispose();
      }

      if (this.videoTrack != null) {
        this.videoTrack.dispose();
      }

      this.factory.dispose();
    }
  }

  public static enum DataMode {
    LOW,
    NORMAL;

    private DataMode() {
    }

    @CalledByNative
    static DataMode fromNativeIndex(int nativeIndex) {
      return values()[nativeIndex];
    }
  }

  public static enum HangupType {
    NORMAL,
    ACCEPTED,
    DECLINED,
    BUSY,
    NEED_PERMISSION;

    private HangupType() {
    }

    @CalledByNative
    static HangupType fromNativeIndex(int nativeIndex) {
      return values()[nativeIndex];
    }
  }

  public static enum RingCancelReason {
    DeclinedByUser,
    Busy;

    private RingCancelReason() {
    }
  }

  public interface ResponseHandler<T> {
    void handleResponse(T var1);
  }

  public static enum CallEvent {
    LOCAL_RINGING,
    REMOTE_RINGING,
    LOCAL_CONNECTED,
    REMOTE_CONNECTED,
    ENDED_LOCAL_HANGUP,
    ENDED_REMOTE_HANGUP,
    ENDED_REMOTE_HANGUP_NEED_PERMISSION,
    ENDED_REMOTE_HANGUP_ACCEPTED,
    ENDED_REMOTE_HANGUP_DECLINED,
    ENDED_REMOTE_HANGUP_BUSY,
    ENDED_REMOTE_BUSY,
    ENDED_REMOTE_GLARE,
    ENDED_REMOTE_RECALL,
    ENDED_TIMEOUT,
    ENDED_INTERNAL_FAILURE,
    ENDED_SIGNALING_FAILURE,
    ENDED_GLARE_HANDLING_FAILURE,
    ENDED_CONNECTION_FAILURE,
    ENDED_APP_DROPPED_CALL,
    REMOTE_VIDEO_ENABLE,
    REMOTE_VIDEO_DISABLE,
    REMOTE_SHARING_SCREEN_ENABLE,
    REMOTE_SHARING_SCREEN_DISABLE,
    RECONNECTING,
    RECONNECTED,
    RECEIVED_OFFER_EXPIRED,
    RECEIVED_OFFER_WHILE_ACTIVE,
    RECEIVED_OFFER_WITH_GLARE;

    private CallEvent() {
    }

    @CalledByNative
    static CallEvent fromNativeIndex(int nativeIndex) {
      return values()[nativeIndex];
    }
  }

  public static enum CallMessageUrgency {
    DROPPABLE,
    HANDLE_IMMEDIATELY;

    private CallMessageUrgency() {
    }
  }

  public static enum HttpMethod {
    GET,
    PUT,
    POST,
    DELETE;

    private HttpMethod() {
    }

    @CalledByNative
    static HttpMethod fromNativeIndex(int nativeIndex) {
      return values()[nativeIndex];
    }
  }

  public static enum RingUpdate {
    REQUESTED,
    EXPIRED_REQUEST,
    ACCEPTED_ON_ANOTHER_DEVICE,
    DECLINED_ON_ANOTHER_DEVICE,
    BUSY_LOCALLY,
    BUSY_ON_ANOTHER_DEVICE,
    CANCELLED_BY_RINGER;

    private RingUpdate() {
    }
  }

  public static class HttpResult<T> {
    @Nullable
    private final T value;
    private final short status;

    @CalledByNative
    HttpResult(@NonNull T value) {
      this.value = value;
      this.status = 200;
    }

    @CalledByNative
    HttpResult(short status) {
      this.value = null;
      this.status = status;
    }

    @Nullable
    public T getValue() {
      return this.value;
    }

    public short getStatus() {
      return this.status;
    }

    public boolean isSuccess() {
      return this.value != null;
    }
  }
}
