package com.example.internfinder.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.internfinder.R;
import com.example.internfinder.activities.LoginActivity;
import com.example.internfinder.activities.MainActivity;
import com.parse.ParseUser;

public class LogoutFragment extends Fragment {

    private static final String TAG = "LogoutFragment";


    public LogoutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Toast.makeText(getContext(), "You are logging out!", Toast.LENGTH_SHORT).show();
                        logout();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        Fragment fragment = new FeedFragment();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.flLogout, fragment);
                        fragmentTransaction.addToBackStack(null);
                        View view = MainActivity.bottomNavigationView.findViewById(R.id.action_feed);
                        fragmentTransaction.commit();
                        view.performClick();
                        break;


                }
            }
        };

       AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure you want to logout of your account, " + ParseUser.getCurrentUser().getString("firstname") + "?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();


    }

    public void logout() {
        ParseUser.logOut();
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
      //  finish();
    }

    }