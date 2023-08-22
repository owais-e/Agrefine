package com.example.agrefine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.agrefine.Adapter.RecylerAdapterProduct;
import com.example.agrefine.model.ProductRecylerModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerDashBoardActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecylerAdapterProduct adapter;
    ArrayList<ProductRecylerModel> productList=new ArrayList<>();
    SearchView searchView;
    FloatingActionButton gotoaddnewproduct;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_dash_board);

        recyclerView =(RecyclerView) findViewById(R.id.rec);
        searchView =findViewById(R.id.sellersearch);
        gotoaddnewproduct = findViewById(R.id.addnewproduct);


        searchView.setQueryHint("Search here");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter=new RecylerAdapterProduct(productList);
        recyclerView.setAdapter(adapter);

        fetchProductsFromFirestore();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        gotoaddnewproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valueToPass = "new product";
                Intent intent = new Intent(SellerDashBoardActivity.this, NewProductUploadActivity.class);
                intent.putExtra("update", valueToPass);
                startActivity(intent);
            }
        });
    }

    private void fetchProductsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("products")
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
                        Toast.makeText(SellerDashBoardActivity.this, "product read faild", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}