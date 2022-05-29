package com.lemzeeyyy.quizadminapplication;

import static com.lemzeeyyy.quizadminapplication.QuestionsActivity.questionsList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class QuestionsDetailsActivity extends AppCompatActivity {
    private EditText question, optA,optB,optC,optD,answer;
    private Button addNewQuest;
    private String quesStr,optAStr,optBStr,optCStr,optDStr,ansStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_details);
        Toolbar toolbar = findViewById(R.id.ques_details_toolBarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Question "+String.valueOf(questionsList.size()+1));
        question = findViewById(R.id.questionID);
        optA = findViewById(R.id.optionAID);
        optB = findViewById(R.id.optionBID);
        optC = findViewById(R.id.optionCID);
        optD = findViewById(R.id.optionDID);
        answer = findViewById(R.id.correctAnsID);
        addNewQuest = findViewById(R.id.addNewQBtn);
        addNewQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quesStr = question.getText().toString();
                optAStr = optA.getText().toString();
                optBStr = optB.getText().toString();
                optCStr = optC.getText().toString();
                optDStr = optD.getText().toString();
                ansStr = answer.getText().toString();
                if(quesStr.isEmpty()){
                    question.setError("Enter Question");
                    return;
                }
                if(optAStr.isEmpty()){
                    optA.setError("Enter Option A");
                    return;
                }
                if(optBStr.isEmpty()){
                    optB.setError("Enter Option B");
                    return;
                }
                if(optCStr.isEmpty()){
                    optC.setError("Enter Option C");
                    return;
                }
                if(optDStr.isEmpty()){
                    optD.setError("Enter Option D");
                    return;
                }
                if(ansStr.isEmpty()){
                    answer.setError("Enter Correct Answer");
                    return;
                }

            }
        });
    }
}