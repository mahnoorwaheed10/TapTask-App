package com.example.taptask;

public class BookingDisplayData {
    public String workerName;
    public String serviceTitle;
    public String date;
    public String status;
    public String statusColor;

    public BookingDisplayData(String workerName, String serviceTitle, String date, String status, String statusColor) {
        this.workerName = workerName;
        this.serviceTitle = serviceTitle;
        this.date = date;
        this.statusColor = statusColor;
        this.status = status;
    }
}