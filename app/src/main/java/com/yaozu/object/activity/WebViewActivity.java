package com.yaozu.object.activity;

import android.support.v7.app.ActionBar;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yaozu.object.R;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.IntentKey;

/**
 * Created by jxj42 on 2017/3/28.
 */

public class WebViewActivity extends BaseActivity {
    private WebView webView;
    private String postid;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_webview);
    }

    @Override
    protected void initView() {
        webView = (WebView) findViewById(R.id.activity_webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    }

    @Override
    protected void initData() {
        postid = getIntent().getStringExtra(IntentKey.INTENT_POST_ID);
        String url = DataInterface.APP_HOST + "superplan/forum.html?postid=" + postid;
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
