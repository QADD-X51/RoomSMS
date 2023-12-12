package com.example.roomsms.activities;
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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class ActivityRegister extends AppCompatActivity {

    EditText usernameBox;
    EditText emailBox;
    EditText passwordBox;
    EditText rePasswordBox;
    Button backButton;
    Button registerButton;

    HubConnector hubConnection;

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

        hubConnection = new HubConnector("/app");

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

                String email = emailBox.getText().toString();
                String password = passwordBox.getText().toString();
                String repassword = rePasswordBox.getText().toString();
                String username = usernameBox.getText().toString();

                if(!password.equals(repassword)) {
                    Toast.makeText(ActivityRegister.this, "The passwords do not match.", Toast.LENGTH_LONG).show();
                    return;
                }

                if(password.isEmpty() || Utils.IsBlankString(password)) {
                    Toast.makeText(ActivityRegister.this, "Password can not be blank.", Toast.LENGTH_LONG).show();
                    return;
                }

                if(username.isEmpty() || Utils.IsBlankString(username)) {
                    Toast.makeText(ActivityRegister.this, "Username can not be blank.", Toast.LENGTH_LONG).show();
                    return;
                }

                if(!Pattern.compile("^[_a-zA-z0-9-.]+@[a-zA-z0-9]+\\.[a-zA-z]+$").matcher(email).matches()) {
                    Toast.makeText(ActivityRegister.this, "Please type a valid email.", Toast.LENGTH_LONG).show();
                    return;
                }

                ExecutorService service = Executors.newSingleThreadExecutor();

                Runnable function = () -> {

                    if(!hubConnection.start()) {
                        Log.e("Connection:Start", "Connection Timeout");
                        runOnUiThread(() -> makeToast("Connection Timeout"));
                        return;
                    }

                    if(hubConnection.isConnected())
                    {

                        String result = hubConnection.getHubConnection().invoke(String.class,"RegisterUser", username, email, password).blockingGet();
                        //makeToast(result);
                        Log.i("Connection:Received", result);

                        hubConnection.stop();
                        runOnUiThread(() -> {
                            if(result.equals("Ok")) {
                                makeToast("Account created!");
                                return;
                            }
                            makeToast(result);
                        });
                        return;
                    }

                    hubConnection.stop();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            makeToast("Connection Error");
                        }
                    });

                };

                service.execute(function);


            }
        });
    }

    public void makeToast(String message) {
        Toast.makeText(ActivityRegister.this, message, Toast.LENGTH_LONG).show();
    }

}
