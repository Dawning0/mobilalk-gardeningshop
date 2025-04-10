package com.example.gardeningshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gardeningshop.ui.LoginActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private GardenAdapter adapter;
    private List<GardenItem> gardenItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        auth = FirebaseAuth.getInstance(); // Initialize Firebase authentication

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Sample items for display
        gardenItemList = new ArrayList<>();
        gardenItemList.add(new GardenItem("Cucumber seeds", "$10", R.drawable.cucumber));
        gardenItemList.add(new GardenItem("Squash seeds", "$5", R.drawable.squash));
        gardenItemList.add(new GardenItem("Tomato seeds", "$3", R.drawable.tomato));
        gardenItemList.add(new GardenItem("Pepper seeds", "$8", R.drawable.pepper));

        adapter = new GardenAdapter(gardenItemList);
        recyclerView.setAdapter(adapter);
    }

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
        }
        return super.onOptionsItemSelected(item);
    }
    private void logoutUser() {
        auth.signOut(); // Sign out from Firebase
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
