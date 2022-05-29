package com.lemzeeyyy.quizadminapplication;

import static com.lemzeeyyy.quizadminapplication.CourseActivity.courseList;
import static com.lemzeeyyy.quizadminapplication.CourseActivity.selectedCourseIndex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DifficultyActivity extends AppCompatActivity {
    private RecyclerView diffRecyclerVier;
    private Button addDiffBtn;
    public static List<String> difficultyIDs = new ArrayList<>();
    private DifficultyAdapter adapter;
    FirebaseFirestore firestore;
    private Dialog loadingDialog;
    public static int selected_diff_level_index =  0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);
        diffRecyclerVier = findViewById(R.id.diff_recyclerID);
        addDiffBtn = findViewById(R.id.addDifficulty);
        Toolbar toolbar = findViewById(R.id.diffToolBarID);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Difficulty Levels");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadingDialog = new Dialog(DifficultyActivity.this);
        loadingDialog.setContentView(R.layout.loadingprogressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progressbackground);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        addDiffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewDiffLevel();
            }
        });
        firestore = FirebaseFirestore.getInstance();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        diffRecyclerVier.setLayoutManager(layoutManager);
        loadDifficulty();
    }

    private void addNewDiffLevel() {
        loadingDialog.dismiss();
        String curr_courseId = courseList.get(selectedCourseIndex).getCourseId();
        final String curr_counter = courseList.get(selectedCourseIndex).getDiffCounter();
        Log.d("DiffCounter", "addNewDiffLevel: "+courseList.get(selectedCourseIndex).getDiffCounter());
        Map<String, Object> qData = new ArrayMap<>();
        qData.put("COUNT","0");
        firestore.collection("QUIZ").document(curr_courseId)
                .collection(curr_counter)
                .document("QUESTION_LIST")
                .set(qData)
                .addOnSuccessListener(unused -> {
                    Map<String,Object> courseDoc = new ArrayMap<>();
                    courseDoc.put("COUNTER",(String.valueOf(Integer.parseInt(curr_counter)+1)));
                    courseDoc.put(new StringBuilder().append("DIFFICULTY").append(difficultyIDs.size() + 1).append("_ID").toString(),(curr_counter));
                    courseDoc.put("DIFFICULTY",difficultyIDs.size()+1);
                    Log.d("counter", "addNewDiffLevel: "+courseDoc.get("COUNTER"));
                    Log.d("Level", "addNewDiffLevel: "+courseDoc.get("DIFFICULTY"));

                    Log.d("TAG", "addNewDiffLevel: " +courseDoc.get("DIFFICULTY10_ID"));

                    firestore.collection("QUIZ").document(curr_courseId)
                            .update(courseDoc)
                            .addOnSuccessListener(unused1 -> {
                                Toast.makeText(DifficultyActivity.this, "Difficulty Level Added Successfully",
                                        Toast.LENGTH_SHORT).show();
                                difficultyIDs.add(curr_counter);
                                courseList.get(selectedCourseIndex).setDifficulty_level(String.valueOf(difficultyIDs.size()));
                                courseList.get(selectedCourseIndex).setDiffCounter(String.valueOf(Integer.parseInt(curr_counter)+1));
                                adapter.notifyItemInserted(difficultyIDs.size());
                                loadingDialog.dismiss();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(DifficultyActivity.this, e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DifficultyActivity.this, e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void loadDifficulty() {
        difficultyIDs.clear();
        loadingDialog.show();
        firestore.collection("QUIZ").document(courseList.get(selectedCourseIndex).getCourseId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                long noOfLevel = (long) documentSnapshot.get("DIFFICULTY");
                for (int i = 1; i <= noOfLevel ; i++){
                    difficultyIDs.add(documentSnapshot.getString("DIFFICULTY"+String.valueOf(i)+"_ID"));
                    Log.d("DifficultyID", "onSuccess: "+documentSnapshot.getString("DIFFICULTY"+String.valueOf(i)+"_ID"));

                }
                courseList.get(selectedCourseIndex).setDiffCounter(documentSnapshot.getString("COUNTER"));
                courseList.get(selectedCourseIndex).setDifficulty_level((String.valueOf(noOfLevel)));
                Log.d("Difficulty", "onSuccess: "+difficultyIDs.get(1));
                adapter = new DifficultyAdapter(difficultyIDs);
                diffRecyclerVier.setAdapter(adapter);
                loadingDialog.dismiss();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DifficultyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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