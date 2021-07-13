package com.example.internfinder;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    // Initializes Parse SDK as soon as the application is created

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("MTcUr9A4knzwIz3WwLJDxdchXXs3275bQCrSrQGJ")
                .clientKey("qOq7Y2KiLR0T2IMKyjTLryoTp2xMi91vJwLQTa1Z")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
