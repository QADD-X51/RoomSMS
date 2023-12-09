package com.example.roomsms.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.roomsms.R;

import java.util.ArrayList;

public class ManageUsersActivity extends AppCompatActivity {

    ListView usersList;
    ArrayList<ManageUserModel> usersArray;
    ManageUsersListViewAdapter adapter;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_settings);

        usersArray = new ArrayList<ManageUserModel>();

        usersArray.add(new ManageUserModel("Nightmares", "Normal"));
        usersArray.add(new ManageUserModel("Nightmares", "Admin"));
        usersArray.add(new ManageUserModel("Relu Dorelu", "Admin"));
        usersArray.add(new ManageUserModel("Q.A.D.D.", "Owner"));
        usersArray.add(new ManageUserModel("Nightmares", "Admin"));
        usersArray.add(new ManageUserModel("Spacedust", "Admin"));
        usersArray.add(new ManageUserModel("Eu Nutu", "Normal"));
        usersArray.add(new ManageUserModel("Nightmares", "Admin"));
        usersArray.add(new ManageUserModel("Who?", "Normal"));
        usersArray.add(new ManageUserModel("Nightmares", "Admin"));

        user = "Spacedust"; // FOR TESTING, DELETE THIS

        adapter = new ManageUsersListViewAdapter(getApplicationContext(), usersArray, user);

        ListView usersList = findViewById(R.id.ChatSettingsList);
        usersList.setAdapter(adapter);

    }

}