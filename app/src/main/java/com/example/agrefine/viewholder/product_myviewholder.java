package com.example.agrefine.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrefine.R;

public class product_myviewholder extends RecyclerView.ViewHolder {
     public ImageView ProductImage;
     public TextView ProductName,ProductPrice,ProductDiscription;
    public product_myviewholder(@NonNull View itemView) {
        super(itemView);
        ProductImage=(ImageView) itemView.findViewById(R.id.pimage);
        ProductName=(TextView) itemView.findViewById(R.id.pname);
        ProductPrice=(TextView) itemView.findViewById(R.id.pprice);
        ProductDiscription=(TextView) itemView.findViewById(R.id.pdiscription);
    }
}
