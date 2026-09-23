package com.example.smartpantrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapter.PantryAdapter;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.model.PantryItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

/**
 * Pantry List screen - shows every ingredient currently in the user's pantry.
 * This is the app's home/starting screen.
 */
public class MainActivity extends AppCompatActivity implements PantryAdapter.OnPantryItemActionListener {

    private DatabaseHelper dbHelper;
    private PantryAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerViewPantry);
        emptyText = findViewById(R.id.textEmptyPantry);
        FloatingActionButton fabAdd = findViewById(R.id.fabAddItem);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavPantry);
        bottomNav.setSelectedItemId(R.id.nav_pantry);
        NavigationHelper.setup(this, bottomNav);

        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, AddEditIngredientActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload every time the screen becomes visible again,
        // so changes made on other screens (add/edit/delete/settings) always show up.
        loadPantryItems();
    }

    private void loadPantryItems() {
        // Read the user's chosen sort order from Settings (defaults to "name"
        // if they've never changed it) so this list always reflects their preference.
        SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
        String sortBy = prefs.getString(SettingsActivity.KEY_SORT_BY, "name");
        List<PantryItem> items = dbHelper.getAllPantryItems(sortBy);

        if (items.isEmpty()) {
            emptyText.setVisibility(android.view.View.VISIBLE);
            recyclerView.setVisibility(android.view.View.GONE);
        } else {
            emptyText.setVisibility(android.view.View.GONE);
            recyclerView.setVisibility(android.view.View.VISIBLE);
        }

        if (adapter == null) {
            adapter = new PantryAdapter(items, this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(items);
        }
    }

    // ---------- PantryAdapter.OnPantryItemActionListener ----------

    @Override
    public void onEdit(PantryItem item) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_NAME, item.getName());
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_QUANTITY, item.getQuantity());
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_UNIT, item.getUnit());
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_EXPIRY, item.getExpiryDate());
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onDelete(PantryItem item) {
        dbHelper.deletePantryItem(item.getId());
        Toast.makeText(this, item.getName() + " deleted", Toast.LENGTH_SHORT).show();
        loadPantryItems(); // refresh the list immediately
    }
}