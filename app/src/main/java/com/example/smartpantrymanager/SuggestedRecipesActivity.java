package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapter.RecipeAdapter;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.model.Recipe;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

/**
 * Runs the strict-matching logic (Section 2.3) and shows only the recipes
 * the user can make with exactly what's currently in their pantry.
 */
public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {

    private DatabaseHelper dbHelper;
    private RecipeAdapter adapter;
    private RecyclerView recyclerView;
    private android.view.View noMatchesLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerViewRecipes);
        noMatchesLayout = findViewById(R.id.layoutNoMatches);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavRecipes);
        bottomNav.setSelectedItemId(R.id.nav_recipes);
        NavigationHelper.setup(this, bottomNav);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {
        List<Recipe> suggested = dbHelper.getSuggestedRecipes();

        if (suggested.isEmpty()) {
            noMatchesLayout.setVisibility(android.view.View.VISIBLE);
            recyclerView.setVisibility(android.view.View.GONE);
        } else {
            noMatchesLayout.setVisibility(android.view.View.GONE);
            recyclerView.setVisibility(android.view.View.VISIBLE);
        }

        if (adapter == null) {
            adapter = new RecipeAdapter(suggested, this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(suggested);
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}