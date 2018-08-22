package sterbenj.com.sharecollection;

import android.app.Application;
import android.util.Log;

import java.io.File;

import ren.yale.android.cachewebviewlib.CacheType;
import ren.yale.android.cachewebviewlib.WebViewCacheInterceptor;
import ren.yale.android.cachewebviewlib.WebViewCacheInterceptorInst;
import ren.yale.android.cachewebviewlib.config.CacheExtensionConfig;


public class BaseApp extends Application {
    private static final String CACHE_NAME = "cache_path";

    @Override
    public void onCreate() {
        super.onCreate();

        WebViewCacheInterceptor.Builder builder = new WebViewCacheInterceptor.Builder(this);


        builder.setCachePath(new File(this.getCacheDir(), "cache_path_name"))//设置缓存路径，默认getCacheDir，名称CacheWebViewCache
                .setCacheSize(1024 * 1024 * 1024)//设置缓存大小，默认100M
                .setConnectTimeoutSecond(20)//设置http请求链接超时，默认20秒
                .setReadTimeoutSecond(20)//设置http请求链接读取超时，默认20秒
                .setCacheType(CacheType.FORCE);


        CacheExtensionConfig extension = new CacheExtensionConfig();
        extension.addExtension("json");

        builder.setCacheExtensionConfig(extension);
        builder.setDebug(false);

        WebViewCacheInterceptorInst.getInstance().
                init(builder);

    }
}
