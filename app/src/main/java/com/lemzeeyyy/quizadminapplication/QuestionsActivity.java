package com.lemzeeyyy.quizadminapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {
    private RecyclerView questionRecycler;
    private Button addQuesBtn;
    public static List<QuestionModel> questionsList = new ArrayList<>();
    private QuestionAdapter adapter;
    private FirebaseFirestore firestore ;
    private Dialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        Toolbar toolbar = findViewById(R.id.ques_toolBarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Questions");
        questionRecycler = findViewById(R.id.ques_recyclerID);
        addQuesBtn = findViewById(R.id.add_questionBtn);
        loadingDialog = new Dialog(QuestionsActivity.this);
        loadingDialog.setContentView(R.layout.loadingprogressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progressbackground);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);


        addQuesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        questionRecycler.setLayoutManager(layoutManager);
        firestore = FirebaseFirestore.getInstance();
        loadQuestions();

    }

    private void loadQuestions() {
        questionsList.clear();
        questionsList.add(new QuestionModel("Q1","A","B","C","D",2,"1"));
        questionsList.add(new QuestionModel("Q1","A","B","C","D",2,"2"));
        questionsList.add(new QuestionModel("Q1","A","B","C","D",2,"3"));
        adapter = new QuestionAdapter(questionsList);
        questionRecycler.setAdapter(adapter);
    }
}