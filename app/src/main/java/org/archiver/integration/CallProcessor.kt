package org.archiver.integration

import android.content.Context
import android.util.Log
import com.tm.androidcopysdk.AndroidCopySDK
import com.tm.androidcopysdk.CallObj
import com.tm.androidcopysdk.DataGrabber
import com.tm.androidcopysdk.Models.RecFileExt
import com.tm.androidcopysdk.utils.Contact
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.archiver.model.CallAnswerType
import org.archiver.model.CallDirection
import org.archiver.model.CallRtcMode
import org.archiver.model.TeleMessage3dPartyCall
import java.io.File


class CallProcessor(
  private val context: Context,

  dispatcher: CoroutineDispatcher
) {

  private val recordedFilePrefix = "recorded_audio_"
  private val recorderFileSuffix = ".pcm"

  private val scope = CoroutineScope(dispatcher)

  private var accountPhoneNumber: String? = null
  private var call: TeleMessage3dPartyCall? = null

  fun setAccountPhoneNumber(phoneNumber: String?) {
    accountPhoneNumber = phoneNumber?.takeIf { it.isNotEmpty() } ?: accountPhoneNumber
  }

  fun onBeginCall(callId: Long, direction: CallDirection, recipientPhoneNumber: String, recipientName: String) {
    call = TeleMessage3dPartyCall(callId, direction, recipientPhoneNumber, recipientName)
  }

  fun setDuration(durationMs: Long) {
    call?.durationMs = durationMs
  }

  fun onCallConcluded() {
    val call = call ?: return
    this.call = null
    scope.launch { submitCall(call) }
  }

  private suspend fun submitCall(call: TeleMessage3dPartyCall) {
    val savedDir = context.cacheDir
    val prefix = "${call.callId}_$recordedFilePrefix"
    var recordedFile = savedDir?.listFiles()?.firstOrNull { it.name.let { it.startsWith(prefix) && it.endsWith(recorderFileSuffix) } }
    if (!isCallSupported(call.rtcMode)) {
      if (recordedFile?.exists() == true)
        recordedFile.delete()
      return
    }
    recordedFile = decodeRecordingFile(recordedFile)
    archiveCallRecordedFile(call, recordedFile)
  }

  fun setAnswerType(answerType: CallAnswerType) {
    call?.answerType = answerType
  }

  fun setRtcMode(rtcMode: CallRtcMode) {
    call?.rtcMode = rtcMode
  }

  fun isRunning() = call != null

  private fun archiveCallRecordedFile(call: TeleMessage3dPartyCall, recordedFile: File?) {
    val callId = call.callId.toString()
    val accountPhoneNumber = accountPhoneNumber ?: return
    val otherSideNumber = call.phoneNumber ?: ""
    val duration = (call.durationMs?.div(1000) ?: 0).toString()
    val date = System.currentTimeMillis().toString()
    val type = call.type().toString()
    val lastModified = ""
    Log.d(javaClass.simpleName, "archiveCallRecordingFile - $accountPhoneNumber $call ${recordedFile?.absolutePath}")
    val archiveCall = CallObj(callId, otherSideNumber, date, type, duration, lastModified, accountPhoneNumber, call.rtcMode.code)
    archiveCall.name = Contact(call.name)

    val extension = ".wav"
    val callFileExt = recordedFile?.absolutePath?.takeIf { it.isNotBlank() }?.let { RecFileExt(it.removeSuffix(extension), extension) }
    val dataGrabber = DataGrabber.getInstance(context)
    dataGrabber.setArchiveCallLogMessages(null, null, null, listOf(archiveCall), listOfNotNull(callFileExt), true)
  }

  private suspend fun decodeRecordingFile(file: File?): File? {
    if (file == null)
      return null
    val wavFilePath = "${context.filesDir.path}/${file.nameWithoutExtension}.wav"
    val decoder = PcmAudioFileDecoder()
    val decodedFile = decoder.decodeToWave(file.absolutePath, wavFilePath)
    file.delete()
    return decodedFile
  }

  private fun isCallSupported(rtcMode: CallRtcMode) = if (rtcMode == CallRtcMode.Voice) isVoiceCallSupported() else isVideoCallSupported()

  private fun isVoiceCallSupported(): Boolean = AndroidCopySDK.getInstance(context).isSupportAudioCalls

  private fun isVideoCallSupported(): Boolean = AndroidCopySDK.getInstance(context).isSupportVideoCalls
}