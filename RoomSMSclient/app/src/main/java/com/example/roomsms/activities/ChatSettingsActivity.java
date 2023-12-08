package com.example.roomsms.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.roomsms.R;

import java.util.ArrayList;
import java.util.Objects;

public class ChatSettingsActivity extends AppCompatActivity {

    ListView settingsList;
    ArrayList<String> settingsArray;
    ArrayAdapter<String> adapter;
    String role;
    String roomId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_settings);

        Bundle extras = getIntent().getExtras();
        if(extras == null)
        {
            Toast.makeText(ChatSettingsActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            onBackPressed();
        }

        role = extras.getString("Role");
        roomId = extras.getString("Room");

        settingsList = findViewById(R.id.ChatSettingsList);

        settingsArray = new ArrayList<String>();

        if(!Objects.equals(role, "Normal"))  settingsArray.add("Add User To Room");
        if(Objects.equals(role, "Owner")) settingsArray.add("Manage Users");
        if(!Objects.equals(role, "Normal")) settingsArray.add("Change Room Name");
        if(Objects.equals(role, "Owner"))
            settingsArray.add("Delete Room");
        else
            settingsArray.add("Leave Room");

        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, settingsArray);
        settingsList.setAdapter(adapter);

        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(ChatSettingsActivity.this, "Clicked: " + settingsArray.get(i), Toast.LENGTH_LONG).show();
            }
        });
    }
}