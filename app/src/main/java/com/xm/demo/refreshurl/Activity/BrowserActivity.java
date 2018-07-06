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
import com.xm.demo.refreshurl.utils.KgWebChromeClient;
import com.xm.demo.refreshurl.utils.KgWebviewClient;
import com.xm.demo.refreshurl.utils.ToastUtils;
import com.xm.demo.refreshurl.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BrowserActivity extends AppCompatActivity {
    public static final String TAG = BrowserActivity.class.getSimpleName();

    @BindView(R.id.btn_go)
    Button btnGo;
    @BindView(R.id.wv_browser)
    WebView wvBrowser;
    @BindView(R.id.wb_title)
    TextView wbTitle;
    @BindView(R.id.et_input_website)
    EditText etInputWebsite;

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
        setContentView(R.layout.activity_browser);
        ButterKnife.bind(this);
        initViews();
        initListenr();
    }

    private void initViews() {
        //隐藏actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        wvBrowser.setWebChromeClient(new KgWebChromeClient(new KgWebChromeClient.KgWebChromeListener() {
            @Override
            public void onReceiverTitle(String title) {
                Message msg = Message.obtain();
                msg.obj = title;
                msg.what = KgWebChromeClient.ON_RECEIVE_TITLE;
                mHandler.sendMessage(msg);
            }
        }));

        wvBrowser.setWebViewClient(new KgWebviewClient(this));
        Utils.setDefaultWebSettings(wvBrowser);
    }

    @OnClick(R.id.btn_go)
    public void onViewClicked() {
        if (!TextUtils.isEmpty(etInputWebsite.getText().toString())) {
            wvBrowser.loadUrl(etInputWebsite.getText().toString());
        } else {
            ToastUtils.showShort(this, "输入不能为空");
        }
    }

    private void initListenr() {
        etInputWebsite.setText("https://www.baidu.com");
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
    }
}
