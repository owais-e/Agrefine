package com.example.agrefine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ChildEventListener;
import androidx.annotation.Nullable;

import java.util.regex.Pattern;

public class PasswordForgetActivity extends AppCompatActivity {
    EditText orignalemail;
    Button changbutton;
    TextView forlogin;
    FirebaseAuth auth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_forget);

        auth = FirebaseAuth.getInstance();

        orignalemail = findViewById(R.id.sameemail);
        changbutton = findViewById(R.id.buttonchange);
        forlogin = findViewById(R.id.gotologin);
        progressBar = findViewById(R.id.progressbar);

        forlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        changbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String eemail = orignalemail.getText().toString();

                String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

                if(!(Pattern.matches(emailPattern,eemail))){
                    Toast.makeText(PasswordForgetActivity.this, "please correct email", Toast.LENGTH_SHORT).show();
                } else {
                    resetPassword(eemail);
                }
            }
        });

    }

    private void resetPassword(String eemail) {
        auth.sendPasswordResetEmail(eemail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PasswordForgetActivity.this, "email is sent to your email account", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                        else {
                            Toast.makeText(PasswordForgetActivity.this, "please enter correct email", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }

}