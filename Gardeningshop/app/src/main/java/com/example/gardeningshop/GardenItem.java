package com.example.gardeningshop;

public class GardenItem {
    private String name;
    private String price;
    private int imageResId; // Resource ID for the image

    public GardenItem(String name, String price, int imageResId) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getPrice() { return price; }
    public int getImageResId() { return imageResId; }
}
