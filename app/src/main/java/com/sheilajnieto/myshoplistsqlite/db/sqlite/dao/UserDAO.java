package com.sheilajnieto.myshoplistsqlite.db.sqlite.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sheilajnieto.myshoplistsqlite.interfaces.DAO;
import com.sheilajnieto.myshoplistsqlite.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO extends DAO<User> {
    private static final String TABLE_NAME = "users";
    private final SQLiteDatabase db;
    private final Map<String, Integer> columnIndex;

    public UserDAO(SQLiteDatabase db) {
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
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Cursor c = db.query(tableName, null, null, null, null, null, null)) {
            if (c.moveToFirst()) {
                do {
                    int id = c.getInt(columnIndex.get("id"));
                    String username = c.getString(columnIndex.get("username"));
                    users.add(new User(id, username));
                } while (c.moveToNext());
            }
        }
        return users;
    }

    @Override
    public User findById(int id) {
        String[] selectionArgs = {String.valueOf(id)};
        try (Cursor c = db.query(tableName, null, "id = ?",  selectionArgs, null, null, null)) {
            if (c.moveToFirst()) {
                String username = c.getString(columnIndex.get("username"));
                return new User(id, username);
            }
        }
        return null;
    }

    @Override
    public List<User> findBy(Map<String, String> condition) {
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
        List<User> users = new ArrayList<>();
        try (Cursor c = db.query(tableName, null, sb.toString(),  selectionArgs, null, null, null)) {
            if (c.moveToFirst()) {
                do {
                    int id = c.getInt(columnIndex.get("id"));
                    String username = c.getString(columnIndex.get("username"));
                    users.add(new User(id, username));
                } while (c.moveToNext());
            }
        }
        return users;
    }

    @Override
    public boolean update(User e) {
        String[] args = new String[] {String.valueOf(e.getId())};
        ContentValues values = new ContentValues();
        values.put("username", e.getName());
        return db.update(tableName, values, "id=?", args) == 1;
    }

    @Override
    public boolean insert(User e) {
        ContentValues values = new ContentValues();
        values.put("username", e.getName());
        // El método insert devuelve el identificador de la fila insertada o
        // -1 en caso de que se haya producido un error
        return db.insert(tableName,null, values) != -1;
    }

    @Override
    public boolean delete(User e) {
        String[] args = new String[] {String.valueOf(e.getId())};
        return db.delete(tableName, "id=?", args) == 1;
    }
}
