package com.example.internfinder;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;


import org.parceler.Parcel;

import java.util.Date;

@Parcel(analyze= Follow.class)
@ParseClassName("Follow")
public class Follow extends ParseObject {

    public static final String KEY_FROM = "from";
    public static final String KEY_TO = "to";


    public ParseUser getFrom() {

        return getParseUser(KEY_FROM);
    }

    public void setFrom(ParseUser parseUser) {

        put(KEY_FROM, parseUser);
    }

    public ParseObject getTo() {
        return getParseObject(KEY_TO);
    }

    public void setTo(ParseUser parseUser) {
        put(KEY_TO, parseUser);
    }



}
