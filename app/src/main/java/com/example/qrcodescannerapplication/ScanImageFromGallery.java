package com.example.qrcodescannerapplication;



import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;


public class ScanImageFromGallery extends AppCompatActivity {
    private ImageView imageView;
    private Button scanButton;

    private static final String TAG = "ImageScanActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_scan_from_gallery);

        imageView = findViewById(R.id.imageView);
        scanButton = findViewById(R.id.scanButton);

        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {

            Uri imageUri = intent.getData();
            Log.d(TAG, "Received image URI: " + imageUri.toString());
            imageView.setImageURI(imageUri);
        } else {
            Log.e(TAG, "No image URI received");
            Toast.makeText(this, "No image Selected", Toast.LENGTH_SHORT).show();
            finish();
        }

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanImage();
            }
        });
    }

    private void scanImage() {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        if (drawable != null){
            Bitmap bitmap = drawable.getBitmap();
            InputImage image = InputImage.fromBitmap(bitmap, 0);

            processBarcode(image);
        }
    }

    private void processBarcode(InputImage image) {
        BarcodeScanner scanner = BarcodeScanning.getClient();
        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    // Task completed successfully
                    if (barcodes.isEmpty()){
                        Toast.makeText(ScanImageFromGallery.this, "No Qr code found", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle the barcode result here
                        StringBuilder resultBuilder = new StringBuilder();
                        for (Barcode barcode : barcodes){
                            String displayValue = barcode.getDisplayValue();
                            resultBuilder.append("").append(displayValue).append("\n");
                        }
                        showResultsDialog(resultBuilder.toString());
                    }
                })
                .addOnFailureListener(e -> {
                    //Task failed with an exception
                    Log.e(TAG, "Barcode scanning failed", e);
                    Toast.makeText(ScanImageFromGallery.this,"Barcode scanning failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showResultsDialog(String results) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Results");
        builder.setMessage(results);
        builder.setPositiveButton("Copy",(dialog, which) ->{
            //Copy the text clipboard
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Scan Results",results);
                    if (clipboardManager != null) {
                        clipboardManager.setPrimaryClip(clip);
                        Toast.makeText(this,"Results copied to clipboard", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setNegativeButton("Close", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

