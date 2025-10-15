package com.reminder.main.UserInterfaces.SettingsPage.LockSettings;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.reminder.main.R;
import com.reminder.main.UserInterfaces.Ppp.Ppp;
import com.reminder.main.UserInterfaces.SettingsPage.LockSettings.PasswordLock.PasswordLock;
import com.reminder.main.UserInterfaces.SettingsPage.LockSettings.PatternLock.PatternLock;
import com.reminder.main.UserInterfaces.SettingsPage.LockSettings.PinLock.PinLock;

public class LockSettingsPreferenceFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.lock_settings, rootKey);

        findPreference(getString(R.string.setPattern)).setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(requireContext(), PatternLock.class));
            return true;
        });


        findPreference(getString(R.string.setPin)).setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(requireContext(), PinLock.class));
            return true;
        });


        findPreference(getString(R.string.setPassword)).setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(requireContext(), PasswordLock.class));
            return true;
        });

        if (Ppp.getKeys(requireContext()) == null) {
            findPreference(getString(R.string.get_private_lock_and_message_lock_state)).setEnabled(false);
            findPreference(getString(R.string.get_settings_lock_state)).setEnabled(false);
        }
    }
}
