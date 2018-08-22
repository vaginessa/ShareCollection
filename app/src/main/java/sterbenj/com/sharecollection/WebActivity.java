package sterbenj.com.sharecollection;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

import ren.yale.android.cachewebviewlib.WebViewCacheInterceptorInst;

public class WebActivity extends BaseActivity {

    Boolean hasFinishLoadWeb;

    WebView webView;

    String base_address;

    File cache;

    String collection_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Intent intent = getIntent();

        collection_id = new Long(((CollectionItem)intent.getSerializableExtra("collectionitem")).getId()).toString();

        base_address = this.getExternalCacheDir().getAbsolutePath();
        cache = new File(base_address, collection_id + ".mhtml");
        Log.d("WebActivityLogd", "onCreate: "+ cache.getAbsolutePath().toString());


        WebViewCacheInterceptorInst.getInstance().enableForce(true);

        //初始化webView
        webView = (WebView)findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAppCacheEnabled(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        CookieManager cookieManager = CookieManager.getInstance();


        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
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
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    WebViewCacheInterceptorInst.getInstance().loadUrl(webView,request.getUrl().toString());
                    hasFinishLoadWeb = false;
                    return false;
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    WebViewCacheInterceptorInst.getInstance().loadUrl(webView,url);
                    hasFinishLoadWeb = false;
                    return false;
                }

                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Nullable
                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                    hasFinishLoadWeb = false;
                    return  WebViewCacheInterceptorInst.getInstance().interceptRequest(request);
                }

                @Nullable
                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                    hasFinishLoadWeb = false;
                    return  WebViewCacheInterceptorInst.getInstance().interceptRequest(url);
                }

                @Override
                public void onPageFinished(final WebView view, String url) {
                    super.onPageFinished(view, url);
                    Log.d("WebActivityLogd", "onPageFinished: ");
                    if (hasFinishLoadWeb){
                        view.saveWebArchive(cache.getAbsolutePath(), false, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                Log.d("WebActivityLogd", "onReceiveValue: ");
                            }
                        });
                    }
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    hasFinishLoadWeb = true;
                    Log.d("WebActivityLogd", "onPageStarted: ");
                }
            });
            webView.loadUrl(intent.getStringExtra("uri"));
            //WebViewCacheInterceptorInst.getInstance().loadUrl(webView, intent.getStringExtra("uri"));
        }
        else{
            WebViewCacheInterceptorInst.getInstance().loadUrl(webView, intent.getStringExtra("uri"));
            //webView.loadUrl(intent.getStringExtra("uri"));
            //webView.loadUrl("file:///storage/emulated/0/Android/data/sterbenj.com.sharecollection/cache/" + collection_id + ".mhtml");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("WebActivityLogd", "onDestroy: " + cache.getAbsolutePath().toString());
        webView.destroy();
        webView = null;
    }
}
