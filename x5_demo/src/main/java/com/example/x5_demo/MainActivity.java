package com.example.x5_demo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ServiceWorkerClient;
import android.webkit.ServiceWorkerController;
import android.webkit.WebResourceResponse;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.smtt.export.external.extension.interfaces.IX5WebViewExtension;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.HashMap;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    WebView mWebView;
    Button button1,button2;
    TextView resultTv;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mWebView = findViewById(R.id.forum_context);
        resultTv = findViewById(R.id.content);
        button1 = findViewById(R.id.goto_web1);
        button2 = findViewById(R.id.goto_web2);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mWebView.loadUrl("javascript:gotoJS("+view.getId()+")");
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
          

            @Override
            public void onClick(View view) {
                mWebView.evaluateJavascript("javascript:gotoJS(" + view.getId() + ")", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        Log.d(TAG, "onReceiveValue: 此处回调结果");
                    }
                });
            }
        });

        mWebView.loadUrl("file:///android_asset/index.html");
//        mWebView.loadUrl("https://www.baidu.com/");
//        mWebView.loadUrl("http://soft.imtt.qq.com/browser/tes/feedback.html");

        mWebView.getSettings().setJavaScriptEnabled(true);
        //该方法可以获取当前文字大小
        mWebView.getSettings().getTextZoom();
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //这里可以对特殊scheme进行拦截处理
                return true;//要返回true否则内核会继续处理
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {

                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    uri = request.getUrl();
                } else {
                    uri = Uri.parse(request.toString());
                }
                // 如果url的协议 = 预先约定的 js 协议,就解析往下解析参数
                if (uri.getScheme().equals("js")) {
                    // 如果 authority = 预先约定协议里的webview，即代表都符合约定的协议
                    // 所以拦截url,下面JS开始调用Android需要的方法
                    if (uri.getAuthority().equals("webview")) {

                        StringBuffer stringBuffer = new StringBuffer();
                        Set<String> collection = uri.getQueryParameterNames();
                        for (String item : collection) {
                            stringBuffer.append(item + "=" + uri.getQueryParameter(item) + " ");
                        }
//                        String result = "Android回调给JS的数据为useid=123456";
//                        view.loadUrl("javascript:returnResult(\"" + result + "\")");
                        resultTv.setText("jsToNative2:" + stringBuffer.toString());
                    }
                    return true;
                }


                return super.shouldOverrideUrlLoading(webView, request);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsPrompt(WebView webView, String s, String s1, String s2, JsPromptResult jsPromptResult) {


                return super.onJsPrompt(webView, s, s1, s2, jsPromptResult);
            }
        });
        //通过addJavascriptInterface() AJavaScriptInterface类对象映射到JS的mjs对象
        mWebView.addJavascriptInterface(new JsToNativeObj(), "jsToNative1");

    }

    public class JsToNativeObj {
        // 定义JS需要调用的方法，被JS调用的方法必须加入@JavascriptInterface注解
        @JavascriptInterface
        public void call(String msg) {
            resultTv.setText("jsToNative1:" + msg);
        }
    }

}