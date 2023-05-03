package com.mike.mybusiness;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerHolder> {

    Context context;
    ArrayList<Customer> customers;

    public CustomerAdapter(Context context, ArrayList<Customer> customers) {
        this.context = context;
        this.customers = customers;
    }

    @NonNull
    @Override
    public CustomerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.customer, parent, false);
        return new CustomerHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerHolder holder, int position) {
        holder.name.setText(customers.get(position).getName());
        holder.address.setText(customers.get(position).getAddress());
        holder.email.setText(customers.get(position).getEmail());
        holder.number.setText(customers.get(position).getNumber());
        holder.due.setText("Pending Dues:" + customers.get(position).getDues());
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public class CustomerHolder extends RecyclerView.ViewHolder {
        TextView name, email, address, number, due;
        public CustomerHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_customer1);
            email = itemView.findViewById(R.id.email_customer1);
            address = itemView.findViewById(R.id.address_customer1);
            number = itemView.findViewById(R.id.number_customer1);
            due = itemView.findViewById(R.id.due_customer1);
        }
    }
}
