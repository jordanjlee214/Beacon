package com.example.beacon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;

/**
 * Bonnie Rilea
 * CSCI 355
 * To adapt user information to XML output
 * DatabaseReference -> Adaptor -> activity_friend.xml
 */
public class RecyclerViewObjectAdaptor extends RecyclerView.Adapter<RecyclerViewObjectAdaptor.UserViewHolder>{

    /**
     * The source of data: a Reference to the Database
     */
    private DataSnapshot datasource;

    /**
     * The set of data?
     */
    private Object dataset;

    /**
     * The context
     */
    final private Context context;

    /**
     * a default message
     */
    private String defaultM;


    /**
     * Constructor (for DatabaseReference)
     * @param context the Context
     * @param datalist the list of users for this specific adapter
     */
    public RecyclerViewObjectAdaptor(Context context, DataSnapshot datalist){
        this.context = context;
        datasource = datalist;
    }

    /**
     * Constructor ("default")
     * @param context the Context
     * @param message a (singular) default message to be used when starting up
     */
    public RecyclerViewObjectAdaptor(Context context, @NonNull String message){
        this.context = context;
        defaultM = message;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater adapterLayout = LayoutInflater.from(parent.getContext());
        return new UserViewHolder(adapterLayout.inflate(R.layout.user_list, parent, false));
    }

    /*
    * Precondition: datasource holds either Users or FriendRequests
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        if(isNull())
            holder.userV.setText(defaultM);
        else {
            Object x = datasource.getValue();
            if (x.getClass().equals(User.class))
                holder.userV.setText(((User) x).getFirstName() + " " + ((User) x).getLastName());
            else {
                assert x.getClass().equals(FriendRequest.class);
                holder.userV.setText(((FriendRequest) x).getSendersNickname());
            }
        }
    }

    /**
     * How many items?
     * @return size of dataset
     */
    @Override
    public int getItemCount() {
        if (isNull()) return 1; //1 default message
        else return (int) datasource.getChildrenCount();
    }

    /**
     * Is dataset null?
     * @return dataset == null
     */
    public boolean isNull(){
        return datasource == null;
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