package com.example.beacon.friend_activity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beacon.R;

import javax.sql.DataSource;

public class FriendActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        //TODO get the current user to grab the various user-lists from Firebase
        //FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();

        //TODO initialize the DataSources (if available)
        String defaultMessage = String.valueOf(R.string.friend_page_default);
        DataSource friendsList = null; //to be used to implement friendsList
        DataSource requests = null; //to be used to implement friend requests
        DataSource blocked = null; //to be used to implement blocking users

        //set up RecyclerView
        RecyclerView userList = findViewById(R.id.listOfX);
        userList.setAdapter(new UserAdapter(this, defaultMessage));
        userList.setHasFixedSize(true);
    }

    //TODO set up Friends button (needs new adaptor)

    //TODO set up Requests button (needs new adaptor)

    //TODO set up Blocked button (needs new adaptor)

    //TODO set up searchbar

}