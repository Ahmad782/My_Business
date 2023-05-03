package com.mike.mybusiness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {

    TextView empty, caution;
    EditText search;
    RecyclerView recyclerView;
    ArchiveAdapter archiveAdapter;
    DatabaseHelper databaseHelper;
    ArrayList<Invoice> invoiceArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        databaseHelper = new DatabaseHelper(this);
        empty = findViewById(R.id.textView5);
        caution = findViewById(R.id.textView19);
        recyclerView = findViewById(R.id.recyclerViewAccount);
        search = findViewById(R.id.editTextAccount);
        invoiceArrayList = databaseHelper.fetchAllBill();
        if (invoiceArrayList.size() > 0) {
            for (int i = 0; i < invoiceArrayList.size(); i++) {
                if (invoiceArrayList.get(i).getIsCredit().equals("false")) {
                    invoiceArrayList.remove(i);
                }
            }
            archiveAdapter = new ArchiveAdapter(this, invoiceArrayList);
            recyclerView.setAdapter(archiveAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
            allViewVisible();
        }
        else {
            emptyVisible();
        }

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.length() == 0) {
                    Intent intent = getIntent();
                    startActivity(intent);
                    finish();
                } else {
                    Invoice invoice = databaseHelper.getBillByCustomerName(editable.toString());
                    if (invoice != null) {
                        invoiceArrayList.clear();
                        invoiceArrayList.add(invoice);
                        archiveAdapter.notifyDataSetChanged();
                    } else {
                        empty.setText("No matching result");
                        empty.setVisibility(View.VISIBLE);
                    }
                }

            }
        });

    }

    void allViewVisible()
    {
        recyclerView.setVisibility(View.VISIBLE);
        search.setVisibility(View.VISIBLE);
        caution.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);
    }

    void emptyVisible()
    {
        recyclerView.setVisibility(View.GONE);
        search.setVisibility(View.GONE);
        caution.setVisibility(View.GONE);
        empty.setVisibility(View.VISIBLE);
    }

}