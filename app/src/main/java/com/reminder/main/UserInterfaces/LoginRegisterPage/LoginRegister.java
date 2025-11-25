package com.reminder.main.UserInterfaces.LoginRegisterPage;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.reminder.main.Custom.CustomAlertDialogue;
import com.reminder.main.R;
import com.reminder.main.UserInterfaces.HomePage.MainActivity.MainActivity;
import com.reminder.main.UserInterfaces.LoginRegisterPage.PhoneNumber.PhoneNumberAuthentication;
import com.reminder.main.UserInterfaces.SettingsPage.AccountSettings.EditAccountInfo;

import java.util.concurrent.Executor;



public class LoginRegister extends AppCompatActivity {

    public static final String SIGN_IN_TYPE = "sit";
    private MaterialButton googleSignInButton, phoneSignInButton;
    public static final String SIGN_IN_TYPE_GOOGLE = "sit_google";
    public static final String SIGN_IN_TYPE_PHONE = "sit_phone";
    private final FirebaseAuth FIREBASE_AUTH = MainActivity.FIREBASE_AUTH;
    private LinearLayout loadingView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_register_layout);
        declaration();
        setClickListener();

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (FIREBASE_AUTH.getCurrentUser() != null) finish();
    }


    private void declaration() {

        loadingView = findViewById(R.id.loadingView);
        googleSignInButton = findViewById(R.id.googleSignInButton);
        phoneSignInButton = findViewById(R.id.phoneSignInButton);

    }


    private void setClickListener() {
        googleSignInButton.setOnClickListener(v -> beginSignIn_GOOGLE());
        phoneSignInButton.setOnClickListener(v -> beginSignIn_PHONE());
    }


    private void beginSignIn_GOOGLE() {

        loadingView.setVisibility(VISIBLE);
        googleSignInButton.setEnabled(false);

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.default_web_client_id))
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        CredentialManager credentialManager = CredentialManager.create(this);

        Executor executor = ContextCompat.getMainExecutor(this);

        CancellationSignal cancellationSignal = new CancellationSignal();

        credentialManager.getCredentialAsync(
                this, // context
                request,
                cancellationSignal, // can be null if no cancellation support needed
                executor,
                new CredentialManagerCallback<>() {
                    @Override
                    public void onResult(GetCredentialResponse getCredentialResponse) {
                        handleSignIn_GOOGLE(getCredentialResponse.getCredential());
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Log.e("TAG", "Sign-in error", e);
                        loadingView.setVisibility(GONE);
                        googleSignInButton.setEnabled(true);
                        Toast.makeText(LoginRegister.this, "Error: Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }


    private void handleSignIn_GOOGLE(Credential credential) {
        // Check if credential is of type Google ID
        Log.d("TAG", "handleSignIn: " + credential.getType());
        if (credential instanceof CustomCredential
                && credential.getType().equals("com.google.android.libraries.identity.googleid.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL")) {
            // Create Google ID Token
            Bundle credentialData = credential.getData();
            GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credentialData);

            // Sign in to Firebase with using the token
            firebaseAuth_GOOGLe(googleIdTokenCredential.getIdToken());
        } else {
            Log.w("TAG", "Credential is not of type Google ID!");
        }
    }


    private void firebaseAuth_GOOGLe(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FIREBASE_AUTH.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithCredential:success");
                        startActivity(
                                new Intent(this, EditAccountInfo.class)
                                        .putExtra(SIGN_IN_TYPE, SIGN_IN_TYPE_GOOGLE)
                        );
                        finish();
                    }
                    else {
                        // If sign in fails, display a message to the user
                        Log.e("TAG", "signInWithCredential:failure", task.getException());
                        new CustomAlertDialogue(this).showAlert(
                                "Something went wrong.",
                                "Please ensure you are connected to the internet and at least one Google account is signed in on this device.",
                                "Ok"
                        );
                    }

                    loadingView.setVisibility(GONE);
                });
    }


    private void beginSignIn_PHONE() {

        startActivity(new Intent(this, PhoneNumberAuthentication.class));

    }



}
