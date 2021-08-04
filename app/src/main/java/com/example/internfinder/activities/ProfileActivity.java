package com.example.internfinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.internfinder.R;
import com.example.internfinder.adapters.PostAdapter;
import com.example.internfinder.models.Answers;
import com.example.internfinder.models.Follow;
import com.example.internfinder.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
    private TextView tvIndustry;
    private Button btnFollow;
    private ImageView ivBackProfile;
    private TextView tvArea;
    protected List<Answers> currentUserAnswersList;
    protected List<Answers> otherUserAnswersList;

    protected List<Follow> followingList;
    protected List<Follow> followersList;
    private ParseUser user;
    private Post post;
    private Follow follow;

    private int score = 0;

    boolean fromPost;
    boolean fromUser;

    private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvPosts;
    protected PostAdapter adapter;
    protected List<Post> allPosts;

    private TextView tvPercent;

    private TextView tvFollowers;
    private TextView tvFollowing;


    public void followCount(String key) {

        ParseQuery<Follow> query = ParseQuery.getQuery("Follow");

        if(key.equals(Follow.KEY_FROM)) {
            query.whereEqualTo("from", user);
            query.setLimit(15);
            query.addDescendingOrder("createdAt");
            try {
                followingList = query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            query.whereEqualTo("to", user);
            query.setLimit(15);
            query.addDescendingOrder("createdAt");
            try {
                followersList = query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    public void getAnswers(ParseUser user) {

        ParseQuery<Answers> query = ParseQuery.getQuery("Answers");


        query.whereEqualTo("user", user);
        query.setLimit(15);
        query.addDescendingOrder("createdAt");
        try {
            if(user.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                currentUserAnswersList = query.find();
            } else {
                otherUserAnswersList = query.find();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public float checkCompatability() {

        //otherUserAnswersList = getAnswers(post.getUser());

        Log.i(TAG, "Entering checkCompatability method");


        Log.i(TAG, String.valueOf(currentUserAnswersList.get(0).getQuestion1()));
        Log.i(TAG, String.valueOf(currentUserAnswersList.get(0).getQuestion2()));
        Log.i(TAG, String.valueOf(currentUserAnswersList.get(0).getQuestion3()));
        Log.i(TAG, String.valueOf(currentUserAnswersList.get(0).getQuestion4()));

        Log.i(TAG, String.valueOf(otherUserAnswersList.get(0).getQuestion1()));
        Log.i(TAG, String.valueOf(otherUserAnswersList.get(0).getQuestion2()));
        Log.i(TAG, String.valueOf(otherUserAnswersList.get(0).getQuestion3()));
        Log.i(TAG, String.valueOf(otherUserAnswersList.get(0).getQuestion4()));



        Log.i(TAG, currentUserAnswersList.toString());

        if(currentUserAnswersList.get(0).getQuestion1().equals(otherUserAnswersList.get(0).getQuestion1())) {
            score++;
        } else if(currentUserAnswersList.get(0).getQuestion2().equals(otherUserAnswersList.get(0).getQuestion2())) {
            score++;
        } else if(currentUserAnswersList.get(0).getQuestion3().equals(otherUserAnswersList.get(0).getQuestion3())) {
            score++;
        } else if (currentUserAnswersList.get(0).getQuestion4().equals(otherUserAnswersList.get(0).getQuestion4())) {
            score++;
        }

        return score;

    }

    public float getScore() {
        return (checkCompatability()/4)*100;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        post = Parcels.unwrap(getIntent().getParcelableExtra("Post"));
        user = Parcels.unwrap(getIntent().getParcelableExtra("User"));

        // score = Parcels.unwrap(getIntent().getParcelableExtra("Score"));


        if (post != null) {
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
        rvPosts = findViewById(R.id.rvUserPosts);
        tvIndustry = findViewById(R.id.tvIndustry);
        btnFollow = findViewById(R.id.btnFollow2);
        ivBackProfile = findViewById(R.id.ivBackProfile);
        tvArea = findViewById(R.id.tvArea);
        tvPercent = findViewById(R.id.tvPercent);
        tvFollowers = findViewById(R.id.tvFollowers);
        tvFollowing = findViewById(R.id.tvFollowing);


        ivBackProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnFollow.setVisibility(View.GONE);


        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);



            if (fromPost) {

                btnFollow.setVisibility(View.VISIBLE);

                tvFirstname.setText(post.getUser().getString("firstname"));
                tvLastname.setText(post.getUser().getString("lastname"));
                tvBio.setText(post.getUser().getString("bio"));
                tvUsernameProfile.setText("@" + post.getUser().getString("username"));
                tvIndustry.setText(post.getUser().getString("industry"));
                tvArea.setText(post.getUser().getString("area"));

                ParseFile profilePic = post.getUser().getParseFile("profilePic");
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
                        swipeContainer.setRefreshing(false);
                    }
                });

                queryPosts(post.getUser());

            } else if (fromUser) {
                btnFollow.setVisibility(View.VISIBLE);

                tvFirstname.setText(user.getString("firstname"));
                tvLastname.setText(user.getString("lastname"));
                tvBio.setText(user.getString("bio"));
                tvUsernameProfile.setText("@" + user.getString("username"));
                tvIndustry.setText(user.getString("industry"));
                tvArea.setText(user.getString("area"));

                ParseFile profilePic = user.getParseFile("profilePic");
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
                swipeContainer.setRefreshing(false);
            }



        currentUserAnswersList = new ArrayList<>();
        otherUserAnswersList = new ArrayList<>();

        getAnswers(ParseUser.getCurrentUser());
        getAnswers(user);

        if(currentUserAnswersList.size() > 0 && otherUserAnswersList.size() > 0) {
            tvPercent.setVisibility(View.VISIBLE);
            tvPercent.setText("You and this user are " + getScore() + "% compatable. Click to see their answers to the Questionnaire.");

            tvPercent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ProfileActivity.this, QuestionnaireActivity.class);
                    i.putExtra("User", Parcels.wrap(user));
                    startActivity(i);
                }
            });
        } else {
            tvPercent.setVisibility(View.GONE);
        }

        // set up the query on the Follow table

        follow = new Follow();

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


        followersList = new ArrayList<>();
        followingList = new ArrayList<>();

        followCount("from");
        followCount("to");

        tvFollowers.setText("Followers:\n" + followersList.size());
        tvFollowing.setText("Following:\n" + followingList.size());

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnFollow.getText().toString().equals("Follow")) {


                    // ParseUser otherUser = post.getParseUser("user");
                    // create an entry in the Follow table
                    follow.setFrom(ParseUser.getCurrentUser());
                    if(fromPost) {
                        follow.setTo(post.getUser());
                    } else {
                        follow.setTo(user);
                    }
                    follow.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e != null) {
                                Log.e(TAG, "error saving follow");
                            }
                        }
                    });

                    btnFollow.setText("Following");
                    btnFollow.setBackgroundColor(getResources().getColor(R.color.lime));

                } else if (btnFollow.getText().toString().equals("Following")) {


                    ParseQuery<Follow> query = ParseQuery.getQuery(Follow.class);
                    query.include(Follow.KEY_TO);
                    query.include(Follow.KEY_FROM);
                    if(fromPost) {
                        query.whereEqualTo(Follow.KEY_TO, post.getUser());
                    } else {
                        query.whereEqualTo(Follow.KEY_TO, user);
                    }
                    query.whereEqualTo(Follow.KEY_FROM, ParseUser.getCurrentUser());

                    query.findInBackground(new FindCallback<Follow>() {
                        @Override
                        public void done(List<Follow> follows, ParseException e) {
                            try {
                                for (Follow follow : follows) {

                                    follow.delete();
                                    follow.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {

                                            if(e!=null) {
                                                Log.e(TAG, "error deleting follow");
                                            }
                                            btnFollow.setText("Follow");
                                            btnFollow.setBackgroundColor(getResources().getColor(R.color.purple_200));
                                            Log.i("OtherUser", "successfully deleted follow");
                                            Toast.makeText(ProfileActivity.this, "you unfollowed @" + user.getUsername(), Toast.LENGTH_SHORT).show();

                                            followCount("from");
                                            followCount("to");

                                            tvFollowers.setText("Followers:\n" + followersList.size());
                                            tvFollowing.setText("Following:\n" + followingList.size());
                                        }
                                    });

                                }
                            } catch (ParseException parseException) {
                                parseException.printStackTrace();
                                Log.i("OtherUser", "problem with deleting follow");
                            }
                        }
                    });


                }


                followCount("from");
                followCount("to");

                tvFollowers.setText("Followers:\n" + followersList.size());
                tvFollowing.setText("Following:\n" + followingList.size());
            }
        });


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

                for (Post post : posts) {

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