package com.example.gardeningshop.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private Button deliveryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }

        db = FirebaseFirestore.getInstance();

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        deliveryButton = findViewById(R.id.deliveryButton);
        deliveryButton.setOnClickListener(v -> navigateToDeliveryPage());

        loadCartItems();
    }

    private void loadCartItems() {
        List<GardenItem> cartItems = CartManager.getInstance().getItems();
        cartAdapter = new CartAdapter(cartItems, this::updateTotalPrice);
        cartRecyclerView.setAdapter(cartAdapter);

        updateEmptyCartMessage(cartItems);
        updateTotalPrice();
        deliveryButton.setEnabled(!cartItems.isEmpty());
    }

    private void updateEmptyCartMessage(List<GardenItem> cartItems) {
        TextView emptyCartMessage = findViewById(R.id.emptyCartMessage);
        emptyCartMessage.setVisibility(cartItems.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void navigateToDeliveryPage() {
        List<GardenItem> cartItems = CartManager.getInstance().getItems();
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty! Add items before proceeding.", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, DeliveryActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logoutUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        FirebaseFirestore.getInstance().clearPersistence();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void updateTotalPrice() {
        double totalPrice = 0;

        for (GardenItem item : CartManager.getInstance().getItems()) {
            double itemPrice = Double.parseDouble(item.getPrice().replace("$", ""));
            totalPrice += itemPrice * item.getQuantity();
        }

        TextView totalPriceText = findViewById(R.id.totalPriceText);
        totalPriceText.setText("Total: $" + String.format("%.2f", totalPrice));
    }
}