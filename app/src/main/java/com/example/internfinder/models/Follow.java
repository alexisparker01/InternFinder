package com.example.internfinder.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;


import org.parceler.Parcel;

@Parcel(analyze= Follow.class)
@ParseClassName("Follow")
public class Follow extends ParseObject {

    public static final String KEY_FROM = "from";
    public static final String KEY_TO = "to";

    public Follow() {
    }

    public ParseUser getFrom() {

        return getParseUser(KEY_FROM);
    }

    public void setFrom(ParseUser parseUser) {

        put(KEY_FROM, parseUser);
    }

    public ParseUser getTo() {
        return getParseUser(KEY_TO);
    }

    public void setTo(ParseUser parseUser) {
        put(KEY_TO, parseUser);
    }



}
