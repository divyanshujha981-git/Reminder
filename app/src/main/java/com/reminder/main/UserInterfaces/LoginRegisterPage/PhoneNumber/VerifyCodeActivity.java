package com.reminder.main.UserInterfaces.LoginRegisterPage.PhoneNumber; // Replace with your package name

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.reminder.main.Custom.CustomInterfaces;
import com.reminder.main.R;
import com.reminder.main.UserInterfaces.HomePage.MainActivity.MainActivity;
import com.reminder.main.UserInterfaces.LoginRegisterPage.LoginRegister;
import com.reminder.main.UserInterfaces.SettingsPage.AccountSettings.EditAccountInfo;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerifyCodeActivity extends AppCompatActivity {

    private static final String TAG = "VerifyCodeActivity";
    private TextInputEditText[] otpBoxes;
    private TextView textView;
    private Button nextButton;
    private TextView resendCodeButton;
    private CoordinatorLayout circularProgress;
    public static final String OTP = "otp", VERIFICATION_ID = "verificationId", PHONE_NUMBER = "phoneNumber";
    private String phoneNumber, otp, verificationId;
    private ActivityResultLauncher<Intent> smsConsentLauncher;
    private BroadcastReceiver smsVerificationReceiver;
    private final DecimalFormat numberFormat = new DecimalFormat("00");





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_number_authentication_layout_2);

        declare();
        setActions();


    }


    @Override
    protected void onStart() {
        super.onStart();

        startTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (smsVerificationReceiver != null) {
            unregisterReceiver(smsVerificationReceiver);
        }
    }



    private void declare() {
        phoneNumber = getIntent().getStringExtra(PHONE_NUMBER);
        verificationId = getIntent().getStringExtra(VERIFICATION_ID);
        otp = getIntent().getStringExtra(OTP);
        otpBoxes = new TextInputEditText[]{
                findViewById(R.id.otp_box_1),
                findViewById(R.id.otp_box_2),
                findViewById(R.id.otp_box_3),
                findViewById(R.id.otp_box_4),
                findViewById(R.id.otp_box_5),
                findViewById(R.id.otp_box_6)
        };
        nextButton = findViewById(R.id.next_button);
        resendCodeButton = findViewById(R.id.resend_code_button);
        circularProgress = findViewById(R.id.circular_progress_middle_view);
        textView = findViewById(R.id.resend_otp_text);


        smsConsentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String message = result.getData().getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                        parseOtpFromMessage(message);
                    } else {
                        Log.d(TAG, "Consent denied by user or flow was cancelled.");
                    }
                });

        registerSmsVerificationReceiver();


    }


    private void registerSmsVerificationReceiver() {
        smsVerificationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                    Bundle extras = intent.getExtras();
                    if (extras != null && extras.containsKey(SmsRetriever.EXTRA_CONSENT_INTENT)) {
                        Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
                        smsConsentLauncher.launch(consentIntent);
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);

        // --- THIS IS THE FIX ---
        // Add the RECEIVER_NOT_EXPORTED flag for Android 13+
        ContextCompat.registerReceiver(this, smsVerificationReceiver, intentFilter, ContextCompat.RECEIVER_NOT_EXPORTED);
    }



    private void parseOtpFromMessage(String message) {
        if (message == null) return;
        Pattern pattern = Pattern.compile("(\\d{6})");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String otp = matcher.group(0);
            if (otp != null) {
                Log.d(TAG, "OTP Found: " + otp);
                for (int i = 0; i < otpBoxes.length && i < otp.length(); i++) {
                    otpBoxes[i].setText(String.valueOf(otp.charAt(i)));
                }
                verifyCode();
            }
        }
    }








    private void setActions() {

        nextButton.setOnClickListener(v -> verifyCode());

        resendCodeButton.setOnClickListener(v -> resendCode());

        setupOtpBoxListeners();

    }



    private void setupOtpBoxListeners() {
        for (int i = 0; i < otpBoxes.length; i++) {
            final int currentIndex = i;
            otpBoxes[currentIndex].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().trim().isEmpty() && currentIndex < otpBoxes.length - 1) {
                        otpBoxes[currentIndex + 1].requestFocus();
                    }

                    if (currentIndex == otpBoxes.length - 1) {
                        otpBoxes[currentIndex].clearFocus();
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });

            otpBoxes[currentIndex].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (currentIndex > 0 && otpBoxes[currentIndex].getText().toString().isEmpty()) {
                        otpBoxes[currentIndex - 1].requestFocus();
                    }
                }
                return false;
            });
        }
        if (otpBoxes.length > 0) {
            otpBoxes[0].requestFocus();
        }
    }





    private void resendCode() {

        circularProgress.setVisibility(VISIBLE);
        PhoneNumberAuthentication.startPhoneNumberAuthentication(this, phoneNumber, (isCodeSent, verificationId, otp) -> {
            circularProgress.setVisibility(GONE);

            if (isCodeSent) {
                startTimer();

                this.verificationId = verificationId;
                this.otp = otp;
            }
            else {
                Toast.makeText(this, "Something went wrong", LENGTH_SHORT).show();
            }
        });
    }







    private void verifyCode() {

        circularProgress.setVisibility(VISIBLE);

        StringBuilder otp = new StringBuilder();
        for (TextInputEditText box : otpBoxes) {
            if (box.getText() != null) {
                otp.append(box.getText().toString());
            }
        }
        if (otp.length() == 6) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp.toString());
            signInWithPhoneAuthCredential(credential, signedIn -> {
                if (signedIn) {
                    startActivity(
                            new Intent(this, EditAccountInfo.class)
                                    .putExtra(LoginRegister.SIGN_IN_TYPE, LoginRegister.SIGN_IN_TYPE_PHONE)
                    );
                    finish();
                }
                else {
                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                }
                circularProgress.setVisibility(GONE);
            });
        }
        else {
            Toast.makeText(this, "Please enter all 6 digits", Toast.LENGTH_SHORT).show();
        }

    }



    private void startTimer() {
        resendCodeButton.setEnabled(false);
        new CountDownTimer(60000, 1000) {
            @Override
            public void onFinish() {
                resendCodeButton.setEnabled(true);
                textView.setText(getString(R.string.didn_t_receive_the_otp));
            }
            @Override
            public void onTick(long millisUntilFinished) {
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                textView.setText(getString(R.string.try_to_get_otp, numberFormat.format(min), numberFormat.format(sec)));
            }
        }.start();
    }



    public static void signInWithPhoneAuthCredential(PhoneAuthCredential credential, CustomInterfaces.CallBack callBack) {
        MainActivity.FIREBASE_AUTH.signInWithCredential(credential)
                .addOnCompleteListener( task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        callBack.callback(true);
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        callBack.callback(false);
                    }
                });
    }



}