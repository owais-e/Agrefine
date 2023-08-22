package com.example.agrefine.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrefine.FavrtProductsActivity;
import com.example.agrefine.ProductDeatailActivity;
import com.example.agrefine.R;
import com.example.agrefine.model.ProductRecylerModel;
import com.example.agrefine.viewholder.product_myviewholder;

import java.util.ArrayList;
import java.util.Locale;

import com.squareup.picasso.Picasso;

public class RecylerAdapterProduct extends RecyclerView.Adapter<product_myviewholder> {

    ArrayList<ProductRecylerModel> data;
    ArrayList<ProductRecylerModel> backup;

    public RecylerAdapterProduct(ArrayList<ProductRecylerModel> data) {
        this.data = data;
        backup =new ArrayList<>(data);
    }

    public RecylerAdapterProduct(String name, double price, String description, String imageUrl) {

    }

    @NonNull
    @Override
    public product_myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.single_row_file,parent,false);
        return new product_myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull product_myviewholder holder, int position) {
        ProductRecylerModel productRecylerModel= data.get(position);

        holder.ProductName.setText(productRecylerModel.getP_name());
        holder.ProductPrice.setText(productRecylerModel.getP_price());
        holder.ProductDiscription.setText(productRecylerModel.getP_discription());
        Picasso.get().load(productRecylerModel.getImg_url()).into(holder.ProductImage);

        holder.ProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(holder.ProductImage.getContext(), ProductDeatailActivity.class);
                intent.putExtra("pimg",productRecylerModel.getImg_url());
                intent.putExtra("pname",productRecylerModel.getP_name());
                intent.putExtra("pprice",productRecylerModel.getP_price());
                intent.putExtra("pdis",productRecylerModel.getP_discription());
                intent.putExtra("key",productRecylerModel.getDocumentId());
                intent.putExtra("uid",productRecylerModel.getUserid());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                holder.ProductImage.getContext().startActivity(intent);
            }
        });
        holder.ProductName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(holder.ProductImage.getContext(), ProductDeatailActivity.class);
                intent.putExtra("pimg",productRecylerModel.getImg_url());
                intent.putExtra("pname",productRecylerModel.getP_name());
                intent.putExtra("pprice",productRecylerModel.getP_price());
                intent.putExtra("pdis",productRecylerModel.getP_discription());
                intent.putExtra("key",productRecylerModel.getDocumentId());
                intent.putExtra("uid",productRecylerModel.getUserid());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.ProductImage.getContext().startActivity(intent);
            }
        });
        holder.ProductPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(holder.ProductImage.getContext(), ProductDeatailActivity.class);
                intent.putExtra("pimg",productRecylerModel.getImg_url());
                intent.putExtra("pname",productRecylerModel.getP_name());
                intent.putExtra("pprice",productRecylerModel.getP_price());
                intent.putExtra("pdis",productRecylerModel.getP_discription());
                intent.putExtra("key",productRecylerModel.getDocumentId());
                intent.putExtra("uid",productRecylerModel.getUserid());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.ProductImage.getContext().startActivity(intent);
            }
        });
        holder.ProductDiscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(holder.ProductImage.getContext(), ProductDeatailActivity.class);
                intent.putExtra("pimg",productRecylerModel.getImg_url());
                intent.putExtra("pname",productRecylerModel.getP_name());
                intent.putExtra("pprice",productRecylerModel.getP_price());
                intent.putExtra("pdis",productRecylerModel.getP_discription());
                intent.putExtra("key",productRecylerModel.getDocumentId());
                intent.putExtra("uid",productRecylerModel.getUserid());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.ProductImage.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public void filter(String query) {
        if (backup.isEmpty()) {
            backup.addAll(data); // Add this line to populate backup with the original data
        }
        data.clear();

        if (query.isEmpty()) {
            data.addAll(backup);
        } else {
            query = query.toLowerCase(Locale.getDefault());

            for (ProductRecylerModel product : backup) {
                if (product.getP_name().toLowerCase(Locale.getDefault()).contains(query)) {
                    data.add(product);
                }
            }
        }

        notifyDataSetChanged();
    }
}
