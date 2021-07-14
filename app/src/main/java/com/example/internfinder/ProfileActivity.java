package com.example.internfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    TextView tvUsernameProfile;
    TextView tvFirstname;
    TextView tvLastname;
    TextView tvBio;
    ImageView ivProfilePicProfile;
    Button btnEditProfile;

    private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvPosts;
    protected ProfilePostsAdapter adapter;
    protected List<Post> allPosts;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        tvUsernameProfile = findViewById(R.id.tvUsername);
        tvFirstname = findViewById(R.id.etFirstName);
        tvLastname = findViewById(R.id.tvLastName);
        tvBio = findViewById(R.id.tvBio);
        ivProfilePicProfile = findViewById(R.id.ivProfilePic);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        rvPosts = findViewById(R.id.rvUserPosts);



        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
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

        // initialize the array that will hold posts and create the adapter for the profile
        allPosts = new ArrayList<>();
        adapter = new ProfilePostsAdapter(this, allPosts);

        // set the adapter on the recycler view
        rvPosts.setAdapter(adapter);

        // set the layout manager on the recycler view
        rvPosts.setLayoutManager(new LinearLayoutManager(this));

        // query posts from server
        queryPosts();

        // add user data to fields
        tvFirstname.setText(ParseUser.getCurrentUser().getString("firstname"));
        tvLastname.setText(ParseUser.getCurrentUser().getString("lastname"));
        tvBio.setText(ParseUser.getCurrentUser().getString("bio"));
        tvUsernameProfile.setText("@" + ParseUser.getCurrentUser().getString("username"));

        // load profile pic
        ParseFile profilePic = ParseUser.getCurrentUser().getParseFile("profilePicture");
        if (profilePic != null) {
            Glide.with(this).load(profilePic.getUrl()).into(ivProfilePicProfile);
        }

        // edit profile button click listener
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, ModifyProfileActivity.class);
                startActivity(i);
            }
        });
    }



    private void queryPosts() {
        // specifying that i am querying data from the Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        // include data referred by user key
       query.include(Post.KEY_USER);
       query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());

        // limit query to latest 15 posts
        query.setLimit(15);

        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");

        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e("ProfileActivity", "Problem with fetching posts", e);
                    return;
                }


                // printing each of the posts I get to see if I'm getting all the posts from the server

                for (Post post: posts) {

                    Log.i("ProfileActivity", "user: " + post.getUser().getUsername() + " desc: " + post.getDescription());

                }

                // save received posts to list and notify adapter of new data
                allPosts.clear();
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });

    }
}