package com.lemzeeyyy.quizadminapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CourseActivity extends AppCompatActivity implements onDeleteClick{
    private RecyclerView courseRecyclerView;
    private Button addCourse;
    public static List<CourseModel> courseList;
    public static int selectedCourseIndex=0;
    private CourseAdapter adapter;
    private FirebaseFirestore firestore;
    private Dialog loadingDialog, addCourseDialog;
    private EditText dialogCourseName;
    private Button dialogAddBtn;

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

        addCourseDialog = new Dialog(CourseActivity.this);
        addCourseDialog.setContentView(R.layout.add_course_layout);
        addCourseDialog.setCancelable(true);
        addCourseDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogCourseName = addCourseDialog.findViewById(R.id.add_course_name);
        dialogAddBtn = addCourseDialog.findViewById(R.id.course_add_btn);

        addCourse.setOnClickListener(view -> {
            dialogCourseName.getText().clear();
            addCourseDialog.show();
        });
        dialogAddBtn.setOnClickListener(view -> {
            if (dialogCourseName.getText().toString().isEmpty()){
                dialogCourseName.setError("Enter Category name");
                return;
            }
            for (int i = 0; i < courseList.size(); i++) {
                if(dialogCourseName.getText().toString().equalsIgnoreCase(courseList.get(i).getCourseName())){
                    Log.d("ExistingCourse", "addNewCourse: Course already exists");
                    break;
                }else
                    addNewCourse(dialogCourseName.getText().toString());
                break;
            }

        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        courseRecyclerView.setLayoutManager(layoutManager);


    }

    private void addNewCourse(String title) {
        addCourseDialog.dismiss();
        loadingDialog.show();
        Map<String, Object> courseData = new ArrayMap<>();
                courseData.put("NAME",title);
                courseData.put("DIFFICULTY",0);
                courseData.put("COUNTER","1");
                String doc_id = firestore.collection("QUIZ").document().getId();
                firestore.collection("QUIZ").document(doc_id)
                        .set(courseData)
                        .addOnSuccessListener(unused -> {
                            Map<String,Object> catDoc = new ArrayMap<>();
                            catDoc.put("CAT" + String.valueOf(courseList.size()+1)+"_NAME",title);
                            catDoc.put("CAT" + String.valueOf(courseList.size()+1)+"_ID",doc_id);
                            catDoc.put("COUNT", courseList.size()+1);
                            firestore.collection("QUIZ").document("Categories")
                                    .update(catDoc).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(CourseActivity.this,"category added successfully",Toast.LENGTH_SHORT).show();
                                    courseList.add(new CourseModel(doc_id,title,"0","1"));
                                    adapter.notifyItemInserted(courseList.size());
                                    loadingDialog.dismiss();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(CourseActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismiss();

                                }
                            });

                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CourseActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                                //

                            }
                        });
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
                                String courseName = doc.getString("CAT"+String.valueOf(i)+"_NAME");
                                String courseId = doc.getString("CAT"+String.valueOf(i)+"_ID");
                                courseList.add(new CourseModel(courseId,courseName,"0","1"));
                            }
                            adapter = new CourseAdapter(courseList,CourseActivity.this,this);
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

    @Override
    public void deleteClick(int position, List<CourseModel> courseList) {
        Log.d("ondeleteclickcheck", "deleteClick: "+CourseActivity.courseList.get(position).getCourseName());
        AlertDialog dialog = new AlertDialog.Builder(CourseActivity.this)
                .setTitle("Delete category")
                .setMessage("Do you want to delete this category")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteCategory(position,CourseActivity.this, adapter);
                    }
                })
                .setNegativeButton("Cancel",null)
                .setIcon(R.drawable.ic_baseline_warning_24)
                .show();
        adapter.notifyDataSetChanged();
    }
    private void deleteCategory(final int pos, Context context, CourseAdapter adapter) {
        loadingDialog.show();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Map<String,Object> courseDoc = new ArrayMap<>();
        int index = 1;
        for (int i = 0; i < courseList.size(); i++) {
            if(i != pos){
                courseDoc.put("CAT"+index +"_ID",courseList.get(i).getCourseId());
                courseDoc.put("CAT"+index +"_NAME",courseList.get(i).getCourseName());
                index++;
            }
        }
        courseDoc.put("COUNT",index-1);
        firestore.collection("QUIZ").document("Categories")
                .set(courseDoc).addOnSuccessListener(unused -> {
                    Toast.makeText(context, "Category deleted successfully", Toast.LENGTH_SHORT)
                            .show();
                    CourseActivity.courseList.remove(pos);
                    adapter.notifyDataSetChanged();
                    loadingDialog.dismiss();

                }).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());

    }
}
