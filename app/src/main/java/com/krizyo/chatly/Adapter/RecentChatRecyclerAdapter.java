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
import com.google.firebase.firestore.DocumentSnapshot;
import com.krizyo.chatly.Model.ChatRoom;
import com.krizyo.chatly.Model.User;
import com.krizyo.chatly.R;
import com.krizyo.chatly.Util.AndroidUtil.AndroidMethod;
import com.krizyo.chatly.Util.FirebaseUtil.FirebaseMethod;
import com.krizyo.chatly.View.Activity.ChatActivity;
import com.krizyo.chatly.databinding.RecentChatRecyclerRowBinding;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatRoom,RecentChatRecyclerAdapter.ViewHolder> {
    Context context;
    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoom> options,Context context) {
        super(options);
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row,parent,false)) ;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ChatRoom model) {
        FirebaseMethod.getOtherUserFromChatRoom(model.getUserIDs()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            boolean lastMessageSendByMe = model.getLastMessageSenderID().equals(FirebaseMethod.currentUserID());

                            User userModel = task.getResult().toObject(User.class);

                            /*FirebaseMethod.getOtherProfileStorageReference(userModel.getUserID()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()){
                                        Uri uri = task.getResult();
                                        AndroidMethod.setProfileImage(context,uri,holder.profile_image_view);
                                    }
                                }
                            });*/

                            holder.binding.usernameText.setText(userModel.getUsername());

                            if (lastMessageSendByMe)
                                holder.binding.lastMessageText.setText("You: " + model.getLastMessage());
                            else
                                holder.binding.lastMessageText.setText(model.getLastMessage());

                            holder.binding.lastMessageTimeText.setText(FirebaseMethod.timestampToString(model.getLastMessageTimestamp()));

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent chat = new Intent(context, ChatActivity.class);
                                    chat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    AndroidMethod.passUserModelAsIntent(chat,userModel);
                                    context.startActivity(chat);
                                }
                            });

                        }
                    }
                });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_image_view;
        RecentChatRecyclerRowBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RecentChatRecyclerRowBinding.bind(itemView);
            profile_image_view = itemView.findViewById(R.id.profile_image_view);
        }
    }
}

