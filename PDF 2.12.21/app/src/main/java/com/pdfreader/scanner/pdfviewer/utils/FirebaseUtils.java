package com.pdfreader.scanner.pdfviewer.utils;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
public class FirebaseUtils {
    public static final String EVENT_TYPE = "event_type";

    public static void sendEventFunctionUsed(Context context, int functionId, String functionName) {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, functionId + "");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, functionName);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "User has used this function");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public static void sendEventFunctionUsed(Context context, String eventName, String eventType) {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);

        Bundle bundle = new Bundle();
        bundle.putString(EVENT_TYPE, eventType);
        firebaseAnalytics.logEvent(eventName, bundle);
    }
}
