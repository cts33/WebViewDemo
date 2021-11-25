# webview 和JS交互且传参


# 1.native 给JS 传参

## 1.1 mWebView.loadUrl

这种形式没有返回值，调用js的gotoJS方法。

另外还有重载方法loadUrl (String url,   Map<String, String> additionalHttpHeaders)，可以添加header数据。另外官方推荐使用兼容性更好的`evaluateJavascript(String, ValueCallback)`.


```java
mWebView.loadUrl("javascript:gotoJS("+view.getId()+")");
```

js文件

```java
<script type="text/javascript">
    function gotoJS(msg){
        document.getElementById("content").innerHTML = "这是原生发送过来的msg:"+msg;
    }
</script>
```



## 1.2 mWebView.evaluateJavascript

```java
public void evaluateJavascript (String script,ValueCallback<String> resultCallback)
```

Asynchronously evaluates JavaScript in the context of the currently displayed page. If non-null, `resultCallback` will be invoked with any result returned from that execution. This method must be called on the UI thread and the callback will be made on the UI thread.

在当前页面的上下文中异步计算js,如果不为null，resultcallback将会被回调且返回结果。该方法和回调都要在UI线程执行。

Compatibility note. Applications targeting `Build.VERSION_CODES.N` or later, JavaScript state from an empty WebView is no longer persisted across navigations like `loadUrl(java.lang.String)`. For example, global variables and functions defined before calling `loadUrl(java.lang.String)` will not exist in the loaded page. Applications should use `addJavascriptInterface(Object, String)` instead to persist JavaScript objects across navigations.

兼容说明，目标版本为N或更高的应用程序，空的WebView中的JavaScript状态不再继续像loadUrl那样在navigations中保留。例如，在调用`loadUrl`之前定义的全局变量和函数将不会存在于加载的页面中。应用程序应该使用 addJavascriptInterface 来跨navigations维护(持久化)JavaScript对象。

```java
mWebView.evaluateJavascript("javascript:gotoJS(" + view.getId() + ")", new ValueCallback<String>() {
    @Override
    public void onReceiveValue(String s) {
    	Log.d(TAG, "onReceiveValue: 此处回调结果");
    }
});
```

js文件

```
<script type="text/javascript">
    function gotoJS(msg){
        document.getElementById("content").innerHTML = "这是原生发送过来的msg:"+msg;
        "原生，我是js，我收到了你发的信息";
    }
</script>
```



# 2.JS给native传参

## 2.1 mWebView.addJavascriptInterface

```java
public void addJavascriptInterface (Object object,String name)
```

Injects the supplied Java object into this WebView. The object is injected into all frames of the web page, including all the iframes, using the supplied name. This allows the Java object's methods to be accessed from JavaScript. 

将提供的 Java 对象注入此 WebView。 使用提供的名称将该对象注入到网页的所有框架中，包括所有 iframe。 这允许从 JavaScript 访问 Java 对象的方法。

For applications targeted to API level `Build.VERSION_CODES.JELLY_BEAN_MR1` and above, only public methods that are annotated with `JavascriptInterface` can be accessed from JavaScript. For applications targeted to API level `Build.VERSION_CODES.JELLY_BEAN` or below, all public methods (including the inherited ones) can be accessed, see the important security note below for implications.

Build.VERSION_CODES.JELLY_BEAN_MR1 及以上的应用程序，只能从 JavaScript 访问使用 JavascriptInterface 注释的公共方法。 对于面向 API 级别 Build.VERSION_CODES.JELLY_BEAN 或更低级别的应用程序，可以访问所有公共方法（包括继承的方法），请参阅下面的重要安全说明以了解含义。

Note that injected objects will not appear in JavaScript until the page is next (re)loaded. JavaScript should be enabled before injecting the object. 

请注意，在下一次（重新）加载页面之前，注入的对象不会出现在 JavaScript 中。 在注入对象之前应该启用 JavaScript。

```java
webview.getSettings().setJavaScriptEnabled(true);
webView.addJavascriptInterface(new JsObject(), "injectedObject");
```

```java
//通过addJavascriptInterface() AJavaScriptInterface类对象映射到JS的mjs对象
mWebView.addJavascriptInterface(new JsToNativeObj(), "jsToNative1");

public class JsToNativeObj {
    // 定义JS需要调用的方法，被JS调用的方法必须加入@JavascriptInterface注解
    @JavascriptInterface
    public void call(String msg) {
    	resultTv.setText("jsToNative1:" + msg);
    }
}
```

JS文件

jsToNative1是映射对象

```java
document.getElementById("goto_native_1") .addEventListener("click", function() {
	jsToNative1.call("js call to native !")
});
```

**IMPORTANT**

