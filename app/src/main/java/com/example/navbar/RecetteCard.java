package com.example.navbar;

import java.io.Serializable;
import java.util.Vector;

public class RecetteCard implements Serializable {
    String titre;
    Vector<String> ingredients;
    String instructions;

    public RecetteCard(String titre,Vector<String> ingredients){
        this.titre = titre;
        this.ingredients = ingredients;
        this.instructions = "";
    }
    public RecetteCard(String titre,Vector<String> ingredients,String instructions){
        this.titre = titre;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public RecetteCard(String titre){
        this.titre = titre;
        this.ingredients = new Vector<>();
        this.instructions = "";

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

    public String getInstructions(){
        return this.instructions;
    }

}
