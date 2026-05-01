package com.example.myapplication.data;

import java.io.Serializable;

public class Snack implements Serializable {
    private int id;
    private String name;
    private double price;
    private String image;
    private int quantity = 0;

    public Snack(int id, String name, double price, String image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getImage() { return image; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