This method can be used to allow JavaScript to control the host application. This is a powerful feature, but also presents a security risk for apps targeting Build.VERSION_CODES.JELLY_BEAN or earlier. Apps that target a version later than Build.VERSION_CODES.JELLY_BEAN are still vulnerable if the app runs on a device running Android earlier than 4.2. The most secure way to use this method is to target Build.VERSION_CODES.JELLY_BEAN_MR1 and to ensure the method is called only when running on Android 4.2 or later. 

此方法可用于允许 JavaScript 控制宿主应用程序(也就是native APP)。 这是一项强大的功能，但也为目标 Build.VERSION_CODES.JELLY_BEAN（16） 或更早版本的应用程序带来了安全风险。 如果应用程序在运行早于 4.2 的 Android 的设备上运行，则目标版本高于 Build.VERSION_CODES.JELLY_BEAN（16） 的应用程序仍然容易受到攻击。 **使用此方法最安全的方法是针对 Build.VERSION_CODES.JELLY_BEAN_MR1 （17）并确保仅在 Android 4.2 或更高版本上运行时调用该方法。** 

With these older versions, JavaScript could use reflection to access an injected object's public fields. Use of this method in a WebView containing untrusted content could allow an attacker to manipulate the host application in unintended ways, executing Java code with the permissions of the host application. Use extreme care when using this method in a WebView which could contain untrusted content.

对于这些旧版本，JavaScript 可以**使用反射来访问注入对象的公共字段**。 在包含不受信任内容的 WebView 中使用此方法可能允许攻击者以意外方式操纵主机应用程序，使用主机应用程序的权限执行 Java 代码。 在可能包含不受信任内容的 WebView 中使用此方法时要格外小心。

JavaScript interacts with Java object on a private, background thread of this WebView. Care is therefore required to maintain thread safety.

js和一个私有 后台线程的webview里的对象交互的时候，要注意线程安全。

Because the object is exposed to all the frames, any frame could obtain the object name and call methods on it. There is no way to tell the calling frame's origin from the app side, so the app must not assume that the caller is trustworthy unless the app can guarantee that no third party content is ever loaded into the WebView even inside an iframe

因为对象暴露给所有的框架，所以任何框架都可以获取对象名称并调用其上的方法。 无法从应用程序端告诉调用框架的来源，因此应用程序不能假设调用者是值得信赖的，除非应用程序可以保证即使在 iframe 内也不会将第三方内容加载到 WebView 中

The Java object's fields are not accessible.

Java 对象的字段不可访问。

For applications targeted to API level `Build.VERSION_CODES.LOLLIPOP` and above, methods of injected Java objects are enumerable from JavaScript.

Build.VERSION_CODES.LOLLIPOP（21） 及更高级别的应用程序，注入的 Java 对象的方法可从 JavaScript 枚举。

## 2.2 mWebView.setWebViewClient（）

```java
public boolean shouldOverrideUrlLoading (WebView view, 
                WebResourceRequest request)
```

Give the host application a chance to take control when a URL is about to be loaded in the current WebView. If a WebViewClient is not provided, by default WebView will ask Activity Manager to choose the proper handler for the URL. If a WebViewClient is provided, returning `true` causes the current WebView to abort loading the URL, while returning `false` causes the WebView to continue loading the URL as usual.

当 URL 即将加载到当前 WebView 中时，让宿主应用程序有机会进行控制。 如果未提供 WebViewClient，默认情况下 WebView 将要求活动管理器为 URL 选择合适的处理程序。 如果提供了 WebViewClient，则返回 true 会导致当前 WebView 中止加载 URL，而返回 false 会导致 WebView 像往常一样继续加载 URL。

> **Note:** Do not call `WebView#loadUrl(String)` with the request's URL and then return `true`. This unnecessarily cancels the current load and starts a new load with the same URL. The correct way to continue loading a given URL is to simply return `false`, without calling `WebView#loadUrl(String)`.
>
> This method is not called for POST requests.
>
> This method may be called for subframes and with non-HTTP(S) schemes; calling `WebView#loadUrl(String)` with such a URL will fail.

```java
  mWebView.setWebViewClient(new WebViewClient() {
       
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
```

JS

```java
 document.getElementById("goto_native_2") .addEventListener("click", function() {
		  document.location = "js://webview?arg1=111&arg2=222";
});
```

js文件内，通过点击事件location，此内容是native和js约定的数据结构，类似于Uri形式，根据固有的数据形式，传送到shouldOverrideUrlLoading方法，然后解析数据。下文的setWebChromeClient也类似于这种形式，只不过方法换做其他方法。

## 2.3 mWebView.setWebChromeClient()

setWebChromeClient的方法里，设置一个WebChromeClient对象，该对象有几个常用的方法；onJsAlert：监听js的alert逻辑，onJsConfirm 监听用户的确认对话框逻辑。onJsPrompt监听用户的输入。我们可以根据自身情况来选择监听这几个方法，大多数监听onJsPrompt，因为这个不常被触发，nativa和js的交互设计用户的输入内容不多，所以该方法用的较少。基于2.2知识点，监听url里的约定，解析数据就可以了。

