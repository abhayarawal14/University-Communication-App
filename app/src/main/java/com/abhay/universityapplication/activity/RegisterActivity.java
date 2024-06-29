package com.abhay.universityapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.abhay.universityapplication.R;
import com.abhay.universityapplication.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    Spinner batch;
    TextView tvBatch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        EditText email = findViewById(R.id.emailEditText);
        EditText studentId  = findViewById(R.id.studentIdEditText);
        EditText fullName = findViewById(R.id.fullNameEditText);
        batch = findViewById(R.id.batchSpinner);
        EditText password = findViewById(R.id.passwordEditText);
        EditText confirmPassword = findViewById(R.id.confirmPasswordEditText);
        TextView gotoLogin = findViewById(R.id.goToLogin);
        Button btnRegister = findViewById(R.id.registerButton);
        progressBar= findViewById(R.id.progressbar);
        tvBatch = findViewById(R.id.tvBatch);
        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        Intent intent = getIntent();
        final String value  = intent.getStringExtra("As");


        if (value.equals("Teacher"))
        {
            tvBatch.setVisibility(View.GONE);
            batch.setVisibility(View.GONE);
        }
        else{
            tvBatch.setVisibility(View.VISIBLE);
            batch.setVisibility(View.VISIBLE);
        }
        String arrayBatch[] = {"21A", "21B", "21C", "22A", "22B", "22C", "22D", "23A", "23B", "24A", "24B", "24C", "25A", "25B", "25C", "26A", "26B"};
        ArrayAdapter batchAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayBatch);
        batch.setAdapter(batchAdapter);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String id = studentId.getText().toString();
                final String name = fullName.getText().toString();
                final String batchText = batch.getSelectedItem().toString();
                final String userEmail = email.getText().toString();
                final String userPassword = password.getText().toString();
                final String userConfirmPassword = confirmPassword.getText().toString();
                final String profileImage = "default";
                final String status = "online";

                if(TextUtils.isEmpty(id)){
                    studentId.setError("Email Not entered");
                    studentId.requestFocus();
                    return;
                }
                else if(TextUtils.isEmpty(name)){
                    fullName.setError("Full name not Entered");
                    fullName.requestFocus();
                    return;
                }
                else if (!userEmail.contains("edu"))
                {
                    email.setError("Email improper format");
                    email.requestFocus();
                    return;
                }
                else if(TextUtils.isEmpty(userPassword)){
                    password.setError("Password not Entered");
                    password.requestFocus();
                    return;
                }
                else if(!userPassword.equals(userConfirmPassword)){
                    confirmPassword.setError("Password doesn't matches");
                    confirmPassword.requestFocus();
                    return;
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                User user;
                                if (value.equals("Student"))
                                {
                                    user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(), id, name, userEmail, batchText, userPassword, profileImage, status,value);
                                }
                                else
                                {
                                    user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(), id, name, userEmail, userPassword, profileImage, status,value);
                                }
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user);

                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "User created", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                            }
                        }

                    });
                }
            }
        });
    }
}