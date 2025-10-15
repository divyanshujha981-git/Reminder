package com.reminder.main.UserInterfaces.SettingsPage.AllSettings;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.reminder.main.R;


public class SettingsPage extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);


    }


    @Override
    protected void onResume() {
        super.onResume();

        getSupportFragmentManager().beginTransaction().add(R.id.settingFrame, new SettingsPreferenceFragment()).commit();

    }
}
