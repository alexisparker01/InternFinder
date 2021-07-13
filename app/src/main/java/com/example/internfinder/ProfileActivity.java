package com.example.internfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class ProfileActivity extends AppCompatActivity {

    TextView tvUsernameProfile;
    TextView tvFirstname;
    TextView tvLastname;
    TextView tvBio;
    ImageView ivProfilePicProfile;
    Button btnEditProfile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvUsernameProfile = findViewById(R.id.tvUsername);
        tvFirstname = findViewById(R.id.etFirstName);
        tvLastname = findViewById(R.id.tvLastName);
        tvBio = findViewById(R.id.tvBio);
        ivProfilePicProfile = findViewById(R.id.ivProfilePic);
        btnEditProfile = findViewById(R.id.btnEditProfile);



            tvFirstname.setText(ParseUser.getCurrentUser().getString("firstname"));
            tvLastname.setText(ParseUser.getCurrentUser().getString("lastname"));
            tvBio.setText(ParseUser.getCurrentUser().getString("bio"));
        tvUsernameProfile.setText("@"+ParseUser.getCurrentUser().getString("username"));

        ParseFile prof = ParseUser.getCurrentUser().getParseFile("profilePicture");

        if(prof != null) {
            Glide.with(this).load(prof.getUrl()).into(ivProfilePicProfile);
        }



        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, ModifyProfileActivity.class);
                startActivity(i);
            }
        });
    }

}