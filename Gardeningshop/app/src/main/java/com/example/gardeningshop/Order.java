package com.example.gardeningshop;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private String email;
    private String deliveryAddress;
    private List<GardenItem> cartItems;
    private long timestamp;

    public Order() {}

    public String getEmail() { return email; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public List<GardenItem> getCartItems() { return cartItems; }
    public long getTimestamp() { return timestamp; }
}