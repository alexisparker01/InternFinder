package com.example.internfinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


//@Parcel
public class MainActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener {

    private static final String TAG = "MainActivity";
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    LatLng loc;
    private Marker myMarker;
    Post post;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionsGranted = false;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;


    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

/*
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFeed);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);


        Log.i(TAG, String.valueOf(loc));
        getLocationPermission();
        initMap();

*/

      Intent i = new Intent(MainActivity.this, FeedActivity.class);
      startActivity(i);




    }
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found your location!");
                            Location currentLocation = (Location) task.getResult();
                            ParseGeoPoint point = new ParseGeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
                            saveCurrentUserLocation(point);
                            Log.i(TAG, "Currnt location: " + String.valueOf(getCurrentUserLocation()));
                            geoLocate(point, "My Location");
                          /*  Intent i = new Intent(CreatePostActivity.this, MainActivity.class);
                            i.putExtra("currentLocation", currentLocationLatLng);
                            startActivity(i);

                           */

                        } else {
                            Log.d(TAG, "onComplete: location is null");
                            Toast.makeText(MainActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: security exception: " + e.getMessage());
        }



    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions ");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                getDeviceLocation();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    private ParseGeoPoint getCurrentUserLocation(){

        // finding currentUser
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser == null) {
            // if it's not possible to find the user, do something like returning to login activity
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        }
        // otherwise, return the current user location
        return currentUser.getParseGeoPoint("location");

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
                   //
                    // saveCurrentUserLocation();
                    mLocationPermissionsGranted = true;
                    //initialize the map
                    getDeviceLocation();
                    //
                }
            }
        }
    }


    private void initMap() {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                map = googleMap;
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    // public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                    return;
                }


                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                queryPosts();
            }
        });
    }

    private void saveCurrentUserLocation(ParseGeoPoint point) {
        Log.i(TAG, "Saving the user's current location");
        // requesting permission to get user's location
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
            if(point != null){
                // if it isn't, save it to Back4App Dashboard

                ParseUser currentUser = ParseUser.getCurrentUser();

                if (currentUser != null) {
                    currentUser.put("location", point);
                    currentUser.saveInBackground();
                } else {
                    // do something like coming back to the login activity
                }
            }
            else {
                // if it is null, do something like displaying error and coming back to the menu activity
            }
        }

    private void geoLocate(ParseGeoPoint point, String title) {
        Log.e("MainActivity", "GeoLocating...");
        Geocoder geocoder = new Geocoder(MainActivity.this);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(point.getLatitude(), point.getLongitude()), 10f));

        if(!title.equals("My Location")) {
            map.setOnMarkerClickListener(this);

            myMarker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(point.getLatitude(), point.getLongitude()))
                    .title(title));
        }

    }

    private void queryPosts() {
        // specifying that i am querying data from the Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        query.whereWithinMiles("latlng", getCurrentUserLocation(), 10);

        // limit query to latest 15 posts
        query.setLimit(15);

        // order posts by creation date (newest first)
       // query.addDescendingOrder("createdAt");

        // start an asynchronous call for posts

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e("MainActivity", "Problem with fetching posts", e);
                    return;
                }

                // printing each of the posts I get to see if I'm getting all the posts from the server

                for (Post thePost : posts) {
                    post = thePost;
                    Log.i("ProfileActivity", "user: " + thePost.getLocation());
                    if(thePost.getLatLng() != null) {
                        geoLocate(thePost.getLatLng(), thePost.getLocation());
                    }

                }

            }
        });

    }


    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Intent i = new Intent(MainActivity.this, OpenPost.class);
        i.putExtra("Post", post);
        startActivity(i);
        return false;
    }
}