package com.example.qrcodescannerapplication;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class GenerateQRCode extends AppCompatActivity
{
    private ImageView qrCodeIV;
    private EditText dataEdt;
    private Button generateQRBtn;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_qr_code);

        //Initialize all variables
        qrCodeIV = findViewById(R.id.GenerateCodeImageView);
        dataEdt = findViewById(R.id.EditTxtQrCodeGenerate);
        generateQRBtn = findViewById(R.id.BtnGenerateQr);

        //Initialize onClick Listener for button
        generateQRBtn.setOnClickListener(
                new
                        View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(TextUtils.isEmpty(
                                        dataEdt.getText().toString()
                                )){
                                    Toast.makeText(GenerateQRCode.this,"Enter some text to generate QR Code",Toast.LENGTH_SHORT).show();
                                }else{
                                    generateQRCode(
                                            dataEdt.getText().toString());
                                }
                            }
                        });
    }

    private void generateQRCode(String text){
        BarcodeEncoder
                barcodeEncoder = new BarcodeEncoder();
        try{
            // Method returns a bitmap image
            Bitmap bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 400,400);
                    //Set the Bitmap to ImageView
            qrCodeIV.setImageBitmap(bitmap);
        }catch (WriterException e){
            e.printStackTrace();
        }
    }
}
