package com.reminder.main.UserInterfaces.LoginRegisterPage.PhoneNumber;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.reminder.main.Other.ApplicationCustomInterfaces;
import com.reminder.main.R;

import java.util.concurrent.TimeUnit;

public class PhoneNumberAuthentication extends AppCompatActivity {

    private static final String TAG = "PhoneAuth";
    private CoordinatorLayout circularProgress;
    //private TextView autoRetrieveText;
    private AutoCompleteTextView countryCodeAutocomplete;
    private final FirebaseAuth FIREBASE_AUTH =  FirebaseAuth.getInstance();
    private TextInputLayout countryCodeLayout, phoneNumberLayout;
    private static PhoneAuthProvider.ForceResendingToken resendingToken;
    private TextInputEditText phoneNumberInput;
    private Button nextButton;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_number_authentication_layout_1);

        initializeViews();
        setActions();

    }

    private void initializeViews() {
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        countryCodeLayout = findViewById(R.id.country_code_layout);
        phoneNumberLayout = findViewById(R.id.phone_number_layout);
        countryCodeAutocomplete = findViewById(R.id.country_code_autocomplete);
        circularProgress = findViewById(R.id.circular_progress_middle_view);
        //autoRetrieveText = findViewById(R.id.auto_retrieve_text);
        phoneNumberInput = findViewById(R.id.phone_number_input);
        nextButton = findViewById(R.id.next_button);


    }



    private void setActions() {

        nextButton.setOnClickListener(v -> startPhoneNumberVerification1());

        new CountryCodeAdapter(this, countryCodeAutocomplete, code -> Log.d(TAG, "getCode: " + code));

    }



    private void startPhoneNumberVerification1() {
        String countryCode = countryCodeAutocomplete.getText().toString();

        String phoneNumber = phoneNumberInput.getText() != null ? phoneNumberInput.getText().toString() : "";

        if (phoneNumber.trim().isEmpty()) {
            Toast.makeText(this, "Please enter a phone number", LENGTH_SHORT).show();
        }
        else {
            String fullPhoneNumber = countryCode + phoneNumber;
            circularProgress.setVisibility(VISIBLE);
            //autoRetrieveText.setVisibility(VISIBLE);
            startPhoneNumberAuthentication(this, fullPhoneNumber, (codeSent, verificationId, otp) -> {
                if (codeSent) {
                    Toast.makeText(this, "Otp sent", LENGTH_SHORT).show();
                    Intent intent = new Intent(this, VerifyCodeActivity.class);
                    intent.putExtra(VerifyCodeActivity.VERIFICATION_ID, verificationId);
                    intent.putExtra(VerifyCodeActivity.PHONE_NUMBER, fullPhoneNumber);
                    intent.putExtra(VerifyCodeActivity.OTP, otp);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(this, "Something went wrong", LENGTH_SHORT).show();
                    circularProgress.setVisibility(GONE);
                    //autoRetrieveText.setVisibility(GONE);

                }
                finish();
            });

        }
    }




    public static void startPhoneNumberAuthentication(Context context, String phoneNumber,ApplicationCustomInterfaces.PhoneAuthCallBack phoneAuthCallBack) {

        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity((Activity) context)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                Log.d(TAG, "onVerificationCompleted: **VERIFICATION COMPLETE**");
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Log.e(TAG, "onVerificationFailed: ", e);
                                phoneAuthCallBack.codeSent(false, null, null);
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(verificationId, forceResendingToken);
                                Log.d(TAG, "onCodeSent:" + verificationId);
                                resendingToken = forceResendingToken;
                                phoneAuthCallBack.codeSent(true, verificationId, null);
                            }

                        });


        if (resendingToken != null) builder.setForceResendingToken(resendingToken);



        PhoneAuthProvider.verifyPhoneNumber(builder.build());

    }

}