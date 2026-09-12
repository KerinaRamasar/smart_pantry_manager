package com.example.smartpantrymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database creation, seeding, CRUD and the strict-matching logic.
 * This is the single source of truth for the app's persisted data (Section 3.2).
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_PANTRY = "pantry_items";
    private static final String TABLE_RECIPES = "recipes";
    private static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    // Pantry item columns
    private static final String COL_PANTRY_ID = "id";
    private static final String COL_PANTRY_NAME = "name";
    private static final String COL_PANTRY_QTY = "quantity";
    private static final String COL_PANTRY_UNIT = "unit";
    private static final String COL_PANTRY_EXPIRY = "expiry_date";

    // Recipe columns
    private static final String COL_RECIPE_ID = "id";
    private static final String COL_RECIPE_NAME = "name";
    private static final String COL_RECIPE_STEPS = "steps";

    // Recipe ingredient columns
    private static final String COL_RI_ID = "id";
    private static final String COL_RI_RECIPE_ID = "recipe_id";
    private static final String COL_RI_NAME = "ingredient_name";
    private static final String COL_RI_QTY = "quantity";
    private static final String COL_RI_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PANTRY_NAME + " TEXT NOT NULL, " +
                COL_PANTRY_QTY + " REAL NOT NULL, " +
                COL_PANTRY_UNIT + " TEXT, " +
                COL_PANTRY_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " (" +
                COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_NAME + " TEXT NOT NULL, " +
                COL_RECIPE_STEPS + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                COL_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RI_RECIPE_ID + " INTEGER NOT NULL, " +
                COL_RI_NAME + " TEXT NOT NULL, " +
                COL_RI_QTY + " REAL NOT NULL, " +
                COL_RI_UNIT + " TEXT, " +
                "FOREIGN KEY(" + COL_RI_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COL_RECIPE_ID + "))");

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    // ---------- SEED DATA (17 starter recipes, required min is 15) ----------
    private void seedRecipes(SQLiteDatabase db) {
        addSeedRecipe(db, "Scrambled Eggs on Toast",
                "1. Crack eggs into a bowl and whisk.\n2. Melt butter in pan, add eggs, stir until soft-set.\n3. Toast bread and serve eggs on top.",
                new Object[][]{{"egg", 2, "unit"}, {"bread", 2, "slice"}, {"butter", 10, "g"}});

        addSeedRecipe(db, "Tomato Pasta",
                "1. Boil pasta until al dente.\n2. Heat oil, add garlic and tomato, simmer 10 min.\n3. Toss pasta through sauce.",
                new Object[][]{{"pasta", 200, "g"}, {"tomato", 3, "unit"}, {"garlic", 2, "clove"}, {"olive oil", 15, "ml"}});

        addSeedRecipe(db, "Chicken Stir Fry",
                "1. Slice chicken and vegetables.\n2. Stir-fry chicken until cooked.\n3. Add vegetables and soy sauce, cook 5 min.",
                new Object[][]{{"chicken breast", 200, "g"}, {"bell pepper", 1, "unit"}, {"onion", 1, "unit"}, {"soy sauce", 20, "ml"}});

        addSeedRecipe(db, "Vegetable Fried Rice",
                "1. Heat oil in wok.\n2. Add rice, vegetables, and egg.\n3. Season with soy sauce and stir-fry until hot.",
                new Object[][]{{"rice", 300, "g"}, {"carrot", 1, "unit"}, {"pea", 50, "g"}, {"egg", 1, "unit"}, {"soy sauce", 15, "ml"}});

        addSeedRecipe(db, "Grilled Cheese Sandwich",
                "1. Butter outside of bread slices.\n2. Place cheese between slices.\n3. Grill in pan until golden both sides.",
                new Object[][]{{"bread", 2, "slice"}, {"cheese", 2, "slice"}, {"butter", 10, "g"}});

        addSeedRecipe(db, "Banana Pancakes",
                "1. Mash banana, mix with flour, egg and milk.\n2. Pour batter onto hot pan.\n3. Flip once bubbles form, cook other side.",
                new Object[][]{{"banana", 1, "unit"}, {"flour", 100, "g"}, {"egg", 1, "unit"}, {"milk", 100, "ml"}});

        addSeedRecipe(db, "Vegetable Soup",
                "1. Chop all vegetables.\n2. Simmer in stock for 20 minutes.\n3. Season to taste and serve.",
                new Object[][]{{"carrot", 2, "unit"}, {"potato", 2, "unit"}, {"onion", 1, "unit"}, {"vegetable stock", 500, "ml"}});

        addSeedRecipe(db, "Beef Tacos",
                "1. Brown beef mince in a pan with spices.\n2. Warm tortillas.\n3. Fill tortillas with beef, lettuce and cheese.",
                new Object[][]{{"beef mince", 250, "g"}, {"tortilla", 4, "unit"}, {"lettuce", 50, "g"}, {"cheese", 50, "g"}});

        addSeedRecipe(db, "Greek Salad",
                "1. Chop cucumber, tomato and onion.\n2. Combine with olives and feta.\n3. Dress with olive oil.",
                new Object[][]{{"cucumber", 1, "unit"}, {"tomato", 2, "unit"}, {"onion", 1, "unit"}, {"feta cheese", 100, "g"}, {"olive oil", 15, "ml"}});

        addSeedRecipe(db, "Omelette",
                "1. Whisk eggs with salt and pepper.\n2. Pour into hot buttered pan.\n3. Fold once set and serve.",
                new Object[][]{{"egg", 3, "unit"}, {"butter", 10, "g"}});

        addSeedRecipe(db, "Peanut Butter Toast",
                "1. Toast bread.\n2. Spread peanut butter on top.\n3. Slice and serve.",
                new Object[][]{{"bread", 2, "slice"}, {"peanut butter", 30, "g"}});

        addSeedRecipe(db, "Chicken Salad Sandwich",
                "1. Shred cooked chicken and mix with mayo.\n2. Spread on bread.\n3. Add lettuce and close sandwich.",
                new Object[][]{{"chicken breast", 150, "g"}, {"mayonnaise", 20, "g"}, {"bread", 2, "slice"}, {"lettuce", 20, "g"}});

        addSeedRecipe(db, "Fruit Smoothie",
                "1. Add fruit and milk to blender.\n2. Blend until smooth.\n3. Pour into a glass and serve.",
                new Object[][]{{"banana", 1, "unit"}, {"strawberry", 100, "g"}, {"milk", 200, "ml"}});

        addSeedRecipe(db, "Mashed Potatoes",
                "1. Boil potatoes until soft.\n2. Mash with butter and milk.\n3. Season with salt and pepper.",
                new Object[][]{{"potato", 4, "unit"}, {"butter", 20, "g"}, {"milk", 50, "ml"}});

        addSeedRecipe(db, "Cheese Quesadilla",
                "1. Place cheese on tortilla, fold in half.\n2. Cook in pan until golden and cheese melts.\n3. Slice and serve.",
                new Object[][]{{"tortilla", 2, "unit"}, {"cheese", 100, "g"}});

        addSeedRecipe(db, "Rice and Beans",
                "1. Cook rice.\n2. Heat beans with onion and spices.\n3. Serve beans over rice.",
                new Object[][]{{"rice", 200, "g"}, {"black beans", 200, "g"}, {"onion", 1, "unit"}});

        addSeedRecipe(db, "Egg Fried Noodles",
                "1. Boil noodles.\n2. Scramble egg in pan, add noodles.\n3. Add soy sauce and stir-fry together.",
                new Object[][]{{"noodles", 200, "g"}, {"egg", 2, "unit"}, {"soy sauce", 15, "ml"}});
    }

    // Helper used only by seedRecipes() - inserts one recipe plus its ingredient rows
    private void addSeedRecipe(SQLiteDatabase db, String name, String steps, Object[][] ingredients) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(COL_RECIPE_NAME, name);
        recipeValues.put(COL_RECIPE_STEPS, steps);
        long recipeId = db.insert(TABLE_RECIPES, null, recipeValues);

        for (Object[] ing : ingredients) {
            ContentValues ingValues = new ContentValues();
            ingValues.put(COL_RI_RECIPE_ID, recipeId);
            ingValues.put(COL_RI_NAME, (String) ing[0]);
            ingValues.put(COL_RI_QTY, ((Number) ing[1]).doubleValue());
            ingValues.put(COL_RI_UNIT, (String) ing[2]);
            db.insert(TABLE_RECIPE_INGREDIENTS, null, ingValues);
        }
    }

    // ---------- PANTRY CRUD ----------

    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PANTRY_NAME, item.getName());
        values.put(COL_PANTRY_QTY, item.getQuantity());
        values.put(COL_PANTRY_UNIT, item.getUnit());
        values.put(COL_PANTRY_EXPIRY, item.getExpiryDate());
        long id = db.insert(TABLE_PANTRY, null, values);
        db.close();
        return id;
    }

    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PANTRY_NAME, item.getName());
        values.put(COL_PANTRY_QTY, item.getQuantity());
        values.put(COL_PANTRY_UNIT, item.getUnit());
        values.put(COL_PANTRY_EXPIRY, item.getExpiryDate());
        int rows = db.update(TABLE_PANTRY, values, COL_PANTRY_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
        db.close();
        return rows;
    }

    public void deletePantryItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PANTRY, COL_PANTRY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null, COL_PANTRY_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                items.add(new PantryItem(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_PANTRY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PANTRY_QTY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_UNIT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_EXPIRY))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }

    // ---------- RECIPE READ ----------

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, null, null, null, null, COL_RECIPE_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                recipes.add(new Recipe(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_RECIPE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_STEPS))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recipes;
    }

    public List<RecipeIngredient> getIngredientsForRecipe(long recipeId) {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPE_INGREDIENTS, null,
                COL_RI_RECIPE_ID + " = ?", new String[]{String.valueOf(recipeId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                ingredients.add(new RecipeIngredient(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_RI_ID)),
                        recipeId,
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_RI_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_RI_QTY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_RI_UNIT))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ingredients;
    }

    // ---------- STRICT-MATCHING LOGIC (Section 2.3 - the core of this assignment) ----------

    /**
     * Returns only the recipes where EVERY required ingredient is present in the
     * pantry in at least the required quantity. A recipe missing even one
     * ingredient, or short on quantity, is excluded entirely.
     */
    public List<Recipe> getSuggestedRecipes() {
        List<Recipe> allRecipes = getAllRecipes();
        List<PantryItem> pantryItems = getAllPantryItems();
        List<Recipe> suggested = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            List<RecipeIngredient> required = getIngredientsForRecipe(recipe.getId());
            if (pantryHasAllIngredients(pantryItems, required)) {
                suggested.add(recipe);
            }
        }
        return suggested;
    }

    private boolean pantryHasAllIngredients(List<PantryItem> pantryItems, List<RecipeIngredient> required) {
        for (RecipeIngredient req : required) {
            if (!pantryHasIngredient(pantryItems, req)) {
                return false; // one missing/short ingredient fails the whole recipe
            }
        }
        return true;
    }

    private boolean pantryHasIngredient(List<PantryItem> pantryItems, RecipeIngredient req) {
        String normalizedReqName = normalize(req.getName());
        for (PantryItem item : pantryItems) {
            if (normalize(item.getName()).equals(normalizedReqName)) {
                return item.getQuantity() >= req.getQuantity();
            }
        }
        return false;
    }

    /**
     * Normalizes an ingredient name so simple real-world differences don't
     * break matching - e.g. "Tomatoes" and "tomato" both become "tomato".
     * This satisfies the brief's requirement to handle basic plural/case mismatches
     * without needing full NLP.
     */
    private String normalize(String name) {
        String n = name.trim().toLowerCase();
        if (n.endsWith("es")) {
            n = n.substring(0, n.length() - 2);
        } else if (n.endsWith("s") && !n.endsWith("ss")) {
            n = n.substring(0, n.length() - 1);
        }
        return n;
    }
}