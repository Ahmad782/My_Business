package com.mike.mybusiness;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillHolder>{
    Context mContext;
    ArrayList<Bill> mItems;
    TextView mTotal;
    EditText mDiscount;
    EditText mTax;
    public BillAdapter(Context context, ArrayList<Bill> items, TextView total, EditText discount, EditText tax)
    {
        mContext = context;
        mItems = items;
        mTotal = total;
        mDiscount = discount;
        mTax = tax;
    }

    @NonNull
    @Override
    public BillHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.bill_item, parent, false);
        return new BillHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BillHolder holder, int position) {
        int i = holder.getAdapterPosition();
        holder.name.setText(mItems.get(i).getItemName());
        holder.distributor.setText(mItems.get(i).getDistributor());
        holder.quantity.setText(mItems.get(i).getQuantity() + "");
        if (mItems.get(i).getDiscount() == 0)
        {
            holder.total.setText(mItems.get(i).getQuantity() * mItems.get(i).getPrice() + "");
        }
        else {
            holder.total.setText(mItems.get(i).getQuantity() * mItems.get(i).getPrice() - mItems.get(i).getDiscount() + "");
        }
        holder.index.setText(i + 1 + "");
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.more);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int itemId = menuItem.getItemId();
                        if (itemId == R.id.remove) {
                            mItems.remove(i);
                            notifyItemRemoved(i);
                            notifyItemRangeChanged(i, mItems.size());
                            if (mDiscount.getText().length() == 0 && mTax.getText().length() == 0) {
                                mTotal.setText("Total: " + calculateTotal(mItems));
                            } else {
                                mDiscount.setText("");
                                mTax.setText("");
                                mTotal.setText("Total: " + calculateTotal(mItems));
                            }
                        } else if (itemId == R.id.change) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                            View v = LayoutInflater.from(mContext).inflate(R.layout.quantity_picker, null);
                            alertDialogBuilder.setView(v);
                            EditText n = v.findViewById(R.id.numberPicker);
                            TextView t = v.findViewById(R.id.pickerTextView);
                            Button b = v.findViewById(R.id.pickerButton);
                            t.setText("Enter Quantity:");
                            int x = i;
                            AlertDialog alertDialog1 = alertDialogBuilder.create();
                            alertDialog1.show();
                            b.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (!n.getText().toString().equals("")) {
                                        int h = Integer.parseInt(n.getText().toString());
                                        if (!(h == 0)) {
                                            mItems.get(x).setQuantity(h);
                                            holder.total.setText(mItems.get(x).quantity * mItems.get(x).price + "");
                                            holder.quantity.setText(mItems.get(x).quantity + "");
                                            if (mDiscount.getText().length() == 0 && mTax.getText().length() == 0) {
                                                mTotal.setText("Total: " + calculateTotal(mItems));
                                            } else {
                                                mDiscount.setText("");
                                                mTax.setText("");
                                                mTotal.setText("Total: " + calculateTotal(mItems));
                                            }
                                            notifyItemChanged(x);
                                        }
                                    }
                                    alertDialog1.dismiss();
                                }
                            });
                        } else if (itemId == R.id.discount) {
                            AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(mContext);
                            View v1 = LayoutInflater.from(mContext).inflate(R.layout.quantity_picker, null);
                            alertDialogBuilder1.setView(v1);
                            EditText n1 = v1.findViewById(R.id.numberPicker);
                            TextView t1 = v1.findViewById(R.id.pickerTextView);
                            Button b1 = v1.findViewById(R.id.pickerButton);
                            t1.setText("Discount:");
                            int x1 = i;
                            AlertDialog alertDialog2 = alertDialogBuilder1.create();
                            alertDialog2.show();
                            b1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (!n1.getText().toString().equals("")) {
                                        float h = Float.parseFloat(n1.getText().toString());
                                        if (!(h == 0 || h > Float.parseFloat(holder.total.getText().toString()))) {
                                            holder.total.setText(mItems.get(x1).quantity * mItems.get(x1).price - h + "");
                                            mItems.get(x1).setDiscount(h);
                                            notifyItemChanged(x1);
                                            if (mDiscount.getText().length() == 0 && mTax.getText().length() == 0) {
                                                mTotal.setText("Total: " + calculateTotal(mItems));
                                            } else {
                                                mDiscount.setText("");
                                                mTax.setText("");
                                                mTotal.setText("Total: " + calculateTotal(mItems));
                                            }
                                        }
                                    }
                                    alertDialog2.dismiss();
                                }
                            });
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class BillHolder extends RecyclerView.ViewHolder {
        ImageButton more;
        TextView name, distributor, quantity, total, index;
        public BillHolder(@NonNull View itemView) {
            super(itemView);
            more = itemView.findViewById(R.id.moreButton);
            name = itemView.findViewById(R.id.nameItem);
            quantity = itemView.findViewById(R.id.quantityItem);
            total = itemView.findViewById(R.id.totalItem);
            distributor = itemView.findViewById(R.id.distributorItem);
            index = itemView.findViewById(R.id.indexTextView);
        }
    }
    int calculateTotal(ArrayList<Bill> bills)
    {
        int x = 0;
        for (int i=0; i<bills.size(); i++)
        {
            if (bills.get(i).getDiscount() == 0)
            {
                x += bills.get(i).price * bills.get(i).quantity;
            }
            else {
                x += bills.get(i).price * bills.get(i).quantity - bills.get(i).getDiscount();
            }

        }
        return x;
    }
}
