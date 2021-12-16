package com.pdfreader.scanner.pdfviewer.data.remote;

import com.pdfreader.scanner.pdfviewer.constants.DataConstants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class ApiHelper implements ApiHelperInterface {
    private static ApiHelper mInstance;
    private ApiHelper() {
    }
    public static ApiHelper getInstance() {
        if (mInstance == null) {
            return new ApiHelper();
        }
        return mInstance;
    }
    private OkHttpClient getOkHttpRequest() {
        return new OkHttpClient().newBuilder()
                .connectTimeout(DataConstants.CONNECT_TIMEOUT_NETWORK, TimeUnit.SECONDS)
                .readTimeout(DataConstants.CONNECT_TIMEOUT_NETWORK, TimeUnit.SECONDS)
                .writeTimeout(DataConstants.CONNECT_TIMEOUT_NETWORK, TimeUnit.SECONDS)
                .build();
    }
}
