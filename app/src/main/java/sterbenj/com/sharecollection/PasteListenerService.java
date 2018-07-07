package sterbenj.com.sharecollection;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class PasteListenerService extends Service {
    private static final String TAG = "FloatViewService";

    static String PasteUri;

    ClipboardManager manager;
    ClipboardManager.OnPrimaryClipChangedListener listener;
    @Override
    public void onCreate() {
        super.onCreate();
        manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        registerClipEvents();
        Log.i(TAG, "onCreate1");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "onStartCommand: 1111111111111111");
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (listener != null && manager != null){
            manager.removePrimaryClipChangedListener(listener);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    //注册剪贴板监听服务
    private void registerClipEvents() {

        listener = new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {

                if (manager.hasPrimaryClip() && manager.getPrimaryClip().getItemCount() > 0) {

                    CharSequence addedText = manager.getPrimaryClip().getItemAt(0).getText();



                    if (addedText != null && addedText.toString().contains("http")) {

                        //剪贴板内容获取
                        PasteUri = addedText.toString().substring(addedText.toString().indexOf("http"));

                        Intent intent = new Intent(getApplication(), FloatButtonService.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(intent);
                        }
                        else{
                            startService(intent);
                        }

                    }
                }
            }
        };
        if (manager != null){
            manager.addPrimaryClipChangedListener(listener);
        }
    }
}
