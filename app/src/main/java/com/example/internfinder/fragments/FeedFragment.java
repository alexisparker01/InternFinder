package com.example.internfinder.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class FeedFragment extends Fragment {

    protected PostAdapter adapter;
    protected List<Post> allPosts;
    List<Post> followingPosts;
    RecyclerView rvFeedPosts;
    private SwipeRefreshLayout swipeContainer;
    private Spinner spinnerFeed;

    private boolean followingBoolean;
    private boolean nearYouBoolean;
    private boolean industryBoolean;

    private boolean hasQueriedPosts = false;


    public FeedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvFeedPosts = view.findViewById(R.id.rvFeedPosts);
        spinnerFeed = view.findViewById(R.id.spinnerFeed);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainerFeedActivity);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                if (followingBoolean) {
                    queryPosts("Following");
                } else if (nearYouBoolean) {
                    queryPosts("Near You");
                } else if (industryBoolean) {

                    queryPosts("industry");
                } else {
                    // queryPosts("");
                }

                swipeContainer.setRefreshing(false);

            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        String[] postSelections = new String[]{"Following", "Near You", "Industry"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, postSelections);

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

        swipeContainer.setRefreshing(false);
        rvFeedPosts.setAdapter(adapter);
        rvFeedPosts.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void queryOnlyFollowing() {
        hasQueriedPosts = true;
        allPosts.clear();
        adapter.notifyDataSetChanged();

        ParseQuery<Follow> query = ParseQuery.getQuery(Follow.class);
        query.whereEqualTo("from", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Follow>() {
            @Override
            public void done(List<Follow> followList, ParseException e) {

                ParseQuery<Post> query2 = ParseQuery.getQuery(Post.class);
                query2.whereEqualTo("user", ParseUser.getCurrentUser());
                query2.include("user");
                query2.addDescendingOrder("createdAt");
                query2.setLimit(20);

                query2.findInBackground(new FindCallback<Post>() {
                    @Override
                    public void done(List<Post> posts, ParseException e) {


                        for (Post post : posts) {
                            allPosts.add(post);
                            adapter.notifyDataSetChanged();

                        }
                    }
                });

                for (Follow follow : followList) {


                    ParseQuery<Post> query3 = ParseQuery.getQuery(Post.class);
                    query3.whereEqualTo("user", follow.getTo());
                    query3.include("user");
                    query3.addDescendingOrder("createdAt");
                    query3.setLimit(20);

                    query3.findInBackground(new FindCallback<Post>() {
                        @Override
                        public void done(List<Post> posts, ParseException e) {


                            for (Post post : posts) {
                                allPosts.add(post);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }

            }


        });

    }

    private void queryPosts(String option) {

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        if (option.equals("Near You")) {
            query.whereWithinMiles("latlng", ParseUser.getCurrentUser().getParseGeoPoint("currentLocation"), 10);
        } else if (option.equals("Industry")) {
            query.whereMatches("industry", ParseUser.getCurrentUser().getString("industry"));
        } else if (option.equals("Following")) {
            queryOnlyFollowing();
            return;
        }
        query.include(Post.KEY_USER);


        query.setLimit(20);

        query.addDescendingOrder("createdAt");

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {

                if (e != null) {
                    return;
                }

                allPosts.clear();
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();

                swipeContainer.setRefreshing(false);
            }
        });
    }


}
