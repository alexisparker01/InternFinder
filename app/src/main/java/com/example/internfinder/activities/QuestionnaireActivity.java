package com.example.internfinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.internfinder.R;
import com.example.internfinder.models.Answers;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireActivity extends AppCompatActivity {

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

    private Button btnApply;
    private Button btnProfile;

    TextView tvQuestionnaireTitle;
    TextView tvIndustryQuestionTitle;
    TextView tvQuestion1;
    TextView tvQuestion2;
    TextView tvQuestion3;
    TextView tvQuestion4;
    TextView tvAnswers;
    TextView tvAnswers2;
    TextView tvAnswers3;

    ParseUser user;

    Answers answer;

    protected List<Answers> currentUserAnswersList;
    protected List<Answers> otherUserAnswersList;

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
        tvAnswers3 = findViewById(R.id.tvAnswers3);

        btnProfile = findViewById(R.id.btnProfile);

        currentUserAnswersList = new ArrayList<>();
        otherUserAnswersList = new ArrayList<>();

        answer = null;

        user = Parcels.unwrap(getIntent().getParcelableExtra("User"));


        btnApply = findViewById(R.id.btnApply);

        btnProfile.setVisibility(View.GONE);


        if (user != null) {


            if (!user.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {

                viewOtherUserAnswers();
            }
        }

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(QuestionnaireActivity.this, ModifyProfileActivity.class);
                startActivity(i);
            }
        });

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnProfile.setVisibility(View.GONE);

                getAnswers(ParseUser.getCurrentUser());

                if (currentUserAnswersList.size() > 0) {
                    answer = currentUserAnswersList.get(0);
                } else {
                    answer = new Answers();
                    answer.setUser(ParseUser.getCurrentUser());

                }


                int radioIDIndustry = radioGroupIndustry.getCheckedRadioButtonId();
                radioButtonIndustry = findViewById(radioIDIndustry);

                ParseUser.getCurrentUser().put("industry", radioButtonIndustry.getText());

                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                    }
                });


                int radioIDQuestion1 = radioGroupQuestion1.getCheckedRadioButtonId();
                radioButtonQuestion1 = findViewById(radioIDQuestion1);
                answer.setQuestion1((String) radioButtonQuestion1.getText());


                int radioIDQuestion2 = radioGroupQuestion2.getCheckedRadioButtonId();
                radioButtonQuestion2 = findViewById(radioIDQuestion2);
                answer.setQuestion2((String) radioButtonQuestion2.getText());


                int radioIDQuestion3 = radioGroupQuestion3.getCheckedRadioButtonId();
                radioButtonQuestion3 = findViewById(radioIDQuestion3);
                answer.setQuestion3((String) radioButtonQuestion3.getText());


                int radioIDQuestion4 = radioGroupQuestion4.getCheckedRadioButtonId();
                radioButtonQuestion4 = findViewById(radioIDQuestion4);
                answer.setQuestion4((String) radioButtonQuestion4.getText());


                answer.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                    }
                });

                viewYourAnswers();
            }
        });
    }

    private void viewYourAnswers() {
        btnApply.setVisibility(View.GONE);
        btnProfile.setVisibility(View.VISIBLE);
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

        getAnswers(ParseUser.getCurrentUser());

        tvAnswers.setText(tvQuestion1.getText().toString() + "\n" + currentUserAnswersList.get(0).getQuestion1());
        tvAnswers2.setText("\n\n" + tvQuestion2.getText().toString() + "\n" + currentUserAnswersList.get(0).getQuestion2());
        tvAnswers3.setText("\n\n" + tvQuestion3.getText().toString() + "\n" + currentUserAnswersList.get(0).getQuestion3() + "\n\n" + tvQuestion4.getText().toString() + "\n" + currentUserAnswersList.get(0).getQuestion4() + "\n\n");

    }


    public void viewOtherUserAnswers() {

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

        tvAnswers.setText(tvQuestion1.getText().toString() + "\n" + otherUserAnswersList.get(0).getQuestion1());
        tvAnswers2.setText("\n\n" + tvQuestion2.getText().toString() + "\n" + otherUserAnswersList.get(0).getQuestion2());
        tvAnswers3.setText("\n\n" + tvQuestion3.getText().toString() + "\n" + otherUserAnswersList.get(0).getQuestion3() + "\n\n" + tvQuestion4.getText().toString() + "\n" + otherUserAnswersList.get(0).getQuestion4() + "\n\n");
    }


    public void getAnswers(ParseUser user) {

        ParseQuery<Answers> query = ParseQuery.getQuery("Answers");


        query.whereEqualTo("user", user);
        query.setLimit(15);
        query.addDescendingOrder("createdAt");
        try {
            if (user.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                currentUserAnswersList = query.find();
            } else {
                otherUserAnswersList = query.find();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

}

