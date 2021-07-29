package com.example.internfinder;

import android.app.Application;

import com.example.internfinder.models.Comment;
import com.example.internfinder.models.Follow;
import com.example.internfinder.models.Post;
import com.parse.Parse;
import com.parse.ParseObject;


public class ParseApplication extends Application {

    // Initializes Parse SDK as soon as the application is created

    @Override
    public void onCreate() {


        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(Follow.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("IPuVytyNpQ82X3oeQKfiEf62MVF8oSRma90hoRmV")
                .clientKey("nkPSDj3DiFDNLY71gkRzJT5BdNttWxbOJnQbb7EO")
                .server("https://parseapi.back4app.com")
                .build());
    }
}

