package com.example.smartpantrymanager;

import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Shared bottom navigation wiring, used by every top-level screen
 * (Pantry, Recipes, Settings) so tapping a tab switches screens consistently.
 */
public class NavigationHelper {

    public static void setup(android.app.Activity activity, BottomNavigationView bottomNav) {
        bottomNav.setOnItemSelectedListener(item -> {
            Class<?> target = null;

            if (item.getItemId() == R.id.nav_pantry) {
                target = MainActivity.class;
            } else if (item.getItemId() == R.id.nav_recipes) {
                target = SuggestedRecipesActivity.class;
            } else if (item.getItemId() == R.id.nav_settings) {
                target = SettingsActivity.class;
            }

            if (target != null && !activity.getClass().equals(target)) {
                Intent intent = new Intent(activity, target);
                // Clears any duplicate stacked screens - tapping tabs never piles up a big back stack
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            return true;
        });
    }
}