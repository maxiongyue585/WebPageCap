package com.xm.demo.refreshurl;

import android.app.Application;

public class MyApplication extends Application{
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this);
    }

}
