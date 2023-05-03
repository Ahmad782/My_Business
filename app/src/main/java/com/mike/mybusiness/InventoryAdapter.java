package com.mike.mybusiness;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryHolder> {

    ArrayList<Product> mProducts;
    Context mContext;
    public InventoryAdapter(Context context, ArrayList<Product> arrayList) {
        mProducts = arrayList;
        mContext = context;
    }

    @NonNull
    @Override
    public InventoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.product, parent, false);
        return new InventoryHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryHolder holder, int position) {
        holder.name.setText(mProducts.get(position).name);
        holder.price.setText("Rs: " + mProducts.get(position).price + "/-");
        holder.stock.setText(mProducts.get(position).stock);
        holder.date.setText(mProducts.get(position).stockDate);
        holder.company.setText(mProducts.get(position).company);
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    class InventoryHolder extends RecyclerView.ViewHolder {
        TextView name, company, date, stock, price;
        ImageButton edit, notify;
        public InventoryHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.productName);
            company = itemView.findViewById(R.id.productCompany);
            date = itemView.findViewById(R.id.productDate);
            stock = itemView.findViewById(R.id.productStock);
            price = itemView.findViewById(R.id.productPrice);
            edit = itemView.findViewById(R.id.editButton);
            notify = itemView.findViewById(R.id.notifyButton);
        }
    }
}
