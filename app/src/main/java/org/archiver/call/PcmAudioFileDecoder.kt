package org.archiver.call

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

private const val RECORDER_SAMPLERATE = 48000 // 44100

class PcmAudioFileDecoder {

  fun decodeToWav(src: String, dest: String): File {
    val result = File(dest)
    try {
      rawToWave(File(src), result)
    } catch (e: IOException) {
      e.printStackTrace()
    }
    return result
  }

  @Throws(IOException::class)
  private fun rawToWave(rawFile: File, waveFile: File) {
    val rawData = ByteArray(rawFile.length().toInt())
    var input: DataInputStream? = null
    try {
      input = DataInputStream(FileInputStream(rawFile))
      input.read(rawData)
    } finally {
      input?.close()
    }
    var output: DataOutputStream? = null
    try {
      output = DataOutputStream(FileOutputStream(waveFile))
      writeHeader(output, rawData)
      writeData(output, rawData, rawFile)
    } finally {
      output?.close()
    }
  }

  // http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
  private fun writeHeader(output: DataOutputStream, rawData: ByteArray) {
    val chunkId = "RIFF"
    val chunkSize = rawData.size + 36
    val format = "WAVE"
    val subChunk1Id = "fmt "
    val subChunk1Size = 16
    val subChunk2Id = "data"
    val subChunk2Size = rawData.size
    val bytesPerSample = 2
    val bitsPerSample = (bytesPerSample * 8).toShort()
    val sampleRate = RECORDER_SAMPLERATE
    val byteRate = sampleRate * bytesPerSample
    val blockAlign = 2.toShort()
    val audioFormat = 1.toShort()
    val numOfChannels = 1.toShort()
    writeString(output, chunkId)
    writeInt(output, chunkSize)
    writeString(output, format)
    writeString(output, subChunk1Id)
    writeInt(output, subChunk1Size)
    writeShort(output, audioFormat)
    writeShort(output, numOfChannels)
    writeInt(output, sampleRate)
    writeInt(output, byteRate)
    writeShort(output, blockAlign)
    writeShort(output, bitsPerSample)
    writeString(output, subChunk2Id)
    writeInt(output, subChunk2Size)
  }

  private fun writeData(output: DataOutputStream, rawData: ByteArray, file: File) {
    val shorts = ShortArray(rawData.size / 2)
    ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()[shorts]
    val bytes = ByteBuffer.allocate(shorts.size * 2)
    for (s in shorts) {
      bytes.putShort(s)
    }
    output.write(fullyReadFileToBytes(file))
  }

  @Throws(IOException::class)
  fun fullyReadFileToBytes(f: File): ByteArray? {
    val size = f.length().toInt()
    val bytes = ByteArray(size)
    val tmpBuff = ByteArray(size)
    val fis = FileInputStream(f)
    try {
      var read = fis.read(bytes, 0, size)
      if (read < size) {
        var remain = size - read
        while (remain > 0) {
          read = fis.read(tmpBuff, 0, remain)
          System.arraycopy(tmpBuff, 0, bytes, size - remain, read)
          remain -= read
        }
      }
    } catch (e: IOException) {
      throw e
    } finally {
      fis.close()
    }
    return bytes
  }

  @Throws(IOException::class)
  private fun writeInt(output: DataOutputStream, value: Int) {
    output.write(value shr 0)
    output.write(value shr 8)
    output.write(value shr 16)
    output.write(value shr 24)
  }

  @Throws(IOException::class)
  private fun writeShort(output: DataOutputStream, value: Short) {
    output.write(value.toInt() shr 0)
    output.write(value.toInt() shr 8)
  }

  @Throws(IOException::class)
  private fun writeString(output: DataOutputStream, value: String) {
    for (i in 0 until value.length) {
      output.write(value[i].code)
    }
  }
}