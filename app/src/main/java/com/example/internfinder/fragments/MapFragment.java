package com.example.internfinder.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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
                getLocation();
            }
        });


        allPosts = new ArrayList<>();
        allUsers = new ArrayList<>();
        adapter = new UserAdapter(getContext(), allUsers);


        hsvInterns.setAdapter(adapter);
        queryUsers();

        hsvInterns.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    ParseGeoPoint currentUserGeoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
                    saveCurrentUserLocation(currentUserGeoPoint);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentUserGeoPoint.getLatitude(), currentUserGeoPoint.getLongitude()), 12f));

                } else {
                    Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getLocationPermission() {
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

        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser == null) {
            Intent i = new Intent(getContext(), LoginActivity.class);
            startActivity(i);
        } else {

        }

        return currentUser.getParseGeoPoint("currentLocation");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }

                    mLocationPermissionsGranted = true;

                    getLocationPermission();
                }
            }
        }
    }


    private void saveCurrentUserLocation(ParseGeoPoint point) {

        if (point != null) {


            ParseUser currentUser = ParseUser.getCurrentUser();

            if (currentUser != null) {
                currentUser.put("currentLocation", point);
                currentUser.saveInBackground();

            }
        }
    }

    private void geoLocate(ParseGeoPoint point, String title) {
        reached = true;
        Marker myMarker;

        map.setOnMarkerClickListener(this);
        myMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(point.getLatitude(), point.getLongitude()))
                .title(title));

    }

    private void queryPosts() {

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        query.whereWithinMiles("latlng", getCurrentUserLocation(), 15);
        query.include("latlng");


        query.setLimit(15);

        query.addDescendingOrder("createdAt");

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                for (Post thePost : posts) {
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


        query.setLimit(15);


        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {

                ParseUser currentUser = new ParseUser();

                for (ParseUser user : users) {
                    if (user.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                        currentUser = user;
                    }

                }
                users.remove(currentUser);

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
        for (Post p : allPosts) {
            if (p.getLocation().equals(title)) {
                correctPost = p;
            }
        }

        return correctPost;
    }


}