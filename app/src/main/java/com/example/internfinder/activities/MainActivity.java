package com.example.internfinder.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.internfinder.R;
import com.example.internfinder.fragments.FeedFragment;
import com.example.internfinder.fragments.LogoutFragment;
import com.example.internfinder.fragments.MapFragment;
import com.example.internfinder.fragments.ProfileFragment;
import com.example.internfinder.fragments.SearchFragment;
import com.example.internfinder.models.Post;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.parceler.Parcels;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private Marker myMarker;
    Post post;
    private Fragment fragment;


     public static BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        FragmentManager fragmentManager = getSupportFragmentManager();





        fragment = Parcels.unwrap(getIntent().getParcelableExtra("Fragment"));

        // handle navigation selection
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.action_map:
                                fragment = new MapFragment();
                                break;
                            case R.id.action_feed:
                                fragment = new FeedFragment();
                                break;
                            case R.id.action_search:
                                fragment  = new SearchFragment();
                                break;
                            case R.id.action_profile:
                                fragment = new ProfileFragment();
                                break;

                            case R.id.action_logout:
                            default:
                                fragment = new LogoutFragment();
                                break;

                        }
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                        return true;
                    }
                });
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_feed);

        Bundle extras = getIntent().getExtras();

        boolean openF2 = false;
        boolean openF3 = false;

        FragmentManager fragmentManagerProfile = getSupportFragmentManager();
        Fragment profileFragment = new ProfileFragment();
        Fragment feedFragment = new FeedFragment();

        if(extras!=null && extras.containsKey("openProfileFragment"))
            openF2 = extras.getBoolean("openProfileFragment");
        if(openF2){
            View view = MainActivity.bottomNavigationView.findViewById(R.id.action_profile);
            fragmentManagerProfile.beginTransaction().replace(R.id.flContainer, profileFragment).commit();
            view.performClick();

        }
        if(extras!=null && extras.containsKey("openFeedFragment"))
            openF3 = extras.getBoolean("openFeedFragment");
        if(openF3) {
            View view = MainActivity.bottomNavigationView.findViewById(R.id.action_feed);
            fragmentManagerProfile.beginTransaction().replace(R.id.flContainer, feedFragment).commit();
            view.performClick();
        }



        /** figure out why whenver i switch to other fragment it will still show that previous fragment button is selected **/


    }


}