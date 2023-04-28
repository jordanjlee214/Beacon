package com.example.beacon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Connected to UserDataModel, activity_friend.xml, FriendActivity, and user_data_view.xml
 * Tutorial Used: https://guides.codepath.com/android/using-the-recyclerview
 */
public class UserAdaptor extends
        RecyclerView.Adapter<UserAdaptor.ViewHolder>{
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nameTextView;
        public Button removeUserButton;

        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.contact_name);
            removeUserButton = itemView.findViewById(R.id.removeFriendButton);
        }
    }

    /**
     * List of (abridged) user data
     */
    private List<User> userlist;


    /**
     * A reference to FriendActivity
     */
    private FriendActivity friendActivity;

    /**
     * The context of the adaptor.
     */
    private Context context;

    /**
     * Prefered Constructor
     * @param ul a list of type user
     */
    public UserAdaptor(List<User> ul){
        userlist = ul;
    }

    /**
     * Alternate Constructor
     * @param m A string message
     */
    public UserAdaptor(String m) {
        userlist = new ArrayList<User>();
        userlist.add(new User());
        userlist.get(0).setUsername(m);
    }

    /**
     * Inflates the XML layout
     */
    @Override
    public UserAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        this.context = context;

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.user_view, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    /**
     * Populates Data
     */
    @Override
    public void onBindViewHolder(UserAdaptor.ViewHolder holder, int pos){
        friendActivity = (FriendActivity) context;

        //Get user
        User user = userlist.get(pos);

        //Set item view
        TextView textView = holder.nameTextView;
        textView.setText(user.getUsername());

        //set up the remove friend button
        Button removeButton = holder.removeUserButton;

        //make sure that the item is an actual user
        //don't show the remove button if it isn't
        if(user.getUsername().equals("~ Press a button ~") || user.getUsername().equals("Nothing to see here!")){
            removeButton.setVisibility(View.GONE);
        }
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("jordanjlee", user.getUserID());
                confirmRemoveDialog(user);
            }
        });
    }

    /**
     * The item count
     */
    @Override
    public int getItemCount() {return userlist.size();}

    /**
     * This helper method updates the database to get rid of the
     * friend relationship between the current user and the
     * user given in the parameters.
     * @param friend
     */
    private void removeFriend(User friend){
        String currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        friendRef.child(currentID).child(friend.getUserID()).removeValue();
        friendRef.child(friend.getUserID()).child(currentID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(context, "You and " + friend.getUsername() + " are no longer friends.", Toast.LENGTH_SHORT);
                friendActivity.showFriends(); //refresh friendlist
            }
        });
    }

    /**
     * This method shows a pop-up window that asks the user to
     * clarify if they want to remove a friend or not.
     */
    private void confirmRemoveDialog(User friend) {;
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Please confirm removal request")
                .setMessage("Are you sure you don't want to be friends with " + friend.getUsername() + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeFriend(friend);
                    }
                })
                .setNegativeButton("No", null)
                .create();
        dialog.show();
    }

}
