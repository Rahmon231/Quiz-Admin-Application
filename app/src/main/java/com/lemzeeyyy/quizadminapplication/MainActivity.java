package com.lemzeeyyy.quizadminapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button loginBtn;
    private FirebaseAuth firebaseAuth;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email  = findViewById(R.id.emailID);
        password = findViewById(R.id.passwordID);
        loginBtn = findViewById(R.id.loginID);
        loadingDialog = new Dialog(MainActivity.this);
        loadingDialog.setContentView(R.layout.loadingprogressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progressbackground);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        firebaseAuth = FirebaseAuth.getInstance();
        loginBtn.setOnClickListener(view -> {
            if(email.getText().toString().isEmpty()){
                email.setError("Enter Email ID");
                return;
            }
            else{
                email.setError(null);
            }
            if(password.getText().toString().isEmpty()){
                password.setError("Enter password");
                return;
            }
            else{
                password.setError(null);
            }
            firebaseLogin();
        });
        if(firebaseAuth.getCurrentUser() != null){
            Intent intent = new Intent(MainActivity.this, CourseActivity.class);
            startActivity(intent);
            finish();
        }
    }
    private void firebaseLogin(){
        loadingDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(MainActivity.this, CourseActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_SHORT).show();

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(MainActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismiss();
                });
    }
}