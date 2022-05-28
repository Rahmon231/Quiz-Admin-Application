package com.lemzeeyyy.quizadminapplication;

import static com.lemzeeyyy.quizadminapplication.CourseActivity.courseList;
import static com.lemzeeyyy.quizadminapplication.CourseActivity.selectedCourseIndex;
import static com.lemzeeyyy.quizadminapplication.DifficultyActivity.difficultyIDs;
import static com.lemzeeyyy.quizadminapplication.DifficultyActivity.selected_diff_level_index;
import static com.lemzeeyyy.quizadminapplication.QuestionsActivity.questionsList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
    private List<QuestionModel> ques_list;
    private Dialog loadingDialog;

    public QuestionAdapter(List<QuestionModel> ques_list) {
        this.ques_list = ques_list;
    }

    @NonNull
    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionAdapter.ViewHolder holder, int position) {
        holder.setData(position,this);
    }

    @Override
    public int getItemCount() {
        return ques_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView question_delete_btn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.courseNameID);
            question_delete_btn = itemView.findViewById(R.id.courseDeleteBtnID);
            loadingDialog = new Dialog(itemView.getContext());
            loadingDialog.setContentView(R.layout.loadingprogressbar);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progressbackground);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        }
        private void setData(int pos,QuestionAdapter adapter){
            title.setText("QUESTION "+String.valueOf(pos+1));
            question_delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Delete question")
                            .setMessage("Do you want to delete this question")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteQuestion(pos,itemView.getContext(),adapter);
                                }
                            })
                            .setNegativeButton("Cancel",null)
                            .setIcon(R.drawable.ic_baseline_warning_24)
                            .show();
                }
            });
        }
    }

    private void deleteQuestion(int pos, Context context, QuestionAdapter adapter) {
        loadingDialog.show();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("QUIZ").document(courseList.get(selectedCourseIndex).getCourseId())
                .collection(difficultyIDs.get(selected_diff_level_index)).document(questionsList.get(pos).getQuestionId())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Map<String,Object> quesDoc = new ArrayMap<>();
                int index = 1 ;
                for(int i = 0 ; i < questionsList.size() ; i++){
                    if(i!=pos){
                        quesDoc.put("Q"+index+"_ID",questionsList.get(i).getQuestionId());
                        index++;
                    }
                }
                quesDoc.put("COUNT",String.valueOf(index-1));
                firestore.collection("QUIZ").document(courseList.get(selectedCourseIndex).getCourseId())
                        .collection(difficultyIDs.get(selected_diff_level_index)).document("QUESTION_LIST")
                        .set(quesDoc)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Question deleted successfully", Toast.LENGTH_SHORT).show();
                                questionsList.remove(pos);
                                adapter.notifyDataSetChanged();
                                loadingDialog.dismiss();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                            }
                        });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });
    }
}
