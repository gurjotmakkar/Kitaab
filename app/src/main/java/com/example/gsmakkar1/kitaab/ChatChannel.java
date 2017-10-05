package com.example.gsmakkar1.kitaab;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ChatChannel extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressDialog mprogress;
    private DatabaseReference mDatabase;

    private final ArrayList<String> messages = new ArrayList<>();
    private final ArrayList<String> users = new ArrayList<>();

    private String chatID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Chats");
        setContentView(R.layout.activity_chat_channel);

        Bundle data = getIntent().getExtras();
        chatID = data.get("channelID").toString();

        /* Future expansion of application.
        DatabaseReference connectedRef = DatabaseUtility.getDatabase().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                } else {
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        */

        mprogress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = DatabaseUtility.getDatabase().getReference().child("Chatrooms").child(chatID).child("Chats");

        mDatabase.keepSynced(true);

        Query query = mDatabase.orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                users.clear();
                messages.clear();

                if(dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot chat : dataSnapshot.getChildren()) {
                        Messages m = chat.getValue(Messages.class);

                        users.add(m.getUsername());
                        messages.add(m.getMessage());
                    }

                    final ListView listview = (ListView) findViewById(R.id.messageList);
                    MessageCustomListAdapter lca = new MessageCustomListAdapter(ChatChannel.this, users, messages);
                    listview.setAdapter(lca);

                    listview.post(new Runnable() {
                        @Override
                        public void run() {
                            listview.setSelection(listview.getCount() - 1);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChatChannel.this, databaseError.getDetails(), Toast.LENGTH_SHORT).show();
            }
        });

        final EditText message = (EditText) findViewById(R.id.messageText);
        final String username = mAuth.getCurrentUser().getEmail().substring(0, mAuth.getCurrentUser().getEmail().indexOf("@"));
        ImageButton send = (ImageButton) findViewById(R.id.sendMessageButton);
        send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                sendMessage(username, message.getText().toString());
                message.setText("");
            }
        });

    }

    private void sendMessage(String u, String m) {
        DatabaseReference chatChannel = FirebaseDatabase.getInstance().getReference().child("Chatrooms").child(chatID);
        DatabaseReference chats = chatChannel.child("Chats");
        Calendar c = Calendar.getInstance();
        String uniqueID = Long.toString(c.getTimeInMillis());
        chats.child(uniqueID).child("username").setValue(u);
        chats.child(uniqueID).child("message").setValue(m);
    }

    @Override
    public void onBackPressed(){
        Intent loginIntent = new Intent(this, ChatActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }
}
