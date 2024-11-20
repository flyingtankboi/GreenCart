package com.example.navbar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "items.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        // Le contexte de l'application, le nom de la base de données, et la version
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crée la table items
        String createTable = "CREATE TABLE IF NOT EXISTS items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL UNIQUE);";
        db.execSQL(createTable);

        // Insère des données
        String insertData = "INSERT INTO items (name) VALUES " +
                "('Android'), " +
                "('Kotlin'), " +
                "('Java'), " +
                "('SQLite');";
        db.execSQL(insertData);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Supprime la table si elle existe et recrée la table
        db.execSQL("DROP TABLE IF EXISTS items");
        onCreate(db);
    }
}

