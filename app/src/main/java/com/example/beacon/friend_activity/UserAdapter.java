package com.example.beacon.friend_activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.beacon.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * Bonnie Rilea
 * CSCI 355
 * To adapt user information to XML output
 * List<Users> (friendsList<K, V> -> List<K>) -> Adaptor -> activity_friend.xml
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    /**
     * The relevant list of users
     */
    private List<String> dataset;

    /**
     * The context
     */
    final private Context context;

    /**
     * a default message
     */
    private String defaultM;

    /**
     * The FirebaseAuth instance
     */
    final FirebaseAuth authInstance = FirebaseAuth.getInstance();

    /**
     * Constructor (recommended)
     * @param context the Context
     * @param datalist the list of users for this specific adapter
     */
    public UserAdapter(Context context, List<String> datalist){
        this.context = context;
        dataset = datalist;
    }

    /**
     * Constructor ("default")
     * @param context the Context
     * @param message a default message to be used when starting up;
     *                if you have a list of default messages, use
     *                the recommended constructor
     */
    public UserAdapter(Context context, @NonNull String message){
        this.context = context;
        defaultM = message;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater adapterLayout = LayoutInflater.from(parent.getContext());
        return new UserViewHolder(adapterLayout.inflate(R.layout.user_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        String x = dataset.get(position);
        //set text to the Username String
        holder.userV.setText(x);
    }

    /**
     * How many items?
     * @return size of dataset
     */
    @Override
    public int getItemCount() {
        if (isNull()) return 1; //1 default message
        else return dataset.size();
    }

    /**
     * Is dataset null?
     * @return dataset == null
     */
    public boolean isNull(){
        return dataset == null;
    }

    class UserViewHolder extends RecyclerView.ViewHolder{
        /**
         * The User View
         */
        private TextView userV;

        /**
         * Constructor
         * @param view a TextView for the user information
         */
        UserViewHolder(View view){
            super(view);
            userV = view.findViewById(R.id.item_title);
        }
    }
}
