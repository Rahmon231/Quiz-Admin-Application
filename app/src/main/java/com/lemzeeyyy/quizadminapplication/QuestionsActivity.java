package com.lemzeeyyy.quizadminapplication;

import static com.lemzeeyyy.quizadminapplication.CourseActivity.courseList;
import static com.lemzeeyyy.quizadminapplication.CourseActivity.selectedCourseIndex;
import static com.lemzeeyyy.quizadminapplication.DifficultyActivity.difficultyIDs;
import static com.lemzeeyyy.quizadminapplication.DifficultyActivity.selected_diff_level_index;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        loadingDialog.show();
        firestore.collection("QUIZ").document(courseList.get(selectedCourseIndex).getCourseId())
                .collection(difficultyIDs.get(selected_diff_level_index))
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Map<String,QueryDocumentSnapshot> docList = new ArrayMap<>();
                for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    docList.put(doc.getId(),doc);
                }
                    QueryDocumentSnapshot quesListDoc = docList.get("QUESTION_LIST");
                Log.d("QuestionDocList", "onSuccess: "+quesListDoc.get("COUNT"));
                Log.d("QUestionDocList", "onSuccess: "+quesListDoc.getString("Q1_ID"));


//                String count = quesListDoc.getString("COUNT");
//                    for(int i = 0 ; i < Integer.valueOf("1") ; i++ ){
//                        String quesId = quesListDoc.getString("Q"+(i+1)+"_ID");
//                        QueryDocumentSnapshot quesDoc = docList.get(quesId);
//                        questionsList.add(new QuestionModel(
//                                quesDoc.getString("QUESTION"),
//                                quesDoc.getString("A"),
//                                quesDoc.getString("B"),
//                                quesDoc.getString("C"),
//                                quesDoc.getString("D"),
//                                Integer.parseInt(quesDoc.getString("ANSWER")),
//                                quesId
//                        ));
//                    }
                    loadingDialog.dismiss();
                    adapter = new QuestionAdapter(questionsList);
                    questionRecycler.setAdapter(adapter);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QuestionsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });
    }
}