package com.example.autolatety.listener;

import com.example.autolatety.data.Result;

public interface RecognizeListener {

    void onDone(Result result);
    void hiDone(Result result);
    void tingDone(Result result);
    void ting2Done(Result result);
    void uttDone(Result result);
    void responeDone(Result result);
    void callAgain(Result result);
    void onError(Result result);
}
