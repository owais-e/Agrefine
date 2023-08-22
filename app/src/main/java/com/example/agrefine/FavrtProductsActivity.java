package com.example.agrefine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

public class FavrtProductsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecylerAdapterProduct adapter;
    ArrayList<ProductRecylerModel> productList=new ArrayList<>();
    SearchView searchView;

    private static final int REFRESH_DELAY = 20000; // Refresh time in milliseconds
    private Handler handler;
    private Runnable refreshRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favrt_products);

        recyclerView =(RecyclerView) findViewById(R.id.fvtrec);
        searchView =findViewById(R.id.favtsearch);

        fetchProductsFromFirestore();

        searchView.setQueryHint("Search here");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter=new RecylerAdapterProduct(productList);
        recyclerView.setAdapter(adapter);

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
    }
    private void fetchProductsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("favorites")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Toast.makeText(SellerDashBoardActivity.this, document.getId(), Toast.LENGTH_SHORT).show();
                            String documentId = document.getId();
                            String name = document.getString("p_name");
                            String price = document.getString("p_price");
                            String description = document.getString("p_discription");
                            String imageUrl = document.getString("img_url");
                            String userid =document.getString("uid");

                            ProductRecylerModel recylerAdapterProduct = new ProductRecylerModel(name, price, description, imageUrl,documentId,userid);
                            productList.add(recylerAdapterProduct);

                        }
                        adapter.notifyDataSetChanged();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FavrtProductsActivity.this, "product read faild", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}