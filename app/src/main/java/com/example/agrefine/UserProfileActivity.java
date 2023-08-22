package com.example.agrefine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    CircleImageView userprofile;
    TextView displayusername,gotoseller,aboutUs,logoutbtn,dell,editdata;
    Button upload;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    private Uri imageUri;

    private static  final int Gallery_code=1;
    Uri imguri=null;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        auth =FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase =FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);



        userprofile = findViewById(R.id.userimage);
        editdata = findViewById(R.id.editprofiledata);
        displayusername = findViewById(R.id.dispusername);
        gotoseller = findViewById(R.id.switchtoselleraccount);

        logoutbtn = findViewById(R.id.logout);
        dell = findViewById(R.id.deleteaccount);
        upload = findViewById(R.id.uploadimg);

        editdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),EditUserProfileActivity.class);
                startActivity(intent);
            }
        });
        gotoseller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), SellerDashBoardActivity.class);
                startActivity(intent);
            }
        });

        if(user == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {

            userprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/");
                    startActivityForResult(intent,Gallery_code);
                    upload.setVisibility(View.VISIBLE);
                }
            });
            upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imageUri != null) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference imageUrlRef = database.getReference("Users").child(userId).child("profilePictureUrl");

                        imageUrlRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Check if the condition is true in your database
                                if (dataSnapshot.exists()) {
                                    // Retrieve the URL from the dataSnapshot
                                    String imageUrl = dataSnapshot.getValue(String.class);

                                    // Step 2: Delete the image from Firebase Storage
                                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                                    storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Image deleted successfully
                                            //Toast.makeText(UserProfileActivity.this, "privious image is deleted", Toast.LENGTH_SHORT).show();
                                            uploadImage(imageUri);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // An error occurred while deleting the image
                                            // Handle the error appropriately
                                        }
                                    });
                                } else {
                                    // The condition is not true in the database, so do not delete the image
                                    //Toast.makeText(UserProfileActivity.this, "no previous image", Toast.LENGTH_SHORT).show();
                                    uploadImage(imageUri);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // An error occurred while retrieving the image URL
                                // Handle the error appropriately
                            }
                        });
                    } else {
                        Toast.makeText(UserProfileActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                    }
                    upload.setVisibility(View.GONE);
                }
            });

            //Showing user name and profile image code is here
            databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        if(task.getResult().exists()){
                            DataSnapshot snapshot = task.getResult();
                            String name= String.valueOf(snapshot.child("regname").getValue());
                            String imguri= String.valueOf(snapshot.child("profilePictureUrl").getValue());
                            if(snapshot.child("profilePictureUrl").getValue() == null ){
                                userprofile.setImageResource(R.drawable.baseline_supervised_user_circle_24);
                            }else {
                                Glide.with(getApplicationContext()).load(imguri).into(userprofile);
                            }
                            displayusername.setText(name);

                        }else {
                            //Toast.makeText(UserProfileActivity.this, "this is no data", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(UserProfileActivity.this, "failed to read", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            databaseReference.child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String profilePictureUrl = task.getResult().getValue(String.class);
                        if (profilePictureUrl != null) {
                            // Load and display the profile picture using a suitable library or method
                            // For example, using Glide library:
                            Glide.with(UserProfileActivity.this)
                                    .load(profilePictureUrl)
                                    .into(userprofile);
                        }
                    }
                }
            });
        }

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog();
            }
        });

        //delete account code is here
        dell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Confirm account deletion!");

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                deleteNestedCollections(docRef);
                dellprofileimage();
                docRef.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dellauthenticationaccount();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UserProfileActivity.this, "nooo", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to Log Out ?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(UserProfileActivity.this, "You are logout", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void dellprofileimage() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference imageUrlRef = database.getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profilePictureUrl");

        imageUrlRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the image URL exists in the Realtime Database
                if (dataSnapshot.exists()) {
                    // Retrieve the URL from the dataSnapshot
                    String imageUrl = dataSnapshot.getValue(String.class);

                    // Step 1: Delete the image from Firebase Storage
                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                    storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Image deleted successfully
                            // Step 2: Remove the image URL from the Realtime Database
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // An error occurred while deleting the image
                            // Handle the error appropriately
                        }
                    });
                } else {
                    // The image URL does not exist in the Realtime Database
                    Toast.makeText(UserProfileActivity.this, "No profile image found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // An error occurred while retrieving the image URL
                // Handle the error appropriately
            }
        });

    }

    private void deleteNestedCollections(final DocumentReference documentReference) {
        documentReference.collection("products")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            String imageUrl = document.getString("imageUrl");
                            deleteImageFromStorage(imageUrl);
                            // Delete nested subcollections/documents recursively
                            deleteNestedCollections(document.getReference());

                            // Delete the nested document
                            document.getReference().delete();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to retrieve nested subcollections/documents
                        // Handle the error
                    }
                });
    }
    private void deleteImageFromStorage(String imageUrl) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Image deleted successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // An error occurred while deleting the image
                // Handle the error appropriately
            }
        });
    }

    private void dellauthenticationaccount() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(UserProfileActivity.this, "account is deleted", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();

                                        } else {
                                            Toast.makeText(UserProfileActivity.this, "account is not deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserProfileActivity.this, "user is not deleted", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //image upload code is here
    private void uploadImage(Uri imageUri){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        String imageName = UUID.randomUUID().toString();

        final StorageReference imageStorageRef = storageReference.child("profilePictures/" + imageName);

        // Upload the image to Firebase Storage
        UploadTask uploadTask = imageStorageRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get the download URL of the uploaded image
                imageStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUri) {
                        // Save the download URL to the Realtime Database under the user's profile picture URL
                        databaseReference.child("profilePictureUrl").setValue(downloadUri.toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        Toast.makeText(UserProfileActivity.this, "Profile picture uploaded successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UserProfileActivity.this, "Profile picture upload failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploading " + (int) progress + "%");
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            try {
                // Set the selected image to the ImageView
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                userprofile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}