package com.example.roomsms.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class ChatSettingsActivity extends AppCompatActivity {

    ListView settingsList;
    ArrayList<String> settingsArray;
    ArrayAdapter<String> adapter;
    String role;
    int roomId;
    int userId;
    HubConnector hubConnection;
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

        roomId = extras.getInt("RoomId");
        userId = extras.getInt("UserId");

        hubConnection = new HubConnector("/app");

        if(!hubConnection.start()) {
            makeToast("Could not connect");
            onBackPressed();
        }

        settingsList = findViewById(R.id.ChatSettingsList);
        settingsArray = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, settingsArray);

        updateRole();

        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                updateRole();
                switch(settingsArray.get(i))
                {
                    case "Add User To Room":
                        showAddUserDialog();
                        break;
                    case "Manage Users":
                        startManageUsersActivity();
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
                        makeToast("");
                }
            }
        });
    }


    private void showMenu() {
        if(!Objects.equals(role, "Member"))  settingsArray.add("Add User To Room");
        if(!Objects.equals(role, "Member")) settingsArray.add("Manage Users");
        if(!Objects.equals(role, "Member")) settingsArray.add("Change Room Name");
        if(Objects.equals(role, "Owner"))
            settingsArray.add("Delete Room");
        else
            settingsArray.add("Leave Room");

        settingsList.setAdapter(adapter);
    }

    private void clearMenu() {
        settingsArray.clear();
        settingsList.setAdapter(adapter);
    }

    private void updateRole() {
        ExecutorService service = Executors.newSingleThreadExecutor();

        service.execute(() -> {
            if(hubConnection.isDisconnected()) {
                runOnUiThread(() -> {
                    makeToast("Connection lost");
                    clearMenu();
                    Log.i("Chat Settings - Update Role","Got Role: None");
                    role = "";
                });
                return;
            }

            String result = hubConnection.getHubConnection().invoke(String.class, "GetMemberRole", userId, roomId).blockingGet();

            Log.i("Chat Settings - Update Role","Got Role: " + result);

            if(Objects.equals(result, "") || result==null) {
                runOnUiThread(() -> {
                    makeToast("Connection lost");
                    clearMenu();
                    Log.i("Chat Settings - Update Role","Got Role: None");
                    role = "";
                });
                return;
            }


            if(!Objects.equals(role, result)) {
                role = result;
                runOnUiThread(this::showMenu);
            }
        });

        service.shutdown();
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
                if(Objects.equals(role, "Owner") || Objects.equals(role, "")) {
                    makeToast("You are not allowed to leave room");
                    dialog.cancel();
                    return;
                }
                leaveRoom();
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void leaveRoom() {
        ExecutorService service = Executors.newSingleThreadExecutor();

        service.execute(() -> {
            if(hubConnection.isDisconnected()) {
                runOnUiThread(() -> {
                    makeToast("Connection lost");
                    clearMenu();
                    Log.i("Chat Settings - Leave Room","Got Role: None");
                    role = "";
                });
                return;
            }

            String result = hubConnection.getHubConnection().invoke(String.class, "RemoveMemberFromRoom", userId, roomId).blockingGet();

            Log.i("Chat Settings - Leave Room","Got Role: " + result);

            if(result == null) {
                runOnUiThread(() -> {
                    makeToast("Connection lost");
                    clearMenu();
                    Log.i("Chat Settings - Leave Room","Got: None");
                    role = "";
                });
                return;
            }

            if(Objects.equals(result, "Ok")) {
                runOnUiThread(() -> {
                    makeToast("You left the room.");
                    goBackToMainActivity();
                });
                return;
            }

            runOnUiThread(() -> {
                makeToast(result);
                if(result.equals("Member is not part of the room")) {
                    goBackToMainActivity();
                }
            });
        });

        service.shutdown();
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
                if(!Objects.equals(role, "Owner")) {
                    makeToast("You are not allowed to delete room");
                    dialog.cancel();
                    return;
                }
                deleteRoom();
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void deleteRoom() {
        ExecutorService service = Executors.newSingleThreadExecutor();

        service.execute(() -> {
            if(hubConnection.isDisconnected()) {
                runOnUiThread(() -> {
                    makeToast("Connection lost");
                    clearMenu();
                    Log.i("Chat Settings - Remove Room","Got Role: None");
                    role = "";
                });
                return;
            }

            String result = hubConnection.getHubConnection().invoke(String.class, "RemoveRoom", userId, roomId).blockingGet();

            Log.i("Chat Settings - Remove Room","Got: " + result);

            if(result == null) {
                runOnUiThread(() -> {
                    makeToast("Connection lost");
                    clearMenu();
                    Log.i("Chat Settings - Remove Room","Got Role: None");
                    role = "";
                });
                return;
            }

            if(Objects.equals(result, "Ok")) {
                runOnUiThread(() -> {
                    makeToast("Room deleted.");
                    goBackToMainActivity();
                });
                return;
            }

            runOnUiThread(() -> {
                makeToast("Room does not exist");
                goBackToMainActivity();
            });
        });

        service.shutdown();
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
                if(Objects.equals(role, "Member") || Objects.equals(role, "")) {
                    makeToast("You are not allowed to add members");
                    dialog.cancel();
                    return;
                }
                String email = editText.getText().toString();
                if(!Pattern.compile("^[_a-zA-z0-9-.]+@[a-zA-z0-9]+\\.[a-zA-z]+$").matcher(email).matches()) {
                    makeToast("Please type a valid email");
                    return;
                }
                addMember(email);
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void addMember(String email) {
        ExecutorService service = Executors.newSingleThreadExecutor();

        service.execute(() -> {
            if(hubConnection.isDisconnected()) {
                runOnUiThread(() -> {
                    makeToast("Connection lost");
                    clearMenu();
                    Log.i("Chat Settings - Add Member","Got Role: None");
                    role = "";
                });
                return;
            }

            String result = hubConnection.getHubConnection().invoke(String.class, "AddMember", roomId, email).blockingGet();

            Log.i("Chat Settings - Add Member","Got: " + result);

            if(result == null) {
                runOnUiThread(() -> {
                    makeToast("Connection lost");
                    clearMenu();
                    Log.i("Chat Settings - Add Member","Got Role: None");
                    role = "";
                });
                return;
            }

            if(Objects.equals(result, "Ok")) {
                runOnUiThread(() -> {
                    makeToast("Member added");
                });
                return;
            }

            runOnUiThread(() -> {
                makeToast("User does not exist");
            });
        });

        service.shutdown();
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
                if(Objects.equals(role, "Member") || Objects.equals(role, "")) {
                    makeToast("You are not allowed to change room name");
                    dialog.cancel();
                    return;
                }
                if(Utils.IsBlankString(editText.getText().toString())) {
                    makeToast("Room name should not be blank");
                    return;
                }
                changeRoomName(editText.getText().toString());
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void changeRoomName(String newName) {
        ExecutorService service = Executors.newSingleThreadExecutor();

        service.execute(() -> {
            if(hubConnection.isDisconnected()) {
                runOnUiThread(() -> {
                    makeToast("Connection lost");
                    clearMenu();
                    Log.i("Chat Settings - Change Room Name","Got Role: None");
                    role = "";
                });
                return;
            }

            String result = hubConnection.getHubConnection().invoke(String.class, "ChangeRoomName", roomId, newName).blockingGet();

            Log.i("Chat Settings - Add Member","Got Name: " + result);

            if(result == null) {
                runOnUiThread(() -> {
                    makeToast("Connection lost");
                    clearMenu();
                    Log.i("Chat Settings - Change Room Name","Got Role: None");
                    role = "";
                });
                return;
            }

            if(Objects.equals(result, "Ok")) {
                runOnUiThread(() -> {
                    makeToast("Room name changed");
                });
                return;
            }

            runOnUiThread(() -> {
                makeToast("Room does not exist");
                goBackToMainActivity();
            });
        });

        service.shutdown();
    }

    private void startManageUsersActivity(){
        if(Objects.equals(role, "Member") || Objects.equals(role, "")) {
            makeToast("You are not allowed to manage members");
            return;
        }

        Intent intent = new Intent(this, ManageUsersActivity.class);
        intent.putExtra("RoomId", roomId);
        intent.putExtra("UserId", userId);
        startActivity(intent);
    }

    private void goBackToMainActivity() {
        Intent intent;
        intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        finish();
        startActivity(intent);
    }

    private void makeToast(String string) {
        Toast.makeText(ChatSettingsActivity.this, string, Toast.LENGTH_LONG).show();
    }

}