package com.xm.demo.refreshurl.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xm.demo.refreshurl.utils.Utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 守护服务
 */
public class GuardService extends Service {
    private static final String TAG = "GuardService";

    private Timer mTimer;

    private TimerTask mTask;

    public GuardService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        thread.start();
        return super.onStartCommand(intent, START_FLAG_REDELIVERY, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    Thread thread = new Thread(new Runnable() {

        @Override
        public void run() {
            mTimer = new Timer();
            mTask = new TimerTask() {

                @Override
                public void run() {

                    if (!Utils.isServiceRunning(GuardService.this, "com.xm.demo.refreshurl.service.RequestService")) {
                        Intent service = new Intent(GuardService.this, RequestService.class);
                        startService(service);
                    }
                }
            };
            mTimer.schedule(mTask, 0, 10000);
        }
    });

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            if (mTask != null) {
                mTask.cancel();
                Log.d(TAG, "stopTimer: ");
                mTask = null;
            }
        }
    }
}
