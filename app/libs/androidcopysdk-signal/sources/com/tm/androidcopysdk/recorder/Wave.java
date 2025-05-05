package com.tm.androidcopysdk.recorder;

import android.content.Context;
import com.tm.logger.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/Wave.class */
public class Wave {
    private final int LONGINT = 4;
    private final int SMALLINT = 2;
    private final int INTEGER = 4;
    private final int ID_STRING_SIZE = 4;
    private final int WAV_RIFF_SIZE = 8;
    private final int WAV_FMT_SIZE = 24;
    private final int WAV_DATA_SIZE = 8;
    private final int WAV_HDR_SIZE = 44;
    private final short PCM = 1;
    private final int SAMPLE_SIZE = 2;
    int cursor = 0;
    int nSamples;
    byte[] output;
    Context mContext;

    public Wave(int sampleRate, short nChannels, short[] data, int start, int end, Context context) {
        this.nSamples = (end - start) + 1;
        this.output = new byte[(this.nSamples * 2) + 44];
        buildHeader(sampleRate, nChannels);
        writeData(data, start, end);
        this.mContext = context;
    }

    private void buildHeader(int sampleRate, short nChannels) {
        write("RIFF");
        write(this.output.length);
        write("WAVE");
        writeFormat(sampleRate, nChannels);
    }

    public void writeFormat(int sampleRate, short nChannels) {
        write("fmt ");
        write(16);
        write((short) 1);
        write(nChannels);
        write(sampleRate);
        write(nChannels * sampleRate * 2);
        write((short) (nChannels * 2));
        write((short) 16);
    }

    public void writeData(short[] data, int start, int end) {
        write("data");
        write(this.nSamples * 2);
        int i = start;
        while (i <= end) {
            int i2 = i;
            i++;
            write(data[i2]);
        }
    }

    private void write(byte b) {
        byte[] bArr = this.output;
        int i = this.cursor;
        this.cursor = i + 1;
        bArr[i] = b;
    }

    private void write(String id) {
        if (id.length() == 4) {
            for (int i = 0; i < 4; i++) {
                write((byte) id.charAt(i));
            }
            return;
        }
        Log.e("Wave", "String" + id + " must have four characters.");
    }

    private void write(int i) {
        write((byte) (i & 255));
        int i2 = i >> 8;
        write((byte) (i2 & 255));
        int i3 = i2 >> 8;
        write((byte) (i3 & 255));
        write((byte) ((i3 >> 8) & 255));
    }

    private void write(short i) {
        write((byte) (i & 255));
        write((byte) (((short) (i >> 8)) & 255));
    }

    public boolean writeToFile(String filename) {
        boolean ok;
        try {
            File path = new File(this.mContext.getFilesDir(), filename);
            FileOutputStream outFile = new FileOutputStream(path);
            outFile.write(this.output);
            outFile.close();
            ok = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ok = false;
        } catch (IOException e2) {
            ok = false;
            e2.printStackTrace();
        }
        return ok;
    }
}
