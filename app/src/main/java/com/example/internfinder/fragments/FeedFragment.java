package com.example.internfinder.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.internfinder.R;
import com.example.internfinder.activities.CreatePostActivity;
import com.example.internfinder.adapters.PostAdapter;
import com.example.internfinder.models.Follow;
import com.example.internfinder.models.Post;
import com.melnykov.fab.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {

    private static final String TAG = "FeedFragment";
    protected PostAdapter adapter;
    protected List<Post> allPosts;
    List<Post> followingPosts;
    RecyclerView rvFeedPosts;
    private SwipeRefreshLayout swipeContainer;
    private Button btnCreate;
    private Spinner spinnerFeed;

    private boolean followingBoolean;
    private boolean nearYouBoolean;
    private boolean industryBoolean;

    private boolean hasQueriedPosts = false;



    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvFeedPosts = view.findViewById(R.id.rvFeedPosts);
        btnCreate = view.findViewById(R.id.btnCreate);
        spinnerFeed = view.findViewById(R.id.spinnerFeed);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainerFeedActivity);


        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                if(followingBoolean) {
                    queryPosts("Following");
                } else if (nearYouBoolean) {
                    queryPosts("Near You");
                } else if(industryBoolean) {

                    queryPosts("industry");
                } else {
                   // queryPosts("");
                }

            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



        String[] postSelections = new String[]{"Following", "Near You", "Industry"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, postSelections);

        //set the spinners adapter to the previously created one.
        spinnerFeed.setAdapter(spinnerAdapter);

        spinnerFeed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {


                if (parent.getItemAtPosition(position).equals("Following")) {
                    nearYouBoolean = false;
                    industryBoolean = false;
                    followingBoolean = true;


                        queryPosts("Following");


                } else if (parent.getItemAtPosition(position).equals("Near You")) {

                    nearYouBoolean = true;
                    followingBoolean = false;
                    industryBoolean = false;
                    queryPosts("Near You");


                } else if (parent.getItemAtPosition(position).equals("Industry")) {

                    industryBoolean = true;
                    followingBoolean = false;
                    nearYouBoolean = false;

                    queryPosts("Industry");

                } else {
                   // queryOnlyFollowing();
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                queryOnlyFollowing();
            }
        });



       // allPosts2 = new ArrayList<>();
        allPosts = new ArrayList<>();
        adapter = new PostAdapter(getContext(), allPosts);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.attachToRecyclerView(rvFeedPosts);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getContext(), CreatePostActivity.class);
                startActivity(i);

            }
        });
        // set the adapter on the recycler view
        rvFeedPosts.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvFeedPosts.setLayoutManager(new LinearLayoutManager(getContext()));



    }
    private void queryOnlyFollowing() {
        hasQueriedPosts = true;
        Log.i(TAG, "queryOnlyFollowing: starting the method");
        allPosts.clear();
        adapter.notifyDataSetChanged();

        ParseQuery<Follow> query = ParseQuery.getQuery(Follow.class);
        query.whereEqualTo("from", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Follow>() {
            @Override
            public void done(List<Follow> followList, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.i(TAG, "NULL REACHED 1: " + e.getMessage());
                    return;
                }

                for (Follow follow : followList) {
                    Log.i(TAG, "size of follow list: " + followList.size());
                    // Log.i(TAG, "person the current user follows: " + follow.getParseUser("to").getUsername());
                    // e == null --> success
                    ParseQuery<Post> query2 = ParseQuery.getQuery(Post.class);
                    query2.whereEqualTo("user", follow.getTo());
                    query2.include("user");
                    query2.addDescendingOrder("createdAt");
                    query2.setLimit(20);

                    query2.findInBackground(new FindCallback<Post>() {
                        @Override
                        public void done(List<Post> posts, ParseException e) {
                            Log.i(TAG, "size of post list: " + posts.size());
                            // check for errors
                            if (e != null) {
                                Log.i(TAG, "NULL REACHED 2");
                                return;
                            }

                            // e == null --> success
                            // adds post from user
                            for (Post post : posts) {
                                Log.i(TAG, "post description: " + post.getDescription() + " post type: " + post.getType());
                                allPosts.add(post);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });


                    query2.whereEqualTo("user", follow.getFrom());
                    query2.include("user");
                    query2.addDescendingOrder("createdAt");
                    query2.setLimit(20);

                    query2.findInBackground(new FindCallback<Post>() {
                        @Override
                        public void done(List<Post> posts, ParseException e) {
                            Log.i(TAG, "size of post list: " + posts.size());
                            // check for errors
                            if (e != null) {
                                Log.i(TAG, "NULL REACHED 2");
                                return;
                            }

                            // e == null --> success
                            // adds post from user
                            for (Post post : posts) {
                                Log.i(TAG, "post description: " + post.getDescription() + " post type: " + post.getType());
                                allPosts.add(post);
                                adapter.notifyDataSetChanged();

                            }
                        }
                    });
                }

                swipeContainer.setRefreshing(false);
            }



        });

    }

    private void queryPosts(String option) {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        if(option.equals("Near You")) {
            query.whereWithinMiles("latlng", ParseUser.getCurrentUser().getParseGeoPoint("currentLocation"), 10);
        } else if (option.equals("Industry")) {
            query.whereMatches("industry", ParseUser.getCurrentUser().getString("industry"));
        } else if(option.equals("Following")) {
            queryOnlyFollowing();
            return;
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
