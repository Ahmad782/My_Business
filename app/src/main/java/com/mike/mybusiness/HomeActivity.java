package com.mike.mybusiness;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity {

    String name = "Default";
    TextView textView;
    AdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        textView = findViewById(R.id.shopName);
        adView = findViewById(R.id.adView);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null)
        {
        SharedPreferences sharedPreferences = getSharedPreferences("default_prefs", MODE_PRIVATE);
        if (sharedPreferences.contains("name"))
        {
            name = sharedPreferences.getString("name", name);
        }
        textView.setText(name.toLowerCase());
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });
        refreshAd();
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

    public void billClicked(View view) {
        Intent intent = new Intent(this, BillActivity.class);
        startActivity(intent);
    }

    public void inventoryClicked(View view) {
        Intent intent = new Intent(this, InventoryActivity.class);
        startActivity(intent);
    }

    public void accountClicked(View view) {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }

    public void statisticsClicked(View view) {
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    public void orderClicked(View view) {
        Intent intent = new Intent(this, OrderActivity.class);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(HomeActivity.this).setTitle("Do you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    public void cloudClicked(View view) {
    }

    public void removeAds(View view) {
    }

    public void archiveClicked(View view) {
        Intent intent = new Intent(this, ArchiveActivity.class);
        startActivity(intent);
    }

    void refreshAd()
    {
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshAd();
                }
            }, 30000);
    }

    public void customerClicked(View view) {
        Intent intent = new Intent(this, CustomerProfiles.class);
        startActivity(intent);
    }
}