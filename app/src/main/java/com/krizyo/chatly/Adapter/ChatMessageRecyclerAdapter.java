package com.krizyo.chatly.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.krizyo.chatly.Model.ChatMessage;
import com.krizyo.chatly.R;
import com.krizyo.chatly.Util.AndroidUtil.AndroidMethod;
import com.krizyo.chatly.Util.FirebaseUtil.FirebaseMethod;
import com.krizyo.chatly.View.Activity.ChatActivity;
import com.krizyo.chatly.databinding.ChatMessageRecyclerRowBinding;

public class ChatMessageRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessage,ChatMessageRecyclerAdapter.ViewHolder> {
    Context context;
    public ChatMessageRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessage> options,Context context) {
        super(options);
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row,parent,false)) ;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ChatMessage model) {
        if (model.getSenderID().equals(FirebaseMethod.currentUserID())){
            holder.binding.leftLinearLayout.setVisibility(View.GONE);
            holder.binding.rightLinearLayout.setVisibility(View.VISIBLE);
            holder.binding.rightText.setText(model.getMessage());
        }else {
            holder.binding.leftLinearLayout.setVisibility(View.VISIBLE);
            holder.binding.rightLinearLayout.setVisibility(View.GONE);
            holder.binding.leftText.setText(model.getMessage());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ChatMessageRecyclerRowBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChatMessageRecyclerRowBinding.bind(itemView);
        }
    }
}
