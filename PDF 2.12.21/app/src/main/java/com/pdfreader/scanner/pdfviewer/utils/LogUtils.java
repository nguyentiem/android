package com.pdfreader.scanner.pdfviewer.utils;

import android.util.Log;

import com.pdfreader.scanner.pdfviewer.constants.AppConstants;

public class LogUtils {
    public static void printStepLog(String tag, String message) {
        Log.d(AppConstants.LOG_APP + ": " + tag, message);
    }

    public static void printStepLog(String tag1, String tag2, String message) {
        Log.d(AppConstants.LOG_APP + ": " + tag1 + ": " + tag2, message);
    }

    public static void printErrorLog(String tag, String message) {
        Log.e(AppConstants.LOG_APP + ": " + tag, message);
    }

    public static void printErrorLog(String tag1, String tag2, String message) {
        Log.e(AppConstants.LOG_APP + ": " + tag1 + ": " + tag2, message);
    }
}