### onJsAlert

```
//url 路径  message  
//jsResult A JsResult to confirm that the user closed the window.
//返回值：true if the request is handled or ignored. false if WebView needs to show the default dialog.
public boolean onJsAlert (WebView view, 
                String url, 
                String message, 
                JsResult result)
                
```

Notify the host application that the web page wants to display a JavaScript `alert()` dialog.

当js触发alert会回调app的onJsAlert方法

The default behavior if this method returns `false` or is not overridden is to show a dialog containing the alert message and suspend JavaScript execution until the dialog is dismissed.

如果此方法返回 false 或未被覆盖，显示包含警报消息的对话框并暂停 JavaScript 执行，直到对话框被关闭。也是默认状态。

To show a custom dialog, the app should return `true` from this method, in which case the default dialog will not be shown and JavaScript execution will be suspended. The app should call `JsResult.confirm()` when the custom dialog is dismissed such that JavaScript execution can be resumed.

如果返回true，显示自定义的对话框，阻塞js执行，默认dialog不显示。当用户dialog取消，会调用confirm方法，js恢复执行。

To suppress the dialog and allow JavaScript execution to continue, call `JsResult.confirm()` immediately and then return `true`.

要抑制对话框并允许 JavaScript 继续执行，请立即调用 JsResult.confirm() 然后返回 true

Note that if the `WebChromeClient` is set to be `null`, or if `WebChromeClient` is not set at all, the default dialog will be suppressed and Javascript execution will continue immediately.

请注意，如果 WebChromeClient 设置为 null，或者 WebChromeClient 根本没有设置，默认对话框将被抑制，Javascript 执行将立即继续。

Note that the default dialog does not inherit the `Display.FLAG_SECURE` flag from the parent window.

请注意，默认对话框不会从父窗口继承 Display.FLAG_SECURE 标志。



### onJsConfirm

```
public boolean onJsConfirm (WebView view, 
                String url, 
                String message, 
                JsResult result)
```

Notify the host application that the web page wants to display a JavaScript `confirm()` dialog.

The default behavior if this method returns `false` or is not overridden is to show a dialog containing the message and suspend JavaScript execution until the dialog is dismissed. The default dialog will return `true` to the JavaScript `confirm()` code when the user presses the 'confirm' button, and will return `false` to the JavaScript code when the user presses the 'cancel' button or dismisses the dialog.

如果此方法返回“false”或未被覆盖，则默认行为是显示包含消息的对话框并暂停 JavaScript 执行，直到对话框被关闭。 当用户按下“确认”按钮时，默认对话框将向 JavaScript 的“confirm()”代码返回“真”，**当用户按下“取消”按钮或关闭对话框时，将向 JavaScript 代码返回“false”** .

To show a custom dialog, the app should return `true` from this method, in which case the default dialog will not be shown and JavaScript execution will be suspended. The app should call `JsResult.confirm()` or `JsResult.cancel()` when the custom dialog is dismissed.

To suppress the dialog and allow JavaScript execution to continue, call `JsResult.confirm()` or `JsResult.cancel()` immediately and then return `true`.

Note that if the `WebChromeClient` is set to be `null`, or if `WebChromeClient` is not set at all, the default dialog will be suppressed and the default value of `false` will be returned to the JavaScript code immediately.

Note that the default dialog does not inherit the `Display.FLAG_SECURE` flag from the parent window.

### onJsPrompt

```
public boolean onJsPrompt (WebView view, 
                String url, 
                String message, 
                String defaultValue, 
                JsPromptResult result)
```

Notify the host application that the web page wants to display a JavaScript `prompt()` dialog.

The default behavior if this method returns `false` or is not overridden is to show a dialog containing the message and suspend JavaScript execution until the dialog is dismissed. Once the dialog is dismissed, JavaScript `prompt()` will return the string that the user typed in, or null if the user presses the 'cancel' button.

如果此方法返回 false 或未被覆盖，则默认显示包含消息的对话框并暂停 JavaScript 执行，直到对话框被关闭。 关闭对话框后，JavaScript prompt() 将**返回用户输入的字符串**，如果用户按下“取消”按钮，则返回 null。

To show a custom dialog, the app should return `true` from this method, in which case the default dialog will not be shown and JavaScript execution will be suspended. The app should call `JsPromptResult.confirm(result)` when the custom dialog is dismissed.

To suppress the dialog and allow JavaScript execution to continue, call `JsPromptResult.confirm(result)` immediately and then return `true`.

Note that if the `WebChromeClient` is set to be `null`, or if `WebChromeClient` is not set at all, the default dialog will be suppressed and `null` will be returned to the JavaScript code immediately.

Note that the default dialog does not inherit the `Display.FLAG_SECURE` flag from the parent window.





https://github.com/cts33/WebViewDemo
