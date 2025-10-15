package com.reminder.main.UserInterfaces.SettingsPage.LockSettings.PasswordLock;

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


public class PasswordLock extends AppCompatActivity implements TextWatcher {
    public static final String PASSWORD = "password";
    private TextView errorText;
    private String finalPassword;
    private String initialPassword;
    private EditText passwordLock;
    private TextView passwordText;
    private TextView reText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_layout);
        this.passwordLock = findViewById(R.id.passLockView);
        this.passwordText = findViewById(R.id.passText);
        this.errorText = findViewById(R.id.passErrorText);
        this.reText = findViewById(R.id.passReText);
        MaterialButton button = findViewById(R.id.button);


        button.setOnClickListener(view -> buttonClick((MaterialButton) view));
        findViewById(R.id.pinReset).setOnClickListener(view -> reset(button));


    }


    public void buttonClick(MaterialButton button) {
        if (initialPassword == null) {
            initialPassword = passwordLock.getText().toString();
            if (initialPassword.length() > 3) {
                passwordLock.setText("");
                button.setText(getString(R.string.confirm));
                passwordText.setText(getString(R.string.re_enter_the_pin));
            } else {
                Toast.makeText(this, getString(R.string.four_digit_pin), Toast.LENGTH_SHORT).show();
                initialPassword = null;
            }
        } else {
            finalPassword = passwordLock.getText().toString();
            if (finalPassword.equals(initialPassword)) {
                LockData.setPin(this, initialPassword);
                finish();
                Log.d("TAG", "onCreate: SUCCESSFUL");
            } else {
                setTextVisibility((byte) 1);
                finalPassword = null;
                passwordLock.addTextChangedListener(this);
            }
        }
    }


    public void reset(MaterialButton button) {
        finalPassword = null;
        initialPassword = null;
        setTextVisibility((byte) 0);
        passwordLock.setText("");
        button.setText(getString(R.string.submit));

        errorText.setVisibility(View.GONE);
        passwordText.setText(getString(R.string.please_enter_the_password));

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
                this.reText.setVisibility(View.VISIBLE);
                this.passwordText.setVisibility(View.GONE);
                return;
            case 2:
                this.reText.setVisibility(View.VISIBLE);
                this.errorText.setVisibility(View.VISIBLE);
                this.passwordText.setVisibility(View.VISIBLE);
                return;
            default:
                this.passwordText.setVisibility(View.VISIBLE);
                this.reText.setVisibility(View.VISIBLE);
                this.errorText.setVisibility(View.VISIBLE);
        }
    }


}
