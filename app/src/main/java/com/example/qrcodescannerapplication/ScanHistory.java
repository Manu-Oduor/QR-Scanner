package com.example.qrcodescannerapplication;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScanHistory extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private List<ScanResult> scanHistory;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_history);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadScanHistory();
        adapter = new HistoryAdapter(scanHistory);
        recyclerView.setAdapter(adapter);
    }
    private void loadScanHistory() {
        SharedPreferences sharedPreferences = getSharedPreferences("ScanHistory", MODE_PRIVATE);
        String historyJson = sharedPreferences.getString("history", "[]");
        Gson gson = new Gson();
        Type type = new TypeToken<List<ScanResult>>() {}.getType();
        scanHistory = gson.fromJson(historyJson, type);
    }
}
