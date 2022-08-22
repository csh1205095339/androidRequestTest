package com.example.requesttest;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.dynamicanimation.animation.SpringAnimation;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class LoggingInterceptor implements Interceptor {
    private static final String TAG = "haohaotest";
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        long t1 = System.nanoTime();
        Response response = chain.proceed(request);
        long t2 = System.nanoTime();
        Log.d(TAG, "intercept: " + (t2 - t1));
        return response;
    }
}
