package com.example.internfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class CreatePostActivity extends AppCompatActivity {

    Spinner spinnerPostType;
    EditText etPostText;
    ImageView ivPostImage;
    Button btnSubmitPost;
    Button btnCam;
    private boolean photoChosen;

    // camera instance variables
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public static final String TAG = "CreatePostActivity";
    public String photoFileName = "photo.jpg";
    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        queryPosts();


        etPostText = findViewById(R.id.etPostText);
        ivPostImage = findViewById(R.id.ivPostImage);
        spinnerPostType = findViewById(R.id.spinnerPostType);
        btnSubmitPost = findViewById(R.id.btnSubmitPost);
        btnCam = findViewById(R.id.btnCam);

        etPostText.setVisibility(View.GONE);
        ivPostImage.setVisibility(View.GONE);
        btnSubmitPost.setVisibility(View.GONE);
        btnCam.setVisibility(View.GONE);

        String[] postSelections = new String[]{"Event Post", "Text Post", "Picture Post"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, postSelections);
//set the spinners adapter to the previously created one.
        spinnerPostType.setAdapter(adapter);

        spinnerPostType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // Event Post using maps
                if (parent.getItemAtPosition(position).equals("Event Post")) {
                    etPostText.setVisibility(View.VISIBLE);
                    ivPostImage.setVisibility(View.GONE);
                    btnSubmitPost.setVisibility(View.VISIBLE);
                    btnCam.setVisibility(View.GONE);


                } else if (parent.getItemAtPosition(position).equals("Text Post")) {
                    etPostText.setVisibility(View.VISIBLE);
                    ivPostImage.setVisibility(View.GONE);
                    btnSubmitPost.setVisibility(View.VISIBLE);
                    btnCam.setVisibility(View.GONE);
                    photoFile = null;

                } else if (parent.getItemAtPosition(position).equals("Picture Post")) {

                    etPostText.setVisibility(View.VISIBLE);
                    ivPostImage.setVisibility(View.VISIBLE);
                    btnSubmitPost.setVisibility(View.VISIBLE);
                    btnCam.setVisibility(View.VISIBLE);
                    photoChosen = true;

                    //takePicture(view);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                Log.i("creatrpost", "hi");

            }
        });

        btnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                launchCamera(v);

            }
        });

        btnSubmitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etPostText.getText().toString();
                if (description.isEmpty()) {
                   Log.e(TAG,"Description can't be empty");
                    return;
                }

                if((photoFile == null || ivPostImage.getDrawable() == null) && photoChosen == true) {
                    Log.e(TAG, "Photo cannot be empty. Choose text or event post if you do not want to upload picture.");
                    return;
                }

                ParseUser currentUser = ParseUser.getCurrentUser();
                savePost(description, currentUser, photoFile);
            }
        });
    }

    private void savePost(String description, ParseUser currentUser, File photoFile) {
        Post post = new Post();
        post.setDescription(description);

        if(photoFile != null || ivPostImage.getDrawable() != null) {
            post.setImage(new ParseFile(photoFile));
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
        Intent i = new Intent(CreatePostActivity.this, FeedActivity.class);
        startActivity(i);
    }
    private void launchCamera(View view) {

        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(CreatePostActivity.this, "com.codepath.fileprovider.internfinder", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }

    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                // ImageView ivPreview = (ImageView) findViewById(R.id.ivPo);
                ivPostImage.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void queryPosts() {

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {

                if(e!=null){
                    return;
                }

            }
        });
    }
}