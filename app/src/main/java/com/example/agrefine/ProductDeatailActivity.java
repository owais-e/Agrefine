package com.example.agrefine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ContentInfoCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agrefine.model.ProductRecylerModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProductDeatailActivity extends AppCompatActivity {
    ImageView showimg,addfvt;
    TextView pnameshow,ppriceshow,pdetailsshow;
    Button update,delete,contactseller;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_deatail);

        showimg = findViewById(R.id.productdetailimage);
        pnameshow = findViewById(R.id.productdetailname);
        ppriceshow = findViewById(R.id.productdetailprice);
        pdetailsshow = findViewById(R.id.productdetaildetail);
        update = findViewById(R.id.updateproduct);
        delete = findViewById(R.id.deleteproduct);
        contactseller= findViewById(R.id.contactsellerBtn);
        addfvt = findViewById(R.id.fvrtproduct);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String img_url = getIntent().getStringExtra("pimg");
        Picasso.get().load(img_url).into(showimg);
        pnameshow.setText(getIntent().getStringExtra("pname"));
        ppriceshow.setText(getIntent().getStringExtra("pprice"));
        pdetailsshow.setText(getIntent().getStringExtra("pdis"));

        String p_name =getIntent().getStringExtra("pname");
        String p_price =getIntent().getStringExtra("pprice");
        String p_discription =getIntent().getStringExtra("pdis");
        String user_id =getIntent().getStringExtra("uid");

        Intent intent = getIntent();
        String productkey = intent.getStringExtra("key");

        CollectionReference favoritesRef = firestore.collection("Users")
                .document(userid)
                .collection("products");
        Query query = favoritesRef.whereEqualTo(FieldPath.documentId(), productkey);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    Seller(userid,productkey,p_name,p_price,p_discription,img_url);
                }
                else {
                    Buyer(userid,productkey,p_name,p_price,p_discription,img_url,user_id);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProductDeatailActivity.this, "wrong path", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Buyer(String userid, String productkey, String productName, String price, String description, String imgUrl,String seller_id) {
        addfvt.setVisibility(View.VISIBLE);
        contactseller.setVisibility(View.VISIBLE);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference favoritesRef = firestore.collection("Users")
                .document(userid)
                .collection("favorites");
        Query query = favoritesRef.whereEqualTo(FieldPath.documentId(), productkey);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    // The document with the specified key exists in the favorites collection
                    addfvt.setImageResource(R.drawable.baseline_favorite_24);
                    addfvt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            favoritesRef.document(productkey).delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            addfvt.setImageResource(R.drawable.baseline_favorite_border_24);
                                            recreate();
                                            Toast.makeText(ProductDeatailActivity.this, "product is removed from your favorites", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ProductDeatailActivity.this, "product is not removed from your favorites", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                }else {
                    addfvt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ProductRecylerModel productRecylerModel =new ProductRecylerModel(productName,price,description,imgUrl,productkey,seller_id);
                            String favoritesCollection = "favorites";
                            firestore.collection("Users").document(userid).collection(favoritesCollection).document(productkey)
                                    .set(productRecylerModel)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            addfvt.setImageResource(R.drawable.baseline_favorite_24);
                                            recreate();
                                            Toast.makeText(ProductDeatailActivity.this, "add to fvrt", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ProductDeatailActivity.this, "faild", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        contactseller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("Users").child(seller_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String contactnumber = snapshot.child("regmobilenumber").getValue(String.class);
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+contactnumber));
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    private void Seller(String userid, String productkey,String p_name,String p_price,String p_discription,String img_url) {
        update.setVisibility(View.VISIBLE);
        delete.setVisibility(View.VISIBLE);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dellproduct(productkey);

            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valueToPass = "update";
                Intent intent = new Intent(ProductDeatailActivity.this, NewProductUploadActivity.class);
                intent.putExtra("update", valueToPass);
                intent.putExtra("pimg",img_url);
                intent.putExtra("pname", p_name);
                intent.putExtra("pprice", p_price);
                intent.putExtra("pdis", p_discription);
                intent.putExtra("key",productkey);
                startActivity(intent);



            }
        });
    }

    private void dellproduct(String productkey) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("Users");
        DocumentReference userDocRef = usersRef.document(userId);
        CollectionReference productsRef = userDocRef.collection("products");
        DocumentReference productDocRef = productsRef.document(productkey);

        //first we delete image then other data
        DocumentReference docRef = db.collection("Users").document(userId).collection("products").document(productkey);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String imageUrl = documentSnapshot.getString("imageUrl");
                        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //Toast.makeText(ProductDeatailActivity.this, "also image is deleted", Toast.LENGTH_SHORT).show();
                                productDocRef.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                    String usersCollection = "Users";
                                                    String favoritesCollection = "favorites";
                                                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
                                                    usersRef.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for (DataSnapshot userSnapshot : snapshot.getChildren()){
                                                                String userId = userSnapshot.getKey();
                                                                DocumentReference userFavoritesRef = db.collection(usersCollection)
                                                                        .document(userId)
                                                                        .collection(favoritesCollection)
                                                                        .document(productkey);
                                                                userFavoritesRef.get()
                                                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                if(documentSnapshot.exists()){
                                                                                    userFavoritesRef.delete()
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void unused) {
                                                                                                    Toast.makeText(ProductDeatailActivity.this, "product is deleted", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                                @Override
                                                                                                public void onFailure(@NonNull Exception e) {
                                                                                                    Toast.makeText(ProductDeatailActivity.this, "product is not deleted", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            });
                                                                                }
                                                                                else {
                                                                                    Toast.makeText(ProductDeatailActivity.this, "product not found", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }else {
                                                    Toast.makeText(ProductDeatailActivity.this, "product is not deleted", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProductDeatailActivity.this, "image is not deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }
}