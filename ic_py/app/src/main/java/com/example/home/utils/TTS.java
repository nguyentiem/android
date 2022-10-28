package com.example.home.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.example.home.listener.TTSListener;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TTS {
        public static TextToSpeech speech;
        private static Bundle params = new Bundle();
        private static final String dir = "/Audiowav";

        private static String getStringTime(){
            Date currentTime = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_hhmmssSSS");
            String strDate = dateFormat.format(currentTime);
            return strDate;
        }

        public static void init(Context context, TTSListener listener){
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
            speech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    if (i != TextToSpeech.ERROR) {
                        speech.setLanguage(Locale.US);
                        speech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                            @Override
                            public void onStart(String s) {
                            }

                            @Override
                            public void onDone(String s) {
                            }

                            @Override
                            public void onError(String s) {
                            }
                        });
                    }
                }
            });
            float pitchSpeed = 1f;
            speech.setPitch(pitchSpeed);
            float rateSpeed = 1f;
            speech.setSpeechRate(rateSpeed);
        }
        public static void play(String s){
            speech.speak(s, TextToSpeech.QUEUE_ADD, params, "UniqueID");
        }

        public static void speakText(String text) {
            String state = Environment.getExternalStorageState();
            FileOutputStream wavOut = null;

            boolean mExternalStorageWriteable = false;
            boolean mExternalStorageAvailable = false;
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                // Can read and write the media
                mExternalStorageAvailable = mExternalStorageWriteable = true;

            } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                // Can only read the media
                mExternalStorageAvailable = true;
                mExternalStorageWriteable = false;
            } else {
                // Can't read or write
                mExternalStorageAvailable = mExternalStorageWriteable = false;
            }

            File rootPath = new File(Environment.getExternalStorageDirectory(), dir);
            if (!rootPath.exists()) {
                rootPath.mkdirs();
            }
            String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + dir;
            fileName += "/" + getStringTime() + ".wav";
            File file = new File(fileName);

            int test = speech.synthesizeToFile((CharSequence) text, null, file,
                    "tts");
        }
    }

