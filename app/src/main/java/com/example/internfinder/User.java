package com.example.internfinder;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;


@Parcel(analyze=User.class)
@ParseClassName("User")


public class User extends ParseObject {

    public static final String KEY_PROFILEPICTURE = "profilePicture";
    public static final String KEY_BIO = "bio";
    public static final String KEY_FIRSTNAME = "firstname";
    public static  final String KEY_LASTNAME = "lastname";
    public static  final String KEY_USER = "user";
    public static  final String KEY_USERNAME = "username";
    public static  final String KEY_PASSWORD = "password";
    public static  final String KEY_INDUSTRY = "industry";



    public ParseFile getProfilePicture() {

        return getParseFile(KEY_PROFILEPICTURE);
    }

    public void setProfilePicture(ParseFile parseFile) {

        put(KEY_PROFILEPICTURE,parseFile);
    }

    public String getBio() {

        return getString(KEY_BIO);
    }

    public void setBio(String description) {

        put(KEY_BIO, description);

    }

    public String getFirstName() {

        return getString(KEY_FIRSTNAME);
    }

    public void setFirstname(String firstname) {

        put(KEY_FIRSTNAME, firstname);

    }

    public String getLastname() {

        return getString(KEY_LASTNAME);
    }

    public void setLastname(String lastname) {

        put(KEY_LASTNAME, lastname);

    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser) {
        put(KEY_USER, parseUser);
    }

    public void setUsername(String username) {
        put(KEY_USERNAME, username);
    }

    public String getUsername() {
        return getString(KEY_USERNAME);
    }

    public void setIndustry(String industry) {
        put(KEY_INDUSTRY, industry);
    }

    public String getIndustry() {
        return getString(KEY_INDUSTRY);
    }

}
