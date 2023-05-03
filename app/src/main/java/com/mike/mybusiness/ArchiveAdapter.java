package com.mike.mybusiness;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ArchiveAdapter extends RecyclerView.Adapter<ArchiveAdapter.ArchiveHolder>{
    Context mContext;
    ArrayList<Invoice> mInvoice;

    public ArchiveAdapter(Context mContext, ArrayList<Invoice> mInvoice) {
        this.mContext = mContext;
        this.mInvoice = mInvoice;
    }

    @NonNull
    @Override
    public ArchiveHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.bill_list_item, parent, false);
        return new ArchiveHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ArchiveHolder holder, int position) {
        holder.invoice.setText(mInvoice.get(position).getInvoiceNumber());
        holder.customer.setText(mInvoice.get(position).getCustomerName());
        holder.date.setText(mInvoice.get(position).getDate());
        holder.total.setText(mInvoice.get(position).getTotal());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mInvoice.size();
    }

    public class ArchiveHolder extends RecyclerView.ViewHolder {

        TextView invoice, customer, date, total;
        ImageButton button;
        public ArchiveHolder(@NonNull View itemView) {
            super(itemView);
            invoice = itemView.findViewById(R.id.textViewInvoice);
            customer = itemView.findViewById(R.id.textViewCustomer);
            date = itemView.findViewById(R.id.textViewDate);
            total = itemView.findViewById(R.id.textViewTotal);
            button = itemView.findViewById(R.id.imageButtonDetail);
        }
    }
}
