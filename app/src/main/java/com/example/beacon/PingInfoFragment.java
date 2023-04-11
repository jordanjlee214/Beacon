package com.example.beacon;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PingInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PingInfoFragment extends Fragment {


    private static final String USERID = "userID";

    private String currentUserID; //id of the current user using the app
    private PingInfo pingInfo;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ping_info, container, false);
    }
}