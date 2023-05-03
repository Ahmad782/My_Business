package com.mike.mybusiness;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaTimestamp;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    TextInputEditText title, location, number;
    FirebaseUser firebaseUser;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null)
        {
            title = findViewById(R.id.title);
            location = findViewById(R.id.location);
            number = findViewById(R.id.contact);
            sharedPreferences = getSharedPreferences("default_prefs", MODE_PRIVATE);
            editor = sharedPreferences.edit();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            if (firebaseUser != null)
            {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
            else if (sharedPreferences.contains("first_run_succeed"))
            {
                Intent intent = new Intent(this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                editor.putBoolean("first_run_succeed", true);
                editor.apply();
            }
        }
        else {
            new AlertDialog.Builder(this).setTitle("Error")
                    .setMessage("Application requires valid internet connection to perform its actions")
                    .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            recreate();
                        }
                    }).setNegativeButton("Abandon", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).setCancelable(false).show().getWindow().setDimAmount(0.5f);
        }
    }

    public void continueSignup(View view) {
        if (TextUtils.isEmpty(title.getText().toString()))
        {
            title.setError("Required");
        } else if (TextUtils.isEmpty(location.getText().toString())) {
            location.setError("Required");

        } else if (TextUtils.isEmpty(number.getText().toString())) {
            number.setError("Required");
        }
        else if (!PhoneNumberUtils.isGlobalPhoneNumber(number.getText().toString())) {
            number.setError("Invalid Number");
        }
        else
        {
            editor.putString("name", title.getText().toString());
            editor.putString("location", location.getText().toString());
            editor.putString("number", number.getText().toString());
            editor.apply();
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
            finish();
        }
    }
}