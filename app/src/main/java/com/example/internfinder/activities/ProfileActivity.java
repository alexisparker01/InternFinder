package com.example.internfinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.internfinder.R;
import com.example.internfinder.adapters.PostAdapter;
import com.example.internfinder.models.Follow;
import com.example.internfinder.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private TextView tvUsernameProfile;
    private TextView tvFirstname;
    private TextView tvLastname;
    private TextView tvBio;
    private ImageView ivProfilePicProfile;
    private Button btnEditProfile;
    private TextView tvIndustry;
    private Button btnFollow;
    private ImageView ivBackProfile;

    private ParseUser user;
    private Post post;
    private Follow follow;

    boolean fromPost;
    boolean fromUser;

    private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvPosts;
    protected PostAdapter adapter;
    protected List<Post> allPosts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        post = Parcels.unwrap(getIntent().getParcelableExtra("Post"));
        user = Parcels.unwrap(getIntent().getParcelableExtra("User"));
        
        Log.i(TAG, "profile activity uuser: " + user.getUsername());

        if(post != null) {
            fromPost = true;
        }

        if (user != null) {
            fromUser = true;
        }



        tvUsernameProfile = findViewById(R.id.tvUsername);
        tvFirstname = findViewById(R.id.etFirstName);
        tvLastname = findViewById(R.id.tvLastName);
        tvBio = findViewById(R.id.tvBio);
        ivProfilePicProfile = findViewById(R.id.ivProfilePic);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        rvPosts = findViewById(R.id.rvUserPosts);
        tvIndustry = findViewById(R.id.tvIndustry);
        btnFollow = findViewById(R.id.btnFollow2);
        ivBackProfile = findViewById(R.id.ivBackProfile);


        ivBackProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.putExtra("openFeedFragment",true);
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);
            }
        });

        btnFollow.setVisibility(View.GONE);
        btnEditProfile.setVisibility(View.GONE);




        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        follow = new Follow();


        // if we are on the logged in user's profile
        if(ParseUser.getCurrentUser().getUsername().equals(user.getUsername())) {
            btnFollow.setVisibility(View.GONE);
            btnEditProfile.setVisibility(View.VISIBLE);

            tvFirstname.setText(ParseUser.getCurrentUser().getString("firstname"));
            tvLastname.setText(ParseUser.getCurrentUser().getString("lastname"));
            tvBio.setText(ParseUser.getCurrentUser().getString("bio"));
            tvUsernameProfile.setText("@" + ParseUser.getCurrentUser().getString("username"));
            tvIndustry.setText(ParseUser.getCurrentUser().getString("industry"));

            // load profile pic
            ParseFile profilePic = ParseUser.getCurrentUser().getParseFile("profilePic");
            if (profilePic != null) {
                Glide.with(this).load(profilePic.getUrl()).into(ivProfilePicProfile);

            }

            // Setup refresh listener which triggers new data loading
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Your code to refresh the list here.
                    // Make sure you call swipeContainer.setRefreshing(false)
                    // once the network request has completed successfully.
                    queryPosts(ParseUser.getCurrentUser());
                }
            });

            queryPosts(ParseUser.getCurrentUser());


        } else {
            // if we are on another user's profile

            if(fromPost == true) {
                btnFollow.setVisibility(View.VISIBLE);
                btnEditProfile.setVisibility(View.GONE);

                tvFirstname.setText(post.getUser().getString("firstname"));
                tvLastname.setText(post.getUser().getString("lastname"));
                tvBio.setText(post.getUser().getString("bio"));
                tvUsernameProfile.setText("@" + post.getUser().getString("username"));
                tvIndustry.setText(post.getUser().getString("industry"));

                ParseFile profilePic = post.getUser().getParseFile("profilePicture");
                if (profilePic != null) {
                    Glide.with(this).load(profilePic.getUrl()).into(ivProfilePicProfile);

                }

                // Setup refresh listener which triggers new data loading
                swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // Your code to refresh the list here.
                        // Make sure you call swipeContainer.setRefreshing(false)
                        // once the network request has completed successfully.
                        queryPosts(post.getUser());
                    }
                });

                queryPosts(post.getUser());

            } else if (fromUser == true) {
                btnFollow.setVisibility(View.VISIBLE);
                btnEditProfile.setVisibility(View.GONE);

                tvFirstname.setText(user.getString("firstname"));
                tvLastname.setText(user.getString("lastname"));
                tvBio.setText(user.getString("bio"));
                tvUsernameProfile.setText("@" + user.getString("username"));
                tvIndustry.setText(user.getString("industry"));

                ParseFile profilePic = user.getParseFile("profilePicture");
                if (profilePic != null) {
                    Glide.with(this).load(profilePic.getUrl()).into(ivProfilePicProfile);

                }
                // Setup refresh listener which triggers new data loading
                swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // Your code to refresh the list here.
                        // Make sure you call swipeContainer.setRefreshing(false)
                        // once the network request has completed successfully.
                        queryPosts(user);
                    }
                });
                queryPosts(user);
            }



        }

         // set up the query on the Follow table
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        query.whereEqualTo("to", user);
        query.whereEqualTo("from", ParseUser.getCurrentUser());

        // execute the query
        query.findInBackground(new FindCallback<ParseObject>() {
                                   public void done(List<ParseObject> followList, ParseException e) {

                                       if (followList.size() > 0) {

                                           btnFollow.setText("Following");
                                           btnFollow.setBackgroundColor(getResources().getColor(R.color.lime));

                                       }
                                   }
                               });


            btnFollow.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {

                                                 if (btnFollow.getText().toString().equals("Follow")) {


                                                    // ParseUser otherUser = post.getParseUser("user");
                                                     ParseUser otherUser = user;

                                                     // create an entry in the Follow table
                                                     follow.setFrom(ParseUser.getCurrentUser());
                                                     follow.setTo(user);
                                                     //  follow.put("date", Date());
                                                     follow.saveInBackground();

                                                     btnFollow.setText("Following");
                                                     btnFollow.setBackgroundColor(getResources().getColor(R.color.lime));

                                                 } else if (btnFollow.getText().toString().equals("Following")) {


                                                     ParseQuery<Follow> query = ParseQuery.getQuery(Follow.class);
                                                     query.include(Follow.KEY_TO);
                                                     query.include(Follow.KEY_FROM);
                                                     query.whereEqualTo(Follow.KEY_TO, post.getUser());
                                                     query.whereEqualTo(Follow.KEY_FROM, ParseUser.getCurrentUser());

                                                     query.findInBackground(new FindCallback<Follow>() {
                                                         @Override
                                                         public void done(List<Follow> follows, ParseException e) {
                                                             try {
                                                                 for (Follow follow : follows) {

                                                                         follow.delete();
                                                                         follow.saveInBackground();
                                                                         btnFollow.setText("Follow");
                                                                         btnFollow.setBackgroundColor(getResources().getColor(R.color.purple_200));
                                                                         Log.i("OtherUser", "deleted follow");

                                                                 }
                                                             }catch (ParseException parseException) {
                                                                 parseException.printStackTrace();
                                                                 Log.i("OtherUser", "problem with deleting follow");
                                                             }
                                                         }
                                                     });


                                                     }}});








        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // initialize the array that will hold posts and create the adapter for the profile
        allPosts = new ArrayList<>();
        adapter = new PostAdapter(this, allPosts);

        // set the adapter on the recycler view
        rvPosts.setAdapter(adapter);


        // set the layout manager on the recycler view
        rvPosts.setLayoutManager(new LinearLayoutManager(this));


        // edit profile button click listener
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, ModifyProfileActivity.class);
                startActivity(i);
            }
        });
    }


    private void queryPosts(ParseUser theUser) {
        // specifying that i am querying data from the Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, theUser);

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