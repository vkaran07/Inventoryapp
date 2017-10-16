package com.example.karan.inventoryapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by karan on 4/23/2017.
 */

public class InventoryProvider extends ContentProvider {
    public static final int Item_Code = 1000;
    public static final int Id_Item_Code = 1001;
    public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(InventoryContract.authority, InventoryContract.content, Item_Code);
        uriMatcher.addURI(InventoryContract.authority, InventoryContract.item_id, Id_Item_Code);
    }

    private String log = InventoryProvider.class.getSimpleName();
    private InventoryDbHElper mHelper;

    @Override
    public boolean onCreate() {
        mHelper = new InventoryDbHElper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase sqLiteDatabase = mHelper.getReadableDatabase();
        Cursor cursor;
        int res = uriMatcher.match(uri);
        switch (res) {
            case Item_Code:
                cursor = sqLiteDatabase.query(InventoryContract.table_name, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case Id_Item_Code:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = sqLiteDatabase.query(InventoryContract.table_name, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("cannaot query for uri " + uri + " result is " + res);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int result = uriMatcher.match(uri);
        switch (result) {
            case Item_Code:
                return InventoryContract.InventoryEntry.list_type;
            case Id_Item_Code:
                return InventoryContract.InventoryEntry.item_type;
            default:
                throw new IllegalArgumentException("Invalid uri of result" + result);

        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.e(log, "Insert method");
        String name = values.getAsString(InventoryContract.InventoryEntry.name);
        String price = values.getAsString(InventoryContract.InventoryEntry.price);
        String phone = values.getAsString(InventoryContract.InventoryEntry.s_phone);
        String email = values.getAsString(InventoryContract.InventoryEntry.s_email);
        String s_name = values.getAsString(InventoryContract.InventoryEntry.s_name);

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(s_name) || TextUtils.isEmpty((price)) || TextUtils.isEmpty(phone)) {
            throw new IllegalArgumentException("Incomplete data");
        }
        int result = uriMatcher.match(uri);
        switch (result) {
            case Item_Code:
                return insertItem(uri, values);
            default:
                throw new IllegalArgumentException("failed");
        }
    }

    private Uri insertItem(Uri uri, ContentValues contentValues) {
        SQLiteDatabase sqLiteDatabase = mHelper.getWritableDatabase();
        long id = sqLiteDatabase.insert(InventoryContract.table_name, null, contentValues);
        if (id == -1)
            throw new IllegalArgumentException("failed to insert");
        getContext().getContentResolver().notifyChange(uri, null);


        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = mHelper.getWritableDatabase();
        int result = uriMatcher.match(uri);
        int r;
        switch (result) {
            case Item_Code:
                r = sqLiteDatabase.delete(InventoryContract.table_name, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return r;
            case Id_Item_Code:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                r = sqLiteDatabase.delete(InventoryContract.table_name, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return r;
            default:
                throw new IllegalArgumentException("failed to delete");
        }
    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, String[] selectionArgs) {
        int result = uriMatcher.match(uri);
        switch (result) {
            case Item_Code:
                return updateI(uri, values, selection, selectionArgs);
            case Id_Item_Code:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateI(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("update failed");
        }

    }

    private int updateI(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (values.containsKey(InventoryContract.InventoryEntry.name)) {
            if ((values.getAsString(InventoryContract.InventoryEntry.name)).isEmpty())
                throw new IllegalArgumentException("item name required");
        }
        if (values.containsKey(InventoryContract.InventoryEntry.s_name)) {
            if ((values.getAsString(InventoryContract.InventoryEntry.s_name)).isEmpty())
                throw new IllegalArgumentException("supplier name required");
        }
        if (values.containsKey(InventoryContract.InventoryEntry.s_email)) {
            if ((values.getAsString(InventoryContract.InventoryEntry.s_email)).isEmpty())
                throw new IllegalArgumentException("supplier email required");
        }
        if (values.containsKey(InventoryContract.InventoryEntry.s_phone)) {
            Integer phone = values.getAsInteger(InventoryContract.InventoryEntry.s_phone);
            if (phone == null || phone <= 0)
                throw new IllegalArgumentException("Invalid phoneno ");
        }
        if (values.containsKey(InventoryContract.InventoryEntry.price)) {
            Integer p = values.getAsInteger(InventoryContract.InventoryEntry.price);
            if (p == null || p <= 0)
                throw new IllegalArgumentException("Invalid price");
        }
        SQLiteDatabase sqldatabase = mHelper.getWritableDatabase();
        int rows = sqldatabase.update(InventoryContract.table_name, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }
}
