package com.mike.mybusiness;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BillActivity extends AppCompatActivity {

    EditText applyDiscount, invoiceNumber, applyTax, cashPaid;
    AutoCompleteTextView searchProduct, searchCustomer;
    TextView total, blankInvoice, due, getTotal;
    RecyclerView recyclerViewBill;
    BillAdapter billAdapter;
    ArrayList<Bill> billItems;
    ActivityResultLauncher<Intent> activityResultLauncher;
    DatabaseHelper databaseHelper;
    ArrayList<Product> products;
    ArrayList<Customer> customers;
    ImageButton button;

    CheckBox walkIn, autoInvoice, percentCheck;
    boolean saved = false;
    ConstraintLayout constraintLayout;

    float finalTotal = 0;
    float previousDue = 0;
    int customerId = 0, productId = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        searchCustomer = findViewById(R.id.searchCustomer);
        searchProduct = findViewById(R.id.searchProduct);
        searchProduct.setThreshold(1);
        applyDiscount = findViewById(R.id.applyDiscount);
        applyTax = findViewById(R.id.applyTax);
        total = findViewById(R.id.total);
        button = findViewById(R.id.button3);
        recyclerViewBill = findViewById(R.id.recyclerViewBill);
        walkIn = findViewById(R.id.walkInCheck);
        autoInvoice = findViewById(R.id.checkBox2);
        invoiceNumber = findViewById(R.id.generate);
        blankInvoice = findViewById(R.id.blankInvoice);
        percentCheck = findViewById(R.id.percentCheck);
        constraintLayout = findViewById(R.id.constraintBill);
        billItems = new ArrayList<>();
        billAdapter = new BillAdapter(this, billItems, total, applyDiscount, applyTax);
        recyclerViewBill.setAdapter(billAdapter);
        recyclerViewBill.setLayoutManager(new LinearLayoutManager(this));
        databaseHelper = new DatabaseHelper(this);
        products = databaseHelper.fetchAllProducts();
        customers = databaseHelper.fetchAllCustomers();
        recyclerViewBill.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));

        if (!(products.isEmpty())) {
            String[] suggestions = new String[products.size()];
            for (int j = 0; j < products.size(); j++) {
                suggestions[j] = products.get(j).getName();
            }

            searchProduct.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, suggestions));
            searchProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    for (int j = 0; j < products.size(); j++) {
                        if (products.get(j).name.equals(searchProduct.getText().toString().trim())) {
                            if (Integer.parseInt(products.get(j).getStock()) > 0)
                            {
                                productId = i;
                                Bill b = products.get(j).getBillItem();
                                if (b != null) {
                                    billItems.add(b);
                                    recyclerViewBill.setVisibility(View.VISIBLE);
                                    blankInvoice.setVisibility(View.GONE);
                                    billAdapter.notifyItemInserted(billAdapter.getItemCount());
                                    recyclerViewBill.scrollToPosition(billAdapter.getItemCount() - 1);
                                    percentCheck.setEnabled(true);
                                    if (applyDiscount.getText().length() == 0) {
                                        finalTotal = calculateTotal(billItems);
                                        total.setText("Total: " + String.format("%.2f", finalTotal));
                                    } else {
                                        finalTotal = calculateTotal(billItems) - Integer.parseInt(applyDiscount.getText().toString());
                                        total.setText("Total: " + String.format("%.2f", finalTotal));
                                    }
                                    searchProduct.setText("");
                                }
                            }
                            else {
                                Snackbar.make(constraintLayout, "Product is out of stock", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });

            searchProduct.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        hideKeyboard(v);
                    }
                }
            });

            searchCustomer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        hideKeyboard(v);
                    }
                }
            });

            invoiceNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        hideKeyboard(v);
                    }
                }
            });

            applyTax.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        hideKeyboard(v);
                    }
                }
            });

            applyDiscount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        hideKeyboard(v);
                    }
                }
            });
        } else {
            new AlertDialog.Builder(this).setTitle("Inventory List Empty")
                    .setMessage("Add at least one product to continue the invoice")
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getApplicationContext(), AddProductActivity.class);
                            startActivity(intent);
                        }
                    }).setNegativeButton("Later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getApplicationContext(),
                                    "You can add product through button next to the search product",
                            Toast.LENGTH_LONG).show();
                        }
                    }).show();
        }

        if (!(customers == null)) {
            String[] suggestions = new String[customers.size()];
            for (int j = 0; j < customers.size(); j++) {
                suggestions[j] = customers.get(j).getName();
            }

            searchCustomer.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, suggestions));
            searchCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    searchCustomer.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
                    button.setVisibility(View.GONE);
                    for (int j=0; j<suggestions.length; j++)
                    {
                        if (suggestions[j].equals(searchCustomer.getText().toString().trim())){
                            customerId = j;
                        }
                    }
                    Toast.makeText(getApplicationContext(), "Customer Id:" + customerId, Toast.LENGTH_SHORT).show();
                    if (!(customers.get(customerId).getDues().equals("0")))
                    {
                        previousDue = Float.parseFloat(customers.get(customerId).getDues());
                    }
                }
            });

            searchCustomer.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    searchCustomer.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0);
                    button.setVisibility(View.VISIBLE);
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
        }
        else {
            new AlertDialog.Builder(this).setTitle("Add Customer Profile")
                    .setMessage("Add customer profile to continue the invoice or use walk-in-customer")
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getApplicationContext(), CustomerActivity.class);
                            startActivity(intent);
                        }
                    }).setNegativeButton("Later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getApplicationContext(),
                                    "You can add customer profile through button next to the search customer",
                                    Toast.LENGTH_LONG).show();
                        }
                    }).show();
        }


        applyDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (billAdapter.getItemCount() > 0) {
                    if (applyDiscount.getText().length() == 0 && applyTax.getText().length() == 0) {
                        finalTotal = calculateTotal(billItems);
                        total.setText("Total: " + String.format("%.2f", finalTotal));
                    } else {
                        if (!(applyDiscount.getText().length() == 0)) {
                            if (applyTax.getText().length() == 0) {
                                calculateOnlyDiscount();
                            } else {
                                finalTotal = calculateTotalWithTaxAndDiscount(billItems);
                                total.setText("Total: " + String.format("%.2f", finalTotal) + "");
                            }
                        }
                        else {
                            if (!(applyTax.getText().length() == 0))
                            {
                                calculateOnlyTax();
                            }
                            else {
                                finalTotal = calculateTotal(billItems);
                                total.setText("Total: " + String.format("%.2f", finalTotal));
                            }
                        }
                    }
                }
                else {
                    applyDiscount.removeTextChangedListener(this);
                    applyDiscount.setText("");
                    applyDiscount.addTextChangedListener(this);
                }
            }
        });

        applyTax.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (billAdapter.getItemCount() > 0) {
                    if (applyDiscount.getText().length() == 0 && applyTax.getText().length() == 0) {
                        finalTotal = calculateTotal(billItems);
                        total.setText("Total: " + String.format("%.2f", finalTotal));
                    } else {
                        if (!(applyTax.getText().length() == 0))
                        {
                            if (applyDiscount.getText().length() == 0)
                            {
                                calculateOnlyTax();
                            }
                            else {
                                finalTotal = calculateTotalWithTaxAndDiscount(billItems);
                                total.setText("Total: " + String.format("%.2f", finalTotal) + "");
                            }
                        }
                        else {
                            if (!(applyDiscount.getText().length() == 0))
                            {
                                calculateOnlyDiscount();
                            }
                            else {
                                finalTotal = calculateTotal(billItems);
                                total.setText("Total: " + String.format("%.2f", finalTotal));
                            }
                        }
                    }
                }
                else {
                    applyTax.removeTextChangedListener(this);
                    applyTax.setText("");
                    applyTax.addTextChangedListener(this);
                }
            }
        });

        activityResultLauncher = registerForActivityResult
                (new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                if (result.getResultCode() == Activity.RESULT_OK) {
                                    Intent data = result.getData();
                                    if (data != null && data.hasExtra("barcode")) {
                                        Bill b = databaseHelper.findProductByBarCode(data.getStringExtra("barcode"));
                                        if (b != null) {
                                            b.setQuantity(data.getIntExtra("quantity", 1));
                                            billItems.add(b);
                                            recyclerViewBill.setVisibility(View.VISIBLE);
                                            blankInvoice.setVisibility(View.GONE);
                                            billAdapter.notifyItemInserted(billAdapter.getItemCount());
                                            recyclerViewBill.scrollToPosition(billAdapter.getItemCount() - 1);
                                            if (applyDiscount.getText().length() == 0 && applyTax.getText().length() ==0) {
                                                finalTotal = calculateTotal(billItems);
                                                total.setText("" + finalTotal);
                                            } else if (applyDiscount.getText().length() > 0 && applyTax.getText().length() == 0) {
                                                    calculateOnlyDiscount();
                                            }
                                            else if (applyTax.getText().length() > 0 && applyDiscount.getText().length() == 0)
                                            {
                                                calculateOnlyTax();
                                            }
                                            else {
                                                calculateTotalWithTaxAndDiscount(billItems);
                                            }
                                        }
                                    }
                                }
                            }
                        });

        walkIn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    searchCustomer.setEnabled(false);
                } else {
                    searchCustomer.setEnabled(true);
                }
            }
        });

        autoInvoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    invoiceNumber.setEnabled(false);
                    invoiceNumber.setText(databaseHelper.autoInvoiceNumber());
                } else {
                    invoiceNumber.setEnabled(true);
                }
            }
        });

        percentCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                applyTax.setText("");
                applyDiscount.setText("");
            }
        });
    }
    public void saveInvoice(View view) {
        if (billItems.size() > 0)
        {
            if (applyDiscount.getText().length() == 0 && applyTax.getText().length() ==0) {
                finalTotal = calculateTotal(billItems);
            } else if (applyDiscount.getText().length() > 0 && applyTax.getText().length() == 0) {
                calculateOnlyDiscount();
            }
            else if (applyTax.getText().length() > 0 && applyDiscount.getText().length() == 0)
            {
                calculateOnlyTax();
            }
            else {
                calculateTotalWithTaxAndDiscount(billItems);
            }
            String totalString = String.valueOf(finalTotal);
            LayoutInflater factory = LayoutInflater.from(this);
            View deleteDialogView = factory.inflate(R.layout.cash_dialogue, null);
            AlertDialog saveDialog = new AlertDialog.Builder(this).create();
            saveDialog.setView(deleteDialogView);
            getTotal = deleteDialogView.findViewById(R.id.textViewTotal);
            getTotal.setText("Total: " + totalString);
            cashPaid = deleteDialogView.findViewById(R.id.editTextPaid);
            due = deleteDialogView.findViewById(R.id.textViewDue);
            if (!customers.get(customerId).getDues().equals("0"))
            {
                previousDue = Float.parseFloat(customers.get(customerId).getDues());
                due.setText("Due Now: " +  totalString +  "\n Previous:"  + previousDue);
            }
            else {
                due.setText("Due: " + totalString);
            }
            cashPaid.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String paid = charSequence.toString();
                    float x = 0;
                    if (!customers.get(customerId).getDues().equals("0"))
                    {
                        previousDue = Float.parseFloat(customers.get(customerId).getDues());
                    }
                    if (paid.length() == 0)
                    {
                        due.setText("Due Now:" + totalString + "\n Previous: " + previousDue);
                        x += finalTotal;
                    } else if (Float.parseFloat(paid) < Float.parseFloat(totalString)){
                        float f = Float.parseFloat(totalString) - Float.parseFloat(paid);
                        x += f;
                        due.setText("Due Now:" + f +  "\n Previous: " + previousDue);
                    }
                    else if (Float.parseFloat(paid) > Float.parseFloat(totalString)){
                        cashPaid.setText("");
                        due.setText("Due: " + totalString);
                    }
                    else {
                        due.setText("Due: 0" +  "\n Previous: " + previousDue);
                    }
                    previousDue +=x;
                    Toast.makeText(getApplicationContext(), previousDue + "", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            Button button = deleteDialogView.findViewById(R.id.buttonSave);
            String customer = searchCustomer.getText().toString();
            if (customer.isEmpty() && !walkIn.isChecked())
            {
                Snackbar.make(constraintLayout, "Provide Customer Name", Snackbar.LENGTH_SHORT).show();
                return;
            }
            else if (walkIn.isChecked())
            {
                customer = "Walk In Customer";
            }
            else {
                if (databaseHelper.searchCustomerByName(customer) == null)
                {
                    Snackbar.make(constraintLayout, "Customer Profile Not Exists", Snackbar.LENGTH_SHORT).show();
                    return;
                }
            }
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String formattedDate = df.format(c);
            String invoiceNumberString = "";
            if (autoInvoice.isChecked())
            {
                invoiceNumberString = invoiceNumber.getText().toString();
            }
            else {
                if (invoiceNumber.getText().length() == 0)
                {
                    Snackbar.make(constraintLayout, "Add or use auto-invoice#", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                else {
                    invoiceNumberString = "a" + invoiceNumber.getText().toString();
                }
            }
            String discountString = applyDiscount.getText().toString();
            String taxString = applyTax.getText().toString();
            String isPercent = String.valueOf(percentCheck.isChecked());
            if (!invoiceNumberString.isEmpty() && !customer.isEmpty())
            {
                String finalCustomer = customer;
                String finalInvoiceNumberString = invoiceNumberString;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String paid = cashPaid.getText().toString();
                        String isCredit = "";
                        if (paid.length() == 0)
                        {
                            previousDue += finalTotal;
                            if (finalCustomer.equals("Walk In Customer"))
                            {
                                Snackbar.make(constraintLayout, "Type 'Walk In Customer' is invalid for credit", Snackbar.LENGTH_SHORT).show();
                                saveDialog.dismiss();
                                return;
                            }
                            else {
                                isCredit = "true";
                            }
                        } else if (Float.parseFloat(paid) < Float.parseFloat(totalString)){
                            if (finalCustomer.equals("Walk In Customer"))
                            {
                                Snackbar.make(constraintLayout, "Type 'Walk In Customer' is invalid for credit", Snackbar.LENGTH_SHORT).show();
                                saveDialog.dismiss();
                                return;
                            }
                            else {
                                isCredit = "true";
                            }
                        }
                        else {
                            isCredit = "false";
                        }
                        for (Bill bill : billItems)
                        {
                            for (int i=0; i<products.size(); i++)
                            {
                                if (products.get(i).getName().equals(bill.getItemName()))
                                {
                                    if (Integer.parseInt(products.get(i).getStock()) < bill.getQuantity())
                                    {
                                        Snackbar.make(constraintLayout, "Product " + products.get(i).getName()
                                        + "'s available stock is less than " + billItems.get(i).getQuantity(), Snackbar.LENGTH_SHORT).show();
                                        saveDialog.dismiss();
                                        return;
                                    }
                                    else {
                                        int l = Integer.parseInt(products.get(i).getStock()) - bill.getQuantity();
                                        Product p = products.get(i);
                                        p.setStock(l + "");
                                        databaseHelper.updateProductStock(p, i+1);
                                    }
                                }
                            }
                        }
                        Customer c = customers.get(customerId);
                        c.setDues(previousDue + "");
                        databaseHelper.updateCustomerDues(c, customerId + 1);
                        databaseHelper.saveBill(new Invoice(finalCustomer, formattedDate, finalInvoiceNumberString, totalString,
                                discountString, taxString, paid, isPercent, isCredit));
                        saved = true;
                        ArrayList<BillContent> billContent = new ArrayList<>();
                        for (int i=0; i<billItems.size(); i++)
                        {
                            billContent.add(billItems.get(i).getBillContent());
                        }
                        databaseHelper.newInvoiceTable(getApplicationContext(), finalInvoiceNumberString, billContent);
                        Intent intent = new Intent(getApplicationContext(), BillActionActivity.class);
                        startActivity(intent);
                    }
                });
            }
            else {
                Snackbar.make(constraintLayout, "Add or use auto-invoice#", Snackbar.LENGTH_SHORT).show();
                saveDialog.dismiss();
            }
            saveDialog.show();
        }
        else {
            Snackbar.make(constraintLayout, "Unable to save blank invoice", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void scanQR(View view) {
        Intent intent = new Intent(this, QrActivity.class);
        intent.putExtra("from", "Bill");
        activityResultLauncher.launch(intent);
    }

    public void newCustomer(View view) {
        Intent intent = new Intent(this, CustomerActivity.class);
        startActivity(intent);
    }

    float calculateTotal(ArrayList<Bill> bills) {
        float x = 0;
        for (int i = 0; i < bills.size(); i++) {
            if (!(bills.get(i).getDiscount() == 0))
            {
                x += bills.get(i).price * bills.get(i).quantity;
            }
            else {
                x += bills.get(i).price * bills.get(i).quantity - bills.get(i).getDiscount();
            }
        }
        return x;
    }

    @Override
    public void onBackPressed() {
        if ((!saved) && billAdapter.getItemCount() != 0) {
            new AlertDialog.Builder(this).setTitle("Are you sure to exit without saving the invoice?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();
        } else {
            finish();
        }
    }

    float calculateTotalWithTaxAndDiscount(ArrayList<Bill> bills)
    {
        float discount = Float.parseFloat(applyDiscount.getText().toString());
        float tax = Float.parseFloat(applyDiscount.getText().toString());
        float total = calculateTotal(bills);
        if (percentCheck.isChecked())
        {
            total = total - discount * total / 100;
            total = total + tax * total / 100;
        }
        else {
            total = total - discount + tax;
        }
        return total;
    }

    void calculateOnlyDiscount()
    {
        if (percentCheck.isChecked()) {
            float f = calculateTotal(billItems);
            f = f - Float.parseFloat(applyDiscount.getText().toString()) * f / 100;
            finalTotal = f;
            total.setText("Total: " + String.format("%.2f", f) + "");
        } else {
            float f = calculateTotal(billItems) - Float.parseFloat(applyDiscount.getText().toString());
            finalTotal = f;
            total.setText("Total: " + String.format("%.2f", f) + "");
        }
    }

    void calculateOnlyTax()
    {
        if (percentCheck.isChecked())
        {
            float f = calculateTotal(billItems);
            f = f + Float.parseFloat(applyTax.getText().toString()) * f / 100;
            finalTotal = f;
            total.setText("Total: " + String.format("%.2f", f) + "");
        }
        else {
            float f = calculateTotal(billItems) - Float.parseFloat(applyTax.getText().toString());
            finalTotal = f;
            total.setText("Total: " + String.format("%.2f", f) + "");
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}