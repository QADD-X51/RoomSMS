package com.example.roomsms.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.roomsms.R;

public class ActivityLogIn extends AppCompatActivity {

    EditText emailBox;
    EditText passwordBox;
    Button logInButton;
    Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        emailBox = findViewById(R.id.Email);
        passwordBox = findViewById(R.id.Password);

        logInButton = findViewById(R.id.LogInButton);
        signUpButton = findViewById(R.id.RegisterButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                openRegisterActivity();
            }
        });
    }

    public void openRegisterActivity(){
        startActivity(new Intent(this, ActivityRegister.class));
    }
}