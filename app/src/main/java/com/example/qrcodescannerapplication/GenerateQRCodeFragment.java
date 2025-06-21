package com.example.qrcodescannerapplication;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class GenerateQRCodeFragment extends Fragment {
    private ImageView qrImageView;
    private EditText qrEditText;
    private Button generateButton;

    public GenerateQRCodeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.generate_qr_code, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        qrImageView = view.findViewById(R.id.GenerateCodeImageView);
        qrEditText = view.findViewById(R.id.EditTxtQrCodeGenerate);
        generateButton = view.findViewById(R.id.BtnGenerateQr);

        generateButton.setOnClickListener(v -> {
            String text = qrEditText.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(getContext(), "Please enter text to generate QR", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                BarcodeEncoder encoder = new BarcodeEncoder();
                Bitmap bitmap = encoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 500, 500);
                qrImageView.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to generate QR code", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
