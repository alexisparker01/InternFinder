package com.example.internfinder.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.internfinder.R;
import com.example.internfinder.activities.ModifyProfileActivity;
import com.example.internfinder.adapters.PostAdapter;
import com.example.internfinder.models.Follow;
import com.example.internfinder.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileActivity";
    private TextView tvUsernameProfile;
    private TextView tvFirstname;
    private TextView tvLastname;
    private TextView tvBio;
    private ImageView ivProfilePic;
    private Button btnEditProfile;
    private TextView tvIndustry;
    private TextView tvCity;

    private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvPosts;
    protected PostAdapter adapter;
    protected List<Post> allPosts;

    private TextView tvFollowing;
    List<Follow> followingList;
    private TextView tvFollowers;
    List<Follow> followersList;

    public int followersCount;
    public int followingCount;

    public void followCount(String key) {

        ParseQuery<Follow> query = ParseQuery.getQuery("Follow");

        if(key.equals(Follow.KEY_FROM)) {
            query.whereEqualTo("from", ParseUser.getCurrentUser());
            query.setLimit(15);
            query.addDescendingOrder("createdAt");
            try {
                followingList = query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            query.whereEqualTo("to", ParseUser.getCurrentUser());
            query.setLimit(15);
            query.addDescendingOrder("createdAt");
            try {
                    followersList = query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvUsernameProfile = view.findViewById(R.id.tvUsername);
        tvFirstname = view.findViewById(R.id.etFirstName);
        tvLastname = view.findViewById(R.id.tvLastName);
        tvBio = view.findViewById(R.id.tvBio);
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        rvPosts = view.findViewById(R.id.rvUserPosts);
        tvIndustry = view.findViewById(R.id.tvIndustry);
        tvCity = view.findViewById(R.id.tvCity);
        tvFollowers = view.findViewById(R.id.tvFollowers);
        tvFollowing = view.findViewById(R.id.tvFollowing);

       followCount("from");
       followCount("to");

        Log.i(TAG, "following count: " + followingList.size());
        Log.i(TAG, "followers count: " + followersList.size());


        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

            tvFirstname.setText(ParseUser.getCurrentUser().getString("firstname"));
            tvLastname.setText(ParseUser.getCurrentUser().getString("lastname"));
            tvBio.setText(ParseUser.getCurrentUser().getString("bio"));
            tvUsernameProfile.setText("@" + ParseUser.getCurrentUser().getString("username"));
            tvIndustry.setText(ParseUser.getCurrentUser().getString("industry"));
            Log.i(TAG, "this is the area: " + ParseUser.getCurrentUser().getString("area"));
            tvCity.setText(ParseUser.getCurrentUser().getString("area"));
           tvFollowers.setText("Followers:\n" + String.valueOf(followersList.size()));
           tvFollowing.setText("Following:\n" + String.valueOf(followingList.size()));

            // load profile pic
            ParseFile profilePic = ParseUser.getCurrentUser().getParseFile("profilePic");
            if (profilePic != null) {
                Glide.with(getContext()).load(profilePic.getUrl()).into(ivProfilePic);

            }

            // Setup refresh listener which triggers new data loading
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Your code to refresh the list here.
                    // Make sure you call swipeContainer.setRefreshing(false)
                    // once the network request has completed successfully.
                    queryPosts(ParseUser.getCurrentUser());
                    swipeContainer.setRefreshing(false);
                }
            });

            queryPosts(ParseUser.getCurrentUser());

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // initialize the array that will hold posts and create the adapter for the profile
        allPosts = new ArrayList<>();
        adapter = new PostAdapter(getContext(), allPosts);

        // set the adapter on the recycler view
        rvPosts.setAdapter(adapter);



        // set the layout manager on the recycler view
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));


        // edit profile button click listener
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ModifyProfileActivity.class);
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

