package com.example.call;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.call.databinding.ActivityMainBinding;
import com.example.call.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CCPCountry;
import com.hbb20.CountryCodePicker;

import java.security.PublicKey;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class registerActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationID = "";
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private CountryCodePicker ccp;
    private CountDownTimer timer;
    private String phoneNumber;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        FirebaseAuth.getInstance().getFirebaseAuthSettings().forceRecaptchaFlowForTesting(false);
        FirebaseAuth.getInstance().getFirebaseAuthSettings()
                .setAppVerificationDisabledForTesting(false);


        ccp = (CountryCodePicker) binding.ccp;
        ccp.registerCarrierNumberEditText(binding.phoneText);

        //mCallBacks function initialization
//
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                Log.d("TAG", "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Log.w("TAG", "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(registerActivity.this, "please enter the valid Number", Toast.LENGTH_SHORT).show();

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(registerActivity.this, "Please try Tomorrow", Toast.LENGTH_SHORT).show();
                }

                binding.continueNextButton.setText("Continue");
                binding.codeLayout.setVisibility(View.GONE);
                binding.phoneAuth.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                binding.continueNextButton.setText("Submit");
                binding.codeLayout.setVisibility(View.VISIBLE);
                binding.phoneAuth.setVisibility(View.GONE);
                phoneNumber =ccp.getFullNumberWithPlus();
                progressDialog.dismiss();

                timer();

                Log.d("TAG", "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationID = verificationId;
                mResendToken = token;
            }
        };

        //getting continue button

        binding.continueNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String button = binding.continueNextButton.getText().toString();
                // for code verifying screen:
                if (button.equals("Submit")) {
                    String code = binding.codeText.getText().toString();
                    if (code.equals("")) {
                        binding.codeText.setError("enter a code please");
                    } else {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationID, code);
                        signInWithPhoneAuthCredential(credential);
                    }

                }
                // for phone number entering screen:
                if (button.equals("Continue")) {

                    String phoneNum = ccp.getFullNumberWithPlus();
                    String phoneText = binding.phoneText.getText().toString();

                    if (phoneText.equals("")) {

                        binding.phoneText.setError("please Enter valid number");

                    } else {
                        String message ="please wait while we are Verfying your Number";
                        String title ="Verifing Your Number";
                        ProgressDialog(title,message);

                        phoneAuthOption(phoneNum);

                    }
                }
            }
        });

        binding.timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.timer.getText().equals("Resend code")){
                    resendVerificationCode(phoneNumber,mResendToken);
                }
            }
        });

    }

    public void phoneAuthOption(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            updateUI();
                        } else {
                            // Sign in failed, display a message and update the UI

                            binding.continueNextButton.setText("Continue");
                            binding.codeLayout.setVisibility(View.GONE);
                            binding.phoneAuth.setVisibility(View.VISIBLE);

                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                binding.codeText.setError("invalid code");
                            }
                        }
                    }
                });

    }

    private void updateUI() {
        Toast.makeText(this, "Verified", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    public void timer(){
        timer =new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long l) {
                String duration =String.format(Locale.ENGLISH,"Resend code in : 00 : %02d"
                ,TimeUnit.MILLISECONDS.toSeconds(l));
                binding.timer.setText(duration);
            }

            @Override
            public void onFinish() {
                binding.timer.setText("Resend code");

            }
        }.start();
    }
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private  void ProgressDialog(String title, String message){
        progressDialog =new ProgressDialog(this);
        progressDialog.setTitle(title);
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            Intent intent =new Intent(registerActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}