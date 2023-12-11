package com.example.roomsms.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.roomsms.R;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActivityLogIn extends AppCompatActivity {

    EditText emailBox;
    EditText passwordBox;
    Button logInButton;
    Button signUpButton;
    HubConnector hubConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        emailBox = findViewById(R.id.Email);
        passwordBox = findViewById(R.id.Password);

        logInButton = findViewById(R.id.LogInButton);
        signUpButton = findViewById(R.id.RegisterButton);

        hubConnection = new HubConnector("/app");

        signUpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                openRegisterActivity();
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String email = emailBox.getText().toString();
                String password = passwordBox.getText().toString();

                if(Utils.IsBlankString(email) || Utils.IsBlankString(password)) {
                    makeToast("Neither field should be blank.");
                    return;
                }

                ExecutorService server = Executors.newSingleThreadExecutor();
                server.execute(new Runnable() {
                    @Override
                    public void run() {
                        if(!hubConnection.start()) {
                            runOnUiThread(() -> {
                                makeToast("Connection Timeout");
                            });
                            return;
                        }

                        if(hubConnection.isConnected()) {
                            Object resultObj = hubConnection.getHubConnection().invoke(Object.class,"Login", email, password).blockingGet();

                            

                            Log.i("Result", resultObj.getClass().toString());
                            hubConnection.stop();
                        }

                        hubConnection.stop();
                    }
                });

                //openMainActivity();
            }
        });
    }

    public void openRegisterActivity(){
        startActivity(new Intent(this, ActivityRegister.class));
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("UserId", 5);
        startActivity(intent);
        finish();
    }

    public void makeToast(String message) {
        Toast.makeText(ActivityLogIn.this, message, Toast.LENGTH_LONG).show();
    }
}