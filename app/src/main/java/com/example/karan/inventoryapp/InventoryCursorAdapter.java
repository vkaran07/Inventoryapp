package com.example.karan.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by karan on 4/22/2017.
 */
public class InventoryCursorAdapter extends CursorAdapter {
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final int idI = cursor.getColumnIndex(InventoryContract.InventoryEntry._ID);
        int price = cursor.getColumnIndex(InventoryContract.InventoryEntry.price);
        int name = cursor.getColumnIndex(InventoryContract.InventoryEntry.name);
        int quantity = cursor.getColumnIndex(InventoryContract.InventoryEntry.quantity);
        int image = cursor.getColumnIndex(InventoryContract.InventoryEntry.i_url);
        long id = cursor.getLong(idI);
        String name_i = cursor.getString(name);
        int price_i = cursor.getInt(price);
        int quantity_i = cursor.getInt(quantity);
        byte[] image_i = cursor.getBlob(image);

        Bitmap bitmap = BitmapFactory.decodeByteArray(image_i, 0, image_i.length);

        TextView name_t = (TextView) view.findViewById(R.id.item_name);
        TextView price_t = (TextView) view.findViewById(R.id.item_price);
        final TextView quantity_t = (TextView) view.findViewById(R.id.item_quantity);
        ImageView imageView = (ImageView) view.findViewById(R.id.Imageview);
        name_t.setText(name_i);
        price_t.setText(String.valueOf(price_i));
        quantity_t.setText(String.valueOf(quantity_i));
        imageView.setImageBitmap(bitmap);
        Button button = (Button) view.findViewById(R.id.sale_item);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(quantity_t.getText().toString());
                if (quantity > 0) {
                    quantity--;
                    quantity_t.setText(String.valueOf(quantity));
                    ContentValues value = new ContentValues();
                    value.put(InventoryContract.InventoryEntry.quantity, quantity);
                    Uri uri = ContentUris.withAppendedId(Uri.withAppendedPath(InventoryContract.item_uri, InventoryContract.content), idI);
                    context.getContentResolver().update(uri,
                            value,
                            null,
                            null);
                }
            }
        });
    }
}
