package com.example.internfinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.internfinder.R;
import com.example.internfinder.adapters.CommentAdapter;
import com.example.internfinder.models.Comment;
import com.example.internfinder.models.Post;
import com.example.internfinder.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class OpenPostActivity extends AppCompatActivity {


    private TextView tvUsernameProfileOpenPost;
    private TextView tvFirstnameOpenPost;
    private TextView tvLastnameOpenPost;
    private ImageView ivProfilePicProfileOpenPost;
    private TextView tvCreatedAtOpenPost;
    private TextView tvDescriptionOpenPost;
    private Post post;
    private User user;
    private ImageView ivImageOpenPost;
    private EditText etComment;
    private Button btnSubmitComment;
    private TextView tvLocation;
    private TextView tvLocationName;


    private SwipeRefreshLayout swipeContainerComments;
    private RecyclerView rvComments;
    protected CommentAdapter adapter;
    protected List<Comment> allComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_post);

       post = Parcels.unwrap(getIntent().getParcelableExtra("Post"));


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        tvUsernameProfileOpenPost= findViewById(R.id.tvUsernameOpenPost);
        tvFirstnameOpenPost = findViewById(R.id.tvFirstnameOpenPost);
        tvLastnameOpenPost = findViewById(R.id.tvLastnameOpenPost);
        tvCreatedAtOpenPost = findViewById(R.id.tvCreatedAtOpenPost);
        ivProfilePicProfileOpenPost = findViewById(R.id.ivProfilePictureOpenPost);
        tvDescriptionOpenPost = findViewById(R.id.tvDescriptionOpenPost);
        rvComments = findViewById(R.id.rvComments);
        ivImageOpenPost = findViewById(R.id.ivImageOpenPost);
        etComment = findViewById(R.id.etComment);
        btnSubmitComment = findViewById(R.id.btnSubmitComment);
        tvLocation = findViewById(R.id.tvPostLocation2);
        tvLocationName = findViewById(R.id.tvLocationName);

        tvUsernameProfileOpenPost.setOnClickListener(new View.OnClickListener() {
                                                         @Override
                                                         public void onClick(View v) {
                                                             if(tvUsernameProfileOpenPost.getText().equals("@"+ParseUser.getCurrentUser().getUsername())) {

                                                                 Intent intent = new Intent(OpenPostActivity.this, MainActivity.class);
                                                                 intent.putExtra("openProfileFragment",true);
                                                                 // overridePendingTransition(0, 0);
                                                                 intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                                 // context.finish();
                                                                startActivity(intent);
                                                             } else {
                                                                 Intent i = new Intent(OpenPostActivity.this, ProfileActivity.class);
                                                                 i.putExtra("User", Parcels.wrap(post.getUser()));
                                                                 i.putExtra("Post", Parcels.wrap(post));
                                                                 startActivity(i);

                                                             }
                                                         }
                                                     });

                btnSubmitComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Comment comment = new Comment();
                        comment.setText(etComment.getText().toString());
                        comment.setUser(ParseUser.getCurrentUser());
                        comment.setPost(post);
                        comment.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.e("openpost", "error while saving");
                                }
                                etComment.setText("");
                                Log.i("openpost", "SAVED SUCCESFFULLY");
                            }
                        });
                    }
                });

        /** swipe refresher and adapter for comments **/
        swipeContainerComments = (SwipeRefreshLayout) findViewById(R.id.swipeContainerComment);
        // Setup refresh listener which triggers new data loading
        swipeContainerComments.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                queryComments();
                swipeContainerComments.setRefreshing(false);

            }
        });

        // Configure the refreshing colors
        swipeContainerComments.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // initialize the array that will hold comments and create the adapter for the profile
        allComments = new ArrayList<>();
        adapter = new CommentAdapter(this, allComments);

        // set the adapter on the recycler view
        rvComments.setAdapter(adapter);

        // set the layout manager on the recycler view
        rvComments.setLayoutManager(new LinearLayoutManager(this));

        // query comments from server
        queryComments();


       // Log.i("TAG", "post: " + post.getParseUser("user").getString("firstname"));
        // add user data to fields
       // fetchIfNeeded()getParseObject("content");
        try {
            tvFirstnameOpenPost.setText(post.getUser().fetchIfNeeded().getString("firstname"));
            tvLastnameOpenPost.setText(post.getUser().fetchIfNeeded().getString("lastname"));
            tvUsernameProfileOpenPost.setText("@" + post.getParseUser("user").fetchIfNeeded().getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        tvDescriptionOpenPost.setText(post.getDescription());
        Date createdAt = post.getCreatedAt();
        String timeAgo = Post.calculateTimeAgo(createdAt);
        tvCreatedAtOpenPost.setText(timeAgo);


        if(post.getType().equals("event")) {
            tvLocation.setText(post.getLocation());
            tvLocationName.setText(post.getLocationName());
        }

        // load profile pic

        ParseFile profilePic = post.getUser().getParseFile("profilePic");
        if (profilePic != null) {
            Glide.with(this).load(profilePic.getUrl()).into(ivProfilePicProfileOpenPost);
        }

        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(OpenPostActivity.this).load(image.getUrl()).into(ivImageOpenPost);
        } else {
            ivImageOpenPost.setVisibility(View.GONE);
        }

    }

    private void queryComments() {
        // specifying that i am querying data from the Comment.class
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);

        // include data referred by post key
        query.include(Comment.KEY_POST);
        // the problem is that post.getobjectid() is a string and i need the parse object of the post
        query.whereEqualTo(Comment.KEY_POST, post);

            // limit query to latest 15 posts
        query.setLimit(15);

        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");

        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e("OpenPostActivity", "Problem with fetching comments", e);
                    return;
                }


                // printing each of the posts I get to see if I'm getting all the posts from the server

                for (Comment comment: comments) {

                   Log.i("OpenPostActivity", "comment: " + comment.getUser());

                }

                // save received posts to list and notify adapter of new data
                allComments.clear();
                allComments.addAll(comments);
                adapter.notifyDataSetChanged();
            }
        });

    }
}
