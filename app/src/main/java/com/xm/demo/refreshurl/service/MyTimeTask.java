package com.xm.demo.refreshurl.service;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimerTask
 */
public class MyTimeTask {
    private Timer timer;
    private TimerTask task;
    private long time;

    public MyTimeTask(long time, TimerTask task) {
        this.task = task;
        this.time = time;
        if (timer == null) {
            timer = new Timer();
        }
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void start() {
        timer.schedule(task, 0, time);//每隔time时间段就执行一次
    }

    //服务被杀死的情况下
//    public void reStart(Date firstTime) {
//        timer.schedule(task, firstTime, time);//每隔time时间段就执行一次
//    }

    public void reStart(long delayTime) {
        timer.schedule(task, delayTime, time);//每隔time时间段就执行一次
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            if (task != null) {
                task.cancel();//将原任务从队列中移除
            }
        }
    }
}
