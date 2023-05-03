package com.mike.mybusiness;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddProductActivity extends AppCompatActivity {

    EditText barcodeEdit;
    TextInputEditText title, price, stockDate, company, available;
    CheckBox checkBox;
    Calendar mCalendar = Calendar.getInstance();
    ActivityResultLauncher<Intent> activityResultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        barcodeEdit = findViewById(R.id.barcodeEditAdd);
        title = findViewById(R.id.titleInput);
        price = findViewById(R.id.priceInput);
        stockDate = findViewById(R.id.stockInput);
        available = findViewById(R.id.availableInput);
        checkBox = findViewById(R.id.checkBox);
        company = findViewById(R.id.companyInput);
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                mCalendar.set(Calendar.YEAR, i);
                mCalendar.set(Calendar.MONTH, i1);
                mCalendar.set(Calendar.DAY_OF_MONTH, i2);
                String myFormat="dd/MM/yyyy";
                SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat);
                stockDate.setText(dateFormat.format(mCalendar.getTime()));
            }
        };

        DatePickerDialog.OnDateSetListener dateSetListener2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                mCalendar.set(Calendar.YEAR, i);
                mCalendar.set(Calendar.MONTH, i1);
                mCalendar.set(Calendar.DAY_OF_MONTH, i2);
                String myFormat="dd/MM/yyyy";
                SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat);
                checkBox.setText(dateFormat.format(mCalendar.getTime()));

            }
        };
        stockDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddProductActivity.this, dateSetListener, mCalendar.get(Calendar.YEAR),mCalendar.get(Calendar.MONTH),mCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.show();
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    new DatePickerDialog(AddProductActivity.this, dateSetListener2, mCalendar.get(Calendar.YEAR),mCalendar.get(Calendar.MONTH),mCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });
        activityResultLauncher  =  registerForActivityResult
                (new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                if (result.getResultCode() == Activity.RESULT_OK)
                                {
                                    Intent data = result.getData();
                                    if (data != null) {
                                        barcodeEdit.setText(data.getStringExtra("barcode"));
                                    }
                                }
                            }
                        });
    }

    public void scanBarCode(View view) {
        Intent intent = new Intent(this, QrActivity.class);
        activityResultLauncher.launch(intent);
    }

    public void saveProduct(View view) {
        if (title.getText().length() == 0 || price.getText().length() == 0
        || company.getText().length() == 0 || stockDate.getText().length() == 0
                || available.getText().length() ==0 || barcodeEdit.getText().length() == 0)
        {
            Toast.makeText(this, "All fields are compulsory except expiry date.", Toast.LENGTH_SHORT).show();
        }
        else {
            String date = "";
            if (isValidDate(checkBox.getText().toString()) && checkBox.isChecked())
            {
                date = checkBox.getText().toString();
            }
            String[] inputs = {title.getText().toString().trim(), price.getText().toString(),
                    barcodeEdit.getText().toString(), company.getText().toString().trim(), available.getText().toString(),
                    stockDate.getText().toString(), date};
            boolean x = new DatabaseHelper(this).newProduct(inputs[0], Integer.parseInt(inputs[1]), inputs[2],
                    inputs[3], inputs[4], inputs[5], inputs[6]);
            if (!x)
            {
                Toast.makeText(this,
                        "Product with this name/barcode already exists", Toast.LENGTH_SHORT).show();
            }
            else {
                finish();
            }
        }
    }

    boolean isValidDate(String input)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
        simpleDateFormat.setLenient(false);
        try {
            simpleDateFormat.parse(input.trim());

        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}