package com.mike.mybusiness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomerProfiles extends AppCompatActivity {

    TextView noCustomer;
    RecyclerView recyclerView;
    DatabaseHelper databaseHelper;
    ArrayList<Customer> customers;
    CustomerAdapter customerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profiles);
        noCustomer = findViewById(R.id.noProfile);
        recyclerView = findViewById(R.id.recyclerViewCustomer);
        databaseHelper = new DatabaseHelper(this);
        customers = databaseHelper.fetchAllCustomers();

        if (customers != null)
        {
            noCustomer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            customerAdapter = new CustomerAdapter(this, customers);
            recyclerView.setAdapter(customerAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
        }
        else {
            noCustomer.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public void addNew(View view) {
        Intent intent = new Intent(this, CustomerActivity.class);
        startActivity(intent);
        finish();
    }
}