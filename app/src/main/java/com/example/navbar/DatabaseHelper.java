package com.example.navbar;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "items.db"; // Nom de la base de données
    private static final int DATABASE_VERSION = 1; // Version (modifiez si vous mettez à jour le schéma)
    private final Context context;
    private final String databasePath;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.databasePath = context.getDatabasePath(DATABASE_NAME).getPath();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Rien ici, car nous utilisons une base de données préexistante
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Gérez les mises à jour si nécessaire
        Log.d("DatabaseHelper", "Database version updated from " + oldVersion + " to " + newVersion);
        deleteDatabase();
        copyDatabase();
    }

    /**
     * Vérifie si la base de données existe déjà dans le répertoire interne
     */
    private boolean checkDatabaseExists() {
        File dbFile = new File(databasePath);
        return dbFile.exists();
    }

    /**
     * Copie la base de données depuis le dossier assets vers le répertoire interne
     */
    public void copyDatabase() {
        if (!checkDatabaseExists()) {
            try {
                InputStream inputStream = context.getAssets().open(DATABASE_NAME); // Ouvre la base de données dans assets
                File databaseFile = new File(databasePath);

                // Crée le répertoire parent si nécessaire
                boolean _placeholder = Objects.requireNonNull(databaseFile.getParentFile()).mkdirs();

                OutputStream outputStream = new FileOutputStream(databaseFile);

                // Copie les données
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                // Ferme les flux
                outputStream.flush();
                outputStream.close();
                inputStream.close();

                Log.d("DatabaseHelper", "Database copied successfully.");
            } catch (Exception e) {
                Log.e("DatabaseHelper", "Error copying database: " + e.getMessage());
            }
        } else {
            Log.d("DatabaseHelper", "Database already exists. No need to copy.");
        }
    }

    public SQLiteDatabase openDatabase() {
        return SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void deleteDatabase() {
        File dbFile = new File(databasePath);
        if (dbFile.exists()) {
            boolean _placeholder = dbFile.delete();
            Log.d("DatabaseHelper", "Old database deleted.");
        }
    }

    public void addItem(String itemName) {
        SQLiteDatabase db = this.getWritableDatabase(); // Ouvre la base de données en écriture
        ContentValues values = new ContentValues();
        values.put("name", itemName); // Ajoute une valeur pour la colonne "name"

        long result = db.insert("items", null, values); // Insère les données dans la table "items"
        if (result == -1) {
            Log.e("DatabaseHelper", "Failed to insert item: " + itemName);
        } else {
            Log.d("DatabaseHelper", "Item inserted successfully: " + itemName);
        }

        db.close();
    }


}
