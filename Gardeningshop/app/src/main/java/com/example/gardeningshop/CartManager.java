package com.example.gardeningshop;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<GardenItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addItem(GardenItem item) {
        for (GardenItem cartItem : cartItems) {
            if (cartItem.getName().equals(item.getName())) {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                return;
            }
        }

        cartItems.add(item);
    }

    public void clearCart() {
        cartItems.clear();
    }

    public List<GardenItem> getItems() {
        return new ArrayList<>(cartItems);
    }

    public void removeItem(GardenItem item) {
        cartItems.remove(item);
    }
}