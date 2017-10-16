package com.example.karan.inventoryapp;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by karan on 4/22/2017.
 */

public final class InventoryContract {

    public static final String table_name = "items";
    public static final String authority = "com.example.karan.inventoryapp";
    public static final String content = "items";
    public static final String item_id = "items/#";
    public static final Uri item_uri = Uri.parse("content://" + authority);

    public static final class InventoryEntry implements BaseColumns {

        public static final Uri Content_Authority = Uri.withAppendedPath(item_uri, content);
        public static final String list_type = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + authority;
        public static final String item_type = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + authority;

        public static final String id = BaseColumns._ID;
        public static final String name = "name";
        public static final String quantity = "quantity";
        public static final String price = "price";
        public static final String s_name = "supplierName";
        public static final String s_phone = "supplierPhone";
        public static final String s_email = "supplierEmail";
        public static final String i_url = "image";

    }
}