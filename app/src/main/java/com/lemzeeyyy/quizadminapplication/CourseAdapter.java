package com.lemzeeyyy.quizadminapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
    private List<CourseModel> courseList;
    private Context context;

    public CourseAdapter(List<CourseModel> courseList, Context context) {
        this.courseList = courseList;
        this.context = context;
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
        holder.setData(title);

    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView courseName;
        private ImageView deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.courseNameID);
            deleteBtn = itemView.findViewById(R.id.courseDeleteBtnID);
        }

        public void setData(String title) {
            courseName.setText(title);
            deleteBtn.setOnClickListener(view -> {

            });
        }
    }
}
