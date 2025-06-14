package com.example.qrcodescannerapplication;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;

import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.Manifest;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.ScanOptions;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_READ_STORAGE_PERMISSION = 1001;
    private static final int REQUEST_CAMERA_PERMISSION = 1002;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    ActivityResultLauncher<ScanOptions> barLauncher;
    ActivityResultLauncher<Intent> imagePickerLauncher;
    private DecoratedBarcodeView barcodeScannerView;


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle toggle button click for drawer
        if (drawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        setupNavigation();
        setupActivityResultLaunchers();
        setupBarcodeScanner();
    }
    private  void  initUI(){
        // Initialize drawer layout and navigation view
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize the barcode scanner view
        barcodeScannerView = findViewById(R.id.barcode_scanner);
    }
    private void setupNavigation() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.scan) {
                    checkCameraPermissionAndScan();
                    drawerLayout.closeDrawers();// Close navigation drawer after selection
                    return true;
                } else if (item.getItemId() == R.id.scanImage) {
                    checkStoragePermissionAndPickImage();
                    drawerLayout.closeDrawers();
                    return true;
                } else if (item.getItemId() == R.id.history) {
                    Intent intent = new Intent(MainActivity.this,ScanHistory.class);
                    startActivity(intent);
                    drawerLayout.closeDrawers();
                    return true;
                }
                else if (item.getItemId() == R.id.generateQrCode){
                    Intent intent = new Intent(getApplicationContext(),GenerateQRCode.class);
                    startActivity(intent);
                    drawerLayout.closeDrawers();
                    return true;
                }
                return false;
            }
        });
    }

    private void setupActivityResultLaunchers() {
        // Initialize the ActivityResultLauncher for image picking
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleImagePickResult);
    }

    private void setupBarcodeScanner() {
        barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                // Handle the scanned result here
                String scannedText = result.getText();
                // Display the scanned result in an alert dialog
                showScanResultDialog(scannedText);
            }
            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
                // Handle potential result points here if needed
            }
        });
    }

    private void checkStoragePermissionAndPickImage() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_READ_STORAGE_PERMISSION);
        }
        else {
            openGallery();
        }
    }
    private void checkCameraPermissionAndScan(){
        //check if camera permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            barcodeScannerView.resume(); // Start scanning
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }
    private void handleImagePickResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                Uri imageUri = result.getData().getData();
                // Start the new activity to display the image and scan button
                Intent intent = new Intent(MainActivity.this, ScanImageFromGallery.class);
                intent.setData(imageUri);
                startActivity(intent);
        }
        else
        {
            Toast.makeText(MainActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
    private void openGallery() {
        // Create intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
    private void showScanResultDialog(String scannedContent) {

        // Save the scan result
        saveScanResult(scannedContent);

            // Display the scanned result in an alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Result");
            builder.setMessage(scannedContent);
            builder.setPositiveButton("Copy",(dialog,which) ->{
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Scan Results", scannedContent);
                if (clipboardManager != null){
                    clipboardManager.setPrimaryClip(clip);
                    Toast.makeText(this,"Copied to Clipboard", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Close", (dialog, which) -> {
            dialog.dismiss();
        });
            AlertDialog dialog = builder.create();
            dialog.show();

    }
    private void saveScanResult(String scannedContent) {
        // Retrieve scan history from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("ScanHistory", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Retrieve the existing history or initialize a new Set if none exists
        String historyJson = sharedPreferences.getString("history", "[]");
        Gson gson = new Gson();
        Type type = new TypeToken<List<ScanResult>>() {}.getType();
        List<ScanResult> scanHistory = gson.fromJson(historyJson, type);

        // Create a Set to track scanned content and avoid duplicates
        Set<String> scanContentSet = new HashSet<>();
        for (ScanResult scanResult : scanHistory) {
            scanContentSet.add(scanResult.getContent());
        }

        // Check if the scanned content is already in the Set
        if (!scanContentSet.contains(scannedContent)) {
            // If it's not a duplicate, add it to the history
            ScanResult newScan = new ScanResult(scannedContent, System.currentTimeMillis());
            scanHistory.add(newScan);

            // Convert the updated scan history list to JSON and save it back to SharedPreferences
            String updatedHistoryJson = gson.toJson(scanHistory);
            editor.putString("history", updatedHistoryJson);
            editor.apply();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            handleCameraPermission(grantResults);
        } else if (requestCode == REQUEST_READ_STORAGE_PERMISSION) {
            handleStoragePermissionResult(grantResults);
        }
    }
    private void handleCameraPermission(int[] grantResults){
        // Check if the request is for camera permission
        if (grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            barcodeScannerView.resume(); // Start scanning
        }
        else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
        }
    }
    private void handleStoragePermissionResult(int[] grantResults){
        // Check if the request is for read storage permission
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            Toast.makeText(this, "Read storage permission denied", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
        barcodeScannerView.resume(); // Resume scanning when the activity is resumed
    }
    @Override
    protected void onPause() {
        super.onPause();
        barcodeScannerView.pause(); // Pause scanning when the activity is paused
    }
}