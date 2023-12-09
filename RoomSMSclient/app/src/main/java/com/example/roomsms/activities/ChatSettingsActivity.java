package com.example.roomsms.activities;

import androidx.appcompat.app.AppCompatActivity;

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
        if(!Objects.equals(role, "Normal")) settingsArray.add("Manage Users");
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
                switch(settingsArray.get(i))
                {
                    case "Add User To Room":
                        showAddUserDialog();
                        break;
                    case "Manage Users":
                        break;
                    case "Change Room Name":
                        showChangeRoomNameDialog();
                        break;
                    case "Leave Room":
                        showLeaveRoomDialog();
                        break;
                    case "Delete Room":
                        showDeleteRoomDialog();
                        break;
                    default:
                        Toast.makeText(ChatSettingsActivity.this, "What did you click?", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showLeaveRoomDialog() {
        Dialog dialog = new Dialog(ChatSettingsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_are_you_sure);

        TextView title = dialog.findViewById(R.id.DialogTitle);
        TextView message = dialog.findViewById(R.id.DialogMessage);
        Button noButton = dialog.findViewById(R.id.NoButton);
        Button yesButton = dialog.findViewById(R.id.YesButton);

        title.setText(getResources().getString(R.string.dialog_leave_room_title));
        message.setText(getResources().getString((R.string.dialog_leave_room_message)));

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Make it do stuff
                Toast.makeText(ChatSettingsActivity.this, "Left Room", Toast.LENGTH_LONG).show();
                dialog.cancel();
                goBackToMainActivity();
            }
        });

        dialog.show();
    }

    private void showDeleteRoomDialog() {
        Dialog dialog = new Dialog(ChatSettingsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_are_you_sure);

        TextView title = dialog.findViewById(R.id.DialogTitle);
        TextView message = dialog.findViewById(R.id.DialogMessage);
        Button noButton = dialog.findViewById(R.id.NoButton);
        Button yesButton = dialog.findViewById(R.id.YesButton);

        title.setText(getResources().getString(R.string.dialog_delete_room_title));
        message.setText(getResources().getString(R.string.dialog_delete_room_message));

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Make it do stuff
                Toast.makeText(ChatSettingsActivity.this, "Deleted Room", Toast.LENGTH_LONG).show();
                dialog.cancel();
                goBackToMainActivity();
            }
        });

        dialog.show();
    }

    private void showAddUserDialog() {
        Dialog dialog = new Dialog(ChatSettingsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_add_new);

        TextView title = dialog.findViewById(R.id.DialogTitle);
        TextView message = dialog.findViewById(R.id.DialogMessage);
        Button addButton = dialog.findViewById(R.id.AddButton);
        EditText editText = dialog.findViewById(R.id.DialogEditText);

        title.setText(getResources().getString(R.string.dialog_add_user_title));
        message.setText(getResources().getString(R.string.dialog_add_user_message));

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Make it do stuff
                Toast.makeText(ChatSettingsActivity.this, "Added User:  " + editText.getText().toString(), Toast.LENGTH_LONG).show();
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void showChangeRoomNameDialog() {

        Dialog dialog = new Dialog(ChatSettingsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_add_new);

        TextView title = dialog.findViewById(R.id.DialogTitle);
        TextView message = dialog.findViewById(R.id.DialogMessage);
        Button confirmButton = dialog.findViewById(R.id.AddButton);
        EditText editText = dialog.findViewById(R.id.DialogEditText);

        title.setText(getResources().getString(R.string.dialog_change_room_name_title));
        message.setText(getResources().getString(R.string.dialog_change_room_name_message));
        confirmButton.setText(getResources().getString(R.string.change));

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Make it do stuff
                Toast.makeText(ChatSettingsActivity.this, "Renamed Room To:  " + editText.getText().toString(), Toast.LENGTH_LONG).show();
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void goBackToMainActivity() {
        Intent intent;
        intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        finish();
        startActivity(intent);
    }
}