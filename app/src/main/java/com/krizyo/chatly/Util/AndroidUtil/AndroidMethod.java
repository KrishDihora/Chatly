package com.krizyo.chatly.Util.AndroidUtil;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.krizyo.chatly.Model.User;

public class AndroidMethod {

    public static void showToast(Context context,String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void passUserModelAsIntent(Intent intent, User model){
        intent.putExtra("username",model.getUsername());
        intent.putExtra("mobileNumber",model.getMobileNumber());
        intent.putExtra("userID",model.getUserID());
    }

    public static User getUserModelAsIntent(Intent intent){
        User userModel = new User();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setMobileNumber(intent.getStringExtra("mobileNumber"));
        userModel.setUserID(intent.getStringExtra("userID"));
        return userModel;
    }

    public static void setProfileImage(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}
