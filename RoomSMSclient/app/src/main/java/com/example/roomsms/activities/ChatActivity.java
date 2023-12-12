package com.example.roomsms.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    ListView chatList;
    TextView roomNameLabel;
    TextView membersCountLabel;
    EditText messageBox;
    ImageView sendMessageButton;
    ImageView settingsButton;
    ArrayList<ChatModel> chatArray;
    ChatListViewAdapter adapter;
    int userId;
    RoomModel currentRoom;
    String role;
    HubConnector hubConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if(extras == null)
        {
            Log.e("Chat - onCreate","Bundles Failed");
            onBackPressed();  //this finishes the activity by itself
        }

        setContentView(R.layout.activity_chat);

        hubConnection = new HubConnector("/app");
        if(!hubConnection.start()) {
            Log.e("Chat - onCreate","Failed to Connect");
            onBackPressed();
        }

        userId = extras.getInt("UserId");
        currentRoom = new RoomModel();
        currentRoom.setId(extras.getInt("RoomId"));
        currentRoom.setName(extras.getString("RoomName"));

        roomNameLabel = findViewById(R.id.RoomName);
        membersCountLabel = findViewById(R.id.MembersCount);
        messageBox = findViewById(R.id.ChatBox);
        sendMessageButton = findViewById(R.id.SendTextButton);
        settingsButton = findViewById(R.id.ChatSettingsButton);
        chatList = findViewById(R.id.ChatList);

        roomNameLabel.setText(currentRoom.getName());
        membersCountLabel.setText("2");

        chatArray = new ArrayList<ChatModel>();

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

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettingsActivity();
            }
        });

    }

    private void addChatMessage(String message) {
        chatArray.add(new ChatModel(message, userId, Calendar.getInstance().getTime()));
        chatList.setAdapter(adapter);
    }

    private void openSettingsActivity()
    {
        Intent intent = new Intent(this, ChatSettingsActivity.class);
        intent.putExtra("RoomId", currentRoom.getId());
        intent.putExtra("Role", role);
        startActivity(intent);
    }

    private void initialElementConnection() {
        ExecutorService service = Executors.newSingleThreadExecutor();

        service.execute(() -> {

        });
    }

    private void makeToast(String string) {
        Toast.makeText(ChatActivity.this, string, Toast.LENGTH_LONG).show();
    }
}