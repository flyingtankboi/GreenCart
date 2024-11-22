package com.example.navbar;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Locale;
import java.util.Vector;


//Classe générale utilisée pour stocker les produits
class Item {
    private int id;
    private boolean isBought;

    public Item(int id, boolean isBought) {
        this.id = id;
        this.isBought = isBought;
    }

    //Récupération du nom français ou anglais dans la base de données en fonction du langage du téléphone
    public String getName(SQLiteDatabase database) {
        String sqlQuery;
        if (Locale.getDefault().getLanguage().equals("fr")) {
            sqlQuery = "SELECT name_fr FROM items WHERE id = " + String.valueOf(id);
        }
        else{
            sqlQuery = "SELECT name FROM items WHERE id = " + String.valueOf(id);
        }
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{});
        cursor.moveToFirst();
        try {
            String res = cursor.getString(0);
            cursor.close();
            return res;
        }
        catch (Exception e){ //On écrit Not Found si le produit n'est pas ou plus dans la base de données
            cursor.close();
            return "Not Found";
        }
    }

    public int getId() {
        return id;
    }

    public boolean isBought() {
        return isBought;
    }

    public void setBought(boolean bought) {
        isBought = bought;
    }
}