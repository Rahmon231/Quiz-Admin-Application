package com.lemzeeyyy.quizadminapplication;

import static com.lemzeeyyy.quizadminapplication.CourseActivity.courseList;
import static com.lemzeeyyy.quizadminapplication.CourseActivity.selectedCourseIndex;
import static com.lemzeeyyy.quizadminapplication.DifficultyActivity.difficultyIDs;
import static com.lemzeeyyy.quizadminapplication.DifficultyActivity.selected_diff_level_index;
import static com.lemzeeyyy.quizadminapplication.QuestionsActivity.questionsList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;


public class QuestionsDetailsActivity extends AppCompatActivity {
    private EditText question, optA,optB,optC,optD,answer;
    private Button addNewQuest;
    private String quesStr,optAStr,optBStr,optCStr,optDStr,ansStr;
    private Dialog loadingDialog;
    private FirebaseFirestore firestore;
    private String action;
    private int qId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_details);
        Toolbar toolbar = findViewById(R.id.ques_details_toolBarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        question = findViewById(R.id.questionID);
        optA = findViewById(R.id.optionAID);
        optB = findViewById(R.id.optionBID);
        optC = findViewById(R.id.optionCID);
        optD = findViewById(R.id.optionDID);
        answer = findViewById(R.id.correctAnsID);
        addNewQuest = findViewById(R.id.addNewQBtn);
        loadingDialog = new Dialog(QuestionsDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loadingprogressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progressbackground);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        firestore = FirebaseFirestore.getInstance();
        action = getIntent().getStringExtra("ACTION");
        if(action.compareTo("EDIT") ==0 ){
            qId = getIntent().getIntExtra("Q_ID",0);
            getSupportActionBar().setTitle("Question "+String.valueOf(qId+1));
            loadData(qId);
            addNewQuest.setText("Update");

        }else {
            getSupportActionBar().setTitle("Question "+questionsList.size()+1);
            addNewQuest.setText("ADD");
        }
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
                if(action.compareTo("EDIT")==0){
                    editQuestion();
                }else{
                    addNewQuestion();
                }
            }
        });
    }

    private void editQuestion() {

        Map<String,Object> quesData = new ArrayMap<>();
        quesData.put("QUESTION",quesStr);
        quesData.put("A",optAStr);
        quesData.put("B",optBStr);
        quesData.put("C",optCStr);
        quesData.put("D",optDStr);
        quesData.put("ANSWER",ansStr);
        firestore.collection("QUIZ").document(courseList.get(selectedCourseIndex).getCourseId())
                .collection(difficultyIDs.get(selected_diff_level_index)).document(questionsList.get(qId).getQuestionId())
                .set(quesData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(QuestionsDetailsActivity.this, "Question updated successfully", Toast.LENGTH_SHORT).show();
                        questionsList.get(qId).setQuestion(quesStr);
                        questionsList.get(qId).setOptionA(optAStr);
                        questionsList.get(qId).setOptionB(optBStr);
                        questionsList.get(qId).setOptionC(optCStr);
                        questionsList.get(qId).setOptionD(optDStr);
                        questionsList.get(qId).setCorrectAnswer(Integer.valueOf(ansStr));
                        QuestionsDetailsActivity.this.finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QuestionsDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void loadData(int pos) {
        question.setText(questionsList.get(pos).getQuestion());
        optA.setText(questionsList.get(pos).getOptionA());
        optB.setText(questionsList.get(pos).getOptionB());
        optC.setText(questionsList.get(pos).getOptionC());
        optD.setText(questionsList.get(pos).getOptionD());
        answer.setText(String.valueOf(questionsList.get(pos).getCorrectAnswer()));

    }

    private void addNewQuestion() {
        loadingDialog.show();
        Map<String,Object> questionData = new ArrayMap<>();
        questionData.put("QUESTION",quesStr);
        questionData.put("A",optAStr);
        questionData.put("B",optBStr);
        questionData.put("C",optCStr);
        questionData.put("D",optDStr);
        questionData.put("ANSWER",ansStr);

        String docId = firestore.collection("QUIZ").document(courseList.get(selectedCourseIndex).getCourseId())
                .collection(difficultyIDs.get(selected_diff_level_index)).document().getId();

        firestore.collection("QUIZ").document(courseList.get(selectedCourseIndex).getCourseId())
                .collection(difficultyIDs.get(selected_diff_level_index)).document(docId)
                .set(questionData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Map<String,Object> quesDoc = new ArrayMap<>();
                        quesDoc.put("Q"+String.valueOf(questionsList.size()+1) +"_ID",docId);
                        quesDoc.put("COUNT",String.valueOf(questionsList.size()+1));

                        firestore.collection("QUIZ").document(courseList.get(selectedCourseIndex).getCourseId())
                                .collection(difficultyIDs.get(selected_diff_level_index)).document("QUESTION_LIST")
                                .update(quesDoc)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(QuestionsDetailsActivity.this, "Question added successfully",
                                                Toast.LENGTH_SHORT).show();
                                        questionsList.add(new QuestionModel(
                                                quesStr,
                                                optAStr,
                                                optBStr,
                                                optCStr,
                                                optDStr,
                                                Integer.valueOf(ansStr),
                                                docId
                                        ));
                                        loadingDialog.dismiss();
                                        QuestionsDetailsActivity.this.finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(QuestionsDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismiss();

                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QuestionsDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();

                    }
                });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}