package com.example.internfinder.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.internfinder.R;
import com.example.internfinder.fragments.FeedFragment;
import com.example.internfinder.fragments.MapFragment;
import com.example.internfinder.fragments.SearchFragment;
import com.example.internfinder.models.Post;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private Marker myMarker;
    Post post;


     BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        final FragmentManager fragmentManager = getSupportFragmentManager();
/*
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
*/
        // handle navigation selection
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment;

                        switch (item.getItemId()) {
                            case R.id.action_map:
                                fragment = new MapFragment();
                                break;
                            case R.id.action_feed:
                                fragment = new FeedFragment();
                                break;
                            case R.id.action_search:
                                default:
                                fragment  = new SearchFragment();
                                break;
                                /*
                            case R.id.action_profile:
                            default:
                                fragment = new ProfileFragment();
                                break;

                             */
                        }
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                        return true;
                    }
                });
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_feed);
    }

}