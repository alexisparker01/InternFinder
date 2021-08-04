package com.example.internfinder.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.internfinder.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ModifyProfileActivity extends AppCompatActivity {

    private TextView tvUsername;
    private EditText etFirstname;
    private EditText etLastname;
    private EditText etBio;
    private Button btnSave;
    private ImageView ivProfilePic;
    private ParseFile image;
    private ImageView ivBackToQuestionnaire;

    public static final int GET_FROM_GALLERY = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile);

        tvUsername = findViewById(R.id.tvUsername);
        etFirstname = findViewById(R.id.etFirstNameEditProfile);
        etLastname = findViewById(R.id.etLastNameEditProfile);
        etBio = findViewById(R.id.etBioEditProfile);
        btnSave = findViewById(R.id.btnSave);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        ivBackToQuestionnaire = findViewById(R.id.ivBackToQuestionnaire);


        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });

        if (ParseUser.getCurrentUser() != null) {

            etFirstname.setText(ParseUser.getCurrentUser().getString("firstname"));
            etLastname.setText(ParseUser.getCurrentUser().getString("lastname"));
            etBio.setText(ParseUser.getCurrentUser().getString("bio"));
            tvUsername.setText("@" + ParseUser.getCurrentUser().getString("username"));

        }


        ParseFile prof = ParseUser.getCurrentUser().getParseFile("profilePic");
        if (prof != null) {
            Glide.with(this).load(prof.getUrl()).into(ivProfilePic);
        }


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String firstname = etFirstname.getText().toString();
                String lastname = etLastname.getText().toString();
                String bio = etBio.getText().toString();
                String username = ParseUser.getCurrentUser().getString("username");


                if (ParseUser.getCurrentUser() != null) {
                    saveProfile(firstname, lastname, bio, username, ParseUser.getCurrentUser());
                }

                goToProfile();

            }
        });

        ivBackToQuestionnaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ModifyProfileActivity.this, QuestionnaireActivity.class);
                i.putExtra("User", Parcels.wrap(ParseUser.getCurrentUser()));
                startActivity(i);

            }
        });

    }

    private void saveProfile(String firstname, String lastname, String bio, String username, ParseUser currentUser) {

        currentUser.put("firstname", firstname);
        currentUser.put("lastname", lastname);
        currentUser.put("bio", bio);
        currentUser.put("username", username);
        if (image != null) {
            currentUser.put("profilePic", image);
        }

        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                etFirstname.setText(ParseUser.getCurrentUser().getString("firstname"));
                etLastname.setText(ParseUser.getCurrentUser().getString("lastname"));
                etBio.setText(ParseUser.getCurrentUser().getString("bio"));
                tvUsername.setText("@" + username);
            }
        });
    }

    private void goToProfile() {

        Intent intent = new Intent(ModifyProfileActivity.this, MainActivity.class);
        intent.putExtra("openProfileFragment", true);
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);

    }

    public ParseFile conversionBitmapParseFile(Bitmap imageBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        ParseFile parseFile = new ParseFile("image_file.jpeg", imageByte);
        return parseFile;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                image = conversionBitmapParseFile(bitmap);
                ParseUser.getCurrentUser().put("profilePic", image);
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        ParseFile profilePic = ParseUser.getCurrentUser().getParseFile("profilePic");
                        if (profilePic != null) {
                            Glide.with(ModifyProfileActivity.this).load(profilePic.getUrl()).into(ivProfilePic);
                        }
                    }
                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}