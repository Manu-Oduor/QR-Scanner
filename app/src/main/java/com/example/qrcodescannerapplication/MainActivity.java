package com.example.qrcodescannerapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
public class MainActivity extends AppCompatActivity {
Button btnScan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get reference to the scan button
        btnScan = (Button) findViewById(R.id.btnScan);
        // Set click listener for the scan button
        btnScan.setOnClickListener(v -> {
                ScanCode();
    });
}
    private void ScanCode() {
        // Create scan options with desired settings
        ScanOptions  options = new ScanOptions();
        options.setPrompt("Volume up to flash on");// Set the prompt message
        options.setBeepEnabled(true);// Enable beep sound on successful scan
        options.setOrientationLocked(true);// Lock the orientation during scanning
        options.setCaptureActivity(CaptureActivity.class);// Set the capture activity class to be used
        // Launch the scan activity and handle the result
        barLauncher.launch(options);
    }
    // Register a result handler for the scan activity
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(),result->{
        if(result.getContents() != null)
        {
            // Display the scanned result in an alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Result");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    });
}