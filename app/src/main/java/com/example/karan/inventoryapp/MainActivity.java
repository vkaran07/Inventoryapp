package com.example.karan.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ListView List;
    private LoaderManager loaderManager;
    private Button s_button;
    private TextView textView;
    private InventoryCursorAdapter mAdapter;
    private InventoryDbHElper Dbhelper;
    private String Log_tag = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v(Log_tag, "onCreate");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, addoredit.class);
                startActivity(intent);
            }
        });
        textView = (TextView) findViewById(R.id.empty);
        List = (ListView) findViewById(R.id.list);
        mAdapter = new InventoryCursorAdapter(this, null);
        List.setAdapter(mAdapter);
        s_button = (Button) findViewById(R.id.sale_item);
        Dbhelper = new InventoryDbHElper(this);
        List.setEmptyView(textView);

        loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(0, null, this);
        List.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.v(Log_tag, "Item clicked");
                Button bt = (Button) view.findViewById(R.id.sale_item);
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v(Log_tag, "send");
                    }
                });
                Intent edi = new Intent(MainActivity.this, addoredit.class);
                Uri uri = ContentUris.withAppendedId(Uri.withAppendedPath(InventoryContract.item_uri, InventoryContract.content), id);
                Log.v("MainActivity : ", uri.toString());
                edi.setData(uri);
                startActivity(edi);
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        textView.setVisibility(View.GONE);
        String[] proj = {
                InventoryContract.InventoryEntry.id,
                InventoryContract.InventoryEntry.name,
                InventoryContract.InventoryEntry.quantity,
                InventoryContract.InventoryEntry.price,
                InventoryContract.InventoryEntry.s_name,
                InventoryContract.InventoryEntry.s_phone,
                InventoryContract.InventoryEntry.s_email,
                InventoryContract.InventoryEntry.i_url
        };
        Log.v(Log_tag, "onCreateLoader: ");
        return new CursorLoader(getApplicationContext(),
                Uri.withAppendedPath(InventoryContract.item_uri, InventoryContract.content), proj,
                null,
                null,
                null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.inserta:
                insertItem();
                return true;
            case R.id.deleteall:
                DeleteAll();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void DeleteAll() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("do you want to delete all items?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                int r = getContentResolver().delete(Uri.withAppendedPath(InventoryContract.item_uri, InventoryContract.content), null, null);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int id) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.i_menu, m);
        return true;
    }


    private void insertItem() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryContract.InventoryEntry.name, "Item1");
        contentValues.put(InventoryContract.InventoryEntry.price, 100);
        contentValues.put(InventoryContract.InventoryEntry.s_name, "Supplier");
        contentValues.put(InventoryContract.InventoryEntry.s_phone, 987654321);
        contentValues.put(InventoryContract.InventoryEntry.s_email, "xyz@12.com");

        Drawable d = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            d = getDrawable(R.drawable.noimagefound);
        }

        BitmapDrawable bd = ((BitmapDrawable) d);
        Bitmap bitmap = bd.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] image = stream.toByteArray();

        contentValues.put(InventoryContract.InventoryEntry.i_url, image);

        Uri uri = getContentResolver().insert(Uri.withAppendedPath(InventoryContract.item_uri,
                InventoryContract.content), contentValues);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(Log_tag, "onLoadFinished :");
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);

    }
}
