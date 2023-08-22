package com.example.agrefine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.agrefine.Adapter.RecylerAdapterProduct;
import com.example.agrefine.model.ProductRecylerModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ECommerceHomeActivity extends AppCompatActivity {
    ImageView favt;
    CircleImageView gotoprofile;
    SearchView searchew;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    RecylerAdapterProduct adapter;
    ArrayList<ProductRecylerModel> productList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecommerce_home);


        auth =FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        favt =findViewById(R.id.gotofavt);
        recyclerView =(RecyclerView) findViewById(R.id.recylerv);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchew = findViewById(R.id.searchView);

        searchew.setQueryHint("Search here");

        adapter=new RecylerAdapterProduct(productList);
        recyclerView.setAdapter(adapter);

        fetchProductsFromFirestore();

        searchew.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });


        if(user == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        gotoprofile = findViewById(R.id.gotouserprofile);

        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        DataSnapshot snapshot = task.getResult();
                        String imguri = String.valueOf(snapshot.child("profilePictureUrl").getValue());
                        if(snapshot.child("profilePictureUrl").getValue() == null ){
                            gotoprofile.setImageResource(R.drawable.baseline_supervised_user_circle_24);
                        }else {
                            Glide.with(getApplicationContext()).load(imguri).into(gotoprofile);
                        }
                    } else {
                        //Toast.makeText(ECommerceHomeActivity.this, "this is no data", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        gotoprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),UserProfileActivity.class);
                startActivity(intent);
            }
        });

        favt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),FavrtProductsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void fetchProductsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    String cuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    if(userId != cuid){
                        db.collection("Users").document(userId).collection("products")
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            //Toast.makeText(SellerDashBoardActivity.this, document.getId(), Toast.LENGTH_SHORT).show();
                                            String documentId = document.getId();
                                            String name = document.getString("productName");
                                            String price = document.getString("price");
                                            String description = document.getString("description");
                                            String imageUrl = document.getString("imageUrl");
                                            String userid = document.getString("uid");

                                            ProductRecylerModel recylerAdapterProduct = new ProductRecylerModel(name, price, description, imageUrl,documentId,userid);
                                            productList.add(recylerAdapterProduct);

                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ECommerceHomeActivity.this, "product read faild", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

    }

}