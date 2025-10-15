package com.reminder.main.UserInterfaces.Ppp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.reminder.main.Other.ApplicationCustomInterfaces;
import com.reminder.main.R;
import com.reminder.main.UserInterfaces.SettingsPage.LockSettings.LockSettings;


public class Pin extends Fragment implements TextWatcher {
    private final String key;
    private EditText pinView;
    private TextView pinText, errorText, reText;
    private ApplicationCustomInterfaces.AllowUserToNavigate allowUserToNavigate;


    public Pin(String key) {
        this.key = key;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pin, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        allowUserToNavigate = (ApplicationCustomInterfaces.AllowUserToNavigate) requireContext();

        pinView = view.findViewById(R.id.pinLockView);

        pinText = view.findViewById(R.id.pinText);
        errorText = view.findViewById(R.id.pinErrorText);
        reText = view.findViewById(R.id.pinReText);

        view.findViewById(R.id.button).setOnClickListener(v -> {
            if (key == null || key.length() == 0) {
                Toast.makeText(requireContext(), "Please setup the pin first", Toast.LENGTH_SHORT).show();
            } else {
                if (key.equals(pinView.getText().toString().trim())) {
                    // do something
                    allowUserToNavigate.authorized();
                } else {
                    setTextVisibility(LockSettings.ERROR_TEXT_VIS);
                    pinView.setText("");
                    pinView.addTextChangedListener(this);

                }

            }
        });

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (count > 0) {
            setTextVisibility(LockSettings.RE_TEXT_VIS);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    private void setTextVisibility(byte con) {
        switch (con) {
            case LockSettings.ERROR_TEXT_VIS:
                errorText.setVisibility(View.VISIBLE);
                reText.setVisibility(View.GONE);
                pinText.setVisibility(View.GONE);
                break;
            case LockSettings.RE_TEXT_VIS:
                reText.setVisibility(View.VISIBLE);
                errorText.setVisibility(View.GONE);
                pinText.setVisibility(View.GONE);
                break;
            default:
                pinText.setVisibility(View.VISIBLE);
                reText.setVisibility(View.GONE);
                errorText.setVisibility(View.GONE);
        }
    }


}
