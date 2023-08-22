package com.example.agrefine;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.firestore.DocumentReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

public class NewProductUploadActivity extends AppCompatActivity {
    EditText PFName,PFPrice,PFDiscription, ContactNumber;
    ImageView PFImage;
    Button buttonupload;
    private Uri imageUri;

    private static  final int Gallery_code=1;
    private static final int PICK_IMAGE_REQUEST = 1;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    FirebaseFirestore firestore;
    private int requestCode;
    private int resultCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product_upload);

        PFName=findViewById(R.id.productfertilizername);
        PFPrice=findViewById(R.id.productfertilizerprice);
        PFDiscription=findViewById(R.id.productfertilizerdiscription);
        PFImage=findViewById(R.id.productfertilizerimage);
        buttonupload=findViewById(R.id.productfertilizerupload);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firestore=FirebaseFirestore.getInstance();
        String receivedValue ="noupdate";

        PFImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/");
                startActivityForResult(intent,Gallery_code);
            }
        });

        Intent intent = getIntent();
        receivedValue = intent.getStringExtra("update");
        if(receivedValue.equals("update")){
            Toast.makeText(this, "Update product details here ", Toast.LENGTH_SHORT).show();
            String oldimageUrl = getIntent().getStringExtra("pimg");
            Picasso.get().load(oldimageUrl).into(PFImage);

            PFName.setText(getIntent().getStringExtra("pname"));
            PFPrice.setText(getIntent().getStringExtra("pprice"));
            PFDiscription.setText(getIntent().getStringExtra("pdis"));

            String productid =getIntent().getStringExtra("key");

            buttonupload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newproductfertilizername = PFName.getText().toString();
                    String newproductfertilizerprice = PFPrice.getText().toString();
                    String newproductfertilizerdiscription = PFDiscription.getText().toString();

                    if(newproductfertilizername.isEmpty()){
                        Toast.makeText(NewProductUploadActivity.this, "please enter Name of Product or Firtilizer", Toast.LENGTH_SHORT).show();
                    } else if (newproductfertilizerprice.isEmpty()) {
                        Toast.makeText(NewProductUploadActivity.this, "please enter price of Product or Firtilizer", Toast.LENGTH_SHORT).show();
                    } else if (newproductfertilizerdiscription.isEmpty() ) {
                        Toast.makeText(NewProductUploadActivity.this, "please enter Discription of Product or Firtilizer", Toast.LENGTH_SHORT).show();
                    } else if (newproductfertilizerdiscription.length() < 50) {
                        Toast.makeText(NewProductUploadActivity.this, "discription must be grater then 50 chracters", Toast.LENGTH_SHORT).show();
                    } else if (newproductfertilizerdiscription.length() > 200) {
                        Toast.makeText(NewProductUploadActivity.this, "discription must be less then 200 chracters", Toast.LENGTH_SHORT).show();
                    } else if(imageUri==null){
                        imageUri = Uri.parse(oldimageUrl);
                        updatedata(imageUri,newproductfertilizername,newproductfertilizerprice,newproductfertilizerdiscription,productid);
                    }else {
                        updatedata(imageUri, newproductfertilizername, newproductfertilizerprice, newproductfertilizerdiscription, productid);
                        PFName.setText(" ");
                        PFDiscription.setText(" ");
                        PFPrice.setText(" ");
                        PFImage.setImageResource(R.drawable.green);

                        recreate();

                    }

                    FirebaseDatabase.getInstance().setPersistenceEnabled(true);

                }
            });
        }
        else {
            buttonupload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String productfertilizername = PFName.getText().toString();
                    String productfertilizerprice = PFPrice.getText().toString();
                    String productfertilizerdiscription = PFDiscription.getText().toString();


                    if(productfertilizername.isEmpty()){
                        Toast.makeText(NewProductUploadActivity.this, "please enter Name of Product or Firtilizer", Toast.LENGTH_SHORT).show();
                    } else if (productfertilizerprice.isEmpty()) {
                        Toast.makeText(NewProductUploadActivity.this, "please enter price of Product or Firtilizer", Toast.LENGTH_SHORT).show();
                    } else if (productfertilizerdiscription.isEmpty() ) {
                        Toast.makeText(NewProductUploadActivity.this, "please enter Discription of Product or Firtilizer", Toast.LENGTH_SHORT).show();
                    } else if (productfertilizerdiscription.length() < 50) {
                        Toast.makeText(NewProductUploadActivity.this, "discription must be grater then 50 chracters", Toast.LENGTH_SHORT).show();
                    } else if (productfertilizerdiscription.length() > 500) {
                        Toast.makeText(NewProductUploadActivity.this, "discription must be less then 500 chracters", Toast.LENGTH_SHORT).show();
                    } else if (imageUri != null) {
                        NewUpload(imageUri,productfertilizername,productfertilizerprice,productfertilizerdiscription);
                        PFName.setText(" ");
                        PFDiscription.setText(" ");
                        PFPrice.setText(" ");
                        PFImage.setImageResource(R.drawable.green);


                        Toast.makeText(NewProductUploadActivity.this, "Done", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        Toast.makeText(NewProductUploadActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void updatedata(Uri imageUri, String newproductfertilizername, String newproductfertilizerprice, String newproductfertilizerdiscription,String productid) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        String imageName = UUID.randomUUID().toString();

        final StorageReference imageStorageRef = storageReference.child("ProductPictures/" + imageName);

        // Upload the image to Firebase Storage
        UploadTask uploadTask = imageStorageRef.putFile(imageUri);
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri imageUri) {
                        String updatedName = newproductfertilizername;
                        String updatedPrice = newproductfertilizerprice;
                        String updatedDescription = newproductfertilizerdiscription;
                        String updatedImageUrl = String.valueOf(imageUri);

                        Map<String, Object> updatedFields = new HashMap<>();
                        updatedFields.put("description", updatedDescription);
                        updatedFields.put("imageUrl", updatedImageUrl);
                        updatedFields.put("price", updatedPrice);
                        updatedFields.put("productName", updatedName);
                        updatedFields.put("uid",userid);


                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String Userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DocumentReference userRef = db.collection("Users").document(Userid);
                        CollectionReference productsRef = userRef.collection("products");
                        DocumentReference productRef = productsRef.document(productid);
                        productRef.set(updatedFields, SetOptions.merge())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(NewProductUploadActivity.this, "Product updated", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        } else {
                                            Toast.makeText(NewProductUploadActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(NewProductUploadActivity.this, "fail", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String updatedName = newproductfertilizername;
                String updatedPrice = newproductfertilizerprice;
                String updatedDescription = newproductfertilizerdiscription;
                String updatedImageUrl = String.valueOf(imageUri);

                Map<String, Object> updatedFields = new HashMap<>();
                updatedFields.put("description", updatedDescription);
                updatedFields.put("imageUrl", updatedImageUrl);
                updatedFields.put("price", updatedPrice);
                updatedFields.put("productName", updatedName);
                updatedFields.put("uid",userid);


                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String Userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference userRef = db.collection("Users").document(Userid);
                CollectionReference productsRef = userRef.collection("products");
                DocumentReference productRef = productsRef.document(productid);
                productRef.set(updatedFields, SetOptions.merge())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(NewProductUploadActivity.this, "Product successfully updated ", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                } else {
                                    Toast.makeText(NewProductUploadActivity.this, "Updation failed", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(NewProductUploadActivity.this, "fail", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
    private void NewUpload(Uri imageUri,String pname,String pprice,String pdiscription) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        String imageName = UUID.randomUUID().toString();

        final StorageReference imageStorageRef = storageReference.child("ProductPictures/" + imageName);

        // Upload the image to Firebase Storage
        UploadTask uploadTask = imageStorageRef.putFile(imageUri);
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        CollectionReference productsCollection = firestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("products");
                        DocumentReference newProductRef = productsCollection.document();

                        // Create a Map to store the product data
                        Map<String, Object> productData = new HashMap<>();
                        productData.put("productName", pname);
                        productData.put("price", pprice);
                        productData.put("description", pdiscription);
                        productData.put("imageUrl", uri.toString());
                        productData.put("uid",userid);

                        newProductRef.set(productData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //Toast.makeText(NewProductUploadActivity.this, "Done", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(NewProductUploadActivity.this, "Failed to upload the data", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(NewProductUploadActivity.this, "Profile picture upload failed", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
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
                PFImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}