package com.example.smartpantrymanager.model;

/** Represents one recipe: its name and its preparation steps. */
public class Recipe {
    private long id;
    private String name;
    private String steps;

    public Recipe(long id, String name, String steps) {
        this.id = id;
        this.name = name;
        this.steps = steps;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getSteps() { return steps; }
}