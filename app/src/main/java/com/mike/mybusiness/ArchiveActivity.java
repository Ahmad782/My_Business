package com.mike.mybusiness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ArchiveActivity extends AppCompatActivity {

    TextView noInvoice, totalInvoice, tag;
    RecyclerView invoiceRecyclerView;
    EditText searchInvoice;
    RadioGroup radioGroup;
    ArrayList<Invoice> mInvoices;
    DatabaseHelper databaseHelper;
    ArchiveAdapter archiveAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);
        noInvoice = findViewById(R.id.noInvoiceTextview);
        invoiceRecyclerView = findViewById(R.id.recylcerViewArcive);
        totalInvoice = findViewById(R.id.textViewSaved);
        tag = findViewById(R.id.tag);
        searchInvoice = findViewById(R.id.editTextArchive);
        radioGroup = findViewById(R.id.radioGroup);
        mInvoices = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);
        mInvoices = databaseHelper.fetchAllBill();
        if (!(mInvoices.size() == 0))
        {
            archiveAdapter = new ArchiveAdapter(this, mInvoices);
            invoiceRecyclerView.setAdapter(archiveAdapter);
            invoiceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            invoiceRecyclerView.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
            showOther();
            ValueAnimator valueAnimator = ValueAnimator.ofInt(0, mInvoices.size());
            valueAnimator.setDuration(2000);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
                    totalInvoice.setText("Invoices Saved: " + valueAnimator.getAnimatedValue().toString());
                }
            });
            valueAnimator.start();
        }
        else {
            hideOther();
        }
    }

    void hideOther()
    {
        noInvoice.setVisibility(View.VISIBLE);
        invoiceRecyclerView.setVisibility(View.GONE);
        totalInvoice.setVisibility(View.GONE);
        tag.setVisibility(View.GONE);
        searchInvoice.setVisibility(View.GONE);
        tag.setVisibility(View.GONE);
        radioGroup.setVisibility(View.GONE);
    }

    void showOther()
    {
        noInvoice.setVisibility(View.GONE);
        invoiceRecyclerView.setVisibility(View.VISIBLE);
        totalInvoice.setVisibility(View.VISIBLE);
        tag.setVisibility(View.VISIBLE);
        searchInvoice.setVisibility(View.VISIBLE);
        tag.setVisibility(View.VISIBLE);
        radioGroup.setVisibility(View.VISIBLE);
    }
}