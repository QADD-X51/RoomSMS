package com.example.roomsms.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roomsms.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    ListView chatList;
    TextView roomNameLabel;
    TextView membersCountLabel;
    EditText messageBox;
    ImageView sendMessageButton;
    ArrayList<ChatModel> chatArray;
    ChatListViewAdapter adapter;
    int userId;
    String roomName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if(extras == null)
        {
            Toast.makeText(ChatActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            onBackPressed();  //this finishes the activity by itself
        }

        setContentView(R.layout.activity_chat);

        userId = extras.getInt("UserId");
        roomName = extras.getString("Room");

        roomNameLabel = findViewById(R.id.RoomName);
        membersCountLabel = findViewById(R.id.MembersCount);
        messageBox = findViewById(R.id.ChatBox);
        sendMessageButton = findViewById(R.id.SendTextButton);
        chatList = findViewById(R.id.ChatList);

        roomNameLabel.setText(roomName);
        membersCountLabel.setText("2");

        chatArray = new ArrayList<ChatModel>();
        switch(roomName){
            case "New Room":
                chatArray.add(new ChatModel("Hello there!", 51, new Date(100000000)));
                chatArray.add(new ChatModel("Supp, m8?", 22, new Date(100010000)));
                chatArray.add(new ChatModel("Not much...", 51, new Date(100025000)));
                break;
            case "Last Room":
                chatArray.add(new ChatModel("Hello", 3, new Date(100340000)));
                chatArray.add(new ChatModel("bye", 44, new Date(100350000)));
                chatArray.add(new ChatModel(">:(", 3, new Date(100355000)));
                break;

            default:
                chatArray.add(new ChatModel("Welcome to the room "+roomName+" !", 0, Calendar.getInstance().getTime()));
        }

        adapter = new ChatListViewAdapter(getApplicationContext(), chatArray);
        chatList.setAdapter(adapter);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = messageBox.getText().toString();
                if(text.length() == 0)
                {
                    Toast.makeText(ChatActivity.this, "Message not sent", Toast.LENGTH_LONG).show();
                    return;
                }
                addChatMessage(text);
                messageBox.setText("");

            }
        });

    }

    private void addChatMessage(String message) {
        chatArray.add(new ChatModel(message, userId, Calendar.getInstance().getTime()));
        chatList.setAdapter(adapter);
    }
}