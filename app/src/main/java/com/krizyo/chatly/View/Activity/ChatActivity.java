package com.krizyo.chatly.View.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.krizyo.chatly.Adapter.ChatMessageRecyclerAdapter;
import com.krizyo.chatly.Adapter.SearchUserRecyclerAdapter;
import com.krizyo.chatly.Model.ChatMessage;
import com.krizyo.chatly.Model.ChatRoom;
import com.krizyo.chatly.Model.User;
import com.krizyo.chatly.R;
import com.krizyo.chatly.Util.AndroidUtil.AndroidMethod;
import com.krizyo.chatly.Util.FirebaseUtil.FirebaseMethod;
import com.krizyo.chatly.databinding.ActivityChatBinding;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    User userModel;
    String chatRoomID;
    ChatRoom chatRoomModel;
    ChatMessageRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        userModel = AndroidMethod.getUserModelAsIntent(getIntent());

        /*FirebaseMethod.getOtherProfileStorageReference(userModel.getUserID()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri uri = task.getResult();
                    AndroidMethod.setProfileImage(ChatActivity.this,uri,binding.profileImageLayout.profileImageView);
                }
            }
        });*/

        binding.usernameText.setText(userModel.getUsername());

        chatRoomID = FirebaseMethod.getChatRoomID(FirebaseMethod.currentUserID(),userModel.getUserID());

        getOrCreateChatRoomModel();

        setUpChatMessageRecyclerView();

        binding.sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.messageEditText.getText().toString().trim();

                if (message.isEmpty())
                    return;

                sendMessageToUser(message);
            }
        });
    }

    void getOrCreateChatRoomModel(){
        FirebaseMethod.getChatRoomReference(chatRoomID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    chatRoomModel = task.getResult().toObject(ChatRoom.class);

                    if (chatRoomModel==null){
                        chatRoomModel = new ChatRoom(
                                chatRoomID,
                                Arrays.asList(FirebaseMethod.currentUserID(),userModel.getUserID()),
                                Timestamp.now(),
                                ""
                        );

                        FirebaseMethod.getChatRoomReference(chatRoomID).set(chatRoomModel);
                    }
                }
            }
        });
    }

    void sendMessageToUser(String message){

        chatRoomModel.setLastMessageTimestamp(Timestamp.now());
        chatRoomModel.setLastMessageSenderID(FirebaseMethod.currentUserID());
        chatRoomModel.setLastMessage(message);

        FirebaseMethod.getChatRoomReference(chatRoomID).set(chatRoomModel);

        ChatMessage chatMessageModel = new ChatMessage(message,FirebaseMethod.currentUserID(),Timestamp.now());
        FirebaseMethod.getChatRoomMessageCollectionReference(chatRoomID).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful())
                    binding.messageEditText.setText("");

                sendNotification(message);
            }
        });

    }

    void setUpChatMessageRecyclerView(){
        Query query = FirebaseMethod.getChatRoomMessageCollectionReference(chatRoomID).orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query,ChatMessage.class).build();

        adapter = new ChatMessageRecyclerAdapter(options,ChatActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        //linearLayoutManager.setReverseLayout(true);
        binding.chatMessageRecyclerView.setLayoutManager(linearLayoutManager);
        binding.chatMessageRecyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                binding.chatMessageRecyclerView.smoothScrollToPosition(0);
            }
        });
    }

    void sendNotification(String message){

    }
}