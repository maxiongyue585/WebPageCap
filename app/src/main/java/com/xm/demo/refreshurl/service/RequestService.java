package com.xm.demo.refreshurl.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xm.demo.refreshurl.Activity.WebViewActivity;
import com.xm.demo.refreshurl.utils.SPUtils;
import com.xm.demo.refreshurl.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.TimerTask;

/**
 * 请求服务
 */
public class RequestService extends Service {
    private static final String TAG = "RequestService";

    public static final long MIN = 60000;//1分钟
    public static final long DEF_TIME = 10 * MIN;//默认10分钟
    private MyTimeTask mTimeTask;


    public static final String URLS_PARMS = "urls_parms";

    private ArrayList<String> mDatas;

    public RequestService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");

        mDatas = Utils.getUrls();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        if (mTimeTask == null) {//重启情况下
            startTimer();
        }
        return super.onStartCommand(intent, START_FLAG_REDELIVERY, startId);
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        stopTimer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void setTimer(long interval) {

        mTimeTask = new MyTimeTask(interval, new TimerTask() {
            @Override
            public void run() {
                // 判断守护进程是否启动
                if (!Utils.isServiceRunning(RequestService.this, "com.xm.demo.refreshurl.service.GuardService")) {
                    Intent service = new Intent(RequestService.this, GuardService.class);
                    startService(service);
                }

                //handler或者发广播，启动服务都是可以的
                Intent wbIntent = new Intent(RequestService.this, WebViewActivity.class);
                wbIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                for (int i = 0; i < mDatas.size(); i++) {
                    if (!WebViewActivity.mIsFront) {
                        Bundle bundle = new Bundle();
                        bundle.putString(URLS_PARMS, mDatas.get(i));
                        wbIntent.putExtras(bundle);

                        startActivity(wbIntent);
                    }
                }
                Log.d(TAG, "run: ");
            }
        });

        Log.d(TAG, "setTimer: ");
    }

    /**
     * 启动计时器
     */
    private void startTimer() {
        //设置timerTask监听
        long setInterval = (long) SPUtils.getParam(getApplicationContext(), SPUtils.K_INTERVAL, DEF_TIME);
        long thisRealityInterval = 0;

        String filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "wb_capture";
        File dir_wbcapture = new File(filePath);
        if (!dir_wbcapture.exists()) {
            Log.d(TAG, "setTimer: 路径不存在");
        } else {
            boolean is_stop = (boolean) SPUtils.getParam(getApplicationContext(), SPUtils.IS_STOP_SERVICE, false);
            if (!is_stop) {
                thisRealityInterval = setInterval - (System.currentTimeMillis() - dir_wbcapture.lastModified());
            }
        }
        //设置间隔
        setTimer(setInterval);

        if (thisRealityInterval <= 0) {
            mTimeTask.start();
        } else {
            mTimeTask.reStart(thisRealityInterval);
        }

        SPUtils.setParam(getApplicationContext(), SPUtils.IS_STOP_SERVICE, false);

        Log.d(TAG, "setTimer: interval:" + setInterval + ", currentTime: " + System.currentTimeMillis() + ", lastModified:"
                + dir_wbcapture.lastModified() + ",thisRealityInterval:" + thisRealityInterval);

    }

    private void stopTimer() {
        if (mTimeTask != null) {
            mTimeTask.stop();
            Log.d(TAG, "stopTimer: ");
            mTimeTask = null;
        }
    }

}
