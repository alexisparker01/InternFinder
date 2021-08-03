package com.example.internfinder.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.internfinder.R;
import com.example.internfinder.adapters.UserAdapter;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    private EditText etSearch;
    private RecyclerView rvUsers;

    protected UserAdapter adapter;
    protected List<ParseUser> allUsers;


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearch = view.findViewById(R.id.etSearch);
        rvUsers = view.findViewById(R.id.rvUsers2);

        // initialize the array that will hold posts and create the adapter for the profile
        allUsers = new ArrayList<>();
        adapter = new UserAdapter(getContext(), allUsers);

        // set the adapter on the recycler view
        rvUsers.setAdapter(adapter);

        // set the layout manager on the recycler view
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));


        // TODO: when search page loads you will automatically see users in your area
        findUsers("");

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
                // Here you may access the text from the object s, like s.toString()

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {

                // TODO Auto-generated method stub
                Log.i(TAG, "letter typed: " + s);
                findUsers(s.toString());

            }
        });


    }

    public void findUsers(String letter) {

        ParseQuery<ParseUser> query = ParseUser.getQuery();

        // compound query parse

        query.include("username");
        //query.include("firstname");
        // query.include("lastname");
        query.whereContains("username", letter.toLowerCase());


        query.findInBackground((users, e) -> {
            if (e == null) {
                // The query was successful, returns the users that matches
                // the criteria.
                for (ParseUser user : users) {
                    Log.i(TAG, user.getUsername());
                }
                allUsers.clear();
                allUsers.addAll(users);
                adapter.notifyDataSetChanged();
            } else {
                // Something went wrong.
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        });

    }
}