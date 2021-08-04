package com.example.internfinder.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.internfinder.R;
import com.example.internfinder.adapters.UserAdapter;
import com.example.internfinder.models.Follow;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class FollowListsActivity extends AppCompatActivity {
    protected List<ParseUser> userList;
    protected List<Follow> followersList;
    protected List<Follow> followingList;
    RecyclerView rvFollows;
    TextView tvTitle;
    UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_lists);

        followersList = Parcels.unwrap(getIntent().getParcelableExtra("FollowersList"));
        followingList = Parcels.unwrap(getIntent().getParcelableExtra("FollowingList"));
        userList = new ArrayList<>();


        rvFollows = findViewById(R.id.rvFollows);
        tvTitle = findViewById(R.id.tvTitle);

        if (followersList != null) {
            for (Follow f : followersList) {
                userList.add(f.getFrom());
            }
            tvTitle.setText("Followers");


        } else {
            for (Follow f : followingList) {
                userList.add(f.getTo());
            }

            tvTitle.setText("Following");
        }


        adapter = new UserAdapter(this, userList);
        rvFollows.setAdapter(adapter);
        rvFollows.setLayoutManager(new LinearLayoutManager(this));

    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}