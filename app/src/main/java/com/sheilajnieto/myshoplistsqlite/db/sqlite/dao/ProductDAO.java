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

import com.sheilajnieto.myshoplistsqlite.interfaces.DAO;
import com.sheilajnieto.myshoplistsqlite.models.Product;
import com.sheilajnieto.myshoplistsqlite.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDAO extends DAO<Product> {

    private static final String TABLE_NAME = "products";
    private final SQLiteDatabase db;
    private final Map<String, Integer> columnIndex;
    private Context context;

    public ProductDAO(SQLiteDatabase db) {
        super(TABLE_NAME);
        this.db = db;
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
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        try (Cursor c = db.query(tableName, null, null, null, null, null, null)) {
            if (c.moveToFirst()) {
                do {
                    int id = c.getInt(columnIndex.get("id"));
                    String productName = c.getString(columnIndex.get("product_name"));
                    String productCategory = c.getString(columnIndex.get("fk_category_id"));
                    String imageName = c.getString(columnIndex.get("image"));

                    // Construimos la ruta de la imagen desde el nombre
                    String imagePath = "android.resource://" + context.getPackageName() + "/drawable/" + imageName;

                    // Cargar la imagen como Bitmap
                    Bitmap imageBitmap = loadImageFromPath(imagePath);

                    products.add(new Product(id, productName, productCategory, imageBitmap));
                } while (c.moveToNext());
            }
        }
        return products;
    }

    // Método para cargar una imagen desde una ruta (path)
    private Bitmap loadImageFromPath(String path) {
        // Usamos BitmapFactory para cargar la imagen desde la ruta

        Bitmap bitmap = BitmapFactory.decodeFile(path);

        return bitmap;
    }

    @Override
    public Product findById(int id) {
        String[] selectionArgs = {String.valueOf(id)};
        try (Cursor c = db.query(tableName, null, "id = ?",  selectionArgs, null, null, null)) {
            if (c.moveToFirst()) {
                String productName = c.getString(columnIndex.get("product_name"));
                String productCategory = c.getString(columnIndex.get("fk_category_id"));
                String imageName = c.getString(columnIndex.get("image"));

                // Construimos la ruta de la imagen desde el nombre
                String imagePath = "android.resource://" + context.getPackageName() + "/drawable/" + imageName;

                // Cargar la imagen como Bitmap
                Bitmap imageBitmap = loadImageFromPath(imagePath);
                return new Product(id, productName, productCategory, imageBitmap);
            }
        }
        return null;
    }

    @Override
    public List<Product> findBy(Map<String, String> condition) {
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
        List<Product> products = new ArrayList<>();
        try (Cursor c = db.query(tableName, null, sb.toString(),  selectionArgs, null, null, null)) {
            if (c.moveToFirst()) {
                do {
                    int id = c.getInt(columnIndex.get("id"));
                    String username = c.getString(columnIndex.get("username"));
                    String productName = c.getString(columnIndex.get("product_name"));
                    String productCategory = c.getString(columnIndex.get("fk_category_id"));
                    String imageName = c.getString(columnIndex.get("image"));

                    // Construimos la ruta de la imagen desde el nombre
                    String imagePath = "android.resource://" + context.getPackageName() + "/drawable/" + imageName;

                    // Cargar la imagen como Bitmap
                    Bitmap imageBitmap = loadImageFromPath(imagePath);
                    products.add(new Product(id, productName, productCategory, imageBitmap));
                } while (c.moveToNext());
            }
        }
        return products;
    }

    @Override
    public boolean update(Product e) {
        String[] args = new String[] {String.valueOf(e.getId())};
        ContentValues values = new ContentValues();
        values.put("product_name", e.getName());
        values.put("fk_category_id", e.getCategory());
        values.put("product_image_path", e.getImage().toString());
        return db.update(tableName, values, "id=?", args) == 1;
    }

    @Override
    public boolean insert(Product e) {
        ContentValues values = new ContentValues();
        values.put("product_name", e.getName());
        values.put("fk_category_id", e.getCategory());
        values.put("product_image_path", e.getImage().toString());
        // El método insert devuelve el identificador de la fila insertada o
        // -1 en caso de que se haya producido un error
        return db.insert(tableName,null, values) != -1;
    }

    @Override
    public boolean delete(Product e) {
        String[] args = new String[] {String.valueOf(e.getId())};
        return db.delete(tableName, "id=?", args) == 1;
    }
}
