package com.example.beacon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

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

        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.contact_name);
        }
    }

    /**
     * List of (abridged) user data
     */
    private List<User> userlist;

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
        //Get user
        User user = userlist.get(pos);

        //Set item view
        TextView textView = holder.nameTextView;
        textView.setText(user.getUsername());
    }

    /**
     * The item count
     */
    @Override
    public int getItemCount() {return userlist.size();}
}
