package org.archiver.device

import android.content.Context
import org.archiver.model.Messages.stringify
import org.signal.core.util.logging.Log
import org.signal.ringrtc.CallId
import org.signal.ringrtc.CallManager
import org.signal.ringrtc.GroupCall
import org.signal.ringrtc.Remote
import org.tm.archive.ringrtc.CallState
import org.tm.archive.ringrtc.RemotePeer
import org.webrtc.audio.JavaAudioDeviceModule
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CallManagerRecordingDelegate(

  private val applicationContext: Context,

  private val executor: ExecutorService
) : ICallManagerRecordingDelegate {

  private var recorder: TMRecordedAudioToFileController? = null
  private val groupRecorder by lazy { createRecorder() }

  override fun createSampleReadyCallback(isGroupCall: Boolean): JavaAudioDeviceModule.SamplesReadyCallback {
    if (isGroupCall)
      return groupRecorder
    return recorder ?: createRecorder().also { recorder = it }
  }

  override fun onEvent(remote: Remote?, event: CallManager.CallEvent?) {
    val peer = remote as? RemotePeer ?: return
    Log.d(javaClass.simpleName, "onEvent ${peer.stringify()} $event ${remote.state}")
    val isOutgoingJustConnected = event == CallManager.CallEvent.REMOTE_CONNECTED && remote.state != CallState.CONNECTED
    val isIncomingJustConnected = !isOutgoingJustConnected && event == CallManager.CallEvent.LOCAL_CONNECTED && remote.state == CallState.LOCAL_RINGING
    if (isOutgoingJustConnected || isIncomingJustConnected) {
      val callId = peer.callId.longValue()
      startRecording(callId.toString(), false)
    }
  }

  override fun onCallConcluded(remote: Remote?) {
    val peer = remote as? RemotePeer ?: return
    val callId = peer.callId.longValue()
    stopRecording(callId.toString(), false)
  }

  override fun handleJoinStateChanged(clientId: Long, joinState: GroupCall.JoinState?, demuxId: Long?, call: GroupCall?) {
    val id = call?.peekInfo?.eraId?.let(CallId::fromEra)?.longValue()?.toString()
    if (id != null && joinState == GroupCall.JoinState.JOINED)
        startRecording(id, true)
      else if (joinState == GroupCall.JoinState.NOT_JOINED)
        stopRecording(id, true)
  }

  private fun startRecording(id: String, isGroupCall: Boolean) {
    val recorder = getRecorder(isGroupCall)
    val started = recorder.start(id)
    if (started)
      Log.d(TAG, "Recording input audio to file is activated for $id")
  }

  private fun stopRecording(id: String?, isGroupCall: Boolean) {
    val recorder = getRecorder(isGroupCall)
    Log.d(TAG, "Closing audio file for recorded input audio  for $id.")
    resetRecorder(isGroupCall)
    recorder.stop()
  }

  private fun getRecorder(isGroupCall: Boolean) = recorder.takeUnless { isGroupCall } ?: groupRecorder

  private fun createRecorder() = TMRecordedAudioToFileController(executor, applicationContext.cacheDir.absolutePath)

  private fun resetRecorder(isGroupCall: Boolean) {
    if (!isGroupCall)
      recorder = null
  }

  companion object {
    private const val TAG = "CallManagerDelegate"

    private var instance: CallManagerRecordingDelegate? = null

    fun getInstance(context: Context): CallManagerRecordingDelegate {
      var instance = instance
      if (instance == null) {
        instance = CallManagerRecordingDelegate(context, Executors.newSingleThreadExecutor())
        this.instance = instance
      }
      return instance
    }
  }

}