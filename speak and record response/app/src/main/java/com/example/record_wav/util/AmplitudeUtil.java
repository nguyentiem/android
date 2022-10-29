package com.example.record_wav.util;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;

public class AmplitudeUtil {

    private MediaRecorder mediaRecorder;
    private AudioRecord ar = null;
    private int minSize;

    public void start() {
        minSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        ar = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minSize);
        ar.startRecording();
    }

    public void stop() {

        if (ar != null) {
            ar.stop();
        }
    }

    public double getAmplitude() {
        short[] buffer = new short[minSize];
        ar.read(buffer, 0, minSize);
        int max = 0;
        for (short s: buffer) {
            if (Math.abs(s) > max) {
                max = Math.abs(s);
            }
        }
        return max;
    }

}
