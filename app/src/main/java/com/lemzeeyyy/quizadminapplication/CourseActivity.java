package com.lemzeeyyy.quizadminapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridView;

public class CourseActivity extends AppCompatActivity {
    private RecyclerView courseRecyclerView;
    private Button addCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        courseRecyclerView = findViewById(R.id.recyclerID);
        Toolbar toolbar = findViewById(R.id.toolBarID);
        addCourse = findViewById(R.id.addCourseBtn);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Courses");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CourseAdapter adapter = new CourseAdapter(catList,CourseActivity.this);
        courseGrid.setAdapter(adapter);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            CourseActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
