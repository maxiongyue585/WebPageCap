package com.xm.demo.refreshurl.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xm.demo.refreshurl.R;
import com.xm.demo.refreshurl.handler.WeakRefHandler;
import com.xm.demo.refreshurl.service.RequestService;
import com.xm.demo.refreshurl.utils.KgWebChromeClient;
import com.xm.demo.refreshurl.utils.KgWebviewClient;
import com.xm.demo.refreshurl.utils.ToastUtils;
import com.xm.demo.refreshurl.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WebViewActivity extends AppCompatActivity {
    public static final String TAG = WebViewActivity.class.getSimpleName();

    public static boolean mIsFront = false;//判断是否在前台

    @BindView(R.id.wv_browser)
    WebView wvBrowser;
    @BindView(R.id.wb_title)
    TextView wbTitle;

    private String mUrl;

    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case KgWebChromeClient.ON_RECEIVE_TITLE:
                    String title = msg.obj.toString();
                    if (title != null)
                        wbTitle.setText(title);
                    break;
            }
            return true;
        }
    };

    private Handler mHandler = new WeakRefHandler(mCallback);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wb);
        ButterKnife.bind(this);

        initViews();

        initdata();
    }

    private void initdata() {
        mUrl = getIntent().getStringExtra(RequestService.URLS_PARMS);
        if (!TextUtils.isEmpty(mUrl)) {
            wvBrowser.loadUrl(mUrl);
        }
    }

    private void initViews() {
        //隐藏actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        wvBrowser.setWebChromeClient(new KgWebChromeClient(new KgWebChromeClient.KgWebChromeListener() {
            @Override
            public void onReceiverTitle(String title) {
                if (TextUtils.isEmpty(title)) {
                    return;
                }
                Message msg = Message.obtain();
                msg.obj = title;
                msg.what = KgWebChromeClient.ON_RECEIVE_TITLE;
                if (mHandler != null) {
                    mHandler.sendMessage(msg);
                }
            }
        }, true, this));

        wvBrowser.setWebViewClient(new KgWebviewClient(this));
        Utils.setDefaultWebSettings(wvBrowser);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        if (wvBrowser != null) {
            wvBrowser = null;
        }

        mIsFront = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsFront = true;
    }
}
