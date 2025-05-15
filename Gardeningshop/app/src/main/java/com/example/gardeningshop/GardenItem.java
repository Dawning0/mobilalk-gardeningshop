package com.example.gardeningshop;

public class GardenItem {
    private String name;
    private String price;
    private int imageResId;
    private int quantity;

    public GardenItem() {}

    public GardenItem(String name, String price, int imageResId, int quantity) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public String getPrice() { return price; }
    public int getImageResId() {return imageResId;}
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
}