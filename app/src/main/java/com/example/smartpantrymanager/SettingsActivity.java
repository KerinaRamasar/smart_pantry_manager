package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Settings screen - lets the user toggle expiry alerts and choose a units
 * preference. Saved with SharedPreferences, Android's simple key-value store
 * for small app settings like this (not worth a full database table for).
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "smart_pantry_prefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts_enabled";
    private static final String KEY_UNITS = "units_preference"; // "metric" or "imperial"

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        Switch expiryAlertsSwitch = findViewById(R.id.switchExpiryAlerts);
        RadioGroup unitsGroup = findViewById(R.id.radioGroupUnits);

        // Load saved values (default: alerts ON, metric units)
        expiryAlertsSwitch.setChecked(prefs.getBoolean(KEY_EXPIRY_ALERTS, true));
        if ("imperial".equals(prefs.getString(KEY_UNITS, "metric"))) {
            unitsGroup.check(R.id.radioImperial);
        } else {
            unitsGroup.check(R.id.radioMetric);
        }

        // Save immediately whenever the user changes either setting
        expiryAlertsSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean(KEY_EXPIRY_ALERTS, isChecked).apply());

        unitsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String value = (checkedId == R.id.radioImperial) ? "imperial" : "metric";
            prefs.edit().putString(KEY_UNITS, value).apply();
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavSettings);
        bottomNav.setSelectedItemId(R.id.nav_settings);
        NavigationHelper.setup(this, bottomNav);
    }
}