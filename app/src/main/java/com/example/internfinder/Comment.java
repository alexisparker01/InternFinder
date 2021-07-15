package com.example.internfinder;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@Parcel(analyze= Comment.class)
@ParseClassName("Comment")
public class Comment extends ParseObject {

    public static final String KEY_POST = "post";
    public static final String KEY_USER = "user";
    public static final String KEY_TEXT = "text";


    public ParseUser getUser() {

        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser) {

        put(KEY_USER, parseUser);
    }


    public ParseObject getPost() {
        return getParseObject(KEY_POST);
    }

    public void setPost(ParseObject post) {
        put(KEY_USER, post);
    }

    public String getText() {
        return getString(KEY_TEXT);
    }

    public void setPost(String text) {
        put(KEY_TEXT, text);
    }

}
