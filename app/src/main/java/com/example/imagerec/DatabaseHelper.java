package com.example.imagerec;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database name
    public static final String databaseName = "imagerec.db";

    // Dishes table and its columns
    public static final String DISHES_TABLE = "dishes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_INGREDIENTS = "ingredients";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_VIDEO_URL = "video_url";
    public static final String COLUMN_IMAGE_URL ="image_url" ;

    public DatabaseHelper(@Nullable Context context) {
        super(context, databaseName, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        db.execSQL("CREATE TABLE users (email TEXT PRIMARY KEY, password TEXT)");

        // Create dishes table with an image URL column
        db.execSQL("CREATE TABLE dishes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "ingredients TEXT, " +
                "description TEXT, " +
                "video_url TEXT, " +
                "image_url TEXT" +  // Add column for image URL
                ")");
        // Create items table
        db.execSQL("CREATE TABLE items (" +
                "item_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_email TEXT, " +  // Foreign key to the user
                "item_name TEXT, " +
                "storage_location TEXT, " +
                "quantity INTEGER, " +
                "expiry_date TEXT, " + // Expiry date for the item
                "item_image BLOB, " +  // Image uploaded by the user
                "icon_image BLOB, " +  // Icon image for the item
                "shelf_life TEXT, " +  // Shelf life (days or date)
                "nutritionInfo TEXT, " +  // Nutritional information
                "storage_date TEXT, " +  // Date the item was stored
                "FOREIGN KEY(user_email) REFERENCES users(email)" +  // Ensure referential integrity
                ")");

        // Check if dishes table is empty, and insert sample dishes if it is
        if (isDishesTableEmpty(db)) {
            insertSampleDishes(db);
        }
    }

    // Method to check if the dishes table is empty
    private boolean isDishesTableEmpty(SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + DISHES_TABLE, null);
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            return count == 0; // Return true if table is empty
        } finally {
            if (cursor != null) cursor.close();
        }
    }
    // Insert sample dishes data into the database
    private void insertSampleDishes(SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();

        // Dish 1: Spaghetti Carbonara
        contentValues.put(COLUMN_NAME, "Spaghetti Carbonara");
        contentValues.put(COLUMN_INGREDIENTS, "Spaghetti, Eggs, Bacon, Parmesan cheese");
        contentValues.put(COLUMN_DESCRIPTION, "Boil spaghetti, cook bacon, mix with eggs and cheese.");
        contentValues.put(COLUMN_VIDEO_URL, "https://youtu.be/3AAdKl1UYZs");
        contentValues.put(COLUMN_IMAGE_URL, "https://images.services.kitchenstories.io/0jDEDt2yRmfbZPbpRXG3a2ATGpk=/1080x0/filters:quality(85)/images.kitchenstories.io/wagtailOriginalImages/R2568-photo-final-_0.jpg");  // Add image URL
        db.insert(DISHES_TABLE, null, contentValues);

        // Dish 2: Chicken Alfredo
        contentValues.clear();
        contentValues.put(COLUMN_NAME, "Chicken Alfredo");
        contentValues.put(COLUMN_INGREDIENTS, "Chicken, Alfredo sauce, Fettuccine");
        contentValues.put(COLUMN_DESCRIPTION, "Cook chicken, mix with Alfredo sauce and pasta.");
        contentValues.put(COLUMN_VIDEO_URL, "https://youtu.be/F7CU0qBdj04");
        contentValues.put(COLUMN_IMAGE_URL, "https://allrecipes.com/thmb/d9KfT0VVhehtF8GMl3t9Em9iuBs=/750x0/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/6627608-c37d68c85eef4b07b55db54339921b00.jpg");  // Corrected image URL
        db.insert(DISHES_TABLE, null, contentValues);

        // Dish 3: Caesar Salad
        contentValues.clear();
        contentValues.put(COLUMN_NAME, "Caesar Salad");
        contentValues.put(COLUMN_INGREDIENTS, "Lettuce, Croutons, Caesar dressing, Parmesan cheese");
        contentValues.put(COLUMN_DESCRIPTION, "Toss lettuce with croutons, dressing, and cheese.");
        contentValues.put(COLUMN_VIDEO_URL, "https://youtu.be/7Mi8DmbAE74");
        contentValues.put(COLUMN_IMAGE_URL, "https://www.allrecipes.com/thmb/GKJL13Wb8TZ9hpJ9c70v0aNXsyQ=/750x0/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/229063-Classic-Restaurant-Caesar-Salad-ddmfs-4x3-231-89bafa5e54dd4a8c933cf2a5f9f12a6f.jpg");  // Add image URL
        db.insert(DISHES_TABLE, null, contentValues);

        // Continue with the rest of your dishes...
        // Dish 9: Mushroom Risotto
        contentValues.clear();
        contentValues.put(COLUMN_NAME, "Mushroom Risotto");
        contentValues.put(COLUMN_INGREDIENTS, "Rice, Mushrooms, Chicken broth, Parmesan cheese");
        contentValues.put(COLUMN_DESCRIPTION, "Cook rice with mushrooms and chicken broth.");
        contentValues.put(COLUMN_VIDEO_URL, "https://youtu.be/ju9H1RlYNxk");
        contentValues.put(COLUMN_IMAGE_URL, "https://www.eatingwell.com/thmb/Mc1Yo_NWBctQAoYv72NEd2KijRs=/750x0/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/mushroom-risotto-beauty-8025316-4000x4000-203a642728ca49c895b487d6df0dc6e3.jpg");  // Add image URL
        db.insert(DISHES_TABLE, null, contentValues);

        contentValues.clear();
        contentValues.put(COLUMN_NAME, "Seafood Risotto");
        contentValues.put(COLUMN_INGREDIENTS, "Rice, shrimp, scallops, clams, crabmeat, lobster");
        contentValues.put(COLUMN_DESCRIPTION, "Heat olive oil in a pot, cook shallots until soft, then add Arborio rice and cook briefly. Stir in white wine until absorbed, then gradually add seafood stock, stirring frequently until rice is al dente (about 25 minutes). Remove from heat, mix in cheese, lemon zest, and optional heavy cream. Season with salt and pepper. Serve topped with cooked seafood, garnished with parsley and lemon wedges.");
        contentValues.put(COLUMN_VIDEO_URL, "https://youtu.be/pWOe0a7rxP4");
        contentValues.put(COLUMN_IMAGE_URL, "https://kitchenconfidante.com/wp-content/uploads/2023/03/Seafood-Risotto-kitchenconfidante.com-5127-FEATURED-IMAGE-750x1124.jpg");  // Add image URL
        db.insert(DISHES_TABLE, null, contentValues);

        contentValues.clear();
        contentValues.put(COLUMN_NAME, "Fried Rice");
        contentValues.put(COLUMN_INGREDIENTS, "Rice, Eggs, Garlic, Soy sauce, Oyster sauce, Toasted sesame oil");
        contentValues.put(COLUMN_DESCRIPTION, "Heat oil in a pan, sauté minced garlic until fragrant. Add cooked rice and stir-fry. Push rice to the side, scramble eggs in the pan, then mix with rice. Add soy sauce, oyster sauce, and a drizzle of toasted sesame oil. Stir well and cook for another minute. Serve hot.");
        contentValues.put(COLUMN_VIDEO_URL, "https://youtu.be/zZNhVv7fmSE");
        contentValues.put(COLUMN_IMAGE_URL, "https://www.gimmesomeoven.com/wp-content/uploads/2017/07/How-To-Make-Fried-Rice-Recipe-3-1-1100x1650.jpg");  // Add image URL
        db.insert(DISHES_TABLE, null, contentValues);

        contentValues.clear();
        contentValues.put(COLUMN_NAME, "Buttery Seasoned Rice");
        contentValues.put(COLUMN_INGREDIENTS, "Rice, Butter,Garlic powder, Onion powder, Paprika , Thyme ,Parsley ");
        contentValues.put(COLUMN_DESCRIPTION, "Cook rice with mushrooms and chicken broth.");
        contentValues.put(COLUMN_VIDEO_URL, "https://youtu.be/OgS8gvifnRw");
        contentValues.put(COLUMN_IMAGE_URL, "https://www.recipetineats.com/tachyon/2020/03/Seasoned-Rice_3.jpg?resize=900%2C1260&zoom=0.72");  // Add image URL
        db.insert(DISHES_TABLE, null, contentValues);

        contentValues.clear();
        contentValues.put(COLUMN_NAME, "Tom Yum Fried Rice");
        contentValues.put(COLUMN_INGREDIENTS, "Rice, Prawns, Garlic ,Shallot onion, chilli , Tom Yum paste");
        contentValues.put(COLUMN_DESCRIPTION, "Heat oil in a pan, sauté minced garlic, shallot, and chili until fragrant. Add prawns and cook until they turn pink. Stir in Tom Yum paste, then add cooked rice and stir-fry well. Mix everything thoroughly, ensuring the flavors blend. Serve hot, garnished with fresh herbs or lime wedges. Enjoy!");
        contentValues.put(COLUMN_VIDEO_URL, "https://youtu.be/KyyQTVAyWDI");
        contentValues.put(COLUMN_IMAGE_URL, "https://khinskitchen.com/wp-content/uploads/2023/03/tom-yum-fried-rice-02.jpg");  // Add image URL
        db.insert(DISHES_TABLE, null, contentValues);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS " + DISHES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS items"); // Drop items table as well
        onCreate(db);
    }

    public long insertItem(String userEmail, String itemName, String storageLocation, int quantity,
                           String expiryDate, byte[] itemImage, byte[] iconImage, String shelfLife,
                           String nutritionInfo, String storageDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("user_email", userEmail);
        contentValues.put("item_name", itemName);
        contentValues.put("storage_location", storageLocation);
        contentValues.put("quantity", quantity);
        contentValues.put("expiry_date", expiryDate);
        contentValues.put("item_image", itemImage);
        contentValues.put("icon_image", iconImage);
        contentValues.put("shelf_life", shelfLife);
        contentValues.put("nutritionInfo", nutritionInfo); // This should be correct as per the table definition
        contentValues.put("storage_date", storageDate);

        return db.insert("items", null, contentValues);
    }

    public Cursor getItemsForLowFridge(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Fetch items where storage_location is 'Down Fridge' and sort by expiry_date (earliest first)
        return db.rawQuery(
                "SELECT * FROM items WHERE storage_location = ? AND user_email = ? ORDER BY expiry_date ASC",
                new String[]{"Down Fridge", userEmail}
        );
    }

    public Cursor getItemsForUpFridge(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Fetch items where storage_location is 'Down Fridge' and user_email matches
        return db.rawQuery(
                "SELECT * FROM items WHERE storage_location = ? AND user_email = ? ORDER BY expiry_date ASC",
                new String[]{"Up Fridge", userEmail}
        );
    }
    public Cursor getItemsForUserLowFridge(String userEmail, int itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM items WHERE user_email = ? AND item_id = ?";
        return db.rawQuery(query, new String[]{userEmail, String.valueOf(itemId)});
    }

    public boolean updateFridgeItem(int itemId, ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected;

        try {
            // Check if the itemId is valid
            if (itemId <= 0) {
                Log.e("DatabaseHelper", "Invalid itemId: " + itemId);
                return false;
            }

            // Update the database
            rowsAffected = db.update(
                    "items",
                    contentValues,
                    "item_id=?",
                    new String[]{String.valueOf(itemId)}
            );

            Log.d("DatabaseHelper", "Rows affected: " + rowsAffected);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error updating item", e);
            return false;
        } finally {
            db.close();
        }

        return rowsAffected > 0; // Return true if at least one row was updated
    }

    public boolean deleteItem(int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Prepare the query to delete the item with the given item_id
        String whereClause = "item_id = ?";
        String[] whereArgs = new String[]{String.valueOf(itemId)};

        // Execute the delete query and get the number of rows affected
        int rowsDeleted = db.delete("items", whereClause, whereArgs);

        // Return true if at least one row was deleted
        return rowsDeleted > 0;
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM users WHERE email = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        // Log or check if cursor is null
        if (cursor != null) {
            Log.d("DatabaseHelper", "Cursor size: " + cursor.getCount());
        }
        return cursor;
    }

    // Method to update user data (email and password)
    public boolean updateUserData(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("password", password);

        // Update the user record where the email matches
        int result = db.update("users", contentValues, "email = ?", new String[]{email});
        return result > 0;
    }


    // Method to get all dishes from the database
    public Cursor getAllDishes(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();

        // First, check if the user has any items
        Cursor itemCheckCursor = db.rawQuery("SELECT COUNT(*) FROM items WHERE user_email = ?", new String[]{userEmail});
        itemCheckCursor.moveToFirst();
        int itemCount = itemCheckCursor.getInt(0);
        itemCheckCursor.close();

        // If the user has no items, return all dishes
        if (itemCount == 0) {
            return db.rawQuery("SELECT * FROM dishes", null);
        }

        // If the user has items, check for matching dishes
        String query = "SELECT DISTINCT d.* FROM dishes d " +
                "JOIN items i ON d.ingredients LIKE '%' || i.item_name || '%' " +
                "WHERE i.user_email = ?";

        Cursor cursor = db.rawQuery(query, new String[]{userEmail});

        // If no dishes match, return a default set of dishes
        if (cursor.getCount() == 0) {
            // Example default set of dishes:
            return db.rawQuery("SELECT * FROM dishes",null );
        }

        return cursor;
    }

    public Cursor getRandomDishes(int limit) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + DISHES_TABLE + " ORDER BY RANDOM() LIMIT ?", new String[]{String.valueOf(limit)});
    }

    // Method to get a specific dish by its name
    public Cursor getDishByName(String dishName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + DISHES_TABLE + " WHERE " + COLUMN_NAME + " = ?", new String[]{dishName});
    }

    // Method to insert a user
    public Boolean insertData(String email, String password) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("password", password);
        long result = MyDatabase.insert("users", null, contentValues);
        return result != -1;
    }

    // Method to check if email exists
    public Boolean checkEmail(String email) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        return cursor.getCount() > 0;
    }

    // Method to check if email and password match
    public Boolean checkEmailPassword(String email, String password) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, password});
        return cursor.getCount() > 0;
    }
}
