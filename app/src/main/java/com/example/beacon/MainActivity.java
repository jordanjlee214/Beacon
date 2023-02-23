package com.example.beacon;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button eventActivityButton, loginActivityButton, friendActivityButton, mapsActivityButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up button objects and connect them to XML layout
        eventActivityButton = findViewById(R.id.eventactivity_button);
        loginActivityButton = findViewById(R.id.loginactivity_button);
        friendActivityButton = findViewById(R.id.friendactivity_button);
        mapsActivityButton = findViewById(R.id.mapactivity_button);

        //set up listeners for each button
        //each listener sends user to the corresponding activity
        eventActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToActivity(EventActivity.class);
            }
        });

        loginActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToActivity(LoginActivity.class);
            }
        });

        friendActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToActivity(FriendActivity.class);
            }
        });

        mapsActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToActivity(MapsActivity.class);
            }
        });
    }

    private void sendToActivity(Class<?> a) { //this method changes the activity to appropriate activity
        Intent switchToNewActivity= new Intent(MainActivity.this, a);
        startActivity(switchToNewActivity);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


}