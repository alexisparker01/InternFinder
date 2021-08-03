package com.example.internfinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.internfinder.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

/* A login screen that offers login via username/password. */

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignup;
    private Button btnLoginChoice;
    private Button btnSignupChoice;
    private Button btnGoBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if (ParseUser.getCurrentUser() != null) {
          goToQuestionnaire();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        btnLoginChoice = findViewById(R.id.btnLoginChoice);
        btnSignupChoice = findViewById(R.id.btnSignupChoice);
        btnGoBack = findViewById(R.id.btnGoBack);


        etUsername.setVisibility(View.GONE);
        etPassword.setVisibility(View.GONE);
        btnLogin.setVisibility(View.GONE);
        btnSignup.setVisibility(View.GONE);
        btnSignupChoice.setVisibility(View.VISIBLE);
        btnLoginChoice.setVisibility(View.VISIBLE);
        btnGoBack.setVisibility(View.GONE);


        btnLoginChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etUsername.setVisibility(View.VISIBLE);
                etPassword.setVisibility(View.VISIBLE);
                btnGoBack.setVisibility(View.VISIBLE);
                btnSignup.setVisibility(View.GONE);
                btnSignupChoice.setVisibility(View.GONE);
                btnLogin.setVisibility(View.VISIBLE);
                btnLoginChoice.setVisibility(View.GONE);

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
            }
        });


        btnSignupChoice.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                etUsername.setVisibility(View.VISIBLE);
                etPassword.setVisibility(View.VISIBLE);
                btnSignup.setVisibility(View.VISIBLE);
                btnGoBack.setVisibility(View.VISIBLE);
                btnSignupChoice.setVisibility(View.GONE);
                btnLogin.setVisibility(View.GONE);
                btnLoginChoice.setVisibility(View.GONE);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                ParseUser user = new ParseUser();
                user.setUsername(username);
                user.setPassword(password);

                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(LoginActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                            goToQuestionnaire();

                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                }
                            });


                        } else {
                            Toast.makeText(LoginActivity.this, "Sign up didn't succeed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });


        btnGoBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void loginUser(String username, String password) {

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e != null) {
                    //TODO: Better error handling
                 //   Toast.makeText(LoginActivity.this, "Issue with login", Toast.LENGTH_SHORT).show();
                    return;
                }
                // TODO: navigate to the main activity if the user has signed in properly

               goToQuestionnaire();
             //   Toast.makeText(LoginActivity.this, "Success!", Toast.LENGTH_SHORT).show();


            }
        });
    }

    private void goToQuestionnaire() {

        Intent i = new Intent(LoginActivity.this, QuestionnaireActivity.class);
        startActivity(i);
        finish();
    }





}
