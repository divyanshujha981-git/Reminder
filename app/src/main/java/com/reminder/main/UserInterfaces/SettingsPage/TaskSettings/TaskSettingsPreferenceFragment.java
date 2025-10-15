package com.reminder.main.UserInterfaces.SettingsPage.TaskSettings;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.reminder.main.R;

public class TaskSettingsPreferenceFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.task_settings, rootKey);
    }
}
