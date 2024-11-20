package com.example.navbar;

import java.io.Serializable;
import java.util.Vector;

public class RecetteCard implements Serializable {
    String titre;
    Vector<String> ingredients;

    public RecetteCard(String titre,Vector<String> ingredients){
        this.titre = titre;
        this.ingredients = ingredients;
    }

    public RecetteCard(String titre){
        this.titre = titre;
        ingredients = new Vector<>();
    }

    public void addIngredient(String ingredient){
        ingredients.add(ingredient);
    }
    public Vector<String> getIngredients(){
        return this.ingredients;
    }
    public String getTitre(){
        return this.titre;
    }

}
