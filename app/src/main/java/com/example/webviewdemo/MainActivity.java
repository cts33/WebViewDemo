package com.example.webviewdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebViewCompat;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
    }

    private static final String TAG = "MainActivity";
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView   =findViewById(R.id.webview);
        String url = "https://blog.csdn.net/chentaishan?t=1";
        webView.loadUrl(url);

        PackageInfo webViewPackageInfo = WebViewCompat.getCurrentWebViewPackage(this);
        Log.d(TAG, "WebView version: " + webViewPackageInfo.versionName);


        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.addJavascriptInterface(new WebAppInterface(this),"android");

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.equals(Uri.parse(url).getHost())) {
                    // This is my website, so do not override; let my WebView load the page
                    return false;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){


        });

//        myWebView.loadData(encodedHtml, "text/html", "base64");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}