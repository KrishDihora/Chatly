package com.krizyo.chatly.View.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.krizyo.chatly.R;
import com.krizyo.chatly.Util.AndroidUtil.AndroidMethod;
import com.krizyo.chatly.Util.Constant;
import com.krizyo.chatly.Util.FirebaseUtil.FirebaseMethod;
import com.krizyo.chatly.View.Fragment.ChatFragment;
import com.krizyo.chatly.View.Fragment.ProfileFragment;
import com.krizyo.chatly.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.serachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SearchUserActivity.class));
            }
        });

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getTitle().equals(Constant.CHAT)){
                    loadFragment(new ChatFragment());
                } else if (item.getTitle().equals(Constant.PROFILE)) {
                    loadFragment(new ProfileFragment());
                }else {
                    //---
                }
                return true;
            }
        });
        binding.bottomNavigation.setSelectedItemId(R.id.chat);

        getFCMToken();

        // On backed pressed
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                
                FragmentManager fm = getSupportFragmentManager();

                if (fm.getBackStackEntryCount()>1){
                    fm.popBackStackImmediate();

                    Fragment currentFragment = fm.findFragmentById(R.id.frame_layout);

                    if (currentFragment instanceof ChatFragment){
                        binding.bottomNavigation.setSelectedItemId(R.id.chat);
                    } else if (currentFragment instanceof ProfileFragment) {
                        binding.bottomNavigation.setSelectedItemId(R.id.profile);
                    }

                } else{
                    finish();
                }

            }
        });

    }

    void loadFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();

        //Find fragment by ID
        //Fragment existingFragment = fm.findFragmentById(R.id.frame_layout);

        //Find fragment by Tag
        String fragmentTag = fragment.getClass().getName();
        Fragment existingFragment = fm.findFragmentByTag(fragmentTag);

        if (existingFragment == null || !existingFragment.isAdded()){

            FragmentTransaction ft = fm.beginTransaction();

            ft.replace(R.id.frame_layout,fragment,fragmentTag);
            ft.addToBackStack(null);
            ft.commit();
        }

    }

    void getFCMToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        FirebaseMethod.currentUserDetails().update("FCMToken",token);
                    }
                });
    }




}