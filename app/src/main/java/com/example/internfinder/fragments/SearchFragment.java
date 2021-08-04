package com.example.internfinder.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

    private EditText etSearch;
    private RecyclerView rvUsers;

    protected UserAdapter adapter;
    protected List<ParseUser> allUsers;


    public SearchFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearch = view.findViewById(R.id.etSearch);
        rvUsers = view.findViewById(R.id.rvUsers2);

        allUsers = new ArrayList<>();
        adapter = new UserAdapter(getContext(), allUsers);


        rvUsers.setAdapter(adapter);


        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));

        findUsers("");

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                findUsers(s.toString());

            }
        });


    }

    public void findUsers(String letter) {

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.include("username");
        query.whereContains("username", letter.toLowerCase());


        query.findInBackground((users, e) -> {
            if (e == null) {

                allUsers.clear();
                allUsers.addAll(users);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        });

    }
}