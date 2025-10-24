package com.reminder.main.UserInterfaces.SettingsPage.AllSettings;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.reminder.main.R;
import com.reminder.main.UserInterfaces.AboutPage.AboutPage;
import com.reminder.main.UserInterfaces.HomePage.MainActivity.MainActivity;
import com.reminder.main.UserInterfaces.LoginRegisterPage.LoginRegister;
import com.reminder.main.UserInterfaces.SettingsPage.AccountSettings.AccountSettings;
import com.reminder.main.UserInterfaces.SettingsPage.LockSettings.LockSettings;
import com.reminder.main.UserInterfaces.SettingsPage.TaskInboxSettings.TaskInboxSettings;
import com.reminder.main.UserInterfaces.SettingsPage.TaskSettings.TaskSetting;

public class SettingsPreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {


    private final FirebaseUser FIREBASE_USER = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        setClickListener();

    }


    private void setClickListener() {
        findPreference("taskOpt").setOnPreferenceClickListener(this);
        findPreference("accountOpt").setOnPreferenceClickListener(this);
        findPreference("lockOpt").setOnPreferenceClickListener(this);
        findPreference("taskInboxOpt").setOnPreferenceClickListener(this);
        findPreference("About").setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(@NonNull Preference preference) {
        String key = preference.getKey();

        switch (key) {
            case "taskOpt":
                startActivity(new Intent(getContext(), TaskSetting.class));
                break;
            case "accountOpt":
                if (redirectToLoginRegisterIfNotSignedIn()) return true;
                startActivity(new Intent(getContext(), AccountSettings.class));
                break;
            case "lockOpt":
                startActivity(new Intent(getContext(), LockSettings.class));
                break;
            case "taskInboxOpt":
                if (redirectToLoginRegisterIfNotSignedIn()) return true;
                startActivity(new Intent(getContext(), TaskInboxSettings.class));
                break;
            case "About":
                startActivity(new Intent(getContext(), AboutPage.class));
                break;
        }

        return true;
    }




    private boolean redirectToLoginRegisterIfNotSignedIn() {
        if (FIREBASE_USER == null) {
            startActivity(new Intent(requireContext(), LoginRegister.class));
            return true;
        }
        return false;
    }



}
