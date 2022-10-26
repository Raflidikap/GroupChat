package com.example.groupchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.groupchat.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText, confPasswordEditText, nameEditText;
    private ProgressBar progressBar;
    private Button registerBtn;
    DatabaseReference reference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        confPasswordEditText = (EditText) findViewById(R.id.confPasswordEditText);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        progressBar = (ProgressBar) findViewById(R.id.progressBarRegis);
        registerBtn = (Button) findViewById(R.id.registerBtn);

    }

    public void regisUser(View view){
        progressBar.setVisibility(View.VISIBLE);
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confPassword = confPasswordEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();

        if (!email.isEmpty() && !password.isEmpty()){
            if (password.equals(confPassword)){
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    //insert data to database
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    Users u = new Users();
                                    u.setName(name);
                                    u.setEmail(email);
                                    u.setUid(firebaseUser.getUid());

                                    reference.child(firebaseUser.getUid()).setValue(u)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        progressBar.setVisibility(View.GONE);
                                                        Toast.makeText(RegisterActivity.this, "Users registered successfully", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                        finish();
                                                    }else{
                                                        progressBar.setVisibility(View.GONE);
                                                        Toast.makeText(RegisterActivity.this, "Users could not register ", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        });
            }else {
                Toast.makeText(this, "Confirm password doesn't matches", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Fill the required data or password at least 6 characters", Toast.LENGTH_LONG).show();
        }

    }
    public void toLogin(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}