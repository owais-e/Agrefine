package com.example.agrefine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agrefine.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    EditText useremail,userpassword,username,usermobile;
    Button buttonregister;
    TextView goTologin;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    ProgressBar progressBar;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent =new Intent(getApplicationContext(),HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth =FirebaseAuth.getInstance();
        useremail =findViewById(R.id.register_email);
        userpassword = findViewById(R.id.register_password);
        username = findViewById(R.id.register_name);
        usermobile =findViewById(R.id.register_mobilenumber);
        buttonregister = findViewById(R.id.register);
        goTologin = findViewById(R.id.login);
        progressBar = findViewById(R.id.ProgressBar);

        database =FirebaseDatabase.getInstance();

        goTologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        buttonregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email= useremail.getText().toString();
                String password= userpassword.getText().toString();
                String name = username.getText().toString();
                String number = usermobile.getText().toString();

                String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

                if(!(Pattern.matches(emailPattern,email))){
                    Toast.makeText(RegisterActivity.this, "please enter valid Email", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 8) {
                    Toast.makeText(RegisterActivity.this, "Password must be 8 character long", Toast.LENGTH_SHORT).show();
                }else if (name.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Enter your name", Toast.LENGTH_SHORT).show();
                }else if (number.length() < 11 || number.length() >11) {
                    Toast.makeText(RegisterActivity.this, "Number must be 11 digit", Toast.LENGTH_SHORT).show();
                }
                else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    UserModel userModel =new UserModel(name,email,number);
                                    String id =task.getResult().getUser().getUid();
                                    database.getReference().child("Users").child(id).setValue(userModel);
                                    if (task.isSuccessful()) {

                                        Toast.makeText(RegisterActivity.this, "Signed up successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent =new Intent(getApplicationContext(),LoginActivity.class);
                                        //intent.putExtra("key", id);
                                        startActivity(intent);
                                        finish();

                                    } else {

                                        Toast.makeText(RegisterActivity.this, "Authentication failed",
                                                Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
            }
        });
    }
}