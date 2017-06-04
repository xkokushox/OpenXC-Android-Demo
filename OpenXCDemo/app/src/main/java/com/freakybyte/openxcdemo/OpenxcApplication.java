package com.freakybyte.openxcdemo;

import android.app.Application;
import android.os.Handler;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by Kokusho on 04/06/17.
 */

public class OpenxcApplication extends Application {

    private static OpenxcApplication singleton;
    private Handler mHandler = new Handler();

    public static OpenxcApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        Fresco.initialize(this);

    }


    public void handlerPost(Runnable runnable) {
        mHandler.post(runnable);
    }

}