package com.example.roomsms.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView roomsList;
    TextView userLabel;
    ArrayList<RoomModel> roomsArray;
    RoomsListViewAdapter adapter;
    Button createRoomButton;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if(extras == null)
        {
            Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, ActivityLogIn.class));
            finish();
        }

        setContentView(R.layout.activity_main);

        roomsArray = new ArrayList<>();
        roomsArray.add(new RoomModel("New Room", "Q.A.D.D."));
        roomsArray.add(new RoomModel("Another Room", "Relu Dorelu"));
        roomsArray.add(new RoomModel("Cool Room", "Eu Nutu"));
        roomsArray.add(new RoomModel("Not So Cool Room", "Somebody That I used To Know"));
        roomsArray.add(new RoomModel("Nice Room", "N66"));
        roomsArray.add(new RoomModel("Bike?", "Spacedust"));
        roomsArray.add(new RoomModel("Racing Room", "Q.A.D.D."));
        roomsArray.add(new RoomModel("Chatting Room", "Relu Dorelu"));
        roomsArray.add(new RoomModel("Gaming Room", "Eu Nutu"));
        roomsArray.add(new RoomModel("Not Enough Room", "Q.A.D.D."));
        roomsArray.add(new RoomModel("Not a Room", "Relu Dorelu"));
        roomsArray.add(new RoomModel("Some Other Room", "Eu Nutu"));
        roomsArray.add(new RoomModel("A Room", "Somebody That I used To Know"));
        roomsArray.add(new RoomModel("Broom", "N66"));
        roomsArray.add(new RoomModel("Old Room", "Spacedust"));
        roomsArray.add(new RoomModel("Subaru V.S. Mitsubishi", "Q.A.D.D."));
        roomsArray.add(new RoomModel("Common Room", "Relu Dorelu"));
        roomsArray.add(new RoomModel("Last Room", "Eu Nutu"));

        userId = extras.getInt("UserId");

        roomsList = findViewById(R.id.RoomsList);
        userLabel = findViewById(R.id.UserLabel);
        createRoomButton = findViewById(R.id.CreateRoomButton);

        userLabel.setText(String.valueOf(userId));

        roomsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, "Entering room: " + roomsArray.get(i).GetName(), Toast.LENGTH_SHORT).show();
                openChatActivity(roomsArray.get(i));
            }
        });

        adapter = new RoomsListViewAdapter(getApplicationContext(), roomsArray);
        roomsList.setAdapter(adapter);

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
                //Make it do stuff
                Toast.makeText(MainActivity.this, "Room Added:  " + editText.getText().toString(), Toast.LENGTH_LONG).show();
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void openChatActivity(RoomModel room) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("UserId", userId);
        intent.putExtra("Room", room.GetName());
        startActivity(intent);
    }
}