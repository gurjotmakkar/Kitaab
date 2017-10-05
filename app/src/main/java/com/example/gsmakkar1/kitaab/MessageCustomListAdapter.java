package com.example.gsmakkar1.kitaab;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageCustomListAdapter extends BaseAdapter {
    private ArrayList<String> userNames;
    private ArrayList<String> messages;
    private Context context;

    public MessageCustomListAdapter(Context c, ArrayList<String> un, ArrayList<String> m){
        super();
        userNames = un;
        messages = m;
        context = c;
    }

    @Override
    public int getCount() {
        return userNames.size();
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

        row = inflater.inflate(R.layout.activity_message_custom_list_adapter, null);


        TextView textView = (TextView) row.findViewById(R.id.messageUser);
        textView.setText(userNames.get(position) == null ? null : userNames.get(position).substring(0, 1));
        TextView username = (TextView) row.findViewById(R.id.messageUsername);
        username.setText(userNames.get(position) == null ? null : userNames.get(position));
        TextView cover = (TextView) row.findViewById(R.id.messageBody);
        cover.setText(messages.get(position) == null ? null : messages.get(position));

        return row;
    }
}
