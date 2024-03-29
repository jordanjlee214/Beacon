package com.example.beacon;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RequestAdaptor extends
    RecyclerView.Adapter<RequestAdaptor.ViewHolder>{
    public class ViewHolder extends RecyclerView.ViewHolder{
        /**
         * TextViews!
         */
        public TextView senderView, messageView;
        /**
         * Buttons!
         */
        public Button acceptButton, denyButton;
        /**
         * Constructor!
         */
        public ViewHolder(View itemView){
            super(itemView);
            senderView = (TextView) itemView.findViewById(R.id.sender_name);
            messageView = (TextView) itemView.findViewById(R.id.message);
            acceptButton = (Button) itemView.findViewById(R.id.acceptButton);
            denyButton = (Button) itemView.findViewById(R.id.denyButton);
        }
    }

    /**
     * List of FriendRequests
     */
    private List<FriendRequest> requestList;

    /**
     * The context of the app.
     */
    private Context currentContext;

    /**
     * A reference to FriendActivity to access its public methods.
     */
    private FriendActivity friendActivity;

    /**
     * Database references to access friend and friend request data
     */
    private DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference().child("Friends");
    private DatabaseReference friendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");

    /**
     * Constructor!
     * @param rl List of Requests!
     */
    public RequestAdaptor(List<FriendRequest> rl){ requestList = rl;

    }
    /**
     * Let us inflate the layout and set up some stuff!
     */
    public RequestAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Context context = parent.getContext();
        currentContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        //inflate the layout
        View requestView = inflater.inflate(R.layout.requests_view, parent, false);
        //Return what needs to be returned!
        ViewHolder viewHolder = new ViewHolder(requestView);
        return viewHolder;
    }
    /**
     * Data Population! How nice!
     */
    @Override
    public void onBindViewHolder(RequestAdaptor.ViewHolder holder, int pos){
        friendActivity = (FriendActivity) currentContext;
        //data model, cha cha cha
        FriendRequest request = requestList.get(pos);

        //set up TextViews
        TextView sender = holder.senderView;
        TextView message = holder.messageView;
        sender.setText(request.getSendersUsername());
        message.setText(request.getMessage());

        //set up buttons
        Button yes = holder.acceptButton;
        Button no = holder.denyButton;
        yes.setEnabled(true);
        no.setEnabled(true);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptFriendRequest(request, request.getSenderUser().getUserID(), currentContext);
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectFriendRequest(request, request.getSenderUser().getUserID(), currentContext);
            }
        });
    }

    /**
     * How many requests are there?
     */
    public int getItemCount(){
        return requestList.size();
    }

    private void acceptFriendRequest(FriendRequest request, String senderID, Context c){
        String currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        friendRef.child(currentID).child(senderID).setValue(request.getSendersUsername());
        friendRef.child(senderID).child(currentID).setValue(request.getReceiverUser().getUsername());
        friendRequestRef.child(currentID+"_"+senderID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(currentContext, "Friend request accepted. You are now friends.", Toast.LENGTH_SHORT);
                    friendActivity.showRequests(); //refresh requests
                }
            }
        });

    }

    private void rejectFriendRequest(FriendRequest request, String senderID, Context c){
        String currentID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        friendRequestRef.child(currentID+"_"+senderID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(currentContext, "Friend request denied.", Toast.LENGTH_SHORT);
                    friendActivity.showRequests(); //refresh requests
                }
            }
        });

    }


}
