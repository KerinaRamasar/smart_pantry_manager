package com.example.smartpantrymanager.model;

/**
 * Represents ONE ingredient required by ONE recipe.
 * A recipe needing 5 ingredients will have 5 of these rows in the database,
 * all sharing the same recipeId.
 */
public class RecipeIngredient {
    private long id;
    private long recipeId;
    private String name;
    private double quantity;
    private String unit;

    public RecipeIngredient(long id, long recipeId, String name, double quantity, String unit) {
        this.id = id;
        this.recipeId = recipeId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public long getId() { return id; }
    public long getRecipeId() { return recipeId; }
    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
}