package sterbenj.com.sharecollection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

/**
 * XJB Created by 野良人 on 2018/5/6.
 */
public class BaseActivity extends AppCompatActivity {

    public static int sTheme;
    public static boolean pasteListenerIsRun;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        sTheme = sharedPreferences.getInt("themeID", R.style.white_transStat);
        pasteListenerIsRun = sharedPreferences.getBoolean("PasteSwitchIsChecked", false);

        this.setTheme(sTheme);

        if (BaseActivity.pasteListenerIsRun){
            final Intent serviceStart = new Intent(getApplication(), PasteListenerService.class);
            startService(serviceStart);
        }

        if (pasteListenerIsRun){
            //TODO 233
        }

        super.onCreate(savedInstanceState);
        ActivityControl.addActivity(this);
    }
    @Override
    protected  void onDestroy(){
        super.onDestroy();
        ActivityControl.removeActivity(this);
    }
}
