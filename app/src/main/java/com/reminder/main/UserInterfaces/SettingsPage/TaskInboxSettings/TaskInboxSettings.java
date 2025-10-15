package com.reminder.main.UserInterfaces.SettingsPage.TaskInboxSettings;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.reminder.main.R;

public class TaskInboxSettings extends AppCompatActivity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);




        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.settingFrame, new TaskInboxSettingsPreferenceFragment())
                .commit();
    }
}
