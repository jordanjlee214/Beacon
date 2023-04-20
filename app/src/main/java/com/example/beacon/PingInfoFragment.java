package com.example.beacon;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Maps;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PingInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PingInfoFragment extends Fragment {
    private static final String USERID = "userID";

    private String currentUserID; //id of the current user using the app
    private PingInfo pingInfo;

    //UI elements
    private EditText titleField, descriptionField;
    private Spinner locationField;
    private TimePicker endTimeField;
    private Button submitField;
    private CheckBox endTimeCheckField;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private DatabaseReference pingInfoRef;

    public PingInfoFragment() {
        // Required empty public constructor
    }

    public static PingInfoFragment newInstance(String id) {
        PingInfoFragment fragment = new PingInfoFragment();
        Bundle args = new Bundle();
        args.putString(USERID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUserID = getArguments().getString(USERID);
        }

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        pingInfoRef = FirebaseDatabase.getInstance().getReference().child("Pings");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ping_info, container, false);
        titleField = v.findViewById(R.id.pingInfo_titleField);
        descriptionField = v.findViewById(R.id.pingInfo_descField);
        locationField = v.findViewById(R.id.pingInfo_locField);
        endTimeField = v.findViewById(R.id.pingInfo_timeField);
        submitField = v.findViewById(R.id.pingInfo_submit);
        endTimeCheckField = v.findViewById(R.id.pingInfo_timeCheckBox);
        endTimeField.setIs24HourView(false);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item_text, CampusLocations.sorted());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationField.setAdapter(adapter);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        submitField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkFields()){
                    sendPing();
                }
                else{
                   Toast.makeText(getContext(), "Fill out required fields: Title and Location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*
     * This method makes sure all the appropriate fields of the ping form are filled.
     */
    public boolean checkFields(){
        return !titleField.getText().toString().isEmpty() && locationField.getSelectedItem() != null;
    }

    public String endTime(){
       if(endTimeCheckField.isChecked()){
           String endHour = "" + endTimeField.getHour();
           String endMinute = "" + endTimeField.getMinute();
           if(endTimeField.getMinute() < 10){
               endMinute = "0" + endMinute;
           }
           return endHour + ":" +endMinute;
       }
       else{
           return "";
       }
    }

    private void sendPing(){
        usersRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pingInfo = new PingInfo(titleField.getText().toString(),
                        descriptionField.getText().toString(),
                        locationField.getSelectedItem().toString(),
                        endTime(),
                        currentUserID,
                        snapshot.child("username").getValue().toString(),
                        snapshot.child("firstName").getValue().toString() + " " + snapshot.child("lastName").getValue().toString());
                pingInfoRef.child(currentUserID).updateChildren(pingInfo.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       Toast.makeText(getContext(), "You have now been pinged!", Toast.LENGTH_SHORT).show();
                        ((MapsActivity) getActivity()).onBackPressed(); //closes the fragment from MapsActivity
                        //TODO: send notification
                        sendNoti();
                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void sendNoti(){
//        // See documentation on defining a message payload.
//        Message message = new Message();
//        Bundle msg = new Bundle();
//        msg.putString("message","hello world!");
//        message.setData(msg)
//        setToken("e5ei75SCT5y7BvQ84BrBDl:APA91bFYdpubTd1aW16r-kimMY5DA5jEX_YOSwhCYw2axOLNeEWygIW8N3uoxLsTcME-9B8D_8zKjXoR7CyT003qjtOE2lqlvg_HaSwtxQ1dFhU3LNwN9CJrT0d7xJILRSvg-ORUNFdP")
//                .build();
//
//// Send a message to the device corresponding to the provided
//// registration token.
//        String response = FirebaseMessaging.getInstance().send(message);
//// Response is a message ID string.
//        System.out.println("Successfully sent message: " + response);
        AtomicInteger msgId = new AtomicInteger();
        FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(getString(R.string.senderID) + "@gcm.googleapis.com")
                .setMessageId(Integer.toString(msgId.incrementAndGet()))
                .addData("my_message", "Hello World")
                .addData("my_action","SAY_HELLO")
                .build());


    }





}