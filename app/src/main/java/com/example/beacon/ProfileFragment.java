package com.example.beacon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.A;

import java.nio.charset.StandardCharsets;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private static final String USERID = "userID";

    private String profileUserID; //id of the profile being viewed
    private String currentUserID; //id of the current user using the app

    private TextView nameDisplay, maleDisplay, femaleDisplay, usernameDisplay, majorDisplay, birthdayDisplay, gradDisplay;
    private CircleImageView profilePicDisplay;

    private Button requestButton;

    private FirebaseAuth mAuth;

    private DatabaseReference usersRef;
    private DatabaseReference friendRequestRef;
    private DatabaseReference friendRef;


    public ProfileFragment() {
        // Required empty public constructor
    }
    public static ProfileFragment newInstance(String id) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(USERID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            profileUserID = getArguments().getString(USERID);
        }

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        friendRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        currentUserID = mAuth.getCurrentUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        nameDisplay = v.findViewById(R.id.profileDisplayName_fragment);
        maleDisplay = v.findViewById(R.id.profileMale_fragment);
        femaleDisplay = v.findViewById(R.id.profileFemale_fragment);
        usernameDisplay = v.findViewById(R.id.profileUsername_fragment);
        majorDisplay = v.findViewById(R.id.profileMajor_fragment);
        birthdayDisplay = v.findViewById(R.id.profileBirthday_fragment);
        gradDisplay = v.findViewById(R.id.profileGradYear_fragment);
        profilePicDisplay = v.findViewById(R.id.profilePicture_fragment);
        requestButton = v.findViewById(R.id.requestRemoveButton);

        maleDisplay.setVisibility(View.GONE);
        femaleDisplay.setVisibility(View.GONE);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        displayUserData();

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentUserID.equals(profileUserID)){ //if you are looking at your own profile
                    Toast.makeText(getContext(), "You cannot friend yourself.", Toast.LENGTH_SHORT).show();
                }
                else{
                    showAddItemDialog(getContext(), usernameDisplay.getText().toString());
                }
            }
        });
    }

    //helper method to display the user data
    private void displayUserData(){
        usersRef.child(profileUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String firstName = snapshot.child("firstName").getValue().toString();
                String lastName = snapshot.child("lastName").getValue().toString();
                String nickname = snapshot.child("nickname").getValue().toString();
                String birthday = snapshot.child("birthday").getValue().toString();
                String username = snapshot.child("username").getValue().toString();
                String gradYear = snapshot.child("graduationYear").getValue().toString();
                String major = snapshot.child("major").getValue().toString();
                String gender = snapshot.child("gender").getValue().toString();
                String photoURL = snapshot.child("photoURL").getValue().toString();

                if(nickname.isEmpty()){
                    nameDisplay.setText(firstName + " " + lastName);
                }
                else{
                    nameDisplay.setText(firstName + " \"" + nickname + "\" " + lastName);
                }

                if(gradYear.isEmpty()) {
                    gradDisplay.setText("Graduation Year Unknown");
                }
                else{
                    gradDisplay.setText("Class of " + gradYear);
                }

                if(birthday.isEmpty()){
                    birthdayDisplay.setText("Birthday Unknown");
                }
                else{
                    birthdayDisplay.setText(birthday.substring(0, 2) + "/" + birthday.substring(2, 4) + "/" + birthday.substring(4, 6));
                }

                if(major.isEmpty()){
                    majorDisplay.setText("Major Unknown");
                }
                else {
                    majorDisplay.setText(major);
                }

                usernameDisplay.setText(username);
                if(gender.equals("M")){
                    maleDisplay.setVisibility(View.VISIBLE);
                    femaleDisplay.setVisibility(View.GONE);

                }
                else if(gender.equals("F")){
                    femaleDisplay.setVisibility(View.VISIBLE);
                    maleDisplay.setVisibility(View.GONE);
                }
                else{
                    femaleDisplay.setVisibility(View.GONE);
                    maleDisplay.setVisibility(View.GONE);
                }

                //get image from a URL and display it
                Picasso.get().load(photoURL).placeholder(R.drawable.blank_profile).into(profilePicDisplay);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*this sends a friend request to the profileID of this user,
    * the argument takes in a message for the friend request
    * */
    private void sendFriendRequest(String msg){
            usersRef.addValueEventListener(new ValueEventListener() { //Check user database and gather user data
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User sendingUser = new User();
                    User receivingUser = new User();
                    sendingUser.buildUserFromSnapshot(snapshot, currentUserID);
                    receivingUser.buildUserFromSnapshot(snapshot, profileUserID);
                    //TODO: Message feature - pop up window?
                    //Make a FriendRequest entity object to store info on the users involved.
                    String message = msg;
                    if(msg.isEmpty()){
                        message = "Let's be friends!";
                    }
                    FriendRequest friendRequest = new FriendRequest(sendingUser, message, receivingUser);
                    Toast.makeText(getContext(), "Receiving user is " + receivingUser.getUsername(), Toast.LENGTH_SHORT).show();
//                    sendingUser.setUsername(snapshot.child(currentUserID).child("username").getValue().toString());
//                    sendingUser.setNickname(snapshot.child(currentUserID).child("nickname").getValue().toString());
//                    sendingUser.setFirstName(snapshot.child(currentUserID).child("firstName").getValue().toString());
//                    sendingUser.setLastName(snapshot.child(currentUserID).child("lastName").getValue().toString());
//                    sendingUser.setPhotoURL(snapshot.child(currentUserID).child("photoURL").getValue().toString());
//                    sendingUser.setMajor(snapshot.child(currentUserID).child("major").getValue().toString());
//                    sendingUser.setBirthday(snapshot.child(currentUserID).child("birthday").getValue().toString());
//                    sendingUser.setGraduationYear(snapshot.child(currentUserID).child("graduationYear").getValue().toString());
//                    sendingUser.setGender(snapshot.child(currentUserID).child("gender").getValue().toString());
//                    sendingUser.setFriends(Integer.parseInt(snapshot.child(currentUserID).child("friends").getValue().toString()));
//                    sendingUser.setLati(Double.parseDouble(snapshot.child(currentUserID).child("lat").getValue().toString()));
//                    sendingUser.setLngi(Double.parseDouble(snapshot.child(currentUserID).child("lng").getValue().toString()));
//                    sendingUser.setPing((Boolean) snapshot.child(currentUserID).child("ping").getValue());
//                    sendingUser.setUserID(currentUserID);
//
//                    receivingUser.setUsername(snapshot.child(profileUserID).child("username").getValue().toString());
//                    receivingUser.setNickname(snapshot.child(profileUserID).child("nickname").getValue().toString());
//                    receivingUser.setFirstName(snapshot.child(profileUserID).child("firstName").getValue().toString());
//                    receivingUser.setLastName(snapshot.child(profileUserID).child("lastName").getValue().toString());
//                    receivingUser.setPhotoURL(snapshot.child(profileUserID).child("photoURL").getValue().toString());
//                    receivingUser.setMajor(snapshot.child(profileUserID).child("major").getValue().toString());
//                    receivingUser.setBirthday(snapshot.child(profileUserID).child("birthday").getValue().toString());
//                    receivingUser.setGraduationYear(snapshot.child(profileUserID).child("graduationYear").getValue().toString());
//                    receivingUser.setGender(snapshot.child(profileUserID).child("gender").getValue().toString());
//                    receivingUser.setFriends(Integer.parseInt(snapshot.child(profileUserID).child("friends").getValue().toString()));
//                    receivingUser.setLati(Double.parseDouble(snapshot.child(profileUserID).child("lat").getValue().toString()));
//                    receivingUser.setLngi(Double.parseDouble(snapshot.child(profileUserID).child("lng").getValue().toString()));
//                    receivingUser.setPing((Boolean) snapshot.child(profileUserID).child("ping").getValue());
//                    receivingUser.setUserID(profileUserID);
                    friendRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.child(currentUserID).child(profileUserID).exists()){ //are they friends?
                                Toast.makeText(getContext(), "You are already friends with this user.", Toast.LENGTH_SHORT).show();
                            }
                            else{ //you aren't friends already
                                friendRequestRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.child(friendRequest.getKey()).exists()){
                                            Toast.makeText(getContext(), "You have already sent a friend request.", Toast.LENGTH_SHORT).show();
                                        }
                                        else if(snapshot.child(friendRequest.getReverseKey()).exists()){
                                            Toast.makeText(getContext(), receivingUser.getUsername() + " has already sent you a request. Check your friends page and accept it.", Toast.LENGTH_SHORT).show();
                                        }
                                        else{ //no friend request has been sent
                                            friendRequestRef.child(friendRequest.getKey()).updateChildren(friendRequest.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(getContext(), "A friend request has been sent.", Toast.LENGTH_SHORT).show();
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

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    /**
     * This method shows a pop-up window that allows the user to type in a FriendRequest message.
     * It is taken from this site: https://alvinalexander.com/source-code/android-mockup-prototype-dialog-text-field/
     */
    private void showAddItemDialog(Context c, String username) {
        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Friend Request to: " + username)
                .setMessage("Type in a message: ")
                .setView(taskEditText)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String requestMessage = String.valueOf(taskEditText.getText());
                        sendFriendRequest(requestMessage);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

}