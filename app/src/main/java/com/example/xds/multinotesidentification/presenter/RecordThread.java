package com.example.xds.multinotesidentification.presenter;


import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.example.xds.multinotesidentification.model.Complex;

import org.jtransforms.fft.DoubleFFT_1D;

public class RecordThread extends Thread {

    AudioRecord audioRecord;
    int minBufferSize;
    boolean isRecording;

    RecordThread() {
        isRecording = false;
        minBufferSize = AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                8000, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,minBufferSize);
    }

    @Override
    public void run() {
        short[] buffer = new short[minBufferSize];
        audioRecord.startRecording();
        while (isRecording){
            audioRecord.read(buffer,0,minBufferSize);

            Complex[] input = new Complex[1024];
            DoubleFFT_1D fft_1D;

        }
    }
}
