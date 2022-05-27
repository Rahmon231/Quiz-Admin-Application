package com.lemzeeyyy.quizadminapplication;

import static com.lemzeeyyy.quizadminapplication.CourseActivity.courseList;
import static com.lemzeeyyy.quizadminapplication.CourseActivity.selectedCourseIndex;
import static com.lemzeeyyy.quizadminapplication.DifficultyActivity.selected_diff_level_index;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.Map;

public class DifficultyAdapter extends RecyclerView.Adapter<DifficultyAdapter.ViewHolder> {
    private List<String> difficultyIds;
    private Dialog loadingDialog;

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
        String difficultyId = difficultyIds.get(position);
        holder.setData(position,difficultyId,this);

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
            loadingDialog = new Dialog(itemView.getContext());
            loadingDialog.setContentView(R.layout.loadingprogressbar);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progressbackground);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        public void setData(int pos, String difficultyId, DifficultyAdapter adapter) {
            difficultyName.setText("LEVEL_"+String.valueOf(pos+1));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                 selected_diff_level_index = pos;
                    Intent intent = new Intent(itemView.getContext(),QuestionsActivity.class);
                    itemView.getContext().startActivity(intent);
                }
            });
            deleteDifficulty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Delete level")
                            .setMessage("Do you want to delete this level")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteDifficulty(pos,difficultyId, itemView.getContext(), adapter);
                                }
                            })
                            .setNegativeButton("Cancel",null)
                            .setIcon(R.drawable.ic_baseline_warning_24)
                            .show();
                }
            });
        }
    }
    public void deleteDifficulty(int position, String difficultyID, Context context,DifficultyAdapter adapter){
        loadingDialog.show();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("QUIZ").document(courseList.get(selectedCourseIndex).getCourseId())
                .collection(difficultyID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        WriteBatch batch = firestore.batch();
                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                            batch.delete(doc.getReference());
                        }
                        batch.commit().addOnSuccessListener(unused -> {
                            Map<String , Object> catDoc = new ArrayMap<>();
                            int index = 1 ;
                            for(int i = 0 ; i < difficultyIds.size() ; i++){
                                if(i!=position){
                                    catDoc.put("DIFFICULTY" +String.valueOf(index)+"_ID",difficultyIds.get(i));
                                    index++;
                                }
                            }
                            catDoc.put("DIFFICULTY",index-1);
                            firestore.collection("QUIZ").document(courseList.get(selectedCourseIndex).getCourseId())
                                    .update(catDoc)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(context, "Difficulty level deleted successfully"
                                                    , Toast.LENGTH_SHORT).show();
                                            DifficultyActivity.difficultyIDs.remove(position);
                                            courseList.get(selectedCourseIndex).setDifficulty_level(DifficultyActivity.difficultyIDs.size());
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
                        }).addOnFailureListener(new OnFailureListener() {
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