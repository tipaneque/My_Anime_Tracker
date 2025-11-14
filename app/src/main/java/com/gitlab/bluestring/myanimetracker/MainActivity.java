package com.gitlab.bluestring.myanimetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gitlab.bluestring.myanimetracker.helpers.DatabaseHelper;
import com.gitlab.bluestring.myanimetracker.model.User;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private DatabaseHelper databaseHelper;
    private String userEmail;
    private Button btnGallery, btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if user is logged in
        if (!isUserLoggedIn()) {
            redirectToLogin();
            return;
        }

        databaseHelper = new DatabaseHelper(this);
        tvWelcome = findViewById(R.id.tvWelcome);
        btnGallery = findViewById(R.id.btnGallery);
        btnProfile = findViewById(R.id.btnProfile);

        // Get the logged user email
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("user_email", "");

        // Load the logged user data
        loadUserData();

        setupClickListeners();
    }

    private void loadUserData() {
        User user = databaseHelper.getUser(userEmail);
        if (user != null) {
            tvWelcome.setText(String.format("Welcome, %s!", user.getFullname()));
        }
    }

    private void setupClickListeners() {
        btnGallery.setOnClickListener(v -> {
            // Navigate to Gallery Activity
            startActivity(new Intent(MainActivity.this, GalleryActivity.class));
        });

        btnProfile.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Profile Section", Toast.LENGTH_SHORT).show();
        });
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        return sharedPreferences.getBoolean("is_logged_in", false);
    }

    private void redirectToLogin() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, "Logout realizado", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}