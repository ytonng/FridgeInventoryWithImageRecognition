package com.example.imagerec;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.VectorDrawable;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;


public class ItemDetailFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST_CODE = 2;

    private ImageView itemImage;
    private EditText storageDate;
    private EditText expiryDate;
    private EditText itemNameField;
    private EditText nutritionalInfo;
    private ImageView chooseIconImageView;
    private Button addIconButton;
    private Button uploadImageButton;
    private Button detectexpirydate;
    private com.google.mlkit.vision.text.TextRecognizer textRecognizer;

    // Define icons to show in the picker
    private List<Integer> icons = new ArrayList<>();

    // Initialize icons
    private void initIcons() {
        icons.add(R.drawable.banana);
        icons.add(R.drawable.burger);
        icons.add(R.drawable.rice);
        icons.add(R.drawable.ramen);
        icons.add(R.drawable.fries);
        icons.add(R.drawable.carrot);
        icons.add(R.drawable.pumpkin);
        icons.add(R.drawable.brocolli);
        icons.add(R.drawable.chocolate);
        icons.add(R.drawable.watermelon);
        icons.add(R.drawable.hotdog);
        icons.add(R.drawable.mushroom);
        icons.add(R.drawable.lemon);
        icons.add(R.drawable.bittergourd);
        icons.add(R.drawable.durian);
        icons.add(R.drawable.egg);
        icons.add(R.drawable.pizza);
        icons.add(R.drawable.peas);
        icons.add(R.drawable.corn);
        icons.add(R.drawable.cannedfood);
        icons.add(R.drawable.apple);
    }

    // Show icon picker dialog
    private void showIconPicker() {
        GridView gridView = new GridView(getContext());
        gridView.setNumColumns(5);
        gridView.setAdapter(createIconAdapter());

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Choose an Icon")
                .setView(gridView)
                .create();

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            int selectedIcon = icons.get(position);
            chooseIconImageView.setImageResource(selectedIcon);
            dialog.dismiss();
        });

        dialog.show();
    }

    // Adapter for icons in the GridView
    private BaseAdapter createIconAdapter() {
        return new BaseAdapter() {
            @Override
            public int getCount() {
                return icons.size();
            }

            @Override
            public Object getItem(int position) {
                return icons.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ImageView imageView = new ImageView(getContext());
                imageView.setImageResource(icons.get(position));
                imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                return imageView;
            }
        };
    }

    private void saveItem() {
        // Retrieve the email from SharedPreferences
        SharedPreferences preferences = getActivity().getSharedPreferences("userSession", getContext().MODE_PRIVATE);
        String userEmail = preferences.getString("email", "defaultEmail@example.com");

        // Retrieve and handle item fields
        String itemName = itemNameField.getText().toString().trim();
        if (itemName.isEmpty()) {
            showToast("Please enter the item name.");
            return;
        }
        String storageLocation = ((Spinner) getView().findViewById(R.id.storageLocationSpinner)).getSelectedItem() != null ?
                ((Spinner) getView().findViewById(R.id.storageLocationSpinner)).getSelectedItem().toString() : "N/A";

        int quantity = ((NumberPicker) getView().findViewById(R.id.quantityPicker)).getValue();
        String expiryDateText = expiryDate.getText().toString().trim();
        if (expiryDateText.isEmpty()) {
            showToast("Please select an expiry date.");
            return;
        }
        String shelfLife = ((NumberPicker) getView().findViewById(R.id.shelfLifePicker)).getValue() > 0 ?
                ((NumberPicker) getView().findViewById(R.id.shelfLifePicker)).getValue() + " days" : "N/A";
        String nutritionInfo = nutritionalInfo.getText().toString().trim();
        if (nutritionInfo.isEmpty()) {
            showToast("Please enter nutritional information.");
            return;
        }
        String storageDateText = storageDate.getText().toString().isEmpty() ? "N/A" : storageDate.getText().toString();

        // Convert item image to byte array
        Drawable drawable = itemImage.getDrawable();
        Bitmap itemBitmap;

        if (drawable instanceof BitmapDrawable) {
            itemBitmap = ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable) {
            itemBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(itemBitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } else {
            itemBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // A default 1x1 bitmap
        }

        ByteArrayOutputStream itemImageStream = new ByteArrayOutputStream();
        itemBitmap.compress(Bitmap.CompressFormat.PNG, 100, itemImageStream);
        byte[] itemImageByteArray = itemImageStream.toByteArray();

        // Convert icon image to byte array
        Drawable iconDrawable = chooseIconImageView.getDrawable();
        Bitmap iconBitmap;

        if (iconDrawable instanceof BitmapDrawable) {
            iconBitmap = ((BitmapDrawable) iconDrawable).getBitmap();
        } else if (iconDrawable instanceof VectorDrawable) {
            iconBitmap = Bitmap.createBitmap(iconDrawable.getIntrinsicWidth(), iconDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas iconCanvas = new Canvas(iconBitmap);
            iconDrawable.setBounds(0, 0, iconCanvas.getWidth(), iconCanvas.getHeight());
            iconDrawable.draw(iconCanvas);
        } else {
            iconBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Default 1x1 bitmap
        }

        ByteArrayOutputStream iconImageStream = new ByteArrayOutputStream();
        iconBitmap.compress(Bitmap.CompressFormat.PNG, 100, iconImageStream);
        byte[] iconImageByteArray = iconImageStream.toByteArray();

        // Insert item into the database
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        long result = dbHelper.insertItem(userEmail, itemName, storageLocation, quantity, expiryDateText,
                itemImageByteArray, iconImageByteArray, shelfLife, nutritionInfo, storageDateText);

        if (result != -1) {
            Toast.makeText(getContext(), "Item saved successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Failed to save item", Toast.LENGTH_SHORT).show();
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_detail, container, false);

        itemNameField = view.findViewById(R.id.itemnameField);
        itemImage = view.findViewById(R.id.itemImage);
        storageDate = view.findViewById(R.id.storageDate);
        nutritionalInfo = view.findViewById(R.id.nutritionalInfo);
        expiryDate = view.findViewById(R.id.expirationDate);
        chooseIconImageView = view.findViewById(R.id.chooseIconImageView);
        addIconButton = view.findViewById(R.id.addiconButton);
        uploadImageButton = view.findViewById(R.id.uploadImageButton);
        detectexpirydate = view.findViewById(R.id.detectexpirydate);
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        Button saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            saveItem();  // Save the item to the database
        });

        initIcons();
        setCurrentDate();
        setupDatePicker(storageDate);
        setupDatePicker(expiryDate);

        uploadImageButton.setOnClickListener(v -> openImageChooserForDisplay());
        detectexpirydate.setOnClickListener(v -> openImageChooserForExpiryDetection());
        addIconButton.setOnClickListener(v -> showIconPicker());

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

        if (getArguments() != null) {
            String itemName = getArguments().getString("item_name");
            Bitmap itemBitmap = getArguments().getParcelable("item_image");
            String nutrients = getArguments().getString("nutrients");
            String shelfLife = getArguments().getString("shelf_life"); // Shelf life from classifyAndNavigate

            itemNameField.setText(itemName);
            if (itemBitmap != null) {
                itemImage.setImageBitmap(itemBitmap);
            }
            nutritionalInfo.setText(nutrients);
            // Set shelf life picker value if detected
            try {
                int shelfLifeValue = Integer.parseInt(shelfLife.replaceAll("[^\\d]", ""));
                shelfLifePicker.setValue(shelfLifeValue);
            } catch (NumberFormatException e) {
                shelfLifePicker.setValue(1); // Default value in case of error
            }
        }

        return view;
    }

    private void setCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        String currentDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
        storageDate.setText(currentDate);
    }

    private void openImageChooserForDisplay() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openImageChooserForExpiryDetection() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            if (requestCode == PICK_IMAGE_REQUEST) {
                itemImage.setImageURI(imageUri);
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                processImage(imageUri);
            }
        }
    }

    private void processImage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            recognizeText(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recognizeText(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        textRecognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text visionText) {
                        extractExpiryDate(visionText);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Text recognition failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void extractExpiryDate(Text visionText) {
        StringBuilder resultText = new StringBuilder();
        String datePattern = "(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}|\\d{4}[/-]\\d{1,2}[/-]\\d{1,2})";
        Pattern pattern = Pattern.compile(datePattern);

        for (Text.TextBlock block : visionText.getTextBlocks()) {
            resultText.append(block.getText()).append("\n");
        }

        Matcher matcher = pattern.matcher(resultText);
        if (matcher.find()) {
            String expiryDateText = matcher.group();
            expiryDate.setText(expiryDateText);
        } else {
            Toast.makeText(getContext(), "Expiry date not detected", Toast.LENGTH_SHORT).show();
        }
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
}
