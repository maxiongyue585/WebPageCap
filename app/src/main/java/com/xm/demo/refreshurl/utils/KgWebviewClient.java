package com.xm.demo.refreshurl.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class KgWebviewClient extends WebViewClient {
    public static final String TAG = KgWebviewClient.class.getSimpleName();

    private Activity activity;

    public KgWebviewClient(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String baseUrl) {


        return super.shouldOverrideUrlLoading(view, baseUrl);
    }

    @Override
    public void onPageStarted(WebView view, String baseUrl, Bitmap favicon) {
        super.onPageStarted(view, baseUrl, favicon);


        String url = baseUrl.toLowerCase();
        if (!(url.startsWith("http") || url.startsWith("https") || url.startsWith("file:///android_asset"))) {
//           Logger.i( "处理自定义scheme");
            try {
//                isCanstart=true;
                // 以下固定写法
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                sparrowActivity.startActivityForResult(intent,502);
                activity.startActivity(intent);
            } catch (Exception e) {
                // 防止没有安装的情况
//                isCanstart=false;
                Log.e(TAG, e.getMessage());
            }
        }
    }


    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        Log.d(TAG, "onPageFinished: -");
    }

}
