package com.example.gsmakkar1.kitaab;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressDialog mprogress;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseChat;

    final ArrayList<String> chatroomBook = new ArrayList<>();
    final ArrayList<String> chatID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Chat Room");
        setContentView(R.layout.activity_chat);

        mprogress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = DatabaseUtility.getDatabase().getReference().child("Users");
        mDatabaseChat = DatabaseUtility.getDatabase().getReference().child("Chatrooms");

        mDatabase.keepSynced(true);
        mDatabaseChat.keepSynced(true);

        final DatabaseReference chatRooms = mDatabase.child(mAuth.getCurrentUser().getUid()).child("UserChatrooms");
        Query query = chatRooms.orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                chatroomBook.clear();
                chatID.clear();
                TextView notFound = (TextView) findViewById(R.id.noChatFound);
                if(dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot chatRooms : dataSnapshot.getChildren()) {
                        String chatRoomName = chatRooms.getValue().toString();

                        chatroomBook.add(chatRoomName.substring(chatRoomName.indexOf("=") + 1, chatRoomName.length() - 1));
                        chatID.add(chatRooms.getKey());
                    }

                    ListView listview = (ListView) findViewById(R.id.chatroomListview);
                    ChatroomsCustomListAdapter lca = new ChatroomsCustomListAdapter(ChatActivity.this, chatroomBook, chatID);
                    listview.setAdapter(lca);
                    listview.bringToFront();
                    notFound.setVisibility(TextView.INVISIBLE);
                } else {
                    notFound.bringToFront();
                    notFound.setVisibility(TextView.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, databaseError.getDetails(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onBackPressed(){
        Intent loginIntent = new Intent(this, MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }
}
