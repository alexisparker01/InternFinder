package com.example.internfinder.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.Date;

@Parcel(analyze=Post.class)
@ParseClassName("Post")

public class Post extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_TYPE = "type";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_LATLNG = "latlng";

    public Post() {
    }

    public String getDescription() {

        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {

        put(KEY_DESCRIPTION, description);

    }

    public ParseFile getImage() {

        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {

        put(KEY_IMAGE,parseFile);
    }

    public ParseUser getUser() {

        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser) {

        put(KEY_USER, parseUser);
    }


    public String getType() {

        return getString(KEY_TYPE);
    }

    public void setType(String type) {

        put(KEY_TYPE, type);
    }
    public String getLocation() {

        return getString(KEY_LOCATION);
    }

    public void setLocation(String location) {

        put(KEY_LOCATION, location);
    }

    public ParseGeoPoint getLatLng() {

        return getParseGeoPoint(KEY_LATLNG);
    }

    public void setLatLng(ParseGeoPoint point) {

        put(KEY_LATLNG, point);

    }

    public static String calculateTimeAgo(Date createdAt) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " minutes ago";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " hours ago";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " days ago";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

}
