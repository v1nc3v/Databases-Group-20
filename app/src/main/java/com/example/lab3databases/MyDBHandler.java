package com.example.lab3databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class MyDBHandler extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "products";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PRODUCT_NAME = "name";
    private static final String COLUMN_PRODUCT_PRICE = "price";
    private static final String DATABASE_NAME = "products.db";
    private static final int DATABASE_VERSION = 1;

    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_table_cmd = "CREATE TABLE " + TABLE_NAME +
                "(" + COLUMN_ID + "INTEGER PRIMARY KEY, " +
                COLUMN_PRODUCT_NAME + " TEXT, " +
                COLUMN_PRODUCT_PRICE + " DOUBLE " + ")";

        db.execSQL(create_table_cmd);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query, null); // returns "cursor" all products from the table
    }

    public void addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_PRODUCT_NAME, product.getProductName());
        values.put(COLUMN_PRODUCT_PRICE, product.getProductPrice());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // This method finds and returns a list of matching products in the database
    public ArrayList<Product> findProduct(Product product) {
        ArrayList<Product> products = new ArrayList<>();
        String name = product.getProductName();
        double price = product.getProductPrice();
        Cursor cursor = getData();

        String productName;
        double productPrice;

        // If price was not entered, show products that start with the name
        if (price == -1) {
            if (cursor.moveToFirst()) {
                do {
                    productName = cursor.getString(1);
                    productPrice = cursor.getDouble(2);

                    // Check if product in database starts with the name inputted (not case sensitive)
                    if (productName.toUpperCase().startsWith(name.toUpperCase())) {
                        products.add(new Product(productName, productPrice));
                    }
                }
                while (cursor.moveToNext());
            }
        }

        // If name was not entered, show products that have the exact price
        else if (product.getProductName().equals("")){
            if (cursor.moveToFirst()) {
                do {
                    productName = cursor.getString(1);
                    productPrice = cursor.getDouble(2);

                    // Check if product in database has a matching price
                    if (productPrice == price) {
                        products.add(new Product(productName, productPrice));
                    }
                }
                while (cursor.moveToNext());
            }
        }

        // If both values were entered, show products that start with the name and have the exact price
        else {
            if (cursor.moveToFirst()) {
                do {
                    productName = cursor.getString(1);
                    productPrice = cursor.getDouble(2);

                    // Check if product in database starts with the name and has a matching price
                    if (productName.toUpperCase().startsWith(name.toUpperCase()) && productPrice == price) {
                        products.add(new Product(productName, productPrice));
                    }
                }
                while (cursor.moveToNext());
            }
        }

        cursor.close();
        return products;
    }

    public void deleteProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        String name = product.getProductName();

        // Delete any products in the database with the exact same name (is case sensitive)
        db.delete(TABLE_NAME, COLUMN_PRODUCT_NAME + "=?", new String[]{name});
        db.close();
    }
}
