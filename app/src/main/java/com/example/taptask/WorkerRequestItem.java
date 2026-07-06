
package com.example.taptask;

public class WorkerRequestItem {
    public String customerName;
    public String description;
    public String area;
    public String timing;

    public WorkerRequestItem(String customerName, String description, String area, String timing) {
        this.customerName = customerName;
        this.description = description;
        this.area = area;
        this.timing = timing;
    }
}