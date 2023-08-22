package com.example.agrefine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.FirebaseUser;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class EditUserProfileActivity extends AppCompatActivity {
    EditText name, email, mobilenumber,currentpassword;
    Button update;
    ProgressBar progressBar;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        database = FirebaseDatabase.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        name = findViewById(R.id.new_name);
        email = findViewById(R.id.new_email);
        mobilenumber = findViewById(R.id.newmobilenumber);
        currentpassword = findViewById(R.id.alreadypassword);
        update = findViewById(R.id.updatetodate);
        //progressBar = findViewById(R.id.progressbar);


        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            //progressBar.setVisibility(View.VISIBLE);
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newemail = email.getText().toString();
                    String newname = name.getText().toString();
                    String newnumber = mobilenumber.getText().toString();
                    String password = currentpassword.getText().toString();

                    String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

                    if (!(Pattern.matches(emailPattern, newemail))) {
                        Toast.makeText(EditUserProfileActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                    } else if (newname.isEmpty()) {
                        Toast.makeText(EditUserProfileActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                    } else if (newnumber.isEmpty() || newnumber.length() < 11) {
                        Toast.makeText(EditUserProfileActivity.this, "Please enter a correct mobile number", Toast.LENGTH_SHORT).show();
                    } else if (password.isEmpty() || password.length() <8) {
                        Toast.makeText(EditUserProfileActivity.this, "please enter correct password", Toast.LENGTH_SHORT).show();
                    } else {
                        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
                        user.reauthenticate(credential)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    user.updateEmail(newemail)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(EditUserProfileActivity.this, "Email updated", Toast.LENGTH_SHORT).show();
                                                                        realtimedatabaseupdate(newemail,newname,newnumber);
                                                                    } else {
                                                                        Toast.makeText(EditUserProfileActivity.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                                else {
                                                    Toast.makeText(EditUserProfileActivity.this, "Please enter correct password", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                    }
                }
            });

        }
    }

    private void realtimedatabaseupdate(String newemail, String newname, String newnumber) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("regemail", newemail);
        updates.put("regmobilenumber", newnumber);
        updates.put("regname", newname);
        myRef.updateChildren(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditUserProfileActivity.this, "realdatabase is updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditUserProfileActivity.this, "realdatabase is not updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}