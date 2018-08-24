package sterbenj.com.sharecollection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.File;
import java.util.Map;

public class WebActivity extends BaseActivity {

    Boolean hasFinishLoadWeb;

    com.tencent.smtt.sdk.WebView webView;

    String base_address;

    File cache;

    String collection_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(getApplicationContext()){
            @Override
            protected void onSizeChanged(int i, int i1, int i2, int i3) {
                super.onSizeChanged(i, i1, i2, i3);
                Log.d("WebActivityLogd", "onSizeChanged: " + i + " " + i1 + " " + i2 + " " + i3);
            }


        };
        setContentView(webView);

        Intent intent = getIntent();

        collection_id = new Long(((CollectionItem)intent.getSerializableExtra("collectionitem")).getId()).toString();

        base_address = this.getExternalCacheDir().getAbsolutePath();

        cache = new File(base_address, collection_id + ".mhtml");

        Log.d("WebActivityLogd", "onCreate: "+ cache.getAbsolutePath().toString());


        //初始化webView

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // 图片过大时自动适应屏幕
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        // 禁用水平垂直滚动条
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setBlockNetworkImage(false);
        // 设置加载进来的页面自适应手机屏幕
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        // (禁止)显示放大缩小Controller
        webSettings.setBuiltInZoomControls(false);
        // (禁止)|(可)缩放
        webSettings.setSupportZoom(false);
        // 不显示webView缩放按钮
        webSettings.setDisplayZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 调整Cookie的使用，否则Cookie的相关操作只能影响系统内核
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
        webSettings.setUserAgent("Mozilla/5.0" + " (Linux; Android 7.1.1) " + "Mobile Safari/537.36");


        webSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        if(tools.NetWork){
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        }
        else{
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

//        webView.getSettings().setSupportMultipleWindows(true);
//        // 开启 DOM storage API 功能
//        webView.getSettings().setDomStorageEnabled(true);
//        // 开启 Application Caches 功能
//        webView.getSettings().setAppCacheEnabled(true);


        //给webView添加拦截用于缓存
        if (tools.NetWork){
            webView.setSaveEnabled(true);
            webView.setWebViewClient(new WebViewClient(){

                @Override
                public boolean shouldOverrideUrlLoading(com.tencent.smtt.sdk.WebView webView, String s) {
                    Log.d("WebActivityLogd", "shouldOverrideUrlLoading:1 " + s);
                    webView.loadUrl(s);
                    return true;
                }

                @Override
                public boolean shouldOverrideUrlLoading(com.tencent.smtt.sdk.WebView webView, com.tencent.smtt.export.external.interfaces.WebResourceRequest webResourceRequest) {
                    webResourceRequest.getUrl().toString().indexOf("http");
                    if (webResourceRequest.getUrl().toString().indexOf("http") == 0){
                        Log.d("WebActivityLogd", "shouldOverrideUrlLoading:2 " + webResourceRequest.getUrl().toString());
                        Log.d("WebActivityLogd", "shouldOverrideUrlLoading:2 " + webResourceRequest.getUrl().toString().indexOf("http"));
                        webView.loadUrl(webResourceRequest.getUrl().toString());
                    }
                    return true;
                }


                @Override
                public void onPageFinished(com.tencent.smtt.sdk.WebView webView, String s) {
                    Log.d("WebActivityLogd", "onPageFinished: " + s);
                    super.onPageFinished(webView, s);
                }

                @Override
                public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest webResourceRequest, Bundle bundle) {
                    Log.d("WebActivityLogd", "shouldInterceptRequest:1 " + webResourceRequest.getUrl().toString());
                    if (webResourceRequest.getUrl().toString().indexOf("http") == 0){
                        return super.shouldInterceptRequest(webView, webResourceRequest);
                    }
                    else{
                        Log.d("WebActivityLogd", "shouldInterceptRequest:1 " + bundle.toString());
                        return super.shouldInterceptRequest(webView, new WebResourceRequest() {
                            @Override
                            public Uri getUrl() {
                                Uri uri = Uri.EMPTY;
                                return uri;
                            }

                            @Override
                            public boolean isForMainFrame() {
                                return false;
                            }

                            @Override
                            public boolean isRedirect() {
                                return false;
                            }

                            @Override
                            public boolean hasGesture() {
                                return false;
                            }

                            @Override
                            public String getMethod() {
                                return null;
                            }

                            @Override
                            public Map<String, String> getRequestHeaders() {
                                return null;
                            }
                        }, null);
                    }
                }

                @Override
                public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest webResourceRequest) {
                    Log.d("WebActivityLogd", "shouldInterceptRequest:2 " + webResourceRequest.getUrl().toString());
                    if (webResourceRequest.getUrl().toString().indexOf("http") == 0){
                        return super.shouldInterceptRequest(webView, webResourceRequest);
                    }
                    else{
                        return super.shouldInterceptRequest(webView, new WebResourceRequest() {
                            @Override
                            public Uri getUrl() {
                                Uri uri = Uri.EMPTY;
                                return uri;
                            }

                            @Override
                            public boolean isForMainFrame() {
                                return false;
                            }

                            @Override
                            public boolean isRedirect() {
                                return false;
                            }

                            @Override
                            public boolean hasGesture() {
                                return false;
                            }

                            @Override
                            public String getMethod() {
                                return null;
                            }

                            @Override
                            public Map<String, String> getRequestHeaders() {
                                return null;
                            }
                        });
                    }
                }

                @Override
                public WebResourceResponse shouldInterceptRequest(WebView webView, String s) {
                    Log.d("WebActivityLogd", "shouldInterceptRequest:3 " + s.toString());
                    if (s.indexOf("http") == 0){
                        return super.shouldInterceptRequest(webView, s);
                    }
                    else {
                        return super.shouldInterceptRequest(webView, "");
                    }
                }



                @Override
                public void onPageStarted(com.tencent.smtt.sdk.WebView webView, String s, Bitmap bitmap) {
                    Log.d("WebActivityLogd", "onPageStarted: " + s);
                    super.onPageStarted(webView, s, bitmap);
                }

            });
            webView.loadUrl(intent.getStringExtra("uri"));
            //WebViewCacheInterceptorInst.getInstance().loadUrl(webView, intent.getStringExtra("uri"));
        }
        else{
            //WebViewCacheInterceptorInst.getInstance().loadUrl(webView, intent.getStringExtra("uri"));
            //webView.loadUrl(intent.getStringExtra("uri"));
            webView.loadUrl("file:///storage/emulated/0/Android/data/sterbenj.com.sharecollection/cache/" + collection_id + ".mhtml");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.saveWebArchive(cache.getAbsolutePath(), false, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                Log.d("WebActivityLogd", "onReceiveValue: ");
                webView.destroy();
            }
        });
        Log.d("WebActivityLogd", "onDestroy: " + cache.getAbsolutePath().toString());
    }
}

