[README.md](https://github.com/user-attachments/files/32679655/README.md)
# Smart Pantry Manager

An Android app I built in Java for my **Mobile App Development 700** practical assignment at Richfield. The idea is simple: track what's actually in your pantry, and only get recipe suggestions you can cook right now with what you already have.

## Features

- **Pantry tracking** – add, edit and delete ingredients with a quantity, unit and optional expiry date
- **Search** – filter your pantry list by ingredient name
- **Sort** – order the list alphabetically or by expiry date, from Settings
- **Strict recipe matching** – a recipe only shows up if I have *every* ingredient it needs, in enough quantity. Miss even one, and it's left out completely.
- **Recipe detail** – full ingredient list and method for each suggested recipe
- **Clear all data** – wipe the pantry in one tap, with a confirmation prompt first (didn't want a stray tap to nuke someone's whole pantry)
- Fully **offline** — no internet connection or login needed

## Screenshots
MobileAppDev\screenshots

## Tech stack

| Layer | Choice |
|---|---|
| Language | Java |
| IDE | Android Studio |
| Local storage | SQLite (via `SQLiteOpenHelper`) |
| UI | XML layouts, `RecyclerView`, Material Components |

## Why SQLite?

I went with SQLite because the data here is genuinely relational, not just a pile of key-value settings — a recipe has a list of ingredients, and checking "can I cook this?" means comparing two tables against each other, not doing a simple lookup. SQLite comes built into Android, needs no server or account, and works fully offline, which matters since the brief doesn't allow any cloud/Maps/location dependency anyway. Its SQL engine also does the heavy lifting for filtering and sorting (like ordering by expiry date), instead of me writing that logic by hand in Java. I did use SharedPreferences too, but only for the two small app settings (sort order) — it's just not built for storing linked, structured data like this.

## Database design

Three tables, all created in `DatabaseHelper.onCreate()`:

- **`pantry_items`** – whatever ingredients I've personally added (`name`, `quantity`, `unit`, `expiry_date`)
- **`recipes`** – the built-in recipe catalogue (`name`, `steps`), seeded once the first time the app runs
- **`recipe_ingredients`** – every ingredient a recipe needs, linked back to it by `recipe_id` — one recipe, many ingredient rows

The strict-matching logic in `getSuggestedRecipes()` goes through every recipe and checks whether each one of its `recipe_ingredients` rows has a matching `pantry_items` row (same normalized name, enough quantity). One failed ingredient and the whole recipe gets excluded — no partial credit.

## Project structure

```
app/src/main/java/com/example/smartpantrymanager/
├── MainActivity.java                 # Pantry List (home screen)
├── AddEditIngredientActivity.java     # Add / edit a pantry item
├── SuggestedRecipesActivity.java      # Strict-matched recipe list
├── RecipeDetailActivity.java          # Full recipe view
├── SettingsActivity.java              # Sort preference + clear data
├── SplashActivity.java                # Launch screen
├── NavigationHelper.java              # Shared bottom navigation wiring
├── database/DatabaseHelper.java       # SQLite schema, seed data, CRUD, matching logic
├── model/                             # PantryItem, Recipe, RecipeIngredient (POJOs)
└── adapter/                           # PantryAdapter, RecipeAdapter (RecyclerView)
```

## How to run it

1. Clone the repo:
   ```
   git clone <your-github-repo-url>
   ```
2. Open the project folder in **Android Studio** (Hedgehog or newer works fine).
3. Let Gradle sync finish — first sync can take a few minutes.
4. Run it on an emulator or a real device, **API level 24 (Android 7.0) or higher**.
5. That's it — no API keys, accounts or internet needed. The database sets itself up and seeds the recipes on first launch.

## Author

Kerina Ramasar — Mobile App Development 700, Richfield

## GitHub

https://github.com/KerinaRamasar/smart_pantry_manager
