package com.example.home.listener;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;

public class CustomRecognizeListener implements RecognitionListener {

    RecognizeListener listener;

    public CustomRecognizeListener(RecognizeListener lis) {
        this.listener = lis;
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {


    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {
    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int i) {

    }

    @Override
    public void onResults(Bundle bundle) {

        ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//                result.getBixbyRespone().setCommand(data.get(0));
//                listener.onDone(result);

    }

    @Override
    public void onPartialResults(Bundle bundle) {
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
    }


}
