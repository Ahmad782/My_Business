package com.mike.mybusiness;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;

public class QrActivity extends AppCompatActivity {

    SurfaceView barcodeImageView;
    TextView resultTextView, scannerStatus;
    BarcodeDetector barcodeDetector;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private CameraSource cameraSource;
    String intentData = "";
    boolean isBilling = false;
    String previous = "";
    int quantity = 1;
    MediaPlayer mediaPlayer;
    long time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        barcodeImageView = findViewById(R.id.barcodeImageView);
        resultTextView = findViewById(R.id.barcodeResult);
        scannerStatus = findViewById(R.id.scannerStatus);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.barcode);
        if (getIntent().hasExtra("from")) {
            isBilling = true;
        }
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(QrActivity.this, new
                    String[]{ Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        if (!barcodeDetector.isOperational()) {
            scannerStatus.setText("Scanner Initialization Failed.");
        }

    }

    private void takeBarcodePicture() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takeBarcodePicture();
            } else {
                scannerStatus.setText("Permission Denied!");
                this.finish();
            }
        }

    }


    private void initialiseDetectorsAndSources() {

        scannerStatus.setText("Scanning...");
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(240, 240)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        barcodeImageView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(QrActivity.this, new
                                String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                    cameraSource.start(barcodeImageView.getHolder());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

            }


        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0 && time + 800 < System.currentTimeMillis()) {
                    resultTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            mediaPlayer.start();
                            barcodes.valueAt(0);
                            resultTextView.removeCallbacks(null);
                            intentData = barcodes.valueAt(0).rawValue;
                            if (isBilling) {
                                Bill b = null;
                                ArrayList<Product> products = new DatabaseHelper(getApplicationContext()).fetchAllProducts();
                                for (Product p : products) {
                                    if (p.getBarcode().equals(intentData)) {
                                        b = p.getBillItem();
                                        break;
                                    }
                                }
                                if (b != null) {
                                    if (previous.equals(intentData)) {
                                        quantity++;
                                        b.setQuantity(quantity);
                                    }
                                    previous = intentData;
                                    scannerStatus.setText("Press back to add.");
                                    resultTextView.setText(b.getQuantity() + " " + b.itemName);
                                } else {
                                    resultTextView.setText("Product not found.");
                                }
                            } else {
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("barcode", intentData);
                                setResult(Activity.RESULT_OK, resultIntent);
                                finish();
                            }
                            time = System.currentTimeMillis();
                        }
                    });
                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    @Override
    public void onBackPressed() {
        if (isBilling && !(intentData.isEmpty()))
        {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("barcode", intentData);
            resultIntent.putExtra("quantity", quantity);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
        else {
            super.onBackPressed();
        }
    }


}