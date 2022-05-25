package com.lemzeeyyy.quizadminapplication;

import static com.lemzeeyyy.quizadminapplication.CourseActivity.courseList;
import static com.lemzeeyyy.quizadminapplication.CourseActivity.selectedCourseIndex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
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

public class DifficultyActivity extends AppCompatActivity {
    private RecyclerView diffRecyclerVier;
    private Button addDiffBtn;
    public static List<String> difficultyIDs = new ArrayList<>();
    private DifficultyAdapter adapter;
    FirebaseFirestore firestore;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);
        diffRecyclerVier = findViewById(R.id.diff_recyclerID);
        addDiffBtn = findViewById(R.id.addDifficulty);
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

    }

    private void loadDifficulty() {
        difficultyIDs.clear();
        loadingDialog.show();
        firestore.collection("QUIZ").document(courseList.get(selectedCourseIndex).getCourseId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                long noOfLevel = (long) documentSnapshot.get("DIFFICULTY");
                for (int i = 0; i < noOfLevel ; i++){
                    difficultyIDs.add(documentSnapshot.get("DIFFICULTY")+String.valueOf(i)+"_ID");
                }
                courseList.get(selectedCourseIndex).setDiffCounter(documentSnapshot.getString("COUNTER"));
                courseList.get(selectedCourseIndex).setDifficulty_level(Integer.parseInt(String.valueOf(noOfLevel)));
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

}