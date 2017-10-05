package com.example.gsmakkar1.kitaab;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class LibraryCustomListAdapter extends BaseAdapter {
    private ArrayList<String> bookTitles;
    private ArrayList<String> bookLinks;
    private ArrayList<String> bookKeys;
    private Context context;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseChat;

    public LibraryCustomListAdapter(Context c, ArrayList<String> bt, ArrayList<String> bl, ArrayList<String> bk){
        super();
        bookTitles = bt;
        bookLinks = bl;
        bookKeys = bk;
        context = c;

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chatrooms");

    }

    @Override
    public int getCount() {
        return bookTitles.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        row = inflater.inflate(R.layout.activity_library_custom_list_adapter, null);


        TextView textView = (TextView) row.findViewById(R.id.libraryBookTitle);
        textView.setText(bookTitles.get(position) == null ? null : bookTitles.get(position));
        TextView cover = (TextView) row.findViewById(R.id.libraryBookCover);
        cover.setText(bookTitles.get(position) == null ? null : bookTitles.get(position).substring(0, 1).toUpperCase());

        row.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(bookLinks.get(position)));
                intent.setDataAndType(Uri.parse(bookLinks.get(position)), "application/pdf");
                context.startActivity(intent);
            }
        });

        row.setLongClickable(true);
        row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                new AlertDialog.Builder(context)
                        .setTitle("Delete Book")
                        .setMessage("Are you sure you want to delete this book?\nChatroom will also be deleted!")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String uniqueID = bookKeys.get(position);

                                DatabaseReference booksRef = mDatabase.child(mAuth.getCurrentUser().getUid()).child("Books").child(uniqueID);
                                booksRef.child("link").removeValue();
                                booksRef.child("title").removeValue();

                                DatabaseReference userChatroom = mDatabase.child(mAuth.getCurrentUser().getUid()).child("UserChatrooms").child(uniqueID);
                                userChatroom.child("name").removeValue();

                                DatabaseReference chatroom = mDatabaseChat.child(uniqueID).child("Users").child(mAuth.getCurrentUser().getUid());
                                chatroom.child("email").removeValue();
                                chatroom.child("user").removeValue();

                                StorageReference file = mStorageRef.child(mAuth.getCurrentUser().getUid()).child(uniqueID);
                                file.delete();

                                notifyDataSetChanged();

                                Toast.makeText(context, bookTitles.get(position) + " is deleted!", Toast.LENGTH_LONG).show();
                            }
                        }).setNegativeButton("No", null).show();

                return true;
            }
        });

        return row;
    }
}
