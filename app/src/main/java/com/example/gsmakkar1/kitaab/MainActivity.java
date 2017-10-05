package com.example.gsmakkar1.kitaab;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private ProgressDialog mprogress;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseChat;

    final static int REQUEST_CODE = 1;

    final ArrayList<String> bookTitles = new ArrayList<>();
    final ArrayList<String> bookLinks = new ArrayList<>();
    final ArrayList<String> bookKeys = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Library");
        setContentView(R.layout.activity_main);

        mprogress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = DatabaseUtility.getDatabase().getReference().child("Users");
        mDatabaseChat = DatabaseUtility.getDatabase().getReference().child("Chatrooms");

        mDatabase.keepSynced(true);
        mDatabaseChat.keepSynced(true);

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    //loadBooksToLibrary();

                    DatabaseReference  books = mDatabase.child(mAuth.getCurrentUser().getUid()).child("Books");
                    Query query = books.orderByKey();

                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            bookTitles.clear();
                            bookLinks.clear();
                            bookKeys.clear();
                            TextView notFound = (TextView) findViewById(R.id.noBookFound);
                            if (dataSnapshot.getChildrenCount() > 0) {
                                for (DataSnapshot books : dataSnapshot.getChildren()) {
                                    Book book = books.getValue(Book.class);

                                    bookTitles.add(book.getTitle());
                                    bookLinks.add(book.getLink());
                                    bookKeys.add(books.getKey());
                                }

                                ListView listview = (ListView) findViewById(R.id.libraryList);
                                LibraryCustomListAdapter lca = new LibraryCustomListAdapter(MainActivity.this, bookTitles, bookLinks, bookKeys);
                                listview.setAdapter(lca);
                                listview.bringToFront();
                                notFound.setVisibility(TextView.INVISIBLE);
                                lca.notifyDataSetChanged();
                            } else {
                                notFound.bringToFront();
                                notFound.setVisibility(TextView.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(MainActivity.this, databaseError.getDetails(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    query.removeEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            bookTitles.clear();
                            bookLinks.clear();
                            bookKeys.clear();
                            TextView notFound = (TextView) findViewById(R.id.noBookFound);
                            if (dataSnapshot.getChildrenCount() > 0) {
                                for (DataSnapshot books : dataSnapshot.getChildren()) {
                                    Book book = books.getValue(Book.class);

                                    bookTitles.add(book.getTitle());
                                    bookLinks.add(book.getLink());
                                    bookKeys.add(books.getKey());
                                }

                                ListView listview = (ListView) findViewById(R.id.libraryList);
                                LibraryCustomListAdapter lca = new LibraryCustomListAdapter(MainActivity.this, bookTitles, bookLinks, bookKeys);
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
                            Toast.makeText(MainActivity.this, databaseError.getDetails(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

        Button chatButton = (Button) findViewById(R.id.libraryChatButton);
        chatButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean initiateUploading() {
        mprogress.setMessage("Uploading book");
        mprogress.show();
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent, REQUEST_CODE);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                final Uri uri = resultData.getData();
                try {
                    final String title = getMetaData(uri);

                    final String uniqueID = Integer.toString(title.trim().hashCode());

                    StorageReference filepath = mStorageRef.child(mAuth.getCurrentUser().getUid()).child(uniqueID);
                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            DatabaseReference booksRef = mDatabase.child(mAuth.getCurrentUser().getUid()).child("Books");
                            booksRef.child(uniqueID).child("title").setValue(title.substring(0, title.indexOf('.')));
                            booksRef.child(uniqueID).child("link").setValue(taskSnapshot.getDownloadUrl().toString());

                            DatabaseReference userChatroomRef = mDatabase.child(mAuth.getCurrentUser().getUid()).child("UserChatrooms");
                            userChatroomRef.child(uniqueID).child("name").setValue(title.substring(0, title.indexOf('.')));


                            DatabaseReference chatroomRef = FirebaseDatabase.getInstance().getReference().child("Chatrooms");
                            chatroomRef.child(uniqueID).child("book").setValue(title.substring(0, title.indexOf('.')));

                            DatabaseReference chatroomUserRef = mDatabaseChat.child(uniqueID).child("Users");
                            chatroomUserRef.child(mAuth.getCurrentUser().getUid()).child("user").setValue(mAuth.getCurrentUser().getEmail().substring(0, mAuth.getCurrentUser().getEmail().indexOf("@")));
                            chatroomUserRef.child(mAuth.getCurrentUser().getUid()).child("email").setValue(mAuth.getCurrentUser().getEmail());

                            mprogress.dismiss();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                mprogress.dismiss();
                //Toast.makeText(this, "result data is null", Toast.LENGTH_LONG).show();
            }
        }else{
            mprogress.dismiss();
            //Toast.makeText(this, "request code and result code not good", Toast.LENGTH_LONG).show();
        }
    }

    public String getMetaData(Uri uri) {
        Cursor cursor = this.getContentResolver().query(uri, null, null, null, null, null);
        String displayName = "";
        try {
            if (cursor != null && cursor.moveToFirst()) {
                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                //Toast.makeText(this, displayName, Toast.LENGTH_LONG).show();
            }
        } finally {
            cursor.close();
        }
        return displayName;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.upload:
                initiateUploading();
                return true;
            case R.id.logout:
                mAuth.signOut();
                return true;
            case R.id.settings:
                Intent loginIntent = new Intent(this, AboutActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
