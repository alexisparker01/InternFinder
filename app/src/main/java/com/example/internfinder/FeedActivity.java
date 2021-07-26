package com.example.internfinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.internfinder.adapters.PostAdapter;
import com.example.internfinder.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {
    private static final String TAG = "FeedActivity";
    protected PostAdapter adapter;
    protected List<Post> allPosts;
    RecyclerView rvFeedPosts;
    private SwipeRefreshLayout swipeContainer;
    Button btnCreatePost;
    private Spinner spinnerFeed;


    @Override
    protected void onResume() {
        // fired whenever the user comes back to this activity
        super.onResume();

        queryPosts("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // fired whenever the activity is first created
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainerFeedActivity);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                queryPosts("");
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvFeedPosts = findViewById(R.id.rvFeedPosts);
        btnCreatePost = findViewById(R.id.btnCreatePost);
        spinnerFeed = findViewById(R.id.spinnerFeed);

        btnCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(FeedActivity.this, CreatePostActivity.class);
                startActivity(i);

            }
        });

        String[] postSelections = new String[]{"Following", "Near You", "Industry", "Event Posts"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, postSelections);

        //set the spinners adapter to the previously created one.
        spinnerFeed.setAdapter(spinnerAdapter);

        spinnerFeed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {


                if (parent.getItemAtPosition(position).equals("Following")) {

                    queryPosts("Following");


                } else if (parent.getItemAtPosition(position).equals("Near You")) {

                    queryPosts("Near You");


                } else if (parent.getItemAtPosition(position).equals("Industry")) {

                    queryPosts("Industry");


                } else if (parent.getItemAtPosition(position).equals("Event Posts")) {
                    queryPosts("event");
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                Log.i(TAG, "Nothing Selected");
            }
        });


        allPosts = new ArrayList<>();
        adapter = new PostAdapter(this, allPosts);

        // set the adapter on the recycler view
        rvFeedPosts.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvFeedPosts.setLayoutManager(new LinearLayoutManager(this));



    }

    private void queryPosts(String option) {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query;
        query = ParseQuery.getQuery(Post.class);
        if(option.equals("Near You")) {
            query.whereWithinMiles("latlng", ParseUser.getCurrentUser().getParseGeoPoint("location"), 10);
        } else if (option.equals("Industry")) {
            query.whereMatches("industry", ParseUser.getCurrentUser().getString("industry"));
        } else if (option.equals("event")) {
            query.whereMatches("type", option);
        }
            query.include(Post.KEY_USER);


        // limit query to latest 20 items
        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    return;
                }
                // e == null --> success

                // save received posts to list and notify adapter of new data
                allPosts.clear();
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();

                swipeContainer.setRefreshing(false);
            }
        });
    }


}