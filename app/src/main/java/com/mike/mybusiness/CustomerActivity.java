package com.mike.mybusiness;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class CustomerActivity extends AppCompatActivity {

    TextInputEditText name, number, address, dues, email;
    DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_customer);
        name = findViewById(R.id.name_customer);
        number = findViewById(R.id.number_customer);
        address = findViewById(R.id.address_customer);
        dues = findViewById(R.id.pending_customer);
        email = findViewById(R.id.email_customer);
        databaseHelper = new DatabaseHelper(this);
    }

    public void saveProfile(View view) {
        if (!(name.getText().length() == 0))
        {
            if (dues.getText().toString().isEmpty())
            {
                dues.setText("0");
            }
            databaseHelper.newCustomer(name.getText().toString().trim(), number.getText().toString(), address.getText().toString(),
            dues.getText().toString(), email.getText().toString());
            finish();
        }
        else {
            Toast.makeText(this, "Name field is compulsory for customer profile.", Toast.LENGTH_SHORT).show();
        }
    }
}