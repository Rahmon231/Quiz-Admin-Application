package com.lemzeeyyy.quizadminapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CourseActivity extends AppCompatActivity {
    private RecyclerView courseRecyclerView;
    private Button addCourse;
    private List<String> courseList;
    private FirebaseFirestore firestore;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        courseRecyclerView = findViewById(R.id.recyclerID);
        Toolbar toolbar = findViewById(R.id.toolBarID);
        addCourse = findViewById(R.id.addCourseBtn);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Courses");
        courseList = new ArrayList<>();
        loadingDialog = new Dialog(CourseActivity.this);
        loadingDialog.setContentView(R.layout.loadingprogressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progressbackground);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        firestore = FirebaseFirestore.getInstance();
        loadDate();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        courseRecyclerView.setLayoutManager(layoutManager);


    }
    private void loadDate() {
        loadingDialog.show();
        courseList.clear();
        firestore.collection("QUIZ").document("Categories")
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        if(doc.exists()){
                            long count = (long)doc.get("COUNT");
                            for (int i = 1; i <= count; i++) {
                                String courseName = doc.getString("CAT"+String.valueOf(i));
                                courseList.add(courseName);
                            }
                            CourseAdapter adapter = new CourseAdapter(courseList,CourseActivity.this);
                            courseRecyclerView.setAdapter(adapter);

                        }else {
                            Toast.makeText(CourseActivity.this, "no category document", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }else {
                        Toast.makeText(CourseActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismiss();

                });
    }
}
