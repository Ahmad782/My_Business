package com.mike.mybusiness;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    InventoryAdapter inventoryAdapter;
    ArrayList<Product> inventoryList;
    DatabaseHelper databaseHelper;
    TextView emptyTextView, totalTextView;
    EditText searchEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        recyclerView = findViewById(R.id.inventoryRecylcerView);
        emptyTextView = findViewById(R.id.emptyTextView);
        totalTextView = findViewById(R.id.totalProducts);
        searchEdit = findViewById(R.id.searchProduct);
        databaseHelper = new DatabaseHelper(this);
    }

    public void newProduct(View view) {
        Intent intent = new Intent(this, AddProductActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!databaseHelper.fetchAllProducts().isEmpty()) {
            emptyTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            inventoryList = databaseHelper.fetchAllProducts();
            inventoryAdapter = new InventoryAdapter(this, inventoryList);
            recyclerView.setAdapter(inventoryAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
            totalTextView.setText(getResources().getString(R.string.total_indexed_products) + " " + inventoryList.size());
            searchEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    ArrayList<Product> products = databaseHelper.findProducts(charSequence.toString());
                        if (!(products.isEmpty())) {
                            inventoryList.clear();
                            inventoryAdapter.notifyDataSetChanged();
                            for  (int j=0; j<products.size(); j++)
                            {
                                inventoryList.add(products.get(j));
                            }
                            inventoryAdapter.notifyDataSetChanged();
                        }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString().isEmpty())
                    {
                        Intent intent = getIntent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                }
            });
        } else {
            emptyTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
}