package com.example.internfinder.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
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
import com.example.internfinder.R;
import com.example.internfinder.models.Post;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CreatePostActivity extends AppCompatActivity implements OnMapReadyCallback {


    public static final String TAG = "CreatePostActivity";


    ParseUser currentUser;

    private Spinner spinnerPostType;
    private EditText etPostText;
    private ImageView ivPostImage;
    private Button btnSubmitPost;
    private Button btnCam;
    private TextView tvLocation;

    private ImageView profilePic;

    private boolean photoChosen;
    private boolean textChosen;
    private boolean eventChosen;

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    private File photoFile;
    ParseFile image;

    public static final int ERROR_DIALOG_REQUEST = 9001;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;


    private ImageView mGps;
    private SupportMapFragment mapFragment;
    private RelativeLayout relativeLayout;


    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private String eventAddress;
    private ParseGeoPoint eventLatLng;
    private String eventName;
    LatLng currentLocationLatLng;

    private AutocompleteSupportFragment fragment;
    String placeId;
    Boolean reached;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            mMap.getUiSettings().setAllGesturesEnabled(true);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);


        currentUser = ParseUser.getCurrentUser();

        reached = false;


        etPostText = findViewById(R.id.etPostText);
        ivPostImage = findViewById(R.id.ivPostImage);
        spinnerPostType = findViewById(R.id.spinnerPostType);
        btnSubmitPost = findViewById(R.id.btnSubmitPost);
        btnCam = findViewById(R.id.btnCam);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mGps = (ImageView) findViewById(R.id.ic_gps2);

        relativeLayout = findViewById(R.id.relLayout2);
        profilePic = findViewById(R.id.profilePicCreate);
        tvLocation = findViewById(R.id.tvLocation);


        etPostText.setVisibility(View.GONE);
        ivPostImage.setVisibility(View.GONE);
        btnSubmitPost.setVisibility(View.GONE);
        btnCam.setVisibility(View.GONE);
        getSupportFragmentManager().findFragmentById(R.id.map).getView().setVisibility(View.INVISIBLE);
        mGps.setVisibility(View.INVISIBLE);
        relativeLayout.setVisibility(View.INVISIBLE);
        profilePic.setVisibility(View.VISIBLE);
        tvLocation.setVisibility(View.GONE);


        ParseFile profilePicture = ParseUser.getCurrentUser().getParseFile("profilePic");
        if (profilePicture != null) {
            Glide.with(this).load(profilePicture.getUrl()).into(profilePic);

        }

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });

        image = null;


        String[] postSelections = new String[]{"Text Post", "Event Post", "Picture Post"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, postSelections);


        spinnerPostType.setAdapter(adapter);


        spinnerPostType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {


                if (parent.getItemAtPosition(position).equals("Event Post")) {
                    etPostText.setVisibility(View.VISIBLE);
                    ivPostImage.setVisibility(View.VISIBLE);
                    btnSubmitPost.setVisibility(View.VISIBLE);
                    btnCam.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.VISIBLE);
                    mGps.setVisibility(View.VISIBLE);
                    tvLocation.setVisibility(View.VISIBLE);


                    getSupportFragmentManager().findFragmentById(R.id.map).getView().setVisibility(View.VISIBLE);
                    getDeviceLocation();

                    photoFile = null;
                    photoChosen = false;
                    eventChosen = true;
                    textChosen = false;

                    Toast.makeText(CreatePostActivity.this, "Map is ready", Toast.LENGTH_SHORT).show();


                    if (isServicesOK()) {
                        getLocationPermission();
                    }


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


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(CreatePostActivity.this, "Nothing Selected", Toast.LENGTH_SHORT).show();
                return;
            }
        });


        Places.initialize(getApplicationContext(), "AIzaSyABbgxUaKHBgwuaVndt5G0tR2XNYkdpCi8");
        PlacesClient placesClient = Places.createClient(this);


        fragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);


        fragment.setTypeFilter(TypeFilter.ESTABLISHMENT);

        ParseGeoPoint point = ParseUser.getCurrentUser().getParseGeoPoint("currentLocation");

        fragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(point.getLatitude(), point.getLongitude()),
                new LatLng(point.getLatitude() + 0.05, point.getLongitude() + 0.05)));

        fragment.setCountries("US");

        fragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));


        fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                moveCamera(place.getLatLng(), DEFAULT_ZOOM, place.getAddress());
                tvLocation.setText(place.getAddress());
                placeId = place.getId();
                tvLocation.setText(place.getAddress());
                eventLatLng = new ParseGeoPoint(place.getLatLng().latitude, place.getLatLng().longitude);
                eventAddress = place.getAddress();
                eventName = place.getName();

                final List<Place.Field> fields = Collections.singletonList(Place.Field.PHOTO_METADATAS);

                final FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeId, fields);

                placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
                    final Place place2 = response.getPlace();

                    final List<PhotoMetadata> metadata = place2.getPhotoMetadatas();
                    if (metadata == null || metadata.isEmpty()) {
                        return;
                    }
                    final PhotoMetadata photoMetadata = metadata.get(0);

                    final String attributions = photoMetadata.getAttributions();

                    final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                            .build();
                    placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                        Bitmap bitmap = fetchPhotoResponse.getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] bitmapBytes = stream.toByteArray();

                        image = new ParseFile("myImage", bitmapBytes);

                    }).addOnFailureListener((exception) -> {
                        if (exception instanceof ApiException) {
                            final ApiException apiException = (ApiException) exception;
                            final int statusCode = apiException.getStatusCode();
                        }
                    });
                });

            }


            @Override
            public void onError(@NonNull Status status) {
                return;
            }
        });


        btnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                launchCamera();

            }
        });

        btnSubmitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etPostText.getText().toString();
                if (description.isEmpty()) {
                    Toast.makeText(CreatePostActivity.this, "Description can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if ((photoFile == null || ivPostImage.getDrawable() == null) && (photoChosen == true)) {
                    Toast.makeText(CreatePostActivity.this, "Photo cannot be empty. Choose text or event post if you do not want to upload picture.", Toast.LENGTH_SHORT).show();
                    return;
                }

                savePost(description, currentUser, photoFile, image);
            }
        });
    }


    public boolean isServicesOK() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(CreatePostActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(CreatePostActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    private void getDeviceLocation() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            currentLocationLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                        } else {
                            Toast.makeText(CreatePostActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Toast.makeText(CreatePostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }

    private void moveCamera(LatLng latLng, float zoom, String title) {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")) {

            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);

            mMap.addMarker(options);


        }


    }

    private void initMap() {
        mapFragment.getMapAsync(CreatePostActivity.this);
    }

    private void getLocationPermission() {
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
                    initMap();
                }
            }
        }
    }


    private void savePost(String description, ParseUser currentUser, File photoFile, ParseFile image) {
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
            post.setLocation(eventAddress);
            post.setLatLng(eventLatLng);
            post.setLocationName(eventName);
            if (image != null) {
                post.setImage(image);
            }
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


    private void goToFeed() {

        Intent intent = new Intent(CreatePostActivity.this, MainActivity.class);
        intent.putExtra("openFeedFragment", true);
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);


    }


    private void launchCamera() {


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(CreatePostActivity.this, "com.codepath.fileprovider.internfinder", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);


        if (intent.resolveActivity(getPackageManager()) != null) {

            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public File getPhotoFileUri(String fileName) {

        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Toast.makeText(CreatePostActivity.this, "Failed to create directory", Toast.LENGTH_SHORT).show();

        }

        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                ivPostImage.setImageBitmap(takenImage);
            } else {
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }

    }
}