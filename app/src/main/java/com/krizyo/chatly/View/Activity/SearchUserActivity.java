package com.krizyo.chatly.View.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.krizyo.chatly.Adapter.SearchUserRecyclerAdapter;
import com.krizyo.chatly.Model.User;
import com.krizyo.chatly.R;
import com.krizyo.chatly.Util.AndroidUtil.AndroidMethod;
import com.krizyo.chatly.Util.FirebaseUtil.FirebaseMethod;
import com.krizyo.chatly.databinding.ActivitySearchUserBinding;

public class SearchUserActivity extends AppCompatActivity {

    ActivitySearchUserBinding binding;
    SearchUserRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchUserActivity.this, MainActivity.class));
                finish();
            }
        });

        binding.usernameEditText.requestFocus();

        binding.searchUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String serachTerm = binding.usernameEditText.getText().toString();

                if (serachTerm.isEmpty()){
                    AndroidMethod.showToast(SearchUserActivity.this,"Invalid username");
                    return;
                }

                setUpSearchRecyclerView(serachTerm);

            }
        });

    }

    void setUpSearchRecyclerView(String searchTerm){

        Query query = FirebaseMethod.allUserCollectionReference().whereGreaterThanOrEqualTo("username",searchTerm);

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query,User.class).build();

        adapter = new SearchUserRecyclerAdapter(options,SearchUserActivity.this);
        binding.searchUserRecyclerView.setLayoutManager(new LinearLayoutManager(SearchUserActivity.this));
        binding.searchUserRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null){
            adapter.startListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null){
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}