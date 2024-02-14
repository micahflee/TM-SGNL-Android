package org.archiver.device

import java.io.File
import java.util.concurrent.ExecutorService

class TMRecordedAudioToFileController(
  executor: ExecutorService,
  private val dirPath: String,
) : RecordedAudioToFileController(executor) {

  private var fileName: String? = null

  fun start(fileName: String?): Boolean {
    this.fileName = fileName
    return super.start()
  }

  override fun stop() {
    fileName = null
    super.stop()
  }

  override fun getFileName(sampleRate: Int, channelCount: Int): String {
    val fileName = this.fileName?.let { "${it}_" } ?: ""
    val channel = if (channelCount == 1) "_mono" else "_stereo"
    return "${dirPath}${File.separator}${fileName}recorded_audio_16bits_${sampleRate}Hz${channel}.pcm"
  }

  override fun isExternalStorageWritable(): Boolean {
    return true
  }
}