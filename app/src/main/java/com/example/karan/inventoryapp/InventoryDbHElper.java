package com.example.karan.inventoryapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.karan.inventoryapp.InventoryContract.InventoryEntry;

/**
 * Created by karan on 4/22/2017.
 */
public class InventoryDbHElper extends SQLiteOpenHelper {
    public static final int version = 1;
    public static String name = "inventory.db";

    public InventoryDbHElper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_query = "CREATE TABLE " + InventoryContract.table_name + "(" +
                InventoryEntry.id + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                InventoryEntry.name + " TEXT NOT NULL," +
                InventoryEntry.quantity + " INTEGER DEFAULT 2," +
                InventoryEntry.price + " INTEGER NOT NULL," +
                InventoryEntry.s_name + " TEXT NOT NULL," +
                InventoryEntry.s_phone + " INTEGER NOT NULL," +
                InventoryEntry.s_email + " TEXT NOT NULL," +
                InventoryEntry.i_url + " BLOB);";
        db.execSQL(create_query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
