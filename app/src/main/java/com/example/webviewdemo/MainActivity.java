package com.example.webviewdemo;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.autofill.AutofillValue;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebViewCompat;

public class MainActivity extends AppCompatActivity {
    public class WebAppInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /**
         * Show a toast from the web page
         */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
    }

    private static final String TAG = "MainActivity";
    WebView webView;
    String url = "https://blog.csdn.net/chentaishan?t=1";
    String baiduUrl = "https://www.baidu.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        webView.loadUrl(url);
        //通过html内容加载网页
//        webView.loadData(encodedHtml, "text/html", "base64");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //支持缩放页面，有些页面通过js限制了缩放，可以找些冷门的网站
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAllowFileAccessFromFileURLs(true);

        webView.addJavascriptInterface(new WebAppInterface(this), "Android");

        //处理页面的状态变化
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                Log.d(TAG, "onPageStarted: ");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                Log.d(TAG, "onPageFinished: ");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "shouldOverrideUrlLoading: ");
                if (url.equals(Uri.parse(url).getHost())) {
                    return false;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        //处理交互事件等
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                Log.d(TAG, "onProgressChanged: ");
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();


        //获取webkit的版本号
        PackageInfo webViewPackageInfo = WebViewCompat.getCurrentWebViewPackage(this);
        Log.d(TAG, "WebView version: " + webViewPackageInfo.versionName);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (webView != null) {
            webView.destroy();
            webView = null;
        }
    }

    /**
     * webview其他方法
     */
//    public void otherWebViewMethod(){
//
//        //Pauses all layout, parsing, and JavaScript timers for all WebViews.
//        webView.pauseTimers();
//        //自动填充页面子项的内容，
//        webView.autofill(AutofillValue);
//
//        //前进或者后退
//        webView.canGoBack();
//        //step 正数向前跳转，负数向后跳转页面
//        webView.canGoBackOrForward(step);
//        webView.canGoForward();
//
//        //可以放大缩小 过时了，被替换为WebViewClient#onScaleChanged.
//        webView.canZoomIn();
//        webView.canZoomOut();
//        webView.zoomIn();
//        webView.zoomOut();
//
//
//
//        //This method was deprecated in API level 19.
//        // Use onDraw(Canvas) to obtain a bitmap snapshot of the WebView,
//        // or saveWebArchive(String) to save the content to a file.
//        // 截图
//        webView.capturePicture();
//
//        webView.clearCache(true);
//        webView.clearFormData();
//        webView.clearHistory();
//        //清除由findAllAsync(String)找到的高亮文本
//        webView.clearMatches();
//        //重置webview
//        //Use WebView.loadUrl("about:blank") to reliably
//        // reset the view state and release page resources (including any running JavaScript).
//        webView.clearView();
//        //异步加载JS
//        webView.evaluateJavascript();
//        //api 19 释放内存
//        webView.freeMemory();
//        //获取当前页面加载进度
//        webView.getProgress();
//        //getscale 获取页面缩放级别
//        webView.freeMemory();
//
//        webView.getTitle();
//        webView.getUrl();
//
//        webView.getHandler();
//
//        webView.getContentHeight();
//        //滑动webview页面的一半、向上或者向下
//        webView.pageDown(true);
//        webView.pageUp(true);
//
//        //请求一个url ,以post方法
//        webView.postUrl(url ,postData);
//
//    }

//    public  void otherWebSettingMethod(){
//        WebSettings webSettings = webView.getSettings();
//        webSettings.setSupportZoom(true);
//        //多窗口？？？
//        webSettings.setSupportMultipleWindows(true);
//        webSettings.setUserAgentString(" ");
//        //Sets whether the WebView should enable support for the "viewport" HTML meta tag
//        // or should use a wide viewport.
//        webSettings.setUseWideViewPort(true);
//        //设置是否安全浏览
//        webSettings.setSafeBrowsingEnabled(true);
//
//        //设置字体  The default is "monospace".
//        webSettings.setFixedFontFamily( );
//
//
//    }
}