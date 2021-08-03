package com.example.internfinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.internfinder.R;
import com.example.internfinder.models.Answers;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireActivity extends AppCompatActivity {
    private static final String TAG = "QuestionnaireActivity";
    RadioGroup radioGroupIndustry;
    RadioGroup radioGroupQuestion1;
    RadioGroup radioGroupQuestion2;
    RadioGroup radioGroupQuestion3;
    RadioGroup radioGroupQuestion4;

    RadioButton radioButtonIndustry;
    RadioButton radioButtonQuestion1;
    RadioButton radioButtonQuestion2;
    RadioButton radioButtonQuestion3;
    RadioButton radioButtonQuestion4;

    Button btnApply;
    TextView tvQuestionnaireTitle;
    TextView tvIndustryQuestionTitle;
    TextView tvQuestion1;
    TextView tvQuestion2;
    TextView tvQuestion3;
    TextView tvQuestion4;
    TextView tvAnswers;
    TextView tvAnswers2;

    ParseUser user;

    private RelativeLayout rlQuestions;

    Answers answer;

    List<Answers> currentUserAnswersList;
    List<Answers> otherUserAnswersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        radioGroupIndustry = findViewById(R.id.radioGroupIndustry);
        radioGroupQuestion1 = findViewById(R.id.radioGroupQuestion1);
        radioGroupQuestion2 = findViewById(R.id.radioGroupQuestion2);
        radioGroupQuestion3 = findViewById(R.id.radioGroupQuestion3);
        radioGroupQuestion4 = findViewById(R.id.radioGroupQuestion4);

        tvIndustryQuestionTitle = findViewById(R.id.tvIndustryQuestionTitle);
        tvQuestionnaireTitle = findViewById(R.id.tvQuestionnaireTitle);
        tvQuestion1 = findViewById(R.id.tvQuestion1);
        tvQuestion2 = findViewById(R.id.tvQuestion2);
        tvQuestion3 = findViewById(R.id.tvQuestion3);
        tvQuestion4 = findViewById(R.id.tvQuestion4);

        tvAnswers = findViewById(R.id.tvAnswers);
        tvAnswers2 = findViewById(R.id.tvAnswers2);


        currentUserAnswersList = new ArrayList<>();
        otherUserAnswersList = new ArrayList<>();

        answer = null;

        btnApply = findViewById(R.id.btnApply);

        user = Parcels.unwrap(getIntent().getParcelableExtra("User"));
        Log.i(TAG, "QUESTIONAIRE USER USERNAME: " + user.getUsername());

        if(!user.equals(ParseUser.getCurrentUser())) {

            afterEdit();
        }

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               getAnswers(ParseUser.getCurrentUser());
                    Log.i(TAG, "REACHED QUESTIONAIRE IF: " + user.getUsername());
                    if (answer == null) {
                        answer = new Answers();
                        answer.setUser(ParseUser.getCurrentUser());

                    }
                    // industry

                    int radioIDIndustry = radioGroupIndustry.getCheckedRadioButtonId();

                    radioButtonIndustry = findViewById(radioIDIndustry);

                    ParseUser.getCurrentUser().put("industry", radioButtonIndustry.getText());

                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Error while saving!");
                            }
                            Log.i(TAG, "Successfully saved!");
                        }
                    });

                    // question 1

                    int radioIDQuestion1 = radioGroupQuestion1.getCheckedRadioButtonId();

                    radioButtonQuestion1 = findViewById(radioIDQuestion1);

                    answer.setQuestion1((String) radioButtonQuestion1.getText());

                    // question 2

                    int radioIDQuestion2 = radioGroupQuestion2.getCheckedRadioButtonId();

                    radioButtonQuestion2 = findViewById(radioIDQuestion2);

                    answer.setQuestion2((String) radioButtonQuestion2.getText());

                    // question 3

                    int radioIDQuestion3 = radioGroupQuestion3.getCheckedRadioButtonId();

                    radioButtonQuestion3 = findViewById(radioIDQuestion3);

                    answer.setQuestion3((String) radioButtonQuestion3.getText());

                    // question 4

                    int radioIDQuestion4 = radioGroupQuestion4.getCheckedRadioButtonId();

                    radioButtonQuestion4 = findViewById(radioIDQuestion4);

                    answer.setQuestion4((String) radioButtonQuestion4.getText());


                    answer.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Error while saving!");
                            }
                            Log.i(TAG, "Successfully saved!");
                        }
                    });

                    getAnswers(ParseUser.getCurrentUser());


                radioGroupIndustry.setVisibility(View.GONE);
                radioGroupQuestion1.setVisibility(View.GONE);
                radioGroupQuestion2.setVisibility(View.GONE);
                radioGroupQuestion3.setVisibility(View.GONE);
                radioGroupQuestion4.setVisibility(View.GONE);

                tvIndustryQuestionTitle.setVisibility(View.GONE);
                tvQuestionnaireTitle.setVisibility(View.GONE);
                tvQuestion1.setVisibility(View.INVISIBLE);
                tvQuestion2.setVisibility(View.INVISIBLE);
                tvQuestion3.setVisibility(View.INVISIBLE);
                tvQuestion4.setVisibility(View.INVISIBLE);

                btnApply.setVisibility(View.GONE);

                tvAnswers.setText(tvQuestion1.getText().toString() + "\n" + radioButtonQuestion1.getText() + "\n\n" +
                        tvQuestion2.getText().toString() + "\n" + radioButtonQuestion2.getText() + "\n\n" +
                        tvQuestion3.getText().toString() + "\n" + radioButtonQuestion3.getText() + "\n\n" +
                        tvQuestion4.getText().toString() + "\n" + radioButtonQuestion4.getText() + "\n\n");

                Intent i = new Intent(QuestionnaireActivity.this, ModifyProfileActivity.class);
                startActivity(i);
            }
        });


    }

    private void afterEdit() {
        btnApply.setVisibility(View.GONE);
        radioGroupIndustry.setVisibility(View.GONE);
        radioGroupQuestion1.setVisibility(View.GONE);
        radioGroupQuestion2.setVisibility(View.GONE);
        radioGroupQuestion3.setVisibility(View.GONE);
        radioGroupQuestion4.setVisibility(View.GONE);

        tvIndustryQuestionTitle.setVisibility(View.GONE);
        tvQuestionnaireTitle.setVisibility(View.GONE);
        tvQuestion1.setVisibility(View.INVISIBLE);
        tvQuestion2.setVisibility(View.INVISIBLE);
        tvQuestion3.setVisibility(View.INVISIBLE);
        tvQuestion4.setVisibility(View.INVISIBLE);

        getAnswers(user);

        Log.i(TAG, "username of answer " + answer.getUser().getUsername());

        //tvAnswers.setText(tvQuestion1.getText().toString() + "\n" + answer.getQuestion1() + "\n\n" + tvQuestion2.getText().toString() + "\n" + answer.getQuestion2());
        //tvAnswers2.setText("\n\n" + tvQuestion3.getText().toString() + "\n" + currentUserAnswersList.get(2)+ "\n\n" + tvQuestion4.getText().toString() + "\n" +     currentUserAnswersList.get(3) + "\n\n");
    }


    public void getAnswers(ParseUser user) {
        ParseQuery<Answers> query = ParseQuery.getQuery("Answers");
        query.whereEqualTo("user", user);
        query.setLimit(15);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Answers>() {
            @Override
            public void done(List<Answers> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Problem with fetching posts", e);
                    return;
                } else {

                    Log.i(TAG, "successfully got answers from method");
                    Log.i(TAG, "answers size in method: " + objects.size());
                    //answer = objects.get(0);
                }
            }
        });
    }

    public void checkCompatability() {

        int score = 0;

        //otherUserAnswersList = getAnswers(post.getUser());

        if(currentUserAnswersList.get(0).getQuestion1().equals(otherUserAnswersList.get(0).getQuestion1())) {
            score++;
        } else if(currentUserAnswersList.get(0).getQuestion2().equals(otherUserAnswersList.get(0).getQuestion2())) {
            score++;
        } else if(currentUserAnswersList.get(0).getQuestion3().equals(otherUserAnswersList.get(0).getQuestion3())) {
            score++;
        } else if (currentUserAnswersList.get(0).getQuestion4().equals(otherUserAnswersList.get(0).getQuestion4())) {
            score++;
        }

        float finalScore =  score/5;

      //  percentage.setText("You and this user are " + finalScore + "% compatible");

    }
}
