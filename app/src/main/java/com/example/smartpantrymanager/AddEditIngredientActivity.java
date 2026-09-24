package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.model.PantryItem;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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

    // First item is a placeholder ("Tap to select unit") that can't actually be
    // chosen - it's disabled in the adapter below so it just acts as a hint,
    // not a real option. "Other" reveals a free-text field for anything not listed.
    private static final List<String> UNIT_OPTIONS = Arrays.asList(
            "Tap to select unit", "g", "kg", "ml", "l", "unit", "slice", "clove", "Other");

    private DatabaseHelper dbHelper;
    private long editingItemId = -1; // -1 means "adding new", anything else means "editing"

    private EditText editName, editQuantity, editCustomUnit, editExpiry;
    private TextView errorName, errorQuantity, errorUnit, textClearExpiry, formTitle;
    private Spinner spinnerUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        dbHelper = new DatabaseHelper(this);

        formTitle = findViewById(R.id.textFormTitle);
        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        editCustomUnit = findViewById(R.id.editCustomUnit);
        editExpiry = findViewById(R.id.editExpiry);
        textClearExpiry = findViewById(R.id.textClearExpiry);
        errorName = findViewById(R.id.errorName);
        errorQuantity = findViewById(R.id.errorQuantity);
        errorUnit = findViewById(R.id.errorUnit);
        Button saveButton = findViewById(R.id.buttonSave);

        setupUnitSpinner();
        setupExpiryPicker();

        // Check if we were launched to EDIT an existing item
        if (getIntent().hasExtra(EXTRA_ITEM_ID)) {
            editingItemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
            formTitle.setText("Edit Ingredient");
            editName.setText(getIntent().getStringExtra(EXTRA_ITEM_NAME));
            editQuantity.setText(String.valueOf(getIntent().getDoubleExtra(EXTRA_ITEM_QUANTITY, 0)));
            preselectUnit(getIntent().getStringExtra(EXTRA_ITEM_UNIT));

            String existingExpiry = getIntent().getStringExtra(EXTRA_ITEM_EXPIRY);
            if (!TextUtils.isEmpty(existingExpiry)) {
                editExpiry.setText(existingExpiry);
                textClearExpiry.setVisibility(android.view.View.VISIBLE);
            }
        }

        saveButton.setOnClickListener(v -> attemptSave());
    }

    /**
     * Wires up the unit Spinner: position 0 is a disabled placeholder ("Tap to
     * select unit") so nothing is pre-selected by default, and picking "Other"
     * reveals the custom-unit text field.
     */
    private void setupUnitSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, UNIT_OPTIONS) {
            @Override
            public boolean isEnabled(int position) {
                // Position 0 is just the placeholder hint - not a selectable option
                return position != 0;
            }

            @Override
            public android.view.View getDropDownView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                android.view.View view = super.getDropDownView(position, convertView, parent);
                TextView text = (TextView) view;
                text.setTextColor(position == 0
                        ? getResources().getColor(R.color.text_secondary)
                        : getResources().getColor(R.color.text_main));
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(adapter);
        spinnerUnit.setSelection(0); // start on the placeholder, not a real unit

        spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                boolean isOther = "Other".equals(UNIT_OPTIONS.get(position));
                editCustomUnit.setVisibility(isOther ? android.view.View.VISIBLE : android.view.View.GONE);
                if (!isOther) {
                    editCustomUnit.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // no-op
            }
        });
    }

    /**
     * Selects the matching preset in the Spinner when editing an existing item.
     * If the saved unit isn't one of the presets, falls back to "Other" and
     * fills the custom field with it so nothing is lost.
     */
    private void preselectUnit(String savedUnit) {
        if (savedUnit == null) return;
        int index = UNIT_OPTIONS.indexOf(savedUnit);
        if (index >= 1 && !"Other".equals(savedUnit)) {
            spinnerUnit.setSelection(index);
        } else {
            spinnerUnit.setSelection(UNIT_OPTIONS.indexOf("Other"));
            editCustomUnit.setVisibility(android.view.View.VISIBLE);
            editCustomUnit.setText(savedUnit);
        }
    }

    /**
     * Wires up the expiry field: tapping it opens a DatePickerDialog instead of
     * the keyboard, so the user can only ever pick a real calendar date. This
     * also removes the need for manual date-format validation entirely.
     */
    private void setupExpiryPicker() {
        editExpiry.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            // If a date is already set, open the picker showing that date
            // instead of today, so editing an existing date feels natural.
            String current = editExpiry.getText().toString().trim();
            if (!TextUtils.isEmpty(current)) {
                String[] parts = current.split("-");
                if (parts.length == 3) {
                    try {
                        calendar.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
                    } catch (NumberFormatException ignored) {
                        // fall back to today if the stored value is somehow malformed
                    }
                }
            }

            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        String formatted = String.format(Locale.getDefault(),
                                "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        editExpiry.setText(formatted);
                        textClearExpiry.setVisibility(android.view.View.VISIBLE);
                    },
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });

        textClearExpiry.setOnClickListener(v -> {
            editExpiry.setText("");
            textClearExpiry.setVisibility(android.view.View.GONE);
        });
    }

    /**
     * Validates all fields; if everything passes, saves the item (insert or update)
     * and closes this screen. If anything fails, shows an inline error and does NOT save.
     */
    private void attemptSave() {
        // Clear old errors first
        errorName.setVisibility(android.view.View.GONE);
        errorQuantity.setVisibility(android.view.View.GONE);
        errorUnit.setVisibility(android.view.View.GONE);

        String name = editName.getText().toString().trim();
        String quantityText = editQuantity.getText().toString().trim();
        String expiry = editExpiry.getText().toString().trim(); // already guaranteed valid by the date picker, or empty

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

        // Work out the actual unit: either the Spinner selection, or the
        // custom text if "Other" was picked. The placeholder counts as
        // "nothing selected" and is rejected just like a blank custom unit.
        String selectedOption = (String) spinnerUnit.getSelectedItem();
        String unit = null;
        if ("Tap to select unit".equals(selectedOption)) {
            errorUnit.setText("Please select a unit");
            errorUnit.setVisibility(android.view.View.VISIBLE);
            isValid = false;
        } else if ("Other".equals(selectedOption)) {
            unit = editCustomUnit.getText().toString().trim();
            if (TextUtils.isEmpty(unit)) {
                errorUnit.setText("Enter a custom unit, or choose one from the list");
                errorUnit.setVisibility(android.view.View.VISIBLE);
                isValid = false;
            }
        } else {
            unit = selectedOption;
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