package com.krizyo.chatly.View.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.krizyo.chatly.Adapter.RecentChatRecyclerAdapter;
import com.krizyo.chatly.Adapter.SearchUserRecyclerAdapter;
import com.krizyo.chatly.Model.ChatRoom;
import com.krizyo.chatly.Model.User;
import com.krizyo.chatly.Util.FirebaseUtil.FirebaseMethod;
import com.krizyo.chatly.View.Activity.SearchUserActivity;
import com.krizyo.chatly.databinding.FragmentChatBinding;

public class ChatFragment extends Fragment {

    FragmentChatBinding binding;
    RecentChatRecyclerAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater);

        setUpRecentChatRecyclerView();

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    void setUpRecentChatRecyclerView(){
        Query query = FirebaseMethod.allChatRoomsCollectionReference().whereArrayContains("userIDs",FirebaseMethod.currentUserID())
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatRoom> options = new FirestoreRecyclerOptions.Builder<ChatRoom>()
                .setQuery(query,ChatRoom.class).build();

        adapter = new RecentChatRecyclerAdapter(options, getContext());
        binding.recentChatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recentChatRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null){
            adapter.startListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null){
            adapter.startListening();
            adapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}