package com.lemzeeyyy.quizadminapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DifficultyAdapter extends RecyclerView.Adapter<DifficultyAdapter.ViewHolder> {
    private List<String> difficultyIds;

    public DifficultyAdapter(List<String> difficultyIds) {
        this.difficultyIds = difficultyIds;
    }

    @NonNull
    @Override
    public DifficultyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.course_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DifficultyAdapter.ViewHolder holder, int position) {
        holder.setData(position);

    }

    @Override
    public int getItemCount() {
        return difficultyIds.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView difficultyName;
        private ImageView deleteDifficulty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            difficultyName = itemView.findViewById(R.id.courseNameID);
            deleteDifficulty = itemView.findViewById(R.id.courseDeleteBtnID);
        }

        public void setData(int pos) {
            difficultyName.setText("DIFFICULTY_"+String.valueOf(pos+1));
        }
    }
}