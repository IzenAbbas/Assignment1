package com.example.myapplication.data;

import java.io.Serializable;
import java.util.List;

public class Booking implements Serializable {
    private String bookingId;
    private String userId;
    private String movieName;
    private int seats;
    private List<String> seatIds;
    private double totalPrice;
    private String dateTime;

    public Booking() {}

    public Booking(String bookingId, String userId, String movieName, int seats, List<String> seatIds, double totalPrice, String dateTime) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.movieName = movieName;
        this.seats = seats;
        this.seatIds = seatIds;
        this.totalPrice = totalPrice;
        this.dateTime = dateTime;
    }

    public String getBookingId() { return bookingId; }
    public String getUserId() { return userId; }
    public String getMovieName() { return movieName; }
    public int getSeats() { return seats; }
    public List<String> getSeatIds() { return seatIds; }
    public double getTotalPrice() { return totalPrice; }
    public String getDateTime() { return dateTime; }
}
