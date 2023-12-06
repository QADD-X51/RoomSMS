package com.example.roomsms.activities;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

        hubConnection = HubConnectionBuilder.create("http://10.0.2.2:5190/register").build();

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if(hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
                    try {
                        hubConnection.start().doOnError(throwable -> {
                                    Log.e("Connection:Start", "doInBackground > doOnError: ", throwable);
                                    //start fail , try again
                                    //note: the start function need try chach when we use this function
                                })
                                .doOnComplete(() -> {
                                    Log.i("Connection:Start", "doInBackground > doOnComplete.");
                                    //start complated
                                })
                                .blockingAwait();
                    }
                    catch(Exception e){
                        Toast.makeText(ActivityRegister.this, "Connection Timeout", Toast.LENGTH_LONG).show();
                        Log.e("Connection:Start", "Connection Timeout");
                        return;
                    }

                }

                String username = usernameBox.getText().toString();
                String email = emailBox.getText().toString();
                String password = passwordBox.getText().toString();
                String repassword = rePasswordBox.getText().toString();

                if(hubConnection.getConnectionState() == HubConnectionState.CONNECTED)
                {

                    String result = hubConnection.invoke(String.class,"RegisterUser", username, email, password).blockingGet();
                    Toast.makeText(ActivityRegister.this, result, Toast.LENGTH_LONG).show();
                    Log.i("Connection:Received", result);

                    hubConnection.stop();
                }

                Toast.makeText(ActivityRegister.this, "Connection Error", Toast.LENGTH_LONG).show();
                hubConnection.stop();
            }
        });
    }

}
