package com.krizyo.chatly.View.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.krizyo.chatly.Model.User;
import com.krizyo.chatly.Util.AndroidUtil.AndroidMethod;
import com.krizyo.chatly.Util.FirebaseUtil.FirebaseMethod;
import com.krizyo.chatly.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(FirebaseMethod.isLoggedIn() && getIntent().getExtras()!= null){
             String userID = getIntent().getExtras().getString("userID");

             FirebaseMethod.allUserCollectionReference().document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                 @Override
                 public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                     User userModel = task.getResult().toObject(User.class);

                     Intent main = new Intent(SplashActivity.this,MainActivity.class);
                     main.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                     startActivity(main);

                     Intent chat = new Intent(SplashActivity.this, ChatActivity.class);
                     chat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                     AndroidMethod.passUserModelAsIntent(chat,userModel);
                     startActivity(chat);
                     finish();

                     //Can't add activity to back directly like fragment.
                     //getSupportFragmentManager().beginTransaction().addToBackStack(null).commit();
                 }
             });

        }else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (FirebaseMethod.isLoggedIn()){
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }
                    finish();
                }
            },2000);

        }

    }
}