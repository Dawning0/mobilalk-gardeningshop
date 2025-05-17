package com.example.gardeningshop.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.gardeningshop.GardenItem;
import com.example.gardeningshop.R;
import com.example.gardeningshop.Order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderDetailsActivity extends AppCompatActivity {
    private TextView orderEmail, orderAddress, orderItems, orderDate; // 🔹 Added orderDate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_order);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        Order order = (Order) getIntent().getSerializableExtra("order");

        if (order == null) {
            Toast.makeText(this, "Error loading order details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        orderEmail = findViewById(R.id.orderEmail);
        orderAddress = findViewById(R.id.orderAddress);
        orderItems = findViewById(R.id.orderItems);
        orderDate = findViewById(R.id.orderDate);

        orderEmail.setText("Email: " + order.getEmail());
        orderAddress.setText("Address: " + order.getDeliveryAddress());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        String formattedDate = dateFormat.format(new Date(order.getTimestamp()));

        orderDate.setText("Order Date: " + formattedDate);

        StringBuilder itemsList = new StringBuilder();
        for (GardenItem item : order.getCartItems()) {
            itemsList.append(item.getName()).append(" (").append(item.getQuantity()).append("x)\n");
        }

        orderItems.setText(itemsList.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_cart) {
            startActivity(new Intent(this, CartActivity.class));
            return true;
        } else if (item.getItemId() == R.id.action_orders) {
            startActivity(new Intent(this, OrdersActivity.class));
            return true;
        } else if (item.getItemId() == R.id.action_logout) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}