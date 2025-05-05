package com.tm.androidcopysdk.device;

import com.tm.androidcopysdk.database.DBHeadersTable;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: PcmAudioFileDecoder.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��@\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n��\n\u0002\u0010\n\n\u0002\b\u0002\u0018��2\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0016\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006J\u0010\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\u0004J\u0018\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u0004H\u0002J \u0010\u000f\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\t2\u0006\u0010\u0013\u001a\u00020\u0004H\u0002J\u0018\u0010\u0014\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\tH\u0002J\u0018\u0010\u0015\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0016\u001a\u00020\u0017H\u0002J\u0018\u0010\u0018\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0016\u001a\u00020\u0019H\u0002J\u0018\u0010\u001a\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0016\u001a\u00020\u0006H\u0002¨\u0006\u001b"}, d2 = {"Lcom/tm/androidcopysdk/device/PcmAudioFileDecoder;", "", "()V", "decodeToWav", "Ljava/io/File;", "src", "", "dest", "fullyReadFileToBytes", "", "f", "rawToWave", "", "rawFile", "waveFile", "writeData", "output", "Ljava/io/DataOutputStream;", "rawData", "file", "writeHeader", "writeInt", DBHeadersTable.HeadersEntry.COLUMN_NAME_VALUE, "", "writeShort", "", "writeString", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/device/PcmAudioFileDecoder.class */
public final class PcmAudioFileDecoder {
    @NotNull
    public final File decodeToWav(@NotNull String src, @NotNull String dest) {
        Intrinsics.checkNotNullParameter(src, "src");
        Intrinsics.checkNotNullParameter(dest, "dest");
        File result = new File(dest);
        try {
            rawToWave(new File(src), result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private final void rawToWave(File rawFile, File waveFile) throws IOException {
        byte[] rawData = new byte[(int) rawFile.length()];
        DataInputStream input = null;
        try {
            input = new DataInputStream(new FileInputStream(rawFile));
            input.read(rawData);
            input.close();
            DataOutputStream output = null;
            try {
                output = new DataOutputStream(new FileOutputStream(waveFile));
                writeHeader(output, rawData);
                writeData(output, rawData, rawFile);
                output.close();
            } catch (Throwable th) {
                DataOutputStream dataOutputStream = output;
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            DataInputStream dataInputStream = input;
            if (dataInputStream != null) {
                dataInputStream.close();
            }
            throw th2;
        }
    }

    private final void writeHeader(DataOutputStream output, byte[] rawData) {
        int chunkSize = rawData.length + 36;
        int subChunk2Size = rawData.length;
        short bitsPerSample = (short) (2 * 8);
        int byteRate = 48000 * 2;
        writeString(output, "RIFF");
        writeInt(output, chunkSize);
        writeString(output, "WAVE");
        writeString(output, "fmt ");
        writeInt(output, 16);
        writeShort(output, (short) 1);
        writeShort(output, (short) 1);
        writeInt(output, 48000);
        writeInt(output, byteRate);
        writeShort(output, (short) 2);
        writeShort(output, bitsPerSample);
        writeString(output, "data");
        writeInt(output, subChunk2Size);
    }

    private final void writeData(DataOutputStream output, byte[] rawData, File file) {
        short[] shorts = new short[rawData.length / 2];
        ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
        ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);
        for (short s : shorts) {
            bytes.putShort(s);
        }
        output.write(fullyReadFileToBytes(file));
    }

    @Nullable
    public final byte[] fullyReadFileToBytes(@NotNull File f) throws IOException {
        Intrinsics.checkNotNullParameter(f, "f");
        int size = (int) f.length();
        byte[] bytes = new byte[size];
        byte[] tmpBuff = new byte[size];
        FileInputStream fis = new FileInputStream(f);
        try {
            try {
                int read = fis.read(bytes, 0, size);
                if (read < size) {
                    int remain = size - read;
                    while (remain > 0) {
                        int read2 = fis.read(tmpBuff, 0, remain);
                        System.arraycopy(tmpBuff, 0, bytes, size - remain, read2);
                        remain -= read2;
                    }
                }
                return bytes;
            } catch (IOException e) {
                throw e;
            }
        } finally {
            fis.close();
        }
    }

    private final void writeInt(DataOutputStream output, int value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
        output.write(value >> 16);
        output.write(value >> 24);
    }

    private final void writeShort(DataOutputStream output, short value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
    }

    private final void writeString(DataOutputStream output, String value) throws IOException {
        int length = value.length();
        for (int i = 0; i < length; i++) {
            output.write(value.charAt(i));
        }
    }
}
