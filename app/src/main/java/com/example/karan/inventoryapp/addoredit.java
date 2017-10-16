package com.example.karan.inventoryapp;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

/**
 * Created by karan on 4/22/2017.
 */

public class addoredit extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    static final int Request = 1;
    Uri receive;
    private EditText Mname;
    private EditText Mprice;
    private LoaderManager mloader;
    private Button sub;
    private Button add;
    private InventoryDbHElper Mhelper;
    private EditText Ms_name;
    private EditText Ms_email;
    private EditText Ms_phone;
    private String log_tag;
    private int q_int;
    private Button add_item;
    private Button i_url;
    private boolean i_captured = false;
    private Button order;
    private TextView quantity;
    private byte[] i_byte;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.addoredit);
        log_tag = addoredit.class.getSimpleName();
        Log.v(log_tag, "onCreate");
        Intent intent = getIntent();
        receive = intent.getData();
        Mhelper = new InventoryDbHElper(this);
        i_url = (Button) findViewById(R.id.i_url);
        Ms_name = (EditText) findViewById(R.id.s_name);
        Ms_phone = (EditText) findViewById(R.id.s_phone);
        Ms_email = (EditText) findViewById(R.id.s_email);
        Mname = (EditText) findViewById(R.id.edit);
        quantity = (TextView) findViewById(R.id.q_int);
        Mprice = (EditText) findViewById(R.id.price_i);
        add = (Button) findViewById(R.id.add);
        sub = (Button) findViewById(R.id.sub);
        order = (Button) findViewById(R.id.order_more);
        add_item = (Button) findViewById(R.id.i_edit);
        if (receive == null) {
            setTitle("Add Item");
            invalidateOptionsMenu();
            order.setVisibility(View.GONE);
            add_item.setText("add item");
        } else {
            setTitle("Edit Item");
            order.setVisibility(View.VISIBLE);
            add_item.setText("Update item");
            mloader = getSupportLoaderManager();
            mloader.initLoader(0, null, this);
            i_captured = true;
        }
        i_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                    ;
                Intent pic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (pic.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(pic, Request);
                    Log.v(log_tag, "Image capyured successfully");
                    i_captured = true;
                } else {
                    ActivityCompat.requestPermissions(addoredit.this, new String[]{Manifest.permission.CAMERA}, 123);
                }
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                q_int = Integer.parseInt(quantity.getText().toString());
                q_int = q_int + 1;
                quantity.setText(String.valueOf(q_int));
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] projections = {
                        InventoryContract.InventoryEntry._ID,
                        InventoryContract.InventoryEntry.s_email
                };
                Cursor cursor = getContentResolver().query(receive,
                        projections, null, null, null

                );
                Log.v(log_tag, "order more");
                if (cursor.moveToFirst()) {
                    int emailI = cursor.getColumnIndex(InventoryContract.InventoryEntry.s_email);
                    String email = cursor.getString(emailI);
                    Intent i = new Intent(Intent.ACTION_SENDTO);
                    i.setData(Uri.parse("mailto:" + email.trim()));
                    startActivity(i);
                }
            }
        });
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                q_int = Integer.parseInt(quantity.getText().toString().trim());
                if (q_int > 0) {
                    q_int = q_int - 1;
                    quantity.setText(String.valueOf(q_int));
                }
            }
        });
        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(Mname.getText().toString())) {
                    Mname.setError("Required");
                }
                if (TextUtils.isEmpty(Mprice.getText().toString())) {
                    Mprice.setError("Required");
                }
                if (TextUtils.isEmpty(Ms_name.getText().toString())) {
                    Ms_name.setError("Required");

                }
                if (TextUtils.isEmpty(Ms_phone.getText().toString())) {
                    Ms_phone.setError("Required");
                }
                if (TextUtils.isEmpty(Ms_email.getText().toString())) {
                    Ms_email.setError("Required");
                }
                if (!TextUtils.isEmpty(Mname.getText().toString()) && !TextUtils.isEmpty(Mprice.getText().toString()) && !TextUtils.isEmpty(Mprice.getText().toString()) && !TextUtils.isEmpty(Ms_name.getText().toString()) && !TextUtils.isEmpty(Ms_phone.getText().toString()) && !TextUtils.isEmpty(Ms_email.getText().toString()) && i_captured) {
                    saveItem();
                    Intent in = new Intent(addoredit.this, MainActivity.class);
                    startActivity(in);
                } else {
                    Toast.makeText(addoredit.this, "add an image!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestC, int resultC, Intent data) {
        Log.v(log_tag, "onActivityResult Started");
        if (requestC == Request && resultC == RESULT_OK) {
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            Log.v(log_tag, "Bitmap created");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
                Log.v(log_tag, "image captured");
                i_byte = byteArrayOutputStream.toByteArray();
                i_captured = true;
                Log.v(log_tag, "byte[]");
            }
        }
    }

    @Override
    protected boolean onPrepareOptionsPanel(View v, Menu menu) {
        if (receive == null) {
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void saveItem() {
        String string = Mname.getText().toString().trim();
        int p = 0;
        if (!TextUtils.isEmpty(Mprice.getText().toString().trim()))
            p = Integer.parseInt(Mprice.getText().toString().trim());
        String s_name = Ms_name.getText().toString().trim();
        long su_phone = 0;
        if (!TextUtils.isEmpty(Ms_phone.getText().toString().trim()))
            su_phone = Long.parseLong(Ms_phone.getText().toString().trim());
        String su_email = Ms_email.getText().toString().trim();
        int quan = Integer.parseInt(quantity.getText().toString().trim());
        Log.v(log_tag, string + p + s_name + su_phone + su_email);

        if (TextUtils.isEmpty(string) && TextUtils.isEmpty(s_name) && TextUtils.isEmpty(su_email)) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryContract.InventoryEntry.name, string);
        contentValues.put(InventoryContract.InventoryEntry.price, p);
        contentValues.put(InventoryContract.InventoryEntry.quantity, quan);
        contentValues.put(InventoryContract.InventoryEntry.s_name, s_name);
        contentValues.put(InventoryContract.InventoryEntry.s_phone, su_phone);
        contentValues.put(InventoryContract.InventoryEntry.s_email, su_email);

        if (receive == null) {
            Log.v(log_tag, "null uri");
            contentValues.put(InventoryContract.InventoryEntry.i_url, i_byte);
            Uri uri = getContentResolver().insert(Uri.withAppendedPath(InventoryContract.item_uri, InventoryContract.content), contentValues);
            Log.v(log_tag, "values added");
            Log.v(log_tag, "uri");
            if (uri != null)
                Toast.makeText(this, "New Item added", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        } else {
            Log.v("Addoredit : ", "Update started");
            String[] projection = new String[]{InventoryContract.InventoryEntry.i_url};
            String selection = InventoryContract.InventoryEntry.id + "=?";
            String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(receive))};
            Cursor cursor = getContentResolver().query(receive, projection, null, null, null);
            cursor.moveToFirst();
            int url = cursor.getColumnIndex(InventoryContract.InventoryEntry.i_url);
            byte[] image = cursor.getBlob(url);
            contentValues.put(InventoryContract.InventoryEntry.i_url, image);
            getContentResolver().notifyChange(receive, null);
            int r = getContentResolver().update(receive
                    , contentValues
                    , null
                    , null);
            if (r == 0)
                Toast.makeText(this, "no row updated", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "item updated", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.e_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.delete:
                showDeleteDialogBox();
        }
        return super.onOptionsItemSelected(menuItem);

    }

    private void showDeleteDialogBox() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("do you want to delete item ?");
        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                delete();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertD = alert.create();
        alertD.show();
    }

    private void delete() {
        if (receive != null) {
            int r = getContentResolver().delete(receive, null, null);
            if (r > 0)
                Toast.makeText(this, "item deleted", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "delete unsuccessful", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] proj = new String[]{
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.name,
                InventoryContract.InventoryEntry.quantity,
                InventoryContract.InventoryEntry.price,
                InventoryContract.InventoryEntry.s_name,
                InventoryContract.InventoryEntry.s_phone,
                InventoryContract.InventoryEntry.s_email,
                InventoryContract.InventoryEntry.i_url
        };
        return new CursorLoader(getApplicationContext(),
                receive,
                proj, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            Log.v(log_tag, "onLoadFinished : addoredit ACtivity");
            int nameI = data.getColumnIndex(InventoryContract.InventoryEntry.name);
            int priceI = data.getColumnIndex(InventoryContract.InventoryEntry.price);
            int quantityI = data.getColumnIndex(InventoryContract.InventoryEntry.quantity);
            int s_name = data.getColumnIndex(InventoryContract.InventoryEntry.s_name);
            int s_phone = data.getColumnIndex(InventoryContract.InventoryEntry.s_phone);
            int s_email = data.getColumnIndex(InventoryContract.InventoryEntry.s_email);
            String name = data.getString(nameI);
            String price = data.getString(priceI);
            String quantity_c = data.getString(quantityI);
            String s_n = data.getString(s_name);
            String s_p = data.getString(s_phone);
            String s_e = data.getString(s_email);
            Mname.setText(name);
            Mprice.setText(price);
            Ms_name.setText(s_n);
            Ms_phone.setText(s_p);
            Ms_email.setText(s_e);
            quantity.setText(quantity_c);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Mname.setText(" ");
        Mprice.setText(" ");
        Ms_name.setText(" ");
        Ms_phone.setText(" ");
        Ms_email.setText(" ");
    }
}
