package com.example.gardeningshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.example.gardeningshop.ui.CartActivity;
import com.example.gardeningshop.ui.LoginActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private GardenAdapter adapter;
    private List<GardenItem> gardenItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); 

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        gardenItemList = new ArrayList<>();
        adapter = new GardenAdapter(gardenItemList);
        recyclerView.setAdapter(adapter);

        loadItemsFromFirestore();
        //uploadDefaultGardenItems(); // Upload template items
    }

    private void loadItemsFromFirestore() {
        db.collection("gardenItems")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    gardenItemList.clear();
                    for (var document : queryDocumentSnapshots) {
                        String name = document.getString("name");
                        String price = document.getString("price");
                        int imageResId = document.getLong("imageResId").intValue();
                        int quantity = document.getLong("quantity").intValue();

                        gardenItemList.add(new GardenItem(name, price, imageResId, quantity));
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load items: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

//    private void uploadDefaultGardenItems() {
//        List<GardenItem> defaultItems = new ArrayList<>();
//        defaultItems.add(new GardenItem("Tomato seeds", "$3", R.drawable.tomato, 1));
//        defaultItems.add(new GardenItem("Cucumber seeds", "$10", R.drawable.cucumber, 1));
//        defaultItems.add(new GardenItem("Pepper seeds", "$8", R.drawable.pepper, 1));
//        defaultItems.add(new GardenItem("Squash seeds", "$5", R.drawable.squash, 1));
//        defaultItems.add(new GardenItem("Cabbage seeds", "$4", R.drawable.cabbage, 1));
//        defaultItems.add(new GardenItem("Carrot seeds", "$5", R.drawable.carrot, 1));
//        defaultItems.add(new GardenItem("Eggplant seeds", "$6", R.drawable.eggplant, 1));
//        defaultItems.add(new GardenItem("Leek seeds", "$2", R.drawable.leek, 1));
//        defaultItems.add(new GardenItem("Melon seeds", "$7", R.drawable.melon, 1));
//        defaultItems.add(new GardenItem("Onion seeds", "$5", R.drawable.onions, 1));
//        defaultItems.add(new GardenItem("Pak Choi seeds", "$5", R.drawable.pakchoi, 1));
//
//        for (GardenItem item : defaultItems) {
//            uploadGardenItem(item);
//        }
//    }
//
//    private void uploadGardenItem(GardenItem item) {
//        db.collection("gardenItems")
//                .document(item.getName())
//                .set(item)
//                .addOnSuccessListener(aVoid ->
//                        Toast.makeText(this, "GardenItem uploaded: " + item.getName(), Toast.LENGTH_SHORT).show())
//                .addOnFailureListener(e ->
//                        Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logoutUser();
            return true;
        } else if (item.getItemId() == R.id.action_cart) {
            startActivity(new Intent(this, CartActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        db.clearPersistence();
        auth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}