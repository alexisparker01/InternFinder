package com.example.internfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ModifyProfileActivity extends AppCompatActivity {

    TextView tvUsername;
    EditText etFirstname;
    EditText etLastname;
    EditText etBio;
    Button btnSave;
    ImageView ivProfilePic;
    Button btnLogout2;



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
        btnLogout2 = findViewById(R.id.btnLogout2);

        if (ParseUser.getCurrentUser() != null) {

            etFirstname.setText(ParseUser.getCurrentUser().getString("firstname"));
            etLastname.setText(ParseUser.getCurrentUser().getString("lastname"));
            etBio.setText(ParseUser.getCurrentUser().getString("bio"));
            tvUsername.setText("@" + ParseUser.getCurrentUser().getString("username"));

            }


        ParseFile prof = ParseUser.getCurrentUser().getParseFile("profilePicture");
        if(prof != null) {
            Glide.with(this).load(prof.getUrl()).into(ivProfilePic);
        }



        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String firstname = etFirstname.getText().toString();
                String lastname = etLastname.getText().toString();
                String bio = etBio.getText().toString();
                String username = ParseUser.getCurrentUser().getString("username");


                if(ParseUser.getCurrentUser()!=null) {
                    saveProfile(firstname, lastname, bio, username, ParseUser.getCurrentUser());
                }

                goToProfile();

            }
        });

        btnLogout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Intent i = new Intent(ModifyProfileActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void saveProfile(String firstname, String lastname, String bio, String username, ParseUser currentUser) {

        currentUser.put("firstname", firstname);
        currentUser.put("lastname", lastname);
        currentUser.put("bio", bio);
        currentUser.put("username", username);

        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null) {
                    Log.e("ModifyProfileActivity", "Error while saving!");
                }

                Log.i("ModifyProfileActivity", "Successfully saved!");

                etFirstname.setText(ParseUser.getCurrentUser().getString("firstname"));
                etLastname.setText(ParseUser.getCurrentUser().getString("lastname"));
                etBio.setText(ParseUser.getCurrentUser().getString("bio"));
                tvUsername.setText("@" + username);


             //   ivProfilePic.setImageBitmap();ParseUser.getCurrentUser().get


            }
        });
    }

    private void goToProfile() {

        Intent i = new Intent(ModifyProfileActivity.this, ProfileActivity.class);
        startActivity(i);
    }
}