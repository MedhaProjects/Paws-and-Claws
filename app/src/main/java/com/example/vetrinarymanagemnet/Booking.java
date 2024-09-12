package com.example.vetrinarymanagemnet;

public class Booking {
    private String date;
    private String time;
    private String petName;

    // Constructor
    public Booking(String date, String time, String petName) {
        this.date = date;
        this.time = time;
        this.petName = petName;
    }

    // Getters
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getPetName() { return petName; }
}
