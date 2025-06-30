package com.krizyo.chatly.Util.FirebaseUtil;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

public class FirebaseMethod {

    public static String currentUserID(){
        return FirebaseAuth.getInstance().getUid();
    }
    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserID());
    }

    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static String getChatRoomID(String userID1,String userID2){
        if (userID1.hashCode()<userID2.hashCode()){
            return userID1+"_"+userID2;
        }
        else {
            return userID2+"_"+userID1;
        }
    }

    public static CollectionReference allChatRoomsCollectionReference() {
        return FirebaseFirestore.getInstance().collection("chat_rooms");
    }

    public static DocumentReference getChatRoomReference(String chatRoomID){
        return FirebaseFirestore.getInstance().collection("chat_rooms").document(chatRoomID);
    }

    public static CollectionReference getChatRoomMessageCollectionReference(String chatRoomID){
        return getChatRoomReference(chatRoomID).collection("chats");
    }

    public static DocumentReference getOtherUserFromChatRoom(List<String> usersIDs){
        if (usersIDs.get(0).equals(FirebaseMethod.currentUserID())){
            return allUserCollectionReference().document(usersIDs.get(1));
        }else {
            return allUserCollectionReference().document(usersIDs.get(0));
        }
    }

    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference getProfileStorageReference(){
        return FirebaseStorage.getInstance().getReference().child("profile_image").child(FirebaseMethod.currentUserID());
    }

    public static StorageReference getOtherProfileStorageReference(String userID){
        return FirebaseStorage.getInstance().getReference().child("profile_image").child(userID);
    }

    public static boolean isLoggedIn(){
        if(currentUserID()!=null){
            return true;
        }
        return false;
    }

}
