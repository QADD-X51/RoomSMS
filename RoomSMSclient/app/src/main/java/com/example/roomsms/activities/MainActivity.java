package com.example.roomsms.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roomsms.R;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.annotations.NonNull;

public class MainActivity extends AppCompatActivity {

    ListView roomsList;
    TextView userLabel;
    ArrayList<RoomModel> roomsArray;
    RoomsListViewAdapter adapter;
    Button createRoomButton;
    int userId;
    HubConnector hubConnection;

    Handler handler;
    Runnable refreshFunction;
    final int delay = 30 * 1000; //Delay for 30 seconds.  One second = 1000 milliseconds.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if(extras == null)
        {
            makeToast("Something went wrong");
            startActivity(new Intent(this, ActivityLogIn.class));
            finish();
        }

        userId = extras.getInt("UserId");

        hubConnection = new HubConnector("/app");
        if(!hubConnection.start()) {
            makeToast("Connection Lost");
            startActivity(new Intent(this, ActivityLogIn.class));
            finish();
        }

        handler = new Handler();

        setContentView(R.layout.activity_main);

        roomsList = findViewById(R.id.RoomsList);
        userLabel = findViewById(R.id.UserLabel);
        createRoomButton = findViewById(R.id.CreateRoomButton);

        roomsArray = new ArrayList<RoomModel>();

        this.initialElementConnection();

        userLabel.setText(String.valueOf(userId));

        roomsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(hubConnection.isDisconnected()) {
                    makeToast("You are not connected, please refresh");
                    return;
                }
                openChatActivity(roomsArray.get(i));
            }
        });


        this.updateList();

        createRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateRoomDialog();
            }
        });

    }

    private void showCreateRoomDialog() {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_add_new);

        TextView title = dialog.findViewById(R.id.DialogTitle);
        TextView message = dialog.findViewById(R.id.DialogMessage);
        Button addButton = dialog.findViewById(R.id.AddButton);
        EditText editText = dialog.findViewById(R.id.DialogEditText);

        title.setText(getResources().getString(R.string.dialog_add_room_title));
        message.setText(getResources().getString(R.string.dialog_add_room_message));

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRoom(editText.getText().toString());
                dialog.cancel();
            }
        });

        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Main - onPause", "Paused");
        hubConnection.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        handler.postDelayed( refreshFunction = () -> {
            Log.i("Main - Handler", "Called Handler");
            refresh();
            handler.postDelayed(refreshFunction, delay);
        }, delay);

        tryToConnect();
        Log.i("Main - onResume", "Resumed");
    }

    private void openChatActivity(RoomModel room) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("UserId", userId);
        intent.putExtra("RoomId", room.getId());
        intent.putExtra("RoomName", room.getName());
        startActivity(intent);
    }

    private void initialElementConnection() {
        ExecutorService service = Executors.newSingleThreadExecutor();

        service.execute(() -> {
            if(hubConnection.isConnected()) {
                String result = hubConnection.getHubConnection().invoke(String.class, "GetUsernameById", userId).blockingGet();

                Log.i("Main - Initial Element Connection", " Result: " + result);

                runOnUiThread(() -> {
                    userLabel.setText(result);
                });
                return;
            }
            Log.e("Main - Initial Element Connection", " Disconnected");
        });

        service.shutdown();
    }

    private void createRoom(String roomName) {
        ExecutorService service = Executors.newSingleThreadExecutor();

        service.execute(() -> {
            if(hubConnection.isConnected()) {
                String result = hubConnection.getHubConnection().invoke(String.class, "CreateRoom", userId, roomName).blockingGet();

                Log.i("Main - Create Room", result);

                runOnUiThread(() -> {
                    if(result.equals("Ok")) {
                        makeToast("Room Was Added");
                        updateList();
                        return;
                    }
                    makeToast("Could not add room");
                });
                return;
            }
            Log.e("Main - Initial Element Connection", " Disconnected");
        });

        service.shutdown();

    }

    private void updateList() {
        ExecutorService service = Executors.newSingleThreadExecutor();

        service.execute(() -> {
            if(hubConnection.isConnected()) {
                RoomModel @NonNull [] result = hubConnection.getHubConnection().invoke(RoomModel[].class, "GetRoomsWithGivenMember", userId).blockingGet();

                Log.i("Main - Update List", "Rooms got: " + String.valueOf(result.length));

                ArrayList<RoomModel> rooms = new ArrayList<RoomModel>();


                for (RoomModel o : result) {
                    Log.i("Main - Update List", "Rooms added: " + o.getName());
                    rooms.add(o);
                }

                runOnUiThread(() -> {
                    roomsArray = rooms;
                    adapter = new RoomsListViewAdapter(getApplicationContext(), roomsArray);
                    roomsList.setAdapter(adapter);
                });
                return;
            }
            Log.e("Main - Initial Element Connection", " Disconnected");
        });

        service.shutdown();
    }

    private boolean tryToConnect() {
        if(!hubConnection.start()) {
            Log.i("Main - onResume", "Connection Failed");
            makeToast("Could not reconnect, please refresh");
            return false;
        }
        return true;
    }

    private void refresh() {
        if(!tryToConnect()) {
            return;
        }
        updateList();
    }

    public void makeToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }
}