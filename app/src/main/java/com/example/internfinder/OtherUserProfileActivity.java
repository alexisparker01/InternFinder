package com.example.internfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
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

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class OtherUserProfileActivity extends AppCompatActivity {

    TextView tvUsernameProfileOtherUser;
    TextView tvFirstnameOtherUser;
    TextView tvLastnameOtherUser;
    TextView tvBioOtherUser;
    ImageView ivProfilePicProfileOtherUser;
    Post post;

    private SwipeRefreshLayout swipeContainerOtherUser;
    private RecyclerView rvPostsOtherUser;
    protected OtherUserProfileAdapter adapter;
    protected List<Post> allPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);

        post = Parcels.unwrap(getIntent().getParcelableExtra("Post"));

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        tvUsernameProfileOtherUser = findViewById(R.id.tvUsernameOtherUser);
        tvFirstnameOtherUser = findViewById(R.id.tvFirstNameOtherUser);
        tvLastnameOtherUser = findViewById(R.id.tvLastNameOtherUser);
        tvBioOtherUser = findViewById(R.id.tvBioOtherUser);
        ivProfilePicProfileOtherUser = findViewById(R.id.ivProfilePicOtherUser);
        rvPostsOtherUser= findViewById(R.id.rvOtherUserPosts);



        swipeContainerOtherUser = (SwipeRefreshLayout) findViewById(R.id.swipeContainerOtherUser);
        // Setup refresh listener which triggers new data loading
        swipeContainerOtherUser.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                queryPosts();
            }
        });
        // Configure the refreshing colors
        swipeContainerOtherUser.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // initialize the array that will hold posts and create the adapter for the profile
        allPosts = new ArrayList<>();
        adapter = new OtherUserProfileAdapter(this, allPosts);

        // set the adapter on the recycler view
        rvPostsOtherUser.setAdapter(adapter);

        // set the layout manager on the recycler view
        rvPostsOtherUser.setLayoutManager(new LinearLayoutManager(this));

        // query posts from server
        queryPosts();

        // add user data to fields
        tvFirstnameOtherUser.setText(post.getUser().getString("firstname"));
        tvLastnameOtherUser.setText(post.getUser().getString("lastname"));
        tvBioOtherUser.setText(post.getUser().getString("bio"));
        tvUsernameProfileOtherUser.setText("@" + post.getUser().getUsername());

        // load profile pic
        ParseFile profilePic = ParseUser.getCurrentUser().getParseFile("profilePicture");
        if (profilePic != null) {
            Glide.with(this).load(profilePic.getUrl()).into(ivProfilePicProfileOtherUser);
        }

    }



    private void queryPosts() {
        // specifying that i am querying data from the Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        // include data referred by user key
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, post.getUser());


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
                    Log.e("OtherUserProfileActivity", "Problem with fetching posts", e);
                    return;
                }


                // printing each of the posts I get to see if I'm getting all the posts from the server

                for (Post post: posts) {

                    Log.i("OtherUserProfileActivity", "user: " + post.getUser().getUsername() + " desc: " + post.getDescription());

                }

                // save received posts to list and notify adapter of new data
                allPosts.clear();
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });

    }
}
