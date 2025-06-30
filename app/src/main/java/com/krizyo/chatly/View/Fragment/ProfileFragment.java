package com.krizyo.chatly.View.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.UploadTask;
import com.krizyo.chatly.Model.User;
import com.krizyo.chatly.Util.AndroidUtil.AndroidMethod;
import com.krizyo.chatly.Util.FirebaseUtil.FirebaseMethod;
import com.krizyo.chatly.View.Activity.SplashActivity;
import com.krizyo.chatly.databinding.FragmentProfileBinding;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    User userModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK){
                    Intent data = result.getData();
                    if (data != null && data.getData() !=null){
                        selectedImageUri = data.getData();
                        AndroidMethod.setProfileImage(getContext(),selectedImageUri,binding.profileImage);
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater);

        getUserData();

        binding.profileUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newUsername = binding.profileUsernameEditText.getText().toString();

                if (newUsername.isEmpty()){
                    AndroidMethod.showToast(getContext(),"Username is empty");
                    return;
                }

                userModel.setUsername(newUsername);
                profileProgress(true);

                /*if (selectedImageUri != null){
                    FirebaseMethod.getProfileStorageReference().putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            updateProfileToFirebase();
                        }
                    });
                }else {
                    updateProfileToFirebase();
                }*/

                updateProfileToFirebase();

            }
        });

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseMethod.logout();
                        Intent intent = new Intent(getContext(), SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        requireActivity().finish();

                    }
                });

            }
        });

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(ProfileFragment.this).cropSquare().compress(512).maxResultSize(512,512)
                        .createIntent(new Function1<Intent, Unit>() {
                            @Override
                            public Unit invoke(Intent intent) {
                                imagePickLauncher.launch(intent);
                                return null;
                            }
                        });
            }
        });

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    void getUserData(){
        profileProgress(true);

        /*FirebaseMethod.getProfileStorageReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                profileProgress(false);

                if (task.isSuccessful()){
                    Uri uri = task.getResult();
                    AndroidMethod.setProfileImage(getContext(),uri,binding.profileImage);
                }
            }
        });*/

        FirebaseMethod.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                profileProgress(false);
                userModel = task.getResult().toObject(User.class);

                binding.profileUsernameEditText.setText(userModel.getUsername());
                binding.profileMobileNumberEditText.setText(userModel.getMobileNumber());
            }
        });
    }

    void profileProgress(boolean isProgress){
        if(isProgress){
            binding.profileUpdateProgress.setVisibility(View.VISIBLE);
            binding.profileUpdateText.setVisibility(View.GONE);
        }else {
            binding.profileUpdateProgress.setVisibility(View.GONE);
            binding.profileUpdateText.setVisibility(View.VISIBLE);
        }
    }

    void updateProfileToFirebase(){
        FirebaseMethod.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                profileProgress(false);
                if (task.isSuccessful()){
                    AndroidMethod.showToast(getContext(),"Updated successfully");
                }else {
                    AndroidMethod.showToast(getContext(),"Update failed");
                }
            }
        });

    }
}