package com.example.autolatety.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.example.autolatety.data.Result;
import com.example.autolatety.listener.MyRecognizeListener;
import com.example.autolatety.listener.RecognizeListener;

import java.util.Locale;

public class RecognizeUtil {
    private final static String TAG ="Main";
    public static Intent speechRecognizerIntent;
    public static  SpeechRecognizer speechRecognizer;
    public static void recognizeInit(Context context, Result result, int type, RecognizeListener listener) {
       // Log.d(TAG, "recognizeInit: "+System.currentTimeMillis());
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
        if(type==3){
           // Log.d(TAG, "recognizeInit: 5s min");
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 8000);
           // speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3000);
        }else{
            //Log.d(TAG, "recognizeInit: 1s min");
            //EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS
            if(type==0){
                  speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1500);
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 2000);
              speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500);
            }else{
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 2000);
            }

        }
        speechRecognizer.setRecognitionListener(new MyRecognizeListener(result,type,listener));
       // Log.d(TAG, "recognizeInit: "+System.currentTimeMillis());
    }

   public static void startRecognize(){
       speechRecognizer.startListening(speechRecognizerIntent);
    }

    public static void stopRecognize(){

        if (speechRecognizer!=null){
          //  Log.d(TAG, "stopRecognize: ");
            speechRecognizer.stopListening();
            speechRecognizer.cancel();
            speechRecognizer.destroy();
//            speechRecognizer =null;
        }

    }
}
