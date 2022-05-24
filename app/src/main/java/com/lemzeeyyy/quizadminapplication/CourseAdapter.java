package com.lemzeeyyy.quizadminapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
    private List<CourseModel> courseList;
    private Context context;
    private onDeleteClick onDeleteClick;
    private Dialog loadingDialog;
    private Dialog editCourseDialog;

    public CourseAdapter(List<CourseModel> courseList, Context context,onDeleteClick onDeleteClick) {
        this.courseList = courseList;
        this.context = context;
        this.onDeleteClick = onDeleteClick;
    }

    @NonNull
    @Override
    public CourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAdapter.ViewHolder holder, int position) {
        String title = courseList.get(position).getCourseName();
        holder.setData(title,position,this);

    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView courseName;
        private ImageView deleteBtn;
        private EditText courseEditText;
        private Button updateCourseBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.courseNameID);
            deleteBtn = itemView.findViewById(R.id.courseDeleteBtnID);
            loadingDialog = new Dialog(itemView.getContext());
            loadingDialog.setContentView(R.layout.loadingprogressbar);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progressbackground);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            editCourseDialog = new Dialog(itemView.getContext());
            editCourseDialog.setContentView(R.layout.edit_course_layout_dialog);
            editCourseDialog.setCancelable(true);
            editCourseDialog.getWindow().setBackgroundDrawableResource(R.drawable.progressbackground);
            editCourseDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            updateCourseBtn = editCourseDialog.findViewById(R.id.updateCourseName);
            courseEditText = editCourseDialog.findViewById(R.id.editCourseNameET);
        }

        public void setData(String title, int pos, CourseAdapter adapter) {
            courseName.setText(title);
            deleteBtn.setOnClickListener(view -> {
                /* Log.d("checkDelete", "setData: deleted");
                AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                        .setTitle("Delete category")
                        .setMessage("Do you want to delete this category")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteCategory(pos,itemView.getContext(), adapter);
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .setIcon(R.drawable.ic_baseline_warning_24)
                        .show(); */

                onDeleteClick.deleteClick(getAdapterPosition(),courseList);
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(),DifficultyActivity.class);
                    itemView.getContext().startActivity(intent);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Log.d("longclick", "onLongClick: Long Clicked");
                    courseEditText.setText(courseList.get(pos).getCourseName());
                    editCourseDialog.show();
                    Log.d("CheckCourseName", "onLongClick: " + courseName.getText().toString());

                    return false;
                }
            });
            updateCourseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("updateclicked", "onClick: update clicked");
                    if (courseEditText.getText().toString().isEmpty()){
                        courseEditText.setError("Enter category name");
                        return;
                    }
                    updateCategory(courseEditText.getText().toString(), pos, itemView.getContext(), adapter);
                }
            });

        }


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
                .set(courseDoc).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "Category deleted successfully", Toast.LENGTH_SHORT)
                        .show();
                CourseActivity.courseList.remove(pos);
                adapter.notifyDataSetChanged();
                loadingDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void updateCategory(String courseNewName, int pos,Context context, CourseAdapter adapter) {
        editCourseDialog.dismiss();
        loadingDialog.show();
        Map<String,Object> courseData = new ArrayMap<>();
        courseData.put("NAME",courseNewName);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("QUIZ").document(courseList.get(pos).getCourseId())
                .update(courseData)
                .addOnSuccessListener(unused -> {
                    Map<String,Object> courseData1 = new ArrayMap<>();
                    courseData1.put("CAT"+(pos+1)+"_NAME",courseNewName);
                    firestore.collection("QUIZ").document("Categories")
                            .update(courseData1)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "Course Added Successfully", Toast.LENGTH_SHORT).show();
                                    CourseActivity.courseList.get(pos).setCourseName(courseNewName);
                                    adapter.notifyDataSetChanged();
                                    loadingDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, e.getMessage(),Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                        }
                    });


                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(),Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();

            }
        });

    }
}
