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
                        Log.d(TAG, "onReceiveValue: ??????????????????");
                    }
                });
            }
        });

        mWebView.loadUrl("file:///android_asset/index.html");
//        mWebView.loadUrl("https://www.baidu.com/");
//        mWebView.loadUrl("http://soft.imtt.qq.com/browser/tes/feedback.html");

        mWebView.getSettings().setJavaScriptEnabled(true);
        //???????????????????????????????????????
        mWebView.getSettings().getTextZoom();
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //?????????????????????scheme??????????????????
                return true;//?????????true???????????????????????????
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {

                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    uri = request.getUrl();
                } else {
                    uri = Uri.parse(request.toString());
                }
                // ??????url????????? = ??????????????? js ??????,???????????????????????????
                if (uri.getScheme().equals("js")) {
                    // ?????? authority = ????????????????????????webview????????????????????????????????????
                    // ????????????url,??????JS????????????Android???????????????
                    if (uri.getAuthority().equals("webview")) {

                        StringBuffer stringBuffer = new StringBuffer();
                        Set<String> collection = uri.getQueryParameterNames();
                        for (String item : collection) {
                            stringBuffer.append(item + "=" + uri.getQueryParameter(item) + " ");
                        }
//                        String result = "Android?????????JS????????????useid=123456";
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
        //??????addJavascriptInterface() AJavaScriptInterface??????????????????JS???mjs??????
        mWebView.addJavascriptInterface(new JsToNativeObj(), "jsToNative1");

    }

    public class JsToNativeObj {
        // ??????JS???????????????????????????JS???????????????????????????@JavascriptInterface??????
        @JavascriptInterface
        public void call(String msg) {
            resultTv.setText("jsToNative1:" + msg);
        }
    }

}