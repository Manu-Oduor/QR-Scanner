package com.example.qrcodescannerapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.Manifest;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1001;
    private static final int REQUEST_CAMERA_PERMISSION = 1002;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    ActivityResultLauncher<ScanOptions> barLauncher;

    ActivityResultLauncher<Intent> imagePickerLauncher;


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

        // Initialize drawer layout and navigation view
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.scan) {
                    checkCameraPermissionandScan();
                    return true;
                } else if (item.getItemId() == R.id.scan_image) {
                    pickImageFromGallery();
                    return true;
                }
                return false;
            }
        });

        // Initialize the ActivityResultLauncher here
        barLauncher = registerForActivityResult(new ScanContract(), result -> handleScanResult(result.getOriginalIntent()));

        // Initialize ActivityResultLauncher for image picking
        imagePickerLauncher =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> handleImagePickResult(result.getData().getData()));
    }
    private void checkCameraPermissionandScan(){
        //check if camera permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            launchScanActivity();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    private void launchScanActivity() {
        // Create scan options with desired settings
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on"); // Set the prompt message
        options.setBeepEnabled(true); // Enable beep sound on successful scan
        options.setOrientationLocked(true); // Lock the orientation during scanning
        options.setCaptureActivity(CaptureActivity.class); // Set the capture activity class to be used
        // Launch the scan activity and handle the result
        barLauncher.launch(options);
    }

    private void pickImageFromGallery(){
        imagePickerLauncher.launch(null);
    }
    private void handleImagePickResult(Uri imageUri){
        if(imageUri != null){
            //Start scanning process with selected image
            startScanning(imageUri);
        }
    }
    private void startScanning(Uri imageUri){
        // Implement the scanning process using the selected image
        // For example, you can pass the image URI to a scanning activity
        Intent scanIntent = new Intent(this, CaptureActivity.class);
        scanIntent.setData(imageUri);
        startActivity(scanIntent);
    }
    private void handleScanResult(Intent data) {
        if (data != null && data.hasExtra("SCAN_RESULT")) {
            String scannedContent = data.getStringExtra("SCAN_RESULT");
            // Display the scanned result in an alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Result");
            builder.setMessage(scannedContent);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION){
            // Check if the request is for camera permission
            if (grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                launchScanActivity();
            }
            else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
