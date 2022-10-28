package com.example.autolatety.listener;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.FloatProperty;
import android.util.Log;

import com.example.autolatety.data.Result;
import com.example.autolatety.utils.RecognizeUtil;

import java.util.ArrayList;

public class MyRecognizeListener implements RecognitionListener {
    private static final String TAG = "Main";
    long millis =0;
    // 0- hi bixby , 1- tingtong, 2-cmd , 3-response, 4 ting 2
    Result result;
    boolean err = true;
    boolean flag = false;
    int type; // 0- tingtong recognize ; 1-cmd recognize;

    RecognizeListener listener;

    public MyRecognizeListener(Result re, int t, RecognizeListener lis) {
       // Log.d(TAG, "MyRecognizeListener: "+t);
        this.result = re;
        this.type = t;
        this.listener = lis;
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {

        flag = false;
        err = true;
        millis =0;
        //Log.d(TAG, ""+System.currentTimeMillis());
    }

    @Override
    public void onBeginningOfSpeech() {
        if (type == 3) {
            result.getBixbyRespone().callStart();
            return;
        }

    }

    @Override
    public void onRmsChanged(float v) {
//        if(type==0 && v > 6.0 && result.getHi().finish>0&&!flag){
//                err = false;
//                result.getTing().callStart();
//                result.getTing().callEnd();
//                RecognizeUtil.stopRecognize();
//                listener.tingDone(result);
//                flag = true;
//            return;
//        }

        if ((v > 7.0 && type == 1)) {
            if (!flag) {
                err = false;
                result.getTing().callStart();
                result.getTing().callEnd();
                //Log.d(TAG, "ting done on new recog "+System.currentTimeMillis());
                RecognizeUtil.stopRecognize();
                listener.tingDone(result);
                flag = true;
            }
        } else {
            if (v > 7.0 && type == 4) {
                if (!flag) {
                    RecognizeUtil.stopRecognize();
                    err = false;
                    listener.ting2Done(result);
                    flag = true;

                }
            }
        }
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
    }

    @Override
    public void onEndOfSpeech() {
        if (type == 3) {
            result.getBixbyRespone().callEnd();
        }

//      F

    }

    @Override
    public void onError(int i) {

       // Log.d("Main", "onError: " + i);
        if (type == 1 && !err) {
            result.getTing().callEnd();
            if (!flag) {
                RecognizeUtil.stopRecognize();
                listener.tingDone(result);
                flag = true;
            }

        } else {
            if (type == 4 && !err) {
                if (!flag) {
                    RecognizeUtil.stopRecognize();
                    listener.ting2Done(result);
                    flag = true;
                }
            } else {
                result.acc = false;
                listener.onError(result);
            }
        }
    }

    @Override
    public void onResults(Bundle bundle) {
        // Log.d(TAG, "onResults: ");
        if (type == 3) {
            ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            result.getBixbyRespone().setCommand(data.get(0));
            listener.onDone(result);
            return;
        }
    }

    @Override
    public void onPartialResults(Bundle bundle) {
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
