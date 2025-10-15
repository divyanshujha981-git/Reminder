package com.reminder.main.UserInterfaces.Ppp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.reminder.main.R;
import com.reminder.main.UserInterfaces.SettingsPage.LockSettings.LockSettings;

public class PPP_Process_Button_Layout extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ppp_procees_button_layout);

        findViewById(R.id.proceed).setOnClickListener(v -> {
            startActivity(new Intent(this, LockSettings.class));
            finish();
        });

    }
}
