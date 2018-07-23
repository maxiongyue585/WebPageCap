package com.xm.demo.refreshurl.utils;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.xm.demo.refreshurl.Activity.WebViewActivity.REQUEST_MEDIA_PROJECTION;


public class KgWebChromeClient extends WebChromeClient {
    public static final String TAG = KgWebChromeClient.class.getSimpleName();

    public static final int ON_RECEIVE_TITLE = 50;
    private KgWebChromeListener kgWebChromeListener;

    private boolean isScreenShot = false;
    private Activity activity;

    public KgWebChromeClient(KgWebChromeListener kgWebChromeListener) {
        this.kgWebChromeListener = kgWebChromeListener;
    }

    public KgWebChromeClient(KgWebChromeListener kgWebChromeListener, boolean isScreenShot, Activity activity) {
        this.kgWebChromeListener = kgWebChromeListener;
        this.isScreenShot = isScreenShot;
        this.activity = activity;
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);

        if (kgWebChromeListener != null && !TextUtils.isEmpty(title)) {
            kgWebChromeListener.onReceiverTitle(title);
        }
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        return super.onJsAlert(view, url, message, result);
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
    }

    public interface KgWebChromeListener {

        void onReceiverTitle(String title);

    }


    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        Log.d(TAG, "onProgressChanged: " + newProgress);
        if (isScreenShot && newProgress == 100) {//需要截屏
            Log.d(TAG, "onPageFinished: 需要截屏");
            isScreenShot = false;
            getSnapshot();

        }
    }

    SimpleDateFormat time_fmt = new SimpleDateFormat("HH_mm");

    //截屏
    private void getSnapshot() {
//        View view = activity.getWindow().getDecorView();
//        final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        view.draw(canvas);

//        Log.d(TAG, "bitmap--" + bitmap);
        new Thread() {

            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    File filePath = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "wb_capture" + File.separator + Utils.getCurrentDateTime());
                    if (!filePath.exists()) {

                        filePath.mkdirs();
                    }

//                    File file = new File(filePath, String.format("%s.jpg", time_fmt.format(new Date())));
//
//                    if(file.exists())
//                        file.delete();
//
//
//                    FileOutputStream fos = new FileOutputStream(file);
//                    //压缩bitmap到输出流中
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos);
//                    fos.close();
                    if (Utils.isRoot()) {
                        Utils.exec("screencap -p " + new File(filePath, String.format("%s_.png", time_fmt.format(new Date()))), true);
                        Log.d(TAG, "getSnapshot: 截屏成功");
                    } else {
                        if (Build.VERSION.SDK_INT >= 21) {
                            activity.startActivityForResult(createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
                        } else {
                            Log.e(TAG, "run: 版本过低,无法截屏");
                        }
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                } finally {

                }
                if (Utils.isRoot()) {
                    activity.finish();
                }
            }
        }.start();
    }

    private Intent createScreenCaptureIntent() {
        //这里用media_projection代替Context.MEDIA_PROJECTION_SERVICE 是防止低于21 api编译不过
        return ((MediaProjectionManager) activity.getSystemService("media_projection")).createScreenCaptureIntent();
    }
}
