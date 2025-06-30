package com.krizyo.chatly.View.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.krizyo.chatly.Model.User;
import com.krizyo.chatly.R;
import com.krizyo.chatly.Util.AndroidUtil.AndroidMethod;
import com.krizyo.chatly.Util.FirebaseUtil.FirebaseMethod;
import com.krizyo.chatly.databinding.ActivityLoginBinding;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth auth=FirebaseAuth.getInstance();

    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    Long timeoutSeconds = 60L;
    User userModel;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.mobileNumberEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    binding.countryCodePicker.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.code_picker_focused_round_corner));
                }
                return false;
            }
        });

        binding.countryCodePicker.registerCarrierNumberEditText(binding.mobileNumberEditText);

        binding.sendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.countryCodePicker.isValidFullNumber()){
                    //binding.mobileNumberEditText.setError("Mobile number is not valid");
                    Toast.makeText(LoginActivity.this, "Enter valid mobile number", Toast.LENGTH_SHORT).show();
                    return;
                }

                binding.llLogin.setVisibility(View.GONE);
                binding.llLoginOtp.setVisibility(View.VISIBLE);

                sendOTP(binding.countryCodePicker.getFullNumberWithPlus(),false);

            }
        });

        binding.verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredOTP = binding.otpEditText.getText().toString();

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode,enteredOTP);
                signIn(credential);
            }
        });

        binding.resendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOTP(binding.countryCodePicker.getFullNumberWithPlus(),true);
            }
        });

        binding.letMeInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUsername();
            }
        });


    }

    void sendOTP(String phoneNumber,boolean isResend){
        verifyProgress(true);

        startResendTimer();

        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(LoginActivity.this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                verifyProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                AndroidMethod.showToast(LoginActivity.this,"OTP verification failed");
                                verifyProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode=s;
                                resendingToken=forceResendingToken;
                                AndroidMethod.showToast(LoginActivity.this,"OTP has been sent");
                                verifyProgress(false);
                            }

                        });

        if (isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    void signIn(PhoneAuthCredential phoneAuthCredential) {
        verifyProgress(true);
        auth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                verifyProgress(false);
                if (task.isSuccessful()){
                    binding.llLoginOtp.setVisibility(View.GONE);
                    binding.llLoginUsername.setVisibility(View.VISIBLE);

                    AndroidMethod.showToast(LoginActivity.this,"Authentication Successful");

                    getUsername();

                }else {
                    AndroidMethod.showToast(LoginActivity.this,"OTP verification failed");
                }
            }
        });
    }

    @SuppressLint("DiscouragedApi")
    void startResendTimer(){
        binding.resendOtpBtn.setEnabled(false);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                runOnUiThread(()->{binding.resendOtpBtn.setText("Resend OTP in " + timeoutSeconds + "second");});

                if (timeoutSeconds<=0){
                    timeoutSeconds=60L;
                    timer.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.resendOtpBtn.setEnabled(true);
                            binding.resendOtpBtn.setText("Resend OTP");
                        }
                    });
                }
            }
        },0,1000);

    }

    void verifyProgress(boolean isProgress){
        if(isProgress){
            binding.verifyProgress.setVisibility(View.VISIBLE);
            binding.verifyText.setVisibility(View.GONE);
        }else {
            binding.verifyProgress.setVisibility(View.GONE);
            binding.verifyText.setVisibility(View.VISIBLE);
        }
    }

    void getUsername(){
        letMeInProgress(true);

        FirebaseMethod.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                letMeInProgress(false);

                if (task.isSuccessful()){
                    userModel = task.getResult().toObject(User.class);

                    if (userModel!=null){
                        binding.usernameEditText.setText(userModel.getUsername());
                    }
                }
            }
        });
    }

    void setUsername(){
        String username = binding.usernameEditText.getText().toString();

        if (username.isEmpty()){
            AndroidMethod.showToast(this,"Username is empty");
            return;
        }

        letMeInProgress(true);

        if (userModel!=null){
            userModel.setUsername(username);
        }else {
            userModel = new User(binding.countryCodePicker.getFullNumberWithPlus(),username, Timestamp.now(),FirebaseMethod.currentUserID());
        }

        FirebaseMethod.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                letMeInProgress(false);
                if (task.isSuccessful()){
                    Intent main = new Intent(LoginActivity.this, MainActivity.class);
                    main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(main);
                    finish();
                }
            }
        });
    }

    void letMeInProgress(boolean isProgress){
        if(isProgress){
            binding.letMeInProgress.setVisibility(View.VISIBLE);
            binding.letMeInText.setVisibility(View.GONE);
        }else {
            binding.letMeInProgress.setVisibility(View.GONE);
            binding.letMeInText.setVisibility(View.VISIBLE);
        }
    }
}