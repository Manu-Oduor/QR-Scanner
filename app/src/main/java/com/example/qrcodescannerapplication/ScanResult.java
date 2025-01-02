package com.example.qrcodescannerapplication;

public class ScanResult {
    private String content;
    private long timestamp;

    public ScanResult(String content, long timestamp) {
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
