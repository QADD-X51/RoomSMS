package com.example.roomsms.activities;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.roomsms.R;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

public class ActivityRegister extends AppCompatActivity {

    EditText usernameBox;
    EditText emailBox;
    EditText passwordBox;
    EditText rePasswordBox;
    Button backButton;
    Button registerButton;

    HubConnection hubConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        usernameBox = findViewById(R.id.Username);
        emailBox = findViewById(R.id.Email);
        passwordBox = findViewById(R.id.Password);
        rePasswordBox = findViewById(R.id.RePassword);

        backButton = findViewById(R.id.BackButton);
        registerButton = findViewById(R.id.RegisterButton);

        hubConnection = HubConnectionBuilder.create("https//127.0.0.1:5190/register").build();

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if(hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED)
                    hubConnection.start();

                String username = usernameBox.getText().toString();
                String email = emailBox.getText().toString();
                String password = passwordBox.getText().toString();
                String repassword = rePasswordBox.getText().toString();



                if(hubConnection.getConnectionState() == HubConnectionState.CONNECTED)
                    Toast.makeText(ActivityRegister.this, "Sending Message", Toast.LENGTH_LONG).show();
                    hubConnection.send("RegisterUser", username, email, password);
                    hubConnection.on("ReceiveRegistrationResult", (message) -> {
                                Toast.makeText(ActivityRegister.this, message, Toast.LENGTH_LONG).show();
                            },String.class);

                    hubConnection.stop();
            }
        });
    }

}
