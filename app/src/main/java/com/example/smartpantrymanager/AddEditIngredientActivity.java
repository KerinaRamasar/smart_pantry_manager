package com.example.smartpantrymanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.model.PantryItem;

import java.util.regex.Pattern;

/**
 * Handles both ADDING a new pantry item and EDITING an existing one.
 * If the Intent that launched this Activity carries an item ID, we're editing;
 * otherwise we're adding a brand new item.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";
    public static final String EXTRA_ITEM_NAME = "extra_item_name";
    public static final String EXTRA_ITEM_QUANTITY = "extra_item_quantity";
    public static final String EXTRA_ITEM_UNIT = "extra_item_unit";
    public static final String EXTRA_ITEM_EXPIRY = "extra_item_expiry";

    // Simple yyyy-MM-dd check - good enough to catch obviously malformed dates
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    private DatabaseHelper dbHelper;
    private long editingItemId = -1; // -1 means "adding new", anything else means "editing"

    private EditText editName, editQuantity, editUnit, editExpiry;
    private TextView errorName, errorQuantity, errorExpiry, formTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        dbHelper = new DatabaseHelper(this);

        formTitle = findViewById(R.id.textFormTitle);
        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        editUnit = findViewById(R.id.editUnit);
        editExpiry = findViewById(R.id.editExpiry);
        errorName = findViewById(R.id.errorName);
        errorQuantity = findViewById(R.id.errorQuantity);
        errorExpiry = findViewById(R.id.errorExpiry);
        Button saveButton = findViewById(R.id.buttonSave);

        // Check if we were launched to EDIT an existing item
        if (getIntent().hasExtra(EXTRA_ITEM_ID)) {
            editingItemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
            formTitle.setText("Edit Ingredient");
            editName.setText(getIntent().getStringExtra(EXTRA_ITEM_NAME));
            editQuantity.setText(String.valueOf(getIntent().getDoubleExtra(EXTRA_ITEM_QUANTITY, 0)));
            editUnit.setText(getIntent().getStringExtra(EXTRA_ITEM_UNIT));
            editExpiry.setText(getIntent().getStringExtra(EXTRA_ITEM_EXPIRY));
        }

        saveButton.setOnClickListener(v -> attemptSave());
    }

    /**
     * Validates all fields; if everything passes, saves the item (insert or update)
     * and closes this screen. If anything fails, shows an inline error and does NOT save.
     */
    private void attemptSave() {
        // Clear old errors first
        errorName.setVisibility(android.view.View.GONE);
        errorQuantity.setVisibility(android.view.View.GONE);
        errorExpiry.setVisibility(android.view.View.GONE);

        String name = editName.getText().toString().trim();
        String quantityText = editQuantity.getText().toString().trim();
        String unit = editUnit.getText().toString().trim();
        String expiry = editExpiry.getText().toString().trim();

        boolean isValid = true;

        // Name is required
        if (TextUtils.isEmpty(name)) {
            errorName.setText("Ingredient name is required");
            errorName.setVisibility(android.view.View.VISIBLE);
            isValid = false;
        }

        // Quantity is required and must be a positive number
        double quantity = 0;
        if (TextUtils.isEmpty(quantityText)) {
            errorQuantity.setText("Quantity is required");
            errorQuantity.setVisibility(android.view.View.VISIBLE);
            isValid = false;
        } else {
            try {
                quantity = Double.parseDouble(quantityText);
                if (quantity <= 0) {
                    errorQuantity.setText("Quantity must be greater than zero");
                    errorQuantity.setVisibility(android.view.View.VISIBLE);
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                errorQuantity.setText("Quantity must be a valid number");
                errorQuantity.setVisibility(android.view.View.VISIBLE);
                isValid = false;
            }
        }

        // Expiry date is OPTIONAL, but if provided, must match yyyy-MM-dd
        if (!TextUtils.isEmpty(expiry) && !DATE_PATTERN.matcher(expiry).matches()) {
            errorExpiry.setText("Date must be in yyyy-MM-dd format");
            errorExpiry.setVisibility(android.view.View.VISIBLE);
            isValid = false;
        }

        if (!isValid) return; // stop here - don't save invalid data

        if (editingItemId == -1) {
            // ADD new item
            PantryItem newItem = new PantryItem(name, quantity, unit, expiry);
            dbHelper.addPantryItem(newItem);
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        } else {
            // UPDATE existing item
            PantryItem updatedItem = new PantryItem(editingItemId, name, quantity, unit, expiry);
            dbHelper.updatePantryItem(updatedItem);
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        }

        finish(); // close this screen, return to Pantry List
    }
}