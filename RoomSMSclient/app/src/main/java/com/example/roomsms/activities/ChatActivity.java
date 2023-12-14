package com.example.roomsms.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.annotations.NonNull;

public class ChatActivity extends AppCompatActivity {

    Handler refreshTopTabHandler;
    Runnable refreshTopTabRunnable;
    final int refreshTopTabDelay = 5 * 1000;
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
    HubConnector hubConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if(extras == null)
        {
            Log.e("Chat - onCreate","Bundles Failed");
            goBackToMainActivity();
        }

        setContentView(R.layout.activity_chat);

        hubConnection = new HubConnector("/app");
        if(!hubConnection.start()) {
            Log.e("Chat - onCreate","Failed to Connect");
            goBackToMainActivity();
        }

        userId = extras.getInt("UserId");
        currentRoom = new RoomModel();
        currentRoom.setId(extras.getInt("RoomId"));
        currentRoom.setName(extras.getString("RoomName"));

        chatArray = new ArrayList<ChatModel>();
        adapter = new ChatListViewAdapter(getApplicationContext(), chatArray);

        roomNameLabel = findViewById(R.id.RoomName);
        membersCountLabel = findViewById(R.id.MembersCount);
        messageBox = findViewById(R.id.ChatBox);
        sendMessageButton = findViewById(R.id.SendTextButton);
        settingsButton = findViewById(R.id.ChatSettingsButton);
        chatList = findViewById(R.id.ChatList);

        refreshTopTabHandler = new Handler();
        initialElementConnection();
        getAllMessages();

        hubConnection.getHubConnection().on("SendMessageToClients", this::updateChat, Integer.class, ChatModel.class);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = messageBox.getText().toString();
                if(text.length() == 0)
                {
                    Toast.makeText(ChatActivity.this, "Message not sent", Toast.LENGTH_LONG).show();
                    return;
                }
                sendChatMessage(text);
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

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Chat - onPause", "Paused");
        refreshTopTabHandler.removeCallbacks(refreshTopTabRunnable);
        hubConnection.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!tryToConnect()) goBackToMainActivity();

        refreshTopTabHandler.postDelayed( refreshTopTabRunnable = () -> {
            Log.i("Chat - Handler", "Called Handler");
            initialElementConnection();
            refreshTopTabHandler.postDelayed(refreshTopTabRunnable, refreshTopTabDelay);
        }, refreshTopTabDelay);

        Log.i("Chat - onResume", "Resumed");
    }

    private boolean tryToConnect() {
        if(!hubConnection.start()) {
            Log.i("Main - onResume", "Connection Failed");
            makeToast("Lost Connection");
            return false;
        }
        return true;
    }

    private void sendChatMessage(String message) {
        ExecutorService service = Executors.newSingleThreadExecutor();

        service.execute(() -> {
            if(hubConnection.isDisconnected()) {
                runOnUiThread(() -> {
                    makeToast("Failed to connect");
                    Log.e("Chat - Send Message", "No Connection");
                    goBackToMainActivity();
                });
                return;
            }
            String result = hubConnection.getHubConnection().invoke(String.class,"SendMessage", userId, currentRoom.getId(), message).blockingGet();

            runOnUiThread(() -> {
                if(!Objects.equals(result, "Ok")) {
                    makeToast("Message Not Sent");
                    Log.e("", "Got error: " + result);
                    return;
                }
                Log.i("", "Message Sent");
            });

        });
        service.shutdown();
    }

    private void updateChat(int roomId, ChatModel message) {
        if(roomId != currentRoom.getId()) return;

        if(message == null) {
            Log.e("Chat - Update Chat", "Got null message");
            return;
        }

        Log.i("Chat - Update Chat", "Message Received: " + message.getSenderId() + " | " + message.getDateString());

        if(message.getMessage() == null) {
            Log.e("Chat - Update Chat", "Got null message");
            return;
        }

        chatArray.add(message);
        chatList.setAdapter(adapter);

    }



    private void initialElementConnection() {
        ExecutorService service = Executors.newSingleThreadExecutor();

        service.execute(() -> {
            if(hubConnection.isDisconnected()) {

                runOnUiThread(() -> {
                    makeToast("Failed to connect");
                    Log.e("Chat - Initial", "No Connection");
                    goBackToMainActivity();
                });
                return;
            }

            StringIntegerModel result = hubConnection.getHubConnection().invoke(StringIntegerModel.class, "GetRoomNameAndMembersCount", currentRoom.getId()).blockingGet();

            Log.i("Chat - Initial", "Got Room: " + result.getString() + " | " + String.valueOf(result.getInteger()));

            if(Objects.equals(result.getString(), "") || result.getString() == null) {
                makeToast("The room you are in does not exist anymore.");
                Log.e("Chat - Initial", "Room Not Found");
                goBackToMainActivity();
                return;
            }

            currentRoom.setName(result.getString());
            runOnUiThread(() -> {
                roomNameLabel.setText(currentRoom.getName());
                membersCountLabel.setText(String.valueOf(result.getInteger()));
            });
        });

        service.shutdown();
    }

    private void getAllMessages() {
        ExecutorService service = Executors.newSingleThreadExecutor();

        service.execute(() -> {

            if(hubConnection.isDisconnected()) {

                runOnUiThread(() -> {
                    makeToast("Failed to connect");
                    Log.e("Chat - Get All Messages", "No Connection");
                    goBackToMainActivity();
                });
                return;
            }

            ChatModel @NonNull [] results = hubConnection.getHubConnection().invoke(ChatModel[].class,"GetRoomMessages", currentRoom.getId()).blockingGet();

            Log.i("Chat - Get All Messages","Got: " + results.length);

            ArrayList<ChatModel> messages = new ArrayList<ChatModel>();

            for (ChatModel item : results) {
                if(item == null || item.getMessage() == null){
                    Log.e("Chat - Get All Messages","Null Item");
                    makeToast("Failed To Connect");
                    goBackToMainActivity();
                    return;
                }
                messages.add(item);
            }

            chatArray.clear();
            chatArray.addAll(messages);

            runOnUiThread(() -> {
                chatList.setAdapter(adapter);
                chatList.scrollTo(0, chatList.getScrollY());
            });
        });
        service.shutdown();
    }

    private void goBackToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        hubConnection.stop();
        finish();
        startActivity(intent);
    }

    private void openSettingsActivity()
    {
        Intent intent = new Intent(this, ChatSettingsActivity.class);
        intent.putExtra("RoomId", currentRoom.getId());
        intent.putExtra("UserId", userId);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBackToMainActivity();
    }

    private void makeToast(String string) {
        Toast.makeText(ChatActivity.this, string, Toast.LENGTH_LONG).show();
    }
}