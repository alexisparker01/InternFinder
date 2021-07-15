package com.example.internfinder;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.internfinder.LogoutActivity;
import com.example.internfinder.MainActivity;
import com.example.internfinder.Post;
import com.example.internfinder.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {
    protected FeedActivityAdapter adapter;
    protected List<Post> allPosts;
    RecyclerView rvFeedPosts;
    private SwipeRefreshLayout swipeContainer;


    @Override
    protected void onResume() {
        // fired whenever the user comes back to this activity
        super.onResume();

        queryPosts();
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
                queryPosts();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvFeedPosts = findViewById(R.id.rvFeedPosts);

        allPosts = new ArrayList<>();
        adapter = new FeedActivityAdapter(this, allPosts);

        // set the adapter on the recycler view
        rvFeedPosts.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvFeedPosts.setLayoutManager(new LinearLayoutManager(this));

        // Define ActionBar object
        ActionBar actionBar;
        actionBar = getSupportActionBar();

        // Define ColorDrawable object and parse color
        // using parseColor method
        // with color hash code as its parameter
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#ffffff"));

        // Set BackgroundDrawable
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color=\"black\">" + getString(R.string.app_name) + "</font>"));
    }





    private void queryPosts() {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
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

    public void postDetails() {
        Toast.makeText(FeedActivity.this, "post clicked", Toast.LENGTH_LONG).show();
    }


}