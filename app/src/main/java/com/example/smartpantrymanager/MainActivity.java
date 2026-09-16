package com.example.smartpantrymanager;

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

        // Add button - will open the Add/Edit screen once we build it next.
        // For now it just confirms the click works.
        fabAdd.setOnClickListener(v ->
                Toast.makeText(this, "Add screen coming in the next step", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload every time the screen becomes visible again,
        // so changes made on other screens (add/edit/delete) always show up.
        loadPantryItems();
    }

    private void loadPantryItems() {
        List<PantryItem> items = dbHelper.getAllPantryItems();

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
        // Will launch AddEditIngredientActivity with this item's data - next session.
        Toast.makeText(this, "Edit screen coming in the next step", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDelete(PantryItem item) {
        dbHelper.deletePantryItem(item.getId());
        Toast.makeText(this, item.getName() + " deleted", Toast.LENGTH_SHORT).show();
        loadPantryItems(); // refresh the list immediately
    }
}