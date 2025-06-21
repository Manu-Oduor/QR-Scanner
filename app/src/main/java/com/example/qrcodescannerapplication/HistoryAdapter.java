package com.example.qrcodescannerapplication;

import android.text.Layout;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private final List<ScanResult> scanHistory;

    public HistoryAdapter(List<ScanResult> scanHistory){
        this.scanHistory = scanHistory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScanResult result = scanHistory.get(position);
        holder.contentTextView.setText(result.getContent());
        holder.timestampTextView.setText(DateFormat.format("yyyy-MM-dd HH:mm:ss", result.getTimestamp()));
    }

    @Override
    public int getItemCount() {

        return scanHistory.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView contentTextView;
        TextView timestampTextView;

        ViewHolder(View itemView) {
            super(itemView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
        }
    }
}
