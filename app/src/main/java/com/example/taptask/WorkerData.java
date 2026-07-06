package com.example.taptask;

public class WorkerData {
    public String name;
    public String title;
    public String area;
    public String experience;
    public String rate;
    public float rating;
    public int reviews;
    public boolean isAvailable;

    public WorkerData(String name, String title, String area, String experience,
                      String rate, float rating, int reviews, boolean isAvailable) {
        this.name = name;
        this.title = title;
        this.area = area;
        this.experience = experience;
        this.rate = rate;
        this.rating = rating;
        this.reviews = reviews;
        this.isAvailable = isAvailable;
    }
}