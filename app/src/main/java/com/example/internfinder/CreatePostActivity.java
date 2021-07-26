package com.example.internfinder;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.internfinder.models.Post;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreatePostActivity extends AppCompatActivity implements OnMapReadyCallback  {

    // tag variable for logging
    public static final String TAG = "CreatePostActivity";

    // currentUser var
    ParseUser currentUser;

    // widget vars
    private Spinner spinnerPostType;
    private EditText etPostText;
    private ImageView ivPostImage;
    private Button btnSubmitPost;
    private Button btnCam;
    private TextView tvLocation;

    private ImageView profilePic;

    // boolean checker vars
    private boolean photoChosen;
    private boolean textChosen;
    private boolean eventChosen;

    // camera vars
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    private File photoFile;

    // map vars
    public static final int ERROR_DIALOG_REQUEST = 9001;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;


    private EditText mSearchText;
    private ImageView mGps;
    private SupportMapFragment mapFragment;
    private RelativeLayout relativeLayout;


    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private String eventLocation;
    private ParseGeoPoint eventLatLng;
    private LatLng currentLocationLatLng;

    //private AutocompleteSupportFragment autocompleteFragment;


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "OnMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            // marks a little blue dot on the map to show my location
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            afterTypingMap();
            // gestures
            //mMap.getUiSettings().isZoomControlsEnabled(true);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);


        /** autocomplete stuff **/
        /*

        Places.initialize(getApplicationContext(), "AIzaSyABbgxUaKHBgwuaVndt5G0tR2XNYkdpCi8");
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.

        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);

        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363),
                new LatLng(-33.858754,151.229596)));

        autocompleteFragment.setCountries("IN");

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));


        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            }


            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

*/
        // set current user
        currentUser = ParseUser.getCurrentUser();

        // initializing widgets
        etPostText = findViewById(R.id.etPostText);
        ivPostImage = findViewById(R.id.ivPostImage);
        spinnerPostType = findViewById(R.id.spinnerPostType);
        btnSubmitPost = findViewById(R.id.btnSubmitPost);
        btnCam = findViewById(R.id.btnCam);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mGps = (ImageView) findViewById(R.id.ic_gps2);
        mSearchText = findViewById(R.id.input_search2);
        relativeLayout = findViewById(R.id.relLayout2);
        profilePic = findViewById(R.id.profilePicCreate);
        tvLocation = findViewById(R.id.tvLocation);


        // set visibility of post widgets to GONE
        etPostText.setVisibility(View.GONE);
        ivPostImage.setVisibility(View.GONE);
        btnSubmitPost.setVisibility(View.GONE);
        btnCam.setVisibility(View.GONE);
        getSupportFragmentManager().findFragmentById(R.id.map).getView().setVisibility(View.INVISIBLE);
        mGps.setVisibility(View.INVISIBLE);
        relativeLayout.setVisibility(View.INVISIBLE);
        profilePic.setVisibility(View.VISIBLE);
        tvLocation.setVisibility(View.GONE);


        ParseFile profilePicture = ParseUser.getCurrentUser().getParseFile("profilePicture");
        if (profilePic != null) {
            Glide.with(this).load(profilePicture.getUrl()).into(profilePic);

        }



        /** set up for spinner widget **/

        // set up spinner options
        String[] postSelections = new String[]{"Text Post", "Event Post", "Picture Post"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, postSelections);

        // set the spinners adapter to the previously created one
        spinnerPostType.setAdapter(adapter);

        // set onItemSelectedListener so that user can pick which post they'd like to submit
        spinnerPostType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                // if user wants to create an event post with google maps
                if (parent.getItemAtPosition(position).equals("Event Post")) {
                    etPostText.setVisibility(View.VISIBLE);
                    ivPostImage.setVisibility(View.GONE);
                    btnSubmitPost.setVisibility(View.VISIBLE);
                    btnCam.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.VISIBLE);
                    mGps.setVisibility(View.VISIBLE);
                    tvLocation.setVisibility(View.VISIBLE);

                    getSupportFragmentManager().findFragmentById(R.id.map).getView().setVisibility(View.VISIBLE);

                    photoFile = null;
                    photoChosen = false;
                    eventChosen = true;
                    textChosen = false;

                    Toast.makeText(CreatePostActivity.this, "Map is ready", Toast.LENGTH_SHORT).show();

                    // initialize the map
                    if (isServicesOK()) {
                        getLocationPermission();
                    }

                    // if user wants to create a text post/status update post
                } else if (parent.getItemAtPosition(position).equals("Text Post")) {
                    etPostText.setVisibility(View.VISIBLE);
                    ivPostImage.setVisibility(View.GONE);
                    btnSubmitPost.setVisibility(View.VISIBLE);
                    btnCam.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.INVISIBLE);
                    mGps.setVisibility(View.INVISIBLE);
                    getSupportFragmentManager().findFragmentById(R.id.map).getView().setVisibility(View.GONE);

                    photoFile = null;

                    photoChosen = false;
                    eventChosen = false;
                    textChosen = true;

                    // if user wants to create a picture post
                } else if (parent.getItemAtPosition(position).equals("Picture Post")) {

                    etPostText.setVisibility(View.VISIBLE);
                    ivPostImage.setVisibility(View.VISIBLE);
                    btnSubmitPost.setVisibility(View.VISIBLE);
                    btnCam.setVisibility(View.VISIBLE);
                    mGps.setVisibility(View.INVISIBLE);
                    relativeLayout.setVisibility(View.INVISIBLE);
                    getSupportFragmentManager().findFragmentById(R.id.map).getView().setVisibility(View.INVISIBLE);

                    photoChosen = true;
                    eventChosen = false;
                    textChosen = false;

                    launchCamera();

                }
            }

            // if no option is selected
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(CreatePostActivity.this, "Nothing Selected", Toast.LENGTH_SHORT).show();
                return;
            }
        });

        /** onClickListeners for button widgets **/

        // when the user clicks on the camera button they will launch the camera
        btnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                launchCamera();

            }
        });

        // when the user clicks on the submit button they
        btnSubmitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etPostText.getText().toString();
                if (description.isEmpty()) {
                    Log.e(TAG, "Description can't be empty for any post type");
                    Toast.makeText(CreatePostActivity.this, "Description can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if ((photoFile == null || ivPostImage.getDrawable() == null) && (photoChosen == true)) {
                    Log.e(TAG, "Photo cannot be empty. Choose text or event post if you do not want to upload picture.");
                    Toast.makeText(CreatePostActivity.this, "Photo cannot be empty. Choose text or event post if you do not want to upload picture.", Toast.LENGTH_SHORT).show();
                    return;
                }

                savePost(description, currentUser, photoFile);
            }
        });
    }

    /**
     * start up methods for GoogleMaps API
     **/

    // checks to see the google services version of the user and if they can make map requests
    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(CreatePostActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // an error occurred but it is resolvable
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(CreatePostActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * other methods for GoogleMaps API
     **/
    private void afterTypingMap() {
        Log.d(TAG, "afterTyping: initializing");
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER
                ) {
                    // execute method for searching
                    geoLocate();
                    return true;

                }
                return false;
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked GPS icon");
                getDeviceLocation();
                mSearchText.setText("");
            }
        });
        hideSoftKeyboard(CreatePostActivity.this);
    }

    private void geoLocate() {

        Log.d(TAG, "geoLocation: geolocating");
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(CreatePostActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocation: IOException: " + e.getMessage());
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            Log.i(TAG, "geoLocate: found location: " + address.toString());
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
            eventLatLng = new ParseGeoPoint(address.getLatitude(), address.getLongitude());
            eventLocation = address.getAddressLine(0);
            tvLocation.setText(eventLocation);

        }



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
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            currentLocationLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                          /*  Intent i = new Intent(CreatePostActivity.this, MainActivity.class);
                            i.putExtra("currentLocation", currentLocationLatLng);
                            startActivity(i);

                           */

                        } else {
                            Log.d(TAG, "onComplete: location is null");
                            Toast.makeText(CreatePostActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: security exception: " + e.getMessage());
        }



    }

    private void moveCamera(LatLng latLng, float zoom, String title) {

        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + " long: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

       if (!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);

            mMap.addMarker(options);


        }

        hideSoftKeyboard(CreatePostActivity.this);

    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        mapFragment.getMapAsync(CreatePostActivity.this);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions ");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
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
                    initMap();
                }
            }
        }
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    /**
     * submit and photo taking methods
     **/

    private void savePost(String description, ParseUser currentUser, File photoFile) {
        Post post = new Post();
        post.setDescription(description);
        post.put("industry", currentUser.getString("industry"));


        if (photoChosen) {
            post.setImage(new ParseFile(photoFile));
            post.setType("photo");

        } else if (textChosen) {

            post.setType("text");

        } else if (eventChosen) {

            post.setType("event");
            post.setLocation(eventLocation);
            post.setLatLng(eventLatLng);
        }


        post.setUser(currentUser);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(CreatePostActivity.this, "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                etPostText.setText("");
                ivPostImage.setImageResource(0);
                Toast.makeText(CreatePostActivity.this, "Saved successfully!", Toast.LENGTH_SHORT).show();
            }


        });

        goToFeed();
    }

    // takes the user back to the feed after they are finished
    private void goToFeed() {
        Intent i = new Intent(CreatePostActivity.this, FeedActivity.class);
        startActivity(i);
    }


    private void launchCamera() {

        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // create a file reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap file object into a content provider
        // required for API >= 24
        Uri fileProvider = FileProvider.getUriForFile(CreatePostActivity.this, "com.codepath.fileprovider.internfinder", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);


        if (intent.resolveActivity(getPackageManager()) != null) {
            // start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }

    }

    public File getPhotoFileUri(String fileName) {
        // get safe storage directory for photos

        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        // return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // photo
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
             // loading photo into image view
                ivPostImage.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }

    }
}