package com.example.beacon;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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

    private FirebaseAuth mAuth;

    private DatabaseReference usersRef;


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

        maleDisplay.setVisibility(View.GONE);
        femaleDisplay.setVisibility(View.GONE);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        displayUserData();
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
}