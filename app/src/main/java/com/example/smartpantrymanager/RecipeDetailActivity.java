package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.List;

/**
 * Shows the full ingredient list and preparation method for one recipe,
 * selected from the Suggested Recipes screen.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        dbHelper = new DatabaseHelper(this);

        TextView nameView = findViewById(R.id.textRecipeDetailName);
        TextView ingredientsView = findViewById(R.id.textRecipeIngredients);
        TextView stepsView = findViewById(R.id.textRecipeSteps);

        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);

        // Find the matching recipe from the full list to get its name and steps
        List<Recipe> allRecipes = dbHelper.getAllRecipes();
        Recipe recipe = null;
        for (Recipe r : allRecipes) {
            if (r.getId() == recipeId) {
                recipe = r;
                break;
            }
        }

        if (recipe == null) {
            nameView.setText("Recipe not found");
            return;
        }

        nameView.setText(recipe.getName());
        stepsView.setText(recipe.getSteps());

        List<RecipeIngredient> ingredients = dbHelper.getIngredientsForRecipe(recipeId);
        StringBuilder sb = new StringBuilder();
        for (RecipeIngredient ing : ingredients) {
            sb.append("• ").append(ing.getName()).append(" - ")
                    .append(ing.getQuantity()).append(" ").append(ing.getUnit()).append("\n");
        }
        ingredientsView.setText(sb.toString().trim());
    }
}