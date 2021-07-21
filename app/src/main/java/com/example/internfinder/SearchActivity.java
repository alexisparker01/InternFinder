package com.example.internfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.internfinder.adapters.UserAdapter;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    EditText etSearch;
    RecyclerView rvUsers;
    Button btnSubmitSearch;

    protected UserAdapter adapter;
    protected List<ParseUser> allUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        etSearch = findViewById(R.id.etSearch);
        rvUsers = findViewById(R.id.rvUsers);
        btnSubmitSearch = findViewById(R.id.btnSubmitSearch);

        // initialize the array that will hold posts and create the adapter for the profile
        allUsers = new ArrayList<>();
        adapter = new UserAdapter(this, allUsers);

        // set the adapter on the recycler view
        rvUsers.setAdapter(adapter);


        // set the layout manager on the recycler view
        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        btnSubmitSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // query users from server
                findUsers(etSearch.getText().toString());

            }
        });


    }

    public void findUsers(String username) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.include("username");
        query.whereEqualTo("username", username);
        query.findInBackground((users, e) -> {
            if (e == null) {
                // The query was successful, returns the users that matches
                // the criteria.
                for(ParseUser user : users) {
                    Log.d("User List ",(user.getUsername()));
                    allUsers.clear();
                    allUsers.addAll(users);
                    adapter.notifyDataSetChanged();
                }
            } else {
                // Something went wrong.
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });


        etSearch.setText("");
    }


}