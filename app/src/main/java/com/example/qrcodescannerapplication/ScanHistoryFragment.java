package com.example.qrcodescannerapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ScanHistoryFragment extends Fragment {
    public ScanHistoryFragment(){

    }
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private final List<ScanResult> scanHistory = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,  ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.scan_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load the scan history from SharedPreferences
        loadScanHistory();

        // Set up the adapter with the loaded history
        adapter = new HistoryAdapter(scanHistory);
        recyclerView.setAdapter(adapter);
    }
    private void loadScanHistory() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("ScanHistory", getContext().MODE_PRIVATE);
        String historyJson = sharedPreferences.getString("history", "[]");
        Gson gson = new Gson();
        Type type = new TypeToken<List<ScanResult>>() {}.getType();
        List<ScanResult> loadedHistory = gson.fromJson(historyJson, type);

        if (loadedHistory != null){
            scanHistory.clear();
            scanHistory.addAll(loadedHistory);
        }
    }
}
