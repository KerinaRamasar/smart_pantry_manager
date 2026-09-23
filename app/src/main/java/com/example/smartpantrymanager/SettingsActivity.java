package com.example.smartpantrymanager;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.database.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

/**
 * Settings screen - lets the user choose how the Pantry List is sorted, and
 * gives them a way to wipe their pantry data. Both are genuinely wired up:
 * the sort preference is read by MainActivity every time it loads the list,
 * and Clear Data actually deletes every row from the pantry_items table.
 * Saved with SharedPreferences, Android's simple key-value store for small
 * app settings like this (not worth a full database table for).
 */
public class SettingsActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "smart_pantry_prefs";
    public static final String KEY_SORT_BY = "sort_by"; // "name" or "expiry"

    private SharedPreferences prefs;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        dbHelper = new DatabaseHelper(this);

        RadioGroup sortGroup = findViewById(R.id.radioGroupSortBy);
        MaterialButton clearDataButton = findViewById(R.id.buttonClearData);

        // Load saved sort preference (default: sort by name)
        if ("expiry".equals(prefs.getString(KEY_SORT_BY, "name"))) {
            sortGroup.check(R.id.radioSortExpiry);
        } else {
            sortGroup.check(R.id.radioSortName);
        }

        // Save immediately whenever the user changes the sort preference.
        // MainActivity reads this same key every time it reloads the list,
        // so the change takes effect the moment the user goes back there.
        sortGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String value = (checkedId == R.id.radioSortExpiry) ? "expiry" : "name";
            prefs.edit().putString(KEY_SORT_BY, value).apply();
        });

        // Clear Data asks for confirmation first since this is destructive
        // and cannot be undone.
        clearDataButton.setOnClickListener(v -> confirmClearData());

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavSettings);
        bottomNav.setSelectedItemId(R.id.nav_settings);
        NavigationHelper.setup(this, bottomNav);
    }

    private void confirmClearData() {
        new AlertDialog.Builder(this)
                .setTitle("Clear all pantry data?")
                .setMessage("This will permanently remove every ingredient from your pantry. This cannot be undone.")
                .setPositiveButton("Clear Data", (dialog, which) -> {
                    dbHelper.clearAllPantryItems();
                    Toast.makeText(this, "Pantry data cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}