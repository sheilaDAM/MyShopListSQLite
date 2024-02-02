package com.sheilajnieto.myshoplistsqlite.db.sqlite.dao;/*
@author sheila j. nieto 
@version 0.1 2024 -01 - 10
*/

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.sheilajnieto.myshoplistsqlite.DateTimeHelper;
import com.sheilajnieto.myshoplistsqlite.interfaces.DAO;
import com.sheilajnieto.myshoplistsqlite.models.Category;
import com.sheilajnieto.myshoplistsqlite.models.ListClass;
import com.sheilajnieto.myshoplistsqlite.models.Product;
import com.sheilajnieto.myshoplistsqlite.models.ProductList;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListDAO extends DAO<ListClass>  {


    private static final String TABLE_NAME = "lists";
    private final SQLiteDatabase db;
    private final Map<String, Integer> columnIndex;
    private Context contextMain;

    public ListDAO(SQLiteDatabase db, Context context) {
        super(TABLE_NAME);
        this.db = db;
        this.contextMain = context;
        columnIndex = new HashMap<>();
        fillColumnIndex();
    }

    private void fillColumnIndex() {
        try (Cursor c = db.rawQuery("SELECT * FROM " + tableName, null)) {
            String [] columnNames = c.getColumnNames();
            for (int i = 0; i < columnNames.length; i++) {
                columnIndex.put(columnNames[i], i);
            }
        }
    }

    @Override
    public List<ListClass> findAll() {
        List<ListClass> lists = new ArrayList<>();
        try (Cursor c = db.query(tableName, null, null, null, null, null, null)) {
            if (c.moveToFirst()) {
                do {
                    int id = c.getInt(columnIndex.get("id"));
                    String listName = c.getString(columnIndex.get("list_name"));
                    String dateString = c.getString(columnIndex.get("creation_date"));

                    Date creationDate = DateTimeHelper.parseDateString(dateString);

                    lists.add(new ListClass(id, listName, creationDate));
                } while (c.moveToNext());
            }
        }
        return lists;
    }

    @Override
    public ListClass findById(int id) {
        String[] selectionArgs = {String.valueOf(id)};
        try (Cursor c = db.query(tableName, null, "id = ?",  selectionArgs, null, null, null)) {
            if (c.moveToFirst()) {
                String listName = c.getString(columnIndex.get("list_name"));
                String dateString = c.getString(columnIndex.get("creation_date"));

                // Convertimos la cadena de fecha a un objeto Date
                Date creationDate = DateTimeHelper.parseDateString(dateString);

                return new ListClass(id, listName, creationDate);
            }
        }
        return null;
    }


    @Override
    public List<ListClass> findBy(Map<String, String> condition) {
        String[] selectionArgs = new String[condition.keySet().size()];
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (String key : condition.keySet()) {
            if (sb.length() == 0) {
                sb.append(key).append("=? ");
            } else {
                sb.append("AND ").append(key).append(" =? ");
            }
            selectionArgs[count++] = condition.get(key);
        }
        List<ListClass> lists = new ArrayList<>();
        try (Cursor c = db.query(tableName, null, sb.toString(),  selectionArgs, null, null, null)) {
            if (c.moveToFirst()) {
                do {
                    int id = c.getInt(columnIndex.get("id"));
                    String listName = c.getString(columnIndex.get("list_name"));
                    String dateString = c.getString(columnIndex.get("creation_date"));

                    Date creationDate = DateTimeHelper.parseDateString(dateString);

                    lists.add(new ListClass(id, listName, creationDate));
                } while (c.moveToNext());
            }
        }
        return lists;
    }

    @Override
    public boolean update(ListClass e) {
        String[] args = new String[] {String.valueOf(e.getId())};
        ContentValues values = new ContentValues();
        values.put("list_name", e.getName());
        return db.update(tableName, values, "id=?", args) == 1;
    }

    @Override
    public boolean insert(ListClass e) {
        ContentValues values = new ContentValues();
        values.put("list_name", e.getName());
        String currentDateTime = DateTimeHelper.getCurrentDateTime();
        values.put("creation_date", currentDateTime);

        // El método insert devuelve el identificador de la fila insertada o
        // -1 en caso de que se haya producido un error
        return db.insert(tableName,null, values) != -1;
    }

    @Override
    public boolean delete(ListClass e) {
        String[] args = new String[] {String.valueOf(e.getId())};
        return db.delete(tableName, "id=?", args) == 1;
    }

    public int countProductsFromShoppingList(int listId) {
        String[] selectionArgs = {String.valueOf(listId)};
        try (Cursor c = db.rawQuery("SELECT COUNT(id) FROM product_list WHERE fk_list_id = ?", selectionArgs)) {
            if (c.moveToFirst()) {
                return c.getInt(0);  // El resultado de COUNT(*) estará en la primera columna
            }
        }
        return 0;  // Si hay un error o no se encuentran productos, devolvemos 0
    }

    public List<Product> getProductsFromShoppingList(int listId) {
        List<Product> products = new ArrayList<>();
        String[] selectionArgs = {String.valueOf(listId)};
        try (Cursor c = db.rawQuery("SELECT p.* FROM products p JOIN product_list pl ON p.id = pl.fk_product_id WHERE pl.fk_list_id = ?" , selectionArgs)) {
            if (c.moveToFirst()) {
                do {
                    int id = c.getInt(columnIndex.get("id"));
                    String productName = c.getString(columnIndex.get("product_name"));
                    int categoryName = c.getInt(columnIndex.get("fk_category_id"));
                    String productImagePath = c.getString(columnIndex.get("product_image_path"));
                    int isPurchasedInt = c.getInt(columnIndex.get("is_purchased"));
                    Boolean isPurchased;
                    if(isPurchasedInt == 1) {
                        isPurchased = true;
                    }
                    else{
                        isPurchased = false;
                    }

                    String imageNameWithoutExtension = productImagePath.replace(".jpg", "");
                    int drawableID = contextMain.getResources().getIdentifier(imageNameWithoutExtension, "drawable", contextMain.getPackageName());

                    //verificamos que se encontró el recurso (que existe la imagen en drawable)
                    if (drawableID != 0) {
                        // Cargar la imagen como Bitmap
                        Bitmap imageBitmap = BitmapFactory.decodeResource(contextMain.getResources(), drawableID);
                        products.add(new Product(id, productName, categoryName, isPurchased, imageBitmap));
                    }

                } while (c.moveToNext());
            }
        }
        return products;  // Si hay un error o no se encuentran productos, devolvemos 0
    }

    public boolean hasProducts(int listId) {
        String query = "SELECT COUNT(pl.fk_product_id) FROM product_list pl " +
                "JOIN lists l ON l.id = pl.fk_list_id " +
                "WHERE pl.fk_list_id = ?";

        String[] selectionArgs = {String.valueOf(listId)};

        try (Cursor c = db.rawQuery(query, selectionArgs)) {
            if (c.moveToFirst()) {
                int productCount = c.getInt(0);
                return productCount > 0;
            }
        }

        return false;
    }

}
