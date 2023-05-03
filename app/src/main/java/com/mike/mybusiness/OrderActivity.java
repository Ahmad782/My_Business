package com.mike.mybusiness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {

    NumberPicker numberPicker;
    TextView empty;
    RecyclerView recyclerView;
    InventoryAdapter inventoryAdapter;
    ArrayList<Product> arrayList;
    DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        databaseHelper = new DatabaseHelper(this);
        numberPicker = findViewById(R.id.numberPicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(1000);
        empty = findViewById(R.id.orderEmpty);
        recyclerView = findViewById(R.id.orderRecyclerView);
        searchOrder();

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                searchOrder();
            }
        });
    }

    void searchOrder()
    {
        if (!databaseHelper.fetchAllProducts().isEmpty()) {
            arrayList = databaseHelper.fetchAllProducts();
            ArrayList<Product> products = new ArrayList<>();
            for (int i=0; i<arrayList.size(); i++)
            {
                if (Integer.parseInt(arrayList.get(i).getStock()) == numberPicker.getValue())
                {
                    products.add(arrayList.get(i));
                }
            }
            if (!(products.size() > 0))
            {
                empty.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
            else {
                inventoryAdapter = new InventoryAdapter(this, products);
                recyclerView.setAdapter(inventoryAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
                empty.setVisibility(View.GONE);
            }
        }
        else  {
            empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

}