package com.reminder.main.UserInterfaces.SettingsPage.LockSettings;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.reminder.main.R;

public class LockSettings extends AppCompatActivity {
    public static final String LOCK_FILE = "lockFile";
    public static final byte TEXT_VIS = 0, ERROR_TEXT_VIS = 1, RE_TEXT_VIS = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.settingFrame, new LockSettingsPreferenceFragment())
                .commit();
    }
}
