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

    // ---------- SEED DATA (21 recipes, required min is 15) ----------
    private void seedRecipes(SQLiteDatabase db) {
        addSeedRecipe(db, "Scrambled Eggs on Toast",
                "1. Crack eggs into a bowl and whisk.\n2. Melt butter in pan, add eggs, stir until soft-set.\n3. Toast bread and serve eggs on top.",
                new Object[][]{{"egg", 2, "unit"}, {"bread", 2, "slice"}, {"butter", 10, "g"}});

        addSeedRecipe(db, "Grilled Cheese Sandwich",
                "1. Butter outside of bread slices.\n2. Place cheese between slices.\n3. Grill in pan until golden both sides.",
                new Object[][]{{"bread", 2, "slice"}, {"cheese", 2, "slice"}, {"butter", 10, "g"}});

        addSeedRecipe(db, "Greek Salad",
                "1. Chop cucumber, tomato and onion.\n2. Combine with olives and feta.\n3. Dress with olive oil.",
                new Object[][]{{"cucumber", 1, "unit"}, {"tomato", 2, "unit"}, {"onion", 1, "unit"}, {"feta cheese", 100, "g"}, {"olive oil", 15, "ml"}});

        addSeedRecipe(db, "Fruit Smoothie",
                "1. Add fruit and milk to blender.\n2. Blend until smooth.\n3. Pour into a glass and serve.",
                new Object[][]{{"banana", 1, "unit"}, {"strawberry", 100, "g"}, {"milk", 200, "ml"}});

        addSeedRecipe(db, "Mashed Potatoes",
                "1. Boil potatoes until soft.\n2. Mash with butter and milk.\n3. Season with salt and pepper.",
                new Object[][]{{"potato", 4, "unit"}, {"butter", 20, "g"}, {"milk", 50, "ml"}});

        addSeedRecipe(db, "Egg Fried Noodles",
                "1. Boil noodles.\n2. Scramble egg in pan, add noodles.\n3. Add soy sauce and stir-fry together.",
                new Object[][]{{"noodles", 200, "g"}, {"egg", 2, "unit"}, {"soy sauce", 15, "ml"}});

        addSeedRecipe(db, "Toasted Chicken and Mayo Sandwich",
                "1. Butter bread and layer chicken, cheese, lettuce and mayo.\n2. Close sandwich and toast in a pan until golden and cheese melts.\n3. Season with salt to taste, slice and serve.",
                new Object[][]{{"leftover fried chicken", 150, "g"}, {"mayonnaise", 20, "g"}, {"bread", 2, "slice"}, {"lettuce", 20, "g"}, {"cheese", 30, "g"}});

        addSeedRecipe(db, "Chicken Fried Rice",
                "1. Cook rice (or use leftover rice) and set aside.\n2. Stir-fry diced chicken with onion until cooked through.\n3. Add rice, soy sauce, garlic sauce, black pepper and paprika, stir-fry until combined.\n4. Season with salt to taste and serve hot.",
                new Object[][]{{"chicken", 150, "g"}, {"rice", 200, "g"}, {"onion", 1, "unit"}, {"soy sauce", 15, "ml"}, {"garlic sauce", 10, "ml"}, {"black pepper", 1, "g"}, {"paprika", 1, "g"}});

        addSeedRecipe(db, "Egg Fried Rice",
                "1. Cook rice (or use leftover rice) and set aside.\n2. Scramble egg in a pan, then stir in onion.\n3. Add rice, soy sauce, garlic sauce, black pepper and paprika, stir-fry until combined.\n4. Season with salt to taste and serve hot.",
                new Object[][]{{"egg", 2, "unit"}, {"rice", 200, "g"}, {"onion", 1, "unit"}, {"soy sauce", 15, "ml"}, {"garlic sauce", 10, "ml"}, {"black pepper", 1, "g"}, {"paprika", 1, "g"}});

        addSeedRecipe(db, "Leftover Braai into Spicy Chutney",
                "1. Chop onion, tomato and chillies finely.\n2. Fry onion until soft, add tomato and chillies, simmer until pulpy.\n3. Stir in chopped leftover braai meat and heat through.\n4. Season with salt to taste and serve as a chutney or relish.",
                new Object[][]{{"onion", 1, "unit"}, {"tomato", 2, "unit"}, {"green chillies", 2, "unit"}, {"leftover braai", 150, "g"}});

        addSeedRecipe(db, "Banana Cake",
                "1. Mash bananas in a bowl.\n2. Mix in oil, baking powder and flour until combined into a batter.\n3. Pour into a greased tin and bake until a skewer comes out clean.",
                new Object[][]{{"banana", 2, "unit"}, {"oil", 60, "ml"}, {"baking powder", 5, "g"}, {"flour", 200, "g"}});

        addSeedRecipe(db, "Shredded Tuna with Mayo Sandwich",
                "1. Mix shredded tuna with mayo, finely chopped onion and chillies.\n2. Season with salt to taste.\n3. Spread onto toasted bread and serve.",
                new Object[][]{{"onion", 1, "unit"}, {"chillies", 1, "unit"}, {"mayonnaise", 20, "g"}, {"tuna", 100, "g"}, {"toasted bread", 2, "slice"}});

        addSeedRecipe(db, "Basic Naan",
                "1. Mix flour, yogurt, baking powder and a pinch of salt into a soft dough.\n2. Knead for a few minutes, then rest covered for 30 minutes.\n3. Divide into balls, roll flat, and cook in a hot dry pan until bubbled and lightly charred on both sides.\n4. Brush with a little butter before serving.",
                new Object[][]{{"flour", 250, "g"}, {"yogurt", 100, "g"}, {"baking powder", 5, "g"}});

        addSeedRecipe(db, "Leftover Chicken Pasta",
                "1. Boil pasta until soft and drain.\n2. Fry chopped onion and tomato until soft.\n3. Add shredded chicken and heat through.\n4. Add pasta and cheese, mix well and serve.",
                new Object[][]{{"leftover chicken", 150, "g"}, {"pasta", 200, "g"}, {"onion", 1, "unit"}, {"tomato", 2, "unit"}, {"cheese", 50, "g"}});

        addSeedRecipe(db, "Quick Chicken Quesadilla",
                "1. Place chicken, onion and cheese on one tortilla.\n2. Cover with the second tortilla.\n3. Cook in a dry pan until golden on both sides.\n4. Cut into triangles and serve.",
                new Object[][]{{"tortilla", 2, "unit"}, {"leftover chicken", 150, "g"}, {"cheese", 100, "g"}, {"onion", 1, "unit"}});

        addSeedRecipe(db, "Creamy Garlic Pasta",
                "1. Boil pasta until cooked and drain.\n2. Melt butter and fry garlic for 1 minute.\n3. Add milk and cheese and stir until creamy.\n4. Add pasta and mix well before serving.",
                new Object[][]{{"pasta", 200, "g"}, {"milk", 150, "ml"}, {"cheese", 50, "g"}, {"garlic", 2, "clove"}, {"butter", 15, "g"}});

        addSeedRecipe(db, "French Toast",
                "1. Whisk eggs, milk and sugar together.\n2. Dip each slice of bread into the mixture.\n3. Fry in butter until golden on both sides.\n4. Serve warm.",
                new Object[][]{{"bread", 4, "slice"}, {"egg", 2, "unit"}, {"milk", 50, "ml"}, {"sugar", 10, "g"}, {"butter", 15, "g"}});

        addSeedRecipe(db, "Garlic Butter Bread",
                "1. Mix softened butter with crushed garlic and parsley.\n2. Spread the mixture over the bread.\n3. Toast in a pan or oven until golden.\n4. Serve warm.",
                new Object[][]{{"bread", 4, "slice"}, {"butter", 30, "g"}, {"garlic", 2, "clove"}, {"parsley", 5, "g"}});

        addSeedRecipe(db, "Easy Chicken Soup",
                "1. Chop the vegetables and chicken.\n2. Add everything to a pot with water and stock.\n3. Simmer for 20-25 minutes until the vegetables are soft.\n4. Season and serve hot.",
                new Object[][]{{"leftover chicken", 150, "g"}, {"potato", 2, "unit"}, {"carrot", 1, "unit"}, {"onion", 1, "unit"}, {"water", 500, "ml"}, {"stock cube", 1, "unit"}});

        addSeedRecipe(db, "Leftover Chicken and Potato Bake",
                "1. Boil sliced potatoes until slightly soft.\n2. Layer potatoes, chicken and onion in a baking dish.\n3. Pour over milk and top with cheese.\n4. Bake at 180C until golden.",
                new Object[][]{{"leftover chicken", 150, "g"}, {"potato", 3, "unit"}, {"cheese", 80, "g"}, {"onion", 1, "unit"}, {"milk", 100, "ml"}});

        addSeedRecipe(db, "Spicy Chicken Toast",
                "1. Shred the leftover chicken.\n2. Mix chicken with mayonnaise and chilli sauce.\n3. Spread onto bread and add cheese.\n4. Toast until golden and the cheese melts.",
                new Object[][]{{"leftover chicken", 100, "g"}, {"bread", 2, "slice"}, {"mayonnaise", 20, "g"}, {"chilli sauce", 10, "ml"}, {"cheese", 40, "g"}});
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

    /**
     * Deletes every row from the pantry table (used by the "Clear All Pantry
     * Data" action in Settings). Recipes are untouched - this only wipes
     * what the user has added to their own pantry.
     */
    public void clearAllPantryItems() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PANTRY, null, null);
        db.close();
    }

    public List<PantryItem> getAllPantryItems() {
        return getAllPantryItems("name");
    }

    /**
     * Overload that supports the Settings "Sort Pantry By" preference.
     * sortBy is either "name" (alphabetical) or "expiry" (soonest first).
     */
    public List<PantryItem> getAllPantryItems(String sortBy) {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String orderBy = "expiry".equals(sortBy)
                ? "CASE WHEN " + COL_PANTRY_EXPIRY + " IS NULL OR " + COL_PANTRY_EXPIRY + " = '' THEN 1 ELSE 0 END, " + COL_PANTRY_EXPIRY + " ASC"
                : COL_PANTRY_NAME + " ASC";
        Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null, orderBy);

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