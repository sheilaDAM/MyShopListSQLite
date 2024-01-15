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
import com.sheilajnieto.myshoplistsqlite.models.Category;
import com.sheilajnieto.myshoplistsqlite.models.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryDAO extends DAO<Category> {

    private static final String TABLE_NAME = "categories";
    private final SQLiteDatabase db;
    private final Map<String, Integer> columnIndex;
    private Context context;

    public CategoryDAO(SQLiteDatabase db) {
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
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        try (Cursor c = db.query(tableName, null, null, null, null, null, null)) {
            if (c.moveToFirst()) {
                do {
                    int id = c.getInt(columnIndex.get("id"));
                    String categoryName = c.getString(columnIndex.get("category_name"));
                    String imageName = c.getString(columnIndex.get("category_image_path"));

                    // Construimos la ruta de la imagen desde el nombre
                    String imagePath = "android.resource://" + context.getPackageName() + "/drawable/" + imageName;

                    // Cargar la imagen como Bitmap
                    Bitmap imageBitmap = loadImageFromPath(imagePath);

                    categories.add(new Category(id, categoryName, imageBitmap));
                } while (c.moveToNext());
            }
        }
        return categories;
    }

    // Método para cargar una imagen desde una ruta (path)
    private Bitmap loadImageFromPath(String path) {
        // Usamos BitmapFactory para cargar la imagen desde la ruta

        Bitmap bitmap = BitmapFactory.decodeFile(path);

        return bitmap;
    }

    @Override
    public Category findById(int id) {
        String[] selectionArgs = {String.valueOf(id)};
        try (Cursor c = db.query(tableName, null, "id = ?",  selectionArgs, null, null, null)) {
            if (c.moveToFirst()) {
                String categoryName = c.getString(columnIndex.get("category_name"));
                String imageName = c.getString(columnIndex.get("category_image_path"));

                // Construimos la ruta de la imagen desde el nombre
                String imagePath = "android.resource://" + context.getPackageName() + "/drawable/" + imageName;

                // Cargar la imagen como Bitmap
                Bitmap imageBitmap = loadImageFromPath(imagePath);
                return new Category(id, categoryName, imageBitmap);
            }
        }
        return null;
    }

    @Override
    public List<Category> findBy(Map<String, String> condition) {
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
        List<Category> categories = new ArrayList<>();
        try (Cursor c = db.query(tableName, null, sb.toString(),  selectionArgs, null, null, null)) {
            if (c.moveToFirst()) {
                do {
                    int id = c.getInt(columnIndex.get("id"));
                    String categoryName = c.getString(columnIndex.get("category_name"));
                    String imageName = c.getString(columnIndex.get("category_image_path"));

                    // Construimos la ruta de la imagen desde el nombre
                    String imagePath = "android.resource://" + context.getPackageName() + "/drawable/" + imageName;

                    // Cargar la imagen como Bitmap
                    Bitmap imageBitmap = loadImageFromPath(imagePath);
                    categories.add(new Category(id, categoryName, imageBitmap));
                } while (c.moveToNext());
            }
        }
        return categories;
    }

    @Override
    public boolean update(Category e) {
        String[] args = new String[] {String.valueOf(e.getId())};
        ContentValues values = new ContentValues();
        values.put("category_name", e.getName());
        values.put("category_image_path", e.getImage().toString());
        return db.update(tableName, values, "id=?", args) == 1;
    }

    @Override
    public boolean insert(Category e) {
        ContentValues values = new ContentValues();
        values.put("category_name", e.getName());
        values.put("category_image_path", e.getImage().toString());
        // El método insert devuelve el identificador de la fila insertada o
        // -1 en caso de que se haya producido un error
        return db.insert(tableName,null, values) != -1;
    }

    @Override
    public boolean delete(Category e) {
        String[] args = new String[] {String.valueOf(e.getId())};
        return db.delete(tableName, "id=?", args) == 1;
    }
}
