package com.reminder.main.UserInterfaces.SettingsPage.LockSettings.PinLock;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.reminder.main.R;
import com.reminder.main.UserInterfaces.SettingsPage.LockSettings.LockData;


public class PinLock extends AppCompatActivity implements TextWatcher {
    public static final String PIN = "pin";
    private TextView errorText;
    private String finalPin;
    private String initialPin;
    private EditText pinLock;
    private TextView pinText;
    private TextView reText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_layout);
        this.pinLock = findViewById(R.id.pinLockView);
        this.pinText = findViewById(R.id.pinText);
        this.errorText = findViewById(R.id.pinErrorText);
        this.reText = findViewById(R.id.pinReText);

        MaterialButton button = findViewById(R.id.button);

        button.setOnClickListener(view -> buttonClick((MaterialButton) view));
        findViewById(R.id.pinReset).setOnClickListener(view -> reset(button));
    }


    private void buttonClick(MaterialButton button) {
        if (initialPin == null) {
            initialPin = pinLock.getText().toString();
            if (initialPin.length() > 3) {
                pinLock.setText("");
                button.setText(getString(R.string.confirm));
                pinText.setText(getString(R.string.re_enter_the_pin));
            } else {
                Toast.makeText(this, getString(R.string.four_digit_pin), Toast.LENGTH_SHORT).show();
                initialPin = null;
            }
        } else {
            finalPin = pinLock.getText().toString();
            if (finalPin.equals(initialPin)) {
                LockData.setPin(this, initialPin);
                finish();
                Log.d("TAG", "onCreate: SUCCESSFUL");
            } else {
                setTextVisibility((byte) 1);
                finalPin = null;
                pinLock.addTextChangedListener(this);
            }
        }
    }


    private void reset(MaterialButton button) {
        finalPin = null;
        initialPin = null;
        setTextVisibility((byte) 0);
        pinLock.setText("");
        button.setText(getString(R.string.submit));

        errorText.setVisibility(View.GONE);
        pinText.setText(getString(R.string.please_enter_the_password));

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (count > 0) {
            setTextVisibility((byte) 2);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }


    private void setTextVisibility(byte con) {
        switch (con) {
            case 1:
                this.errorText.setVisibility(View.VISIBLE);
                this.reText.setVisibility(View.GONE);
                this.pinText.setVisibility(View.GONE);
                return;
            case 2:
                this.reText.setVisibility(View.VISIBLE);
                this.errorText.setVisibility(View.GONE);
                this.pinText.setVisibility(View.GONE);
                return;
            default:
                this.pinText.setVisibility(View.VISIBLE);
                this.reText.setVisibility(View.GONE);
                this.errorText.setVisibility(View.GONE);
        }
    }


}
