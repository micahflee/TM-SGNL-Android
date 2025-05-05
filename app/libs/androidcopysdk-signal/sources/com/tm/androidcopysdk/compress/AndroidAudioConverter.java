package com.tm.androidcopysdk.compress;

import android.content.Context;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.tm.logger.Log;
import java.io.File;
import java.io.IOException;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/compress/AndroidAudioConverter.class */
public class AndroidAudioConverter {
    private static final String TAG = "AndroidAudioConverter";
    private static boolean loaded = true;
    private Context context;
    private File audioFile;
    private AudioFormat format;
    private IConvertCallback callback;

    private AndroidAudioConverter(Context context) {
        this.context = context;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static void load(Context context, ILoadCallback callback) {
    }

    public static AndroidAudioConverter with(Context context) {
        return new AndroidAudioConverter(context);
    }

    public AndroidAudioConverter setFile(File originalFile) {
        this.audioFile = originalFile;
        return this;
    }

    public AndroidAudioConverter setFormat(AudioFormat format) {
        this.format = format;
        return this;
    }

    public AndroidAudioConverter setCallback(IConvertCallback callback) {
        this.callback = callback;
        return this;
    }

    public void convert(boolean isOutgoingCall, long durationSec, long timeBeforeAnswerSec, String bitRate) {
        if (!isLoaded()) {
            this.callback.onFailure(new Exception("FFmpeg not loaded"));
        } else if (this.audioFile == null || !this.audioFile.exists()) {
            this.callback.onFailure(new IOException("File not exists"));
        } else if (!this.audioFile.canRead()) {
            this.callback.onFailure(new IOException("Can't read the file. Missing permission?"));
        } else {
            File convertedFile = getConvertedFile(this.audioFile, this.format);
            if (((float) timeBeforeAnswerSec) < 0.0f) {
                timeBeforeAnswerSec = 0;
            }
            String startPosition = timeBeforeAnswerSec > 0 ? "" + timeBeforeAnswerSec : "0";
            String[] cmd = {"-y", "-i", this.audioFile.getPath(), "-ss", startPosition, "-t", String.valueOf(durationSec), "-b:a", bitRate, convertedFile.getPath()};
            int returnCode = FFmpeg.execute(cmd);
            if (returnCode == 0) {
                Log.d(TAG, "Async command execution completed successfully.");
                this.callback.onSuccess(convertedFile);
                Log.d(TAG, "convert success");
            } else if (returnCode == 255) {
                Log.d(TAG, "Async command execution cancelled by user.");
            } else {
                Log.d(TAG, String.format("Async command execution failed with returnCode=%d.", Integer.valueOf(returnCode)));
                this.callback.onFailure(new IOException("XXXXX XXXX XXXX"));
                Log.d(TAG, "convert failure");
            }
        }
    }

    private static File getConvertedFile(File originalFile, AudioFormat format) {
        String[] f = originalFile.getPath().split("\\.");
        String filePath = originalFile.getPath().replace(f[f.length - 1], format.getFormat());
        return new File(filePath);
    }
}
