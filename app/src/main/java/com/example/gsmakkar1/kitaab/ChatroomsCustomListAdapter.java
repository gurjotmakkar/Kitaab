package com.example.gsmakkar1.kitaab;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatroomsCustomListAdapter extends BaseAdapter {
    private ArrayList<String> chatTitles;
    private  ArrayList<String> chatIDs;
    private Context context;

    public ChatroomsCustomListAdapter(Context c, ArrayList<String> ct, ArrayList<String> ci) {
        super();
        chatTitles = ct;
        chatIDs = ci;
        context = c;
    }

    @Override
    public int getCount() {
        return chatTitles.size();
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

        row = inflater.inflate(R.layout.activity_chatrooms_custom_list_adapter, null);

        TextView textView = (TextView) row.findViewById(R.id.chatroomListLabel);
        textView.setText(chatTitles.get(position));
        TextView cover = (TextView) row.findViewById(R.id.chatroomListIcon);
        cover.setText(chatTitles.get(position).substring(0, 1).toUpperCase());

        row.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ChatChannel.class);
                i.putExtra("URL", chatTitles.get(position));
                i.putExtra("channelID", chatIDs.get(position));
                context.startActivity(i);
            }
        });

        return row;
    }
}