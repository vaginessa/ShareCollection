package sterbenj.com.sharecollection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * XJB Created by 野良人 on 2018/5/31.
 */
public class SettingFragment extends PreferenceFragment {

    private SharedPreferences.Editor editor;
    private ListPreference theme;
    private SwitchPreference pasteListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_fragment);
        initSetting();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //主题变换监听
        theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (preference instanceof ListPreference){
                    ListPreference listPreference = (ListPreference)preference;
                    int index = listPreference.findIndexOfValue((String)newValue);
                    if(!listPreference.getSummary().equals(listPreference.getEntries()[index])){
                        listPreference.setSummary(listPreference.getEntries()[index]);
                        editor = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit();
                        CharSequence value = listPreference.getEntryValues()[index];

                        //主题变更
                        if (value.equals("0")){
                            editor.putInt("themeID", R.style.white_transStat);
                            editor.apply();
                            ActivityControl.recreateAll();
                        }
                        if (value.equals("1")){
                            editor.putInt("themeID", R.style.Dark);
                            editor.apply();
                            ActivityControl.recreateAll();
                        }

                    }
                }
                return true;
            }
        });

        pasteListener.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (preference instanceof SwitchPreference){
                    editor = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit();
                    SwitchPreference switchPreference = (SwitchPreference)preference;
                    if (switchPreference.isChecked()){
                        switchPreference.setChecked(false);
                        final Intent serviceStart = new Intent(SettingActivity.settingActivity.getApplication(), PasteListenerService.class);
                        SettingActivity.settingActivity.stopService(serviceStart);
                        editor.putBoolean("PasteSwitchIsChecked", false);
                        editor.apply();
                    }
                    else{
                        switchPreference.setChecked(true);
                        SettingActivity.settingActivity.getFloatWindowPermission();
                        final Intent serviceStart = new Intent(SettingActivity.settingActivity.getApplication(), PasteListenerService.class);
                        SettingActivity.settingActivity.startService(serviceStart);
                        editor.putBoolean("PasteSwitchIsChecked", true);
                        editor.apply();
                    }
                }
                return false;
            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
    }

    public void initSetting(){
        //初始化theme_list
        theme = (ListPreference)findPreference("key_theme_list");
        theme.setSummary(theme.getEntry());

        //初始化paste_listener_switch
        pasteListener = (SwitchPreference)findPreference("key_paste_listener_switch");
        pasteListener.setSummary(pasteListener.getSummary());
    }
}
