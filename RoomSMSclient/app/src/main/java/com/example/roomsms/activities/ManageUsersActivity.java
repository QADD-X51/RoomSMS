package com.example.roomsms.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roomsms.R;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.annotations.NonNull;

public class ManageUsersActivity extends AppCompatActivity {

    ListView usersList;
    ArrayList<ManageUserModel> usersArray;
    ManageUsersListViewAdapter adapter;
    public int userId;
    int roomId;
    String role;
    HubConnector hubConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_settings);

        Bundle extras = getIntent().getExtras();
        if(extras == null)
        {
            makeToast("Something went wrong");
            goBackToChatSettings();
            return;
        }

        userId = extras.getInt("UserId");
        roomId = extras.getInt("RoomId");

        hubConnection = new HubConnector("/app");
        if(!hubConnection.start()) {
            makeToast("Could not connect");
            goBackToChatSettings();
        }

        usersArray = new ArrayList<ManageUserModel>();
        adapter = new ManageUsersListViewAdapter(this, usersArray, userId);

        usersList = findViewById(R.id.ChatSettingsList);
        usersList.setAdapter(adapter);

        getList();

    }

    private void getList() {
        ExecutorService service = Executors.newSingleThreadExecutor();

        service.execute(() -> {
            if(!hubConnection.start()) {
                runOnUiThread(() -> makeToast("Connection Lost"));
                goBackToChatSettings();
            }

            ManageUserModel @NonNull [] result = hubConnection.getHubConnection().invoke(ManageUserModel[].class, "GetRoomUsersRoles", roomId).blockingGet();

            Log.i("Manage Users - Get List", "Got: " + result.length);

            usersArray.clear();
            for(ManageUserModel user : result) {
                if(user == null || user.getUsername() == null) {
                    Log.e("Manage Users - Get List", "Got bad user");
                    continue;
                }
                usersArray.add(user);
                Log.i("Manage Users - Get List", "Got user: " + user.getUsername());
            }

            runOnUiThread(() -> {
                usersList.setAdapter(adapter);
            });
        });

        service.shutdown();
    }


    public void updateRole() {
        ExecutorService service = Executors.newSingleThreadExecutor();

        service.execute(() -> {
            if(hubConnection.isDisconnected()) {
                runOnUiThread(() -> {
                    makeToast("Connection lost");
                    Log.e("Manage Users - Update Role","Got Role: None");
                    role = "";
                    goBackToChatSettings();
                });
                return;
            }

            String result = hubConnection.getHubConnection().invoke(String.class, "GetMemberRole", userId, roomId).blockingGet();

            Log.i("Manage Users - Update Role","Got Role: " + result);

            if(Objects.equals(result, "") || result==null) {
                runOnUiThread(() -> {
                    makeToast("Connection lost");
                    Log.e("Chat Settings - Update Role","Got Role: None");
                    role = "";
                    goBackToChatSettings();
                });
                return;
            }


            if(!Objects.equals(role, result)) {
                role = result;
            }
        });

        service.shutdown();
    }

    public void showKickDialog(ManageUserModel user) {
        Dialog dialog = new Dialog(ManageUsersActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_are_you_sure);

        TextView title = dialog.findViewById(R.id.DialogTitle);
        TextView message = dialog.findViewById(R.id.DialogMessage);
        Button noButton = dialog.findViewById(R.id.NoButton);
        Button yesButton = dialog.findViewById(R.id.YesButton);

        title.setText(getResources().getString(R.string.dialog_kick_title));
        String messageString = getResources().getString(R.string.dialog_kick_message) + user.getUsername() + " ?";
        message.setText(messageString);

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Objects.equals(role, "Member") || Objects.equals(role, "")) {
                    makeToast("You are not allowed to kick members.");
                    dialog.cancel();
                    return;
                }
                kickUser(user);
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void kickUser(ManageUserModel userToKick) {
        ExecutorService service = Executors.newSingleThreadExecutor();

        service.execute(() -> {
            if(hubConnection.isDisconnected()) {
                runOnUiThread(() -> {
                    makeToast("Connection lost");
                    Log.e("Manage Users - Kick","Lost Connection");
                    goBackToChatSettings();
                });
                return;
            }

            String result = hubConnection.getHubConnection().invoke(String.class, "RemoveMemberFromRoom", userToKick.getId(), roomId).blockingGet();

            if(result == null) {
                runOnUiThread(() -> {
                    makeToast("Could not kick user");
                    Log.e("Manage Users - Kick","Got: null");
                });
                return;
            }

            Log.i("Manage Users - Kick","Got: " + result);

            if(result.equals("Ok")) {
                runOnUiThread(() -> {
                    makeToast("Kicked "+ userToKick.getUsername());
                    getList();
                });
                return;
            }
            runOnUiThread(()->{
                makeToast(result);
                if(result.equals("Room does not exist")) {
                    goBackToMainActivity();
                }
            });
        });

        service.shutdown();
    }

    public void showChangeRoleDialog(ManageUserModel user) {
        Dialog dialog = new Dialog(ManageUsersActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_are_you_sure);

        TextView title = dialog.findViewById(R.id.DialogTitle);
        TextView message = dialog.findViewById(R.id.DialogMessage);
        Button noButton = dialog.findViewById(R.id.NoButton);
        Button yesButton = dialog.findViewById(R.id.YesButton);

        title.setText(getResources().getString(R.string.dialog_change_role_title));
        String messageString = getResources().getString(R.string.dialog_change_role_message) + user.getUsername() + " ?";
        message.setText(messageString);

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Objects.equals(role, "Member") || Objects.equals(role, "")) {
                    makeToast("You are not allowed to kick members.");
                    dialog.cancel();
                    return;
                }
                changeRoleUser(user);
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void changeRoleUser(ManageUserModel targetUser) {
        ExecutorService service = Executors.newSingleThreadExecutor();

        service.execute(() -> {
            if(hubConnection.isDisconnected()) {
                runOnUiThread(() -> {
                    makeToast("Connection lost");
                    Log.e("Manage Users - Change Role","Lost Connection");
                    goBackToChatSettings();
                });
                return;
            }

            String result = hubConnection.getHubConnection().invoke(String.class, "ChangeMemberRole", targetUser.getId(), roomId).blockingGet();

            if(result == null) {
                runOnUiThread(() -> {
                    makeToast("Could not change role");
                    Log.e("Manage Users - Change Role","Got: null");
                });
                return;
            }

            Log.i("Manage Users - Kick","Got: " + result);

            if(result.equals("Ok")) {
                runOnUiThread(() -> {
                    makeToast("Changed role of " + targetUser.getUsername());
                    getList();
                });
                return;
            }
            runOnUiThread(()->{
                makeToast(result);
                if(result.equals("Room does not exist")) {
                    goBackToMainActivity();
                }
            });
        });

        service.shutdown();
    }

    private void goBackToChatSettings() {
        Intent intent = new Intent(this, ChatSettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        hubConnection.stop();
        finish();
        startActivity(intent);
    }

    private void goBackToMainActivity() {
        Intent intent;
        intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        finish();
        startActivity(intent);
    }

    public void makeToast(String message) {
        Toast.makeText(ManageUsersActivity.this, message, Toast.LENGTH_LONG).show();
    }
}