package com.sheilajnieto.myshoplistsqlite.db.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.NoSuchAlgorithmException;

public class ShoppingListSQLiteHelper extends SQLiteOpenHelper {
    private static ShoppingListSQLiteHelper sInstance;
    private static final String DB_NAME = "shoppinglist.db";
    private static final int DB_VERSION = 1;

    private static final String sqlCreateTableUsers = "CREATE TABLE users (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "username TEXT NOT NULL);";

    private static final String sqlCreateTableLists = "CREATE TABLE lists (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "list_name TEXT NOT NULL, " +
            "creation_date DATETIME DEFAULT CURRENT_TIMESTAMP); ";

    private static final String sqlCreateTableCategories =
            "CREATE TABLE categories (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "category_name TEXT NOT NULL, " +
                    "category_image_path TEXT);";

    private static final String sqlCreateTableProducts = "CREATE TABLE products (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "product_name TEXT NOT NULL, " +
            "fk_category_id INTEGER NOT NULL, " +
            "product_image_path TEXT, " + // Ruta de la imagen
            "FOREIGN KEY (fk_category_id) REFERENCES categories(id));";

    private static final String sqlCreateTableProductList =
            "CREATE TABLE product_list (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "fk_product_id INTEGER, " +
                    "fk_list_id INTEGER, " +
                    "is_purchased INTEGER DEFAULT 0, " +
                    "FOREIGN KEY (fk_product_id) REFERENCES products(id), " +
                    "FOREIGN KEY (fk_list_id) REFERENCES lists(id));";

        private static final String sqlInsertTableUsers;

        static {
            sqlInsertTableUsers = "INSERT INTO users (username) " +
                                "VALUES ('Sheila');";
        }

    private static final String sqlInsertTableCategories;

        static {
            sqlInsertTableCategories = "INSERT INTO categories (category_name, category_image_path) " +
                    "VALUES " +
                    "('Frutas, verduras, tubérculos', 'vegetablesfruits.jpg'), " +
                    "('Bebidas vegetales', 'vegetablesdrinks.jpg'), " +
                    "('Lácteos', 'milks.jpg'), " +
                    "('Productos de limpieza', 'cleaning.jpg'), " +
                    "('Condimentos', 'condiments.jpg'), " +
                    "('Cereales', 'cereal.jpg'), " +
                    "('Proteínas', 'protein.jpg');";
        }

    private static final String sqlInsertTableProducts;

        static {
            sqlInsertTableProducts = "INSERT INTO products (product_name, fk_category_id, product_image_path) " +
                    "VALUES " +
                    "('Manzana', 1, 'apples.jpg'), " +
                    "('Pepino', 1, 'cucumber.jpg'), " +
                    "('Uvas', 1, 'grapes.jpg'), " +
                    "('Yuka', 1, 'yuka.jpg'), " +
                    "('Patatas', 1, 'potatoes.jpg'), " +
                    "('Boniato', 1, 'sweetpotatoe.jpg'), " +
                    "('Zanahorias', 1, 'carrots.jpg'), " +
                    "('Calabacín', 1, 'courgette.jpg'), " +
                    "('Aguacate', 1, 'avocado.jpg'), " +
                    "('Cebolla', 1, 'onion.jpg'), " +
                    "('Leche de coco', 2, 'coconutmilk.jpg'), " +
                    "('Yogur de coco', 2, 'cocoyoghurt.jpg'), " +
                    "('Yogur queso cabra', 3, 'goatyoghurt.jpg'), " +
                    "('Aceite de oliva', 5, 'oliveoil.jpg'), " +
                    "('Sal', 5, 'salt.jpg'), " +
                    "('Vinagre de manzana', 5, 'vinegar.jpg'), " +
                    "('Orégano', 5, 'oregano.jpg'), " +
                    "('Limpiador de baños', 4, 'bathcleaner.jpg'), " +
                    "('Limpiador vajilla', 4, 'dishcleaner.jpg'), " +
                    "('Servilletas', 4, 'napkins.jpg'), " +
                    "('Toallitas', 4, 'wipes.jpg'), " +
                    "('Arroz Basmati', 6, 'basmatirice.jpg'), " +
                    "('Huevos', 7, 'eggs.jpg');";
        }


    public static synchronized ShoppingListSQLiteHelper getInstance(Context context) {
        if (sInstance == null) {
            // Usamos el contexto de la aplicación para asegurarnos que no se pierde
            // el contexto, por ejemplo de una Activity.
            sInstance = new ShoppingListSQLiteHelper(context.getApplicationContext());
        }
        return  sInstance;
    }

    // Definimos el constructor privado para asegurarnos que no lo utilice nadie desde fuera
    // Así forzamos a utilizar getInstance()
    private ShoppingListSQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Este método sólo se ejecuta si la base de datos no existe
        db.execSQL(sqlCreateTableUsers);
        db.execSQL(sqlCreateTableLists);
        db.execSQL(sqlCreateTableProducts);
        db.execSQL(sqlCreateTableCategories);
        db.execSQL(sqlCreateTableProductList);

        // Creamos un usuario por defecto
         db.execSQL(sqlInsertTableCategories);
         db.execSQL(sqlInsertTableProducts);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Aquí irán las sentencias de actualización de la base de datos
    }
}
