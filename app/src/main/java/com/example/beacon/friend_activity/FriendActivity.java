package com.example.beacon.friend_activity;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.beacon.R;
import javax.sql.DataSource;

public class FriendActivity extends AppCompatActivity {

    //The RecyclerView (doesn't/shouldn't change)
    private RecyclerView userList = findViewById(R.id.listOfX);
    //The error message will be bought up several times
    private static String errorMessage = String.valueOf(R.string.friend_page_nullDataError);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        //TODO get the current user to grab the various user-lists from Firebase
        //FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();

        //set up RecyclerView
        String defaultMessage = String.valueOf(R.string.friend_page_default);
        userList.setAdapter(new UserAdapter(this, defaultMessage));
        userList.setHasFixedSize(true);
    }
    //TODO initialize the DataSources (if available)
    //TODO set up Friends button (needs new adaptor)
    public void showFriends(View view){
        DataSource friendsList;
        if () friendsList = null;
        if(friendsList == null) userList.setAdapter(new UserAdapter(this, errorMessage));
        else userList.setAdapter(new UserAdapter(this, friendsList));
    }

    //TODO set up Requests button (needs new adaptor)
    public void showRequests(View view){
        DataSource requests;
        if() requests = null;
        if(requests == null) userList.setAdapter(new UserAdapter(this, errorMessage));
        else userList.setAdapter(new UserAdapter(this, requests));}

    //TODO set up Blocked button (needs new adaptor)
    public void showBlocked(View view){
        DataSource blocked;
        if() blocked = null;
        if(blocked == null) userList.setAdapter(new UserAdapter(this, errorMessage));
        else userList.setAdapter(new UserAdapter(this, blocked));}

    //TODO set up searchbar

}