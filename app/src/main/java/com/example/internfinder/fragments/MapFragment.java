package com.example.internfinder.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.internfinder.R;
import com.example.internfinder.activities.LoginActivity;
import com.example.internfinder.activities.OpenPostActivity;
import com.example.internfinder.adapters.UserAdapter;
import com.example.internfinder.models.Post;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment implements GoogleMap.OnMarkerClickListener {

    private static final String TAG = "MapFragment";
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private TextView tvMapTitle;

    public static boolean reached;

    private ImageView gps;

    private List<Post> allPosts;


    private RecyclerView hsvInterns;
    private TextView tvInternTitle;
    protected UserAdapter adapter;
    protected List<ParseUser> allUsers;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionsGranted = false;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;


    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;


    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFeed);
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);



        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                getLocationPermission();
                queryPosts();
            }
        });



        return view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvMapTitle = view.findViewById(R.id.tvMapTitle);
        tvInternTitle = view.findViewById(R.id.tvInternTitle);
        hsvInterns = view.findViewById(R.id.hsvInterns);
        gps = view.findViewById(R.id.ic_gps2);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked GPS icon");
                getLocation();
            }
        });


        // initialize the array that will hold posts and create the adapter for the profile
        allPosts = new ArrayList<>();
        allUsers = new ArrayList<>();
        adapter = new UserAdapter(getContext(), allUsers);

        // set the adapter on the recycler view
        hsvInterns.setAdapter(adapter);

        // set the layout manager on the recycler view
        hsvInterns.setLayoutManager(new LinearLayoutManager(getContext()));

        queryUsers();

    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.



            return;
        }
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.d(TAG, "onComplete: found your location! You are at: " + location);
                    ParseGeoPoint currentUserGeoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
                    saveCurrentUserLocation(currentUserGeoPoint);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentUserGeoPoint.getLatitude(), currentUserGeoPoint.getLongitude()), 12f));

                } else {
                    Log.d(TAG, "onComplete: location is null");
                    Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        try {
            if (mLocationPermissionsGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                if(location == null) {

                    Log.e(TAG, "LOCATION IS NULL");

                } else {
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Location currentLocation = (Location) task.getResult();
                                Log.d(TAG, "onComplete: found your location! You are at: " + currentLocation);
                                ParseGeoPoint currentUserGeoPoint = new ParseGeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
                                saveCurrentUserLocation(currentUserGeoPoint);
                                //geoLocate(currentUserGeoPoint, "My Location");
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentUserGeoPoint.getLatitude(), currentUserGeoPoint.getLongitude()), 11f));

                            } else {
                                Log.d(TAG, "onComplete: location is null");
                                Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                }
            }

        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: security exception: " + e.getMessage());
        }


    }
*/

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions ");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                getLocation();
            } else {
                ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private ParseGeoPoint getCurrentUserLocation() {

        // finding currentUser
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser == null) {
            // if it's not possible to find the user, do something like returning to login activity
            Intent i = new Intent(getContext(), LoginActivity.class);
            startActivity(i);
        } else {

        }
        // otherwise, return the current user location
        return currentUser.getParseGeoPoint("currentLocation");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult: called.");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");

                    mLocationPermissionsGranted = true;
                    //initialize the map
                    getLocationPermission();
                }
            }
        }
    }


    private void saveCurrentUserLocation(ParseGeoPoint point) {
        Log.i(TAG, "Saving the user's current location");

        if (point != null) {
            // if it isn't, save it to Back4App Dashboard

            ParseUser currentUser = ParseUser.getCurrentUser();

            if (currentUser != null) {
                currentUser.put("currentLocation", point);
                currentUser.saveInBackground();
            } else {
                // do something like coming back to the login activity
            }
        } else {
            // if it is null, do something like displaying error and coming back to the menu activity
        }
    }

    private void geoLocate(ParseGeoPoint point, String title) {
        reached = true;
        Log.e(TAG, "GeoLocating...");
        Geocoder geocoder = new Geocoder(getContext());
        Marker myMarker;

        Log.e(TAG, "Adding user post...");

            map.setOnMarkerClickListener(this);
            myMarker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(point.getLatitude(), point.getLongitude()))
                    .title(title));

           // onMarkerClick(myMarker);

    }

    private void queryPosts() {
        // specifying that i am querying data from the Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        query.whereWithinMiles("latlng", getCurrentUserLocation(), 15);
        query.include("latlng");

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
                    Log.e("MagFragment", "Problem with fetching posts", e);
                    return;
                }

                // printing each of the posts I get to see if I'm getting all the posts from the server


                for (Post thePost : posts) {
                    Log.i("MapFragment", "user: " + thePost.getLatLng() + " " + thePost.getLocation());
                    if (thePost.getLatLng() != null) {
                        geoLocate(thePost.getLatLng(), thePost.getLocation());
                    }
                }

                allPosts.addAll(posts);

                }

            });
    }


    public void queryUsers() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.whereWithinMiles("currentLocation", getCurrentUserLocation(), 20);

        // limit query to latest 15 posts
        query.setLimit(15);

        // order posts by creation date (newest first)
       // query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e("ProfileActivity", "Problem with fetching posts", e);
                    return;
                }

                // printing each of the posts I get to see if I'm getting all the posts from the server
                ParseUser currentUser = new ParseUser();

                for (ParseUser user: users) {

                    if(user.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                        currentUser = user;
                    }

                }
                users.remove(currentUser);

                // save received posts to list and notify adapter of new data
                allUsers.clear();
                allUsers.addAll(users);
                adapter.notifyDataSetChanged();
            }
        });

    }

    public boolean onMarkerClick(@NonNull Marker marker) {

              Post post = queryPostsByTitle(marker.getTitle());
              Intent i = new Intent(getActivity(), OpenPostActivity.class);
              i.putExtra("Post", Parcels.wrap(post));
              startActivity(i);

            return false;


    }

    private Post queryPostsByTitle(String title) {

        Post correctPost = new Post();
        for(Post p:allPosts) {
            if(p.getLocation().equals(title)) {
                correctPost = p;
            }
        }

return correctPost;
        }


    }