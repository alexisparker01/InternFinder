package com.example.internfinder.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@Parcel(analyze= Answers.class)
@ParseClassName("Answers")
public class Answers extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_QUESTION1 = "question1";
    public static final String KEY_QUESTION2 = "question2";
    public static final String KEY_QUESTION3 = "question3";
    public static final String KEY_QUESTION4 = "question4";

    public Answers() {
    }

    public ParseUser getUser() {

        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser) {

        put(KEY_USER, parseUser);
    }

    public String getQuestion1() {
        return getString(KEY_QUESTION1);
    }

    public void setQuestion1(String question1) {
        put(KEY_QUESTION1, question1);
    }

    public String getQuestion2() {
        return getString(KEY_QUESTION2);
    }

    public void setQuestion2(String question2) {
        put(KEY_QUESTION2, question2);
    }

    public String getQuestion3() {
        return getString(KEY_QUESTION3);
    }

    public void setQuestion3(String question3) {
        put(KEY_QUESTION3, question3);
    }

    public String getQuestion4() {
        return getString(KEY_QUESTION4);
    }

    public void setQuestion4(String question4) {
        put(KEY_QUESTION4, question4);
    }


}
