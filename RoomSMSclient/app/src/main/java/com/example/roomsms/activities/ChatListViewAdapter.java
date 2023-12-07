package com.example.roomsms.activities;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.roomsms.R;

import java.util.ArrayList;

public class ChatListViewAdapter  extends ArrayAdapter<ChatModel> {

    ArrayList<ChatModel> items;
    Context context;
    public ChatListViewAdapter(Context context, ArrayList<ChatModel> items) {
        super(context, R.layout.chat_row, items);
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.chat_row, null);

            TextView senderName = convertView.findViewById(R.id.SenderName);
            senderName.setText(String.valueOf(items.get(position).getSenderId()));

            TextView dateText = convertView.findViewById(R.id.Date);
            dateText.setText(items.get(position).getDateString());

            TextView message = convertView.findViewById(R.id.ChatMessage);
            message.setText(items.get(position).getMessage());

            return convertView;
        }

        TextView senderName = convertView.findViewById(R.id.SenderName);
        senderName.setText(String.valueOf(items.get(position).getSenderId()));

        TextView dateText = convertView.findViewById(R.id.Date);
        dateText.setText(items.get(position).getDateString());

        TextView message = convertView.findViewById(R.id.ChatMessage);
        message.setText(items.get(position).getMessage());

        return convertView;
    }
}
