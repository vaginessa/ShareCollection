package sterbenj.com.sharecollection;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

public class SettingActivity extends BaseActivity {

    public static SettingActivity settingActivity;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingActivity = this;
        setContentView(R.layout.activity_setting);
        initToolbar();
        initPreferenceFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return false;
    }

    public void initPreferenceFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        SettingFragment settingFragment = new SettingFragment();
        transaction.add(R.id.fragment_setting, settingFragment);
        transaction.commit();
    }

    public void initToolbar(){
        toolbar = (Toolbar)findViewById(R.id.toobar_setting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void getFloatWindowPermission(){
        final Intent serviceStart = new Intent(getApplication(), PasteListenerService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.canDrawOverlays(getApplicationContext())) {
                //启动Activity让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent,2);
            }
        }
    }
}