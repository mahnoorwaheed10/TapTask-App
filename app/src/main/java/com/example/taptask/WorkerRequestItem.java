package com.example.taptask;

public class WorkerRequestItem {
    public String customerName;
    public String customerId;
    public String description;
    public String area;
    public String timing;
    public String bookingId;

    public WorkerRequestItem(String customerName, String description, String area, String timing) {
        this.customerName = customerName;
        this.customerId = null;
        this.description = description;
        this.area = area;
        this.timing = timing;
        this.bookingId = null;
    }

    public WorkerRequestItem(String customerName, String description, String area, String timing, String bookingId) {
        this.customerName = customerName;
        this.customerId = null;
        this.description = description;
        this.area = area;
        this.timing = timing;
        this.bookingId = bookingId;
    }
}