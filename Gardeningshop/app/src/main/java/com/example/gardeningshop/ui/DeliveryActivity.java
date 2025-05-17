package com.example.gardeningshop.ui;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.gardeningshop.MainActivity;
import com.example.gardeningshop.OrderReminderReceiver;
import com.example.gardeningshop.R;
import com.example.gardeningshop.CartManager;
import com.example.gardeningshop.GardenItem;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DeliveryActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText addressField;
    private Button confirmDeliveryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        addressField = findViewById(R.id.addressField);
        confirmDeliveryButton = findViewById(R.id.confirmDeliveryButton);

        confirmDeliveryButton.setOnClickListener(v -> saveCartToFirebase());

        requestLocationPermission();
        createNotificationChannel();
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fetchLocation();
        }
    }

    private void fetchLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            getAddressFromCoordinates(location.getLatitude(), location.getLongitude());
                        } else {
                            Toast.makeText(this, "Unable to get location. Please enter your address manually.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Log.e("DeliveryActivity", "Error getting location: ", e));
        }
    }

    private void getAddressFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                addressField.setText(addresses.get(0).getAddressLine(0)); // Autofill address field
            } else {
                Toast.makeText(this, "No address found. Please enter manually.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Error fetching address: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveCartToFirebase() {
        String userEmail = auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : "unknown";
        String deliveryAddress = addressField.getText().toString().trim();
        List<GardenItem> cartItems = CartManager.getInstance().getItems();

        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (deliveryAddress.isEmpty()) {
            Toast.makeText(this, "Please enter a valid delivery address.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        List<Map<String, Object>> cartData = new ArrayList<>();
        for (GardenItem item : cartItems) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("name", item.getName());
            itemMap.put("price", item.getPrice());
            itemMap.put("quantity", item.getQuantity());
            cartData.add(itemMap);
        }

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("email", userEmail);
        orderData.put("deliveryAddress", deliveryAddress);
        orderData.put("cartItems", cartData);
        orderData.put("timestamp", System.currentTimeMillis());

        db.collection("orders").document(userEmail + "_" + System.currentTimeMillis())
                .set(orderData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Order confirmed!", Toast.LENGTH_SHORT).show();

                    CartManager.getInstance().clearCart();
                    showOrderNotification();
                    scheduleOrderReminder();

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error saving order: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showOrderNotification() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            return;
        }

        Intent ordersIntent = new Intent(this, OrdersActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ordersIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "order_notifications")
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Order Confirmed!")
                .setContentText("Your order has been placed successfully.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(R.drawable.icon, "View Orders", pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            } else {
                Toast.makeText(this, "Location permission denied. Please enter your address manually.", Toast.LENGTH_SHORT).show();
                addressField.setHint("Enter delivery address");
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "order_notifications",
                    "Order Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void scheduleOrderReminder() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }

        Intent intent = new Intent(this, OrderReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        long triggerTime = System.currentTimeMillis() + (20 * 1000);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
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