package com.example.beacon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeleteEventAdapter extends RecyclerView.Adapter<DeleteEventAdapter.ViewHolder> {

    private ArrayList<Event> data;

    private Context context;


    public DeleteEventAdapter(ArrayList<Event> data) {
        this.data = data;
    }

    @Override
    public DeleteEventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        this.context = context;
        View view = inflater.inflate(R.layout.delete_event_recycler_view_row, parent, false);
        return new DeleteEventAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event item = data.get(position);
        holder.mTextView.setText(item.toString());
        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteEventWindow(item);

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.delete_event_view);
        }
    }

    private void showDeleteEventWindow(Event event){
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Please confirm event deletion")
                .setMessage("Are you sure you want to delete this event: " + event.getEventName() + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteEvent(event);
//                        ((DeleteEventPage) context).sendToActivity(EventActivity.class);
                    }
                })
                .setNegativeButton("No", null)
                .create();
        dialog.show();
    }
    private void deleteEvent(Event event){
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("Events");
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currentEventID = "";
                for(DataSnapshot eventChild : snapshot.getChildren()){
                    if(eventChild.child("eventName").getValue().toString().equals(event.getEventName())
                    && eventChild.child("creatorUsername").getValue().toString().equals(event.getCreatorUsername())
                    && eventChild.child("eventDate").getValue().toString().equals(event.getEventDate())
                            && eventChild.child("eventLocation").getValue().toString().equals(event.getEventLocation())
                            && eventChild.child("eventStartTime").getValue().toString().equals(event.getEventStartTime())
                            && eventChild.child("eventEndTime").getValue().toString().equals(event.getEventEndTime())
                            && eventChild.child("eventDescription").getValue().toString().equals(event.getEventDescription())
                    )
                    {
                        currentEventID =eventChild.getKey();
                    }
                }
                if(!currentEventID.isEmpty()){
                    eventRef.child(currentEventID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(context, "Event succesfully deleted.", Toast.LENGTH_SHORT);
                            }
                            else{
                                Toast.makeText(context, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
