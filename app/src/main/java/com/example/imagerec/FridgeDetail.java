package com.example.imagerec;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class FridgeDetail extends Fragment {

    private EditText itemNameField, storageDateField, expirationDateField, nutritionalInfoField;
    private Spinner storageLocationSpinner;
    private NumberPicker shelfLifePicker, quantityPicker;
    private ImageView itemImage, chooseIconImageView;
    private Button saveButton;
    private Button deleteButton;
    private DatabaseHelper databaseHelper;
    private int itemId; // Item ID passed from LowFridge

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fridge_detail, container, false);

        // Initialize UI elements
        itemNameField = view.findViewById(R.id.itemnameField);
        storageDateField = view.findViewById(R.id.storageDate);
        expirationDateField = view.findViewById(R.id.expirationDate);
        nutritionalInfoField = view.findViewById(R.id.nutritionalInfo);
        storageLocationSpinner = view.findViewById(R.id.storageLocationSpinner);
        shelfLifePicker = view.findViewById(R.id.shelfLifePicker);
        quantityPicker = view.findViewById(R.id.quantityPicker);
        itemImage = view.findViewById(R.id.itemImage);
        chooseIconImageView = view.findViewById(R.id.chooseIconImageView);
        saveButton = view.findViewById(R.id.saveButton);
        deleteButton = view.findViewById(R.id.deleteitemButton);
        setupDatePicker(storageDateField);
        setupDatePicker(expirationDateField);
        Spinner storageLocationSpinner = view.findViewById(R.id.storageLocationSpinner);
        NumberPicker quantityPicker = view.findViewById(R.id.quantityPicker);
        NumberPicker shelfLifePicker = view.findViewById(R.id.shelfLifePicker);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.storage_location_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storageLocationSpinner.setAdapter(adapter);
        quantityPicker.setMinValue(1);
        quantityPicker.setMaxValue(100);
        quantityPicker.setValue(1);

        shelfLifePicker.setMinValue(1);
        shelfLifePicker.setMaxValue(365);
        quantityPicker.setMinValue(1);
        quantityPicker.setMaxValue(100);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(getContext());

        // Get data from bundle and populate UI
        Bundle args = getArguments();
        if (getArguments() != null) {
            itemId = args.getInt("item_id", -1);

            itemNameField.setText(getArguments().getString("item_name"));
            storageDateField.setText(getArguments().getString("storage_date"));
            expirationDateField.setText(getArguments().getString("expiration_date"));
            nutritionalInfoField.setText(getArguments().getString("nutritional_info"));
            shelfLifePicker.setValue(getArguments().getInt("shelf_life"));
            quantityPicker.setValue(getArguments().getInt("quantity"));

            Bitmap itemBitmap = getArguments().getParcelable("item_image");
            if (itemBitmap != null) itemImage.setImageBitmap(itemBitmap);

            Bitmap iconBitmap = getArguments().getParcelable("icon_image");
            if (iconBitmap != null) chooseIconImageView.setImageBitmap(iconBitmap);
        }

        if (itemId == -1) {
            Log.e("FridgeDetail", "Invalid item ID passed to Fragment");
            Toast.makeText(getContext(), "Failed to load item details.", Toast.LENGTH_SHORT).show();
        }
            // Handle Save Button Click
        saveButton.setOnClickListener(v -> saveUpdatedData());
        deleteButton.setOnClickListener(v -> deleteItem());
        return view;
    }

    private void setupDatePicker(final EditText dateField) {
        dateField.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                        dateField.setText(date);
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
    }

    public void deleteItem() {
        if (itemId == -1) {
            Toast.makeText(getContext(), "Invalid item ID. Cannot delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isDeleted = databaseHelper.deleteItem(itemId); // Call deleteItem from DatabaseHelper

        if (isDeleted) {
            Toast.makeText(getContext(), "Item deleted successfully!", Toast.LENGTH_SHORT).show();
            // Navigate back to the previous screen or update the UI as needed
            requireActivity().getSupportFragmentManager().popBackStack(); // Go back to the previous fragment
        } else {
            Toast.makeText(getContext(), "Failed to delete item.", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveUpdatedData() {
        // Validate mandatory fields
        if (itemNameField.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Item name cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Collect data from UI elements
        String itemName = itemNameField.getText().toString();
        if (itemName.isEmpty()) {
            showToast("Please enter the item name.");
            return;
        }
        String storageDate = storageDateField.getText().toString();
        String expirationDate = expirationDateField.getText().toString();
        String nutritionalInfo = nutritionalInfoField.getText().toString();
        if (nutritionalInfo.isEmpty()) {
            showToast("Please enter nutritional information.");
            return;
        }
        String storageLocation = storageLocationSpinner.getSelectedItem().toString();
        int shelfLife = shelfLifePicker.getValue();
        int quantity = quantityPicker.getValue();

        // Convert image to byte array
        byte[] itemImageBytes = null;
        if (itemImage.getDrawable() != null) {
            Bitmap bitmap = ((BitmapDrawable) itemImage.getDrawable()).getBitmap();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            itemImageBytes = outputStream.toByteArray();
        }

        byte[] iconImageBytes = null;
        if (chooseIconImageView.getDrawable() != null) {
            Bitmap bitmap = ((BitmapDrawable) chooseIconImageView.getDrawable()).getBitmap();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            iconImageBytes = outputStream.toByteArray();
        }

        // Prepare ContentValues
        ContentValues contentValues = new ContentValues();
        contentValues.put("item_name", itemName);
        contentValues.put("storage_location", storageLocation);
        contentValues.put("quantity", quantity);
        contentValues.put("expiry_date", expirationDate);
        contentValues.put("item_image", itemImageBytes);
        contentValues.put("icon_image", iconImageBytes);
        contentValues.put("shelf_life", shelfLife);
        contentValues.put("nutritionInfo", nutritionalInfo);
        contentValues.put("storage_date", storageDate);

        // Update database
        boolean isUpdated = databaseHelper.updateFridgeItem(itemId, contentValues);

        if (isUpdated) {
            Toast.makeText(getContext(), "Item updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Failed to update item.", Toast.LENGTH_SHORT).show();
        }
    }
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
