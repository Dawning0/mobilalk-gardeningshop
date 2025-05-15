package com.example.gardeningshop.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gardeningshop.CartAdapter;
import com.example.gardeningshop.CartManager;
import com.example.gardeningshop.GardenItem;
import com.example.gardeningshop.MainActivity;
import com.example.gardeningshop.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

@SuppressLint("MissingInflatedId")
public class CartActivity extends AppCompatActivity {
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable toolbar's built-in navigation functionality
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enables navigation icon
            toolbar.setNavigationOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set up RecyclerView with a Grid layout (2 columns)
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Fetch cart items dynamically
        loadCartItems();
    }
    private void loadCartItems() {
        List<GardenItem> cartItems = CartManager.getInstance().getItems();
        cartAdapter = new CartAdapter(cartItems, this::updateTotalPrice);
        cartRecyclerView.setAdapter(cartAdapter);

        TextView emptyCartMessage = findViewById(R.id.emptyCartMessage);

        if (cartItems.isEmpty()) {
            emptyCartMessage.setVisibility(View.VISIBLE);
        } else {
            emptyCartMessage.setVisibility(View.GONE);
        }

        updateTotalPrice();
    }

    // Inflate menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        invalidateOptionsMenu(); // Inflate the menu
        return true;
    }

    // Handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logoutUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        FirebaseFirestore.getInstance().clearPersistence(); // Optionally clear cache
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void updateTotalPrice() {
        double totalPrice = 0;

        for (GardenItem item : CartManager.getInstance().getItems()) {
            double itemPrice = Double.parseDouble(item.getPrice().replace("$", ""));
            totalPrice += itemPrice * item.getQuantity(); // Multiply price by quantity
        }

        TextView totalPriceText = findViewById(R.id.totalPriceText);
        totalPriceText.setText("Total: $" + String.format("%.2f", totalPrice));
    }
}