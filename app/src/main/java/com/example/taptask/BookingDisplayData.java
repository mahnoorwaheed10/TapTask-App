package com.example.taptask;

public class BookingDisplayData {

    public String workerName;
    public String serviceTitle;
    public String date;
    public String status;
    public String statusColor;
    public String bookingId;

    public BookingDisplayData(
            String workerName,
            String serviceTitle,
            String date,
            String status,
            String statusColor,
            String bookingId) {

        this.workerName = workerName;
        this.serviceTitle = serviceTitle;
        this.date = date;
        this.status = status;
        this.statusColor = statusColor;
        this.bookingId = bookingId;
    }
}