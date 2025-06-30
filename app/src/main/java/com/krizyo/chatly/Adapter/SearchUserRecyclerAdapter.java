package com.krizyo.chatly.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.krizyo.chatly.Model.User;
import com.krizyo.chatly.R;
import com.krizyo.chatly.Util.AndroidUtil.AndroidMethod;
import com.krizyo.chatly.Util.FirebaseUtil.FirebaseMethod;
import com.krizyo.chatly.View.Activity.ChatActivity;
import com.krizyo.chatly.databinding.SearchUserRecyclerRowBinding;

public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<User,SearchUserRecyclerAdapter.ViewHolder> {
    Context context;
    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<User> options,Context context) {
        super(options);
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row,parent,false)) ;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull User model) {
        holder.binding.usernameText.setText(model.getUsername());
        holder.binding.mobileNumberText.setText(model.getMobileNumber());

        if (model.getUserID().equals(FirebaseMethod.currentUserID())){
            holder.binding.usernameText.setText(model.getUsername() + "(Me)");
        }

        /*FirebaseMethod.getOtherProfileStorageReference(model.getUserID()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri uri = task.getResult();
                    AndroidMethod.setProfileImage(context,uri,holder.profile_image_view);
                }
            }
        });*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chat = new Intent(context, ChatActivity.class);
                chat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                AndroidMethod.passUserModelAsIntent(chat,model);
                context.startActivity(chat);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_image_view;
        SearchUserRecyclerRowBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SearchUserRecyclerRowBinding.bind(itemView);
            profile_image_view = itemView.findViewById(R.id.profile_image_view);
        }
    }
}
