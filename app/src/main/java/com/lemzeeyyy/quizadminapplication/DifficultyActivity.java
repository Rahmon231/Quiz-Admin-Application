package com.lemzeeyyy.quizadminapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class DifficultyActivity extends AppCompatActivity {
    private RecyclerView diffRecyclerVier;
    private Button addDiffBtn;
    public static List<String> difficultyIDs = new ArrayList<>();
    private DifficultyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);
        diffRecyclerVier = findViewById(R.id.diff_recyclerID);
        addDiffBtn = findViewById(R.id.addDifficulty);
        addDiffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        diffRecyclerVier.setLayoutManager(layoutManager);
        loadDifficulty();
    }

    private void loadDifficulty() {

        adapter = new DifficultyAdapter(difficultyIDs);
        diffRecyclerVier.setAdapter(adapter);



    }

}