package com.example.x5_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class MainActivity extends AppCompatActivity {
    String url = "https://blog.csdn.net/chentaishan?t=1";
    WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mWebView = findViewById(R.id.forum_context);

        mWebView.loadUrl(url);
        // textZoom:100表示正常，120表示文字放大1.2倍
        mWebView.getSettings().setTextZoom(200);
//该方法可以获取当前文字大小
        mWebView.getSettings().getTextZoom();
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //这里可以对特殊scheme进行拦截处理
                return true;//要返回true否则内核会继续处理
            }
        });
    }
}