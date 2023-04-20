package com.example.beacon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

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

            //Apply onClickListener
            acceptButton.setOnClickListener(new View.OnClickListener(){
                /**
                 * Removes the request from firebase
                 *  and adds sending user to friend list
                 * @param v The view that was clicked.
                 */
                @Override
                public void onClick(View v) {

                }
            });
            denyButton.setOnClickListener(new View.OnClickListener(){
                /**
                 * Removes request from firebase
                 * @param v The view that was clicked.
                 */
                @Override
                public void onClick(View v){

                }
            });
        }
    }

    /**
     * List of FriendRequests
     */
    private List<FriendRequest> requestList;
    /**
     * Constructor!
     * @param rl List of Requests!
     */
    public RequestAdaptor(List<FriendRequest> rl){ requestList = rl; }
    /**
     * Let us inflate the layout and set up some stuff!
     */
    public RequestAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Context context = parent.getContext();
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
        //data model, cha cha cha
        FriendRequest request = requestList.get(pos);

        //set up TextViews
        TextView sender = holder.senderView;
        TextView message = holder.messageView;
        sender.setText(request.getSendersNickname());
        message.setText(request.getMessage());

        //set up buttons
        Button yes = holder.acceptButton;
        Button no = holder.denyButton;
        yes.setEnabled(true);
        no.setEnabled(false);
    }

    /**
     * How many requests are there?
     */
    public int getItemCount(){
        return requestList.size();
    }
}
