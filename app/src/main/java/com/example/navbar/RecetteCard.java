package com.example.navbar;


import java.io.Serializable;
import java.util.Vector;

public class RecetteCard implements Serializable {
    String titre;
    Vector<Item> ingredients;
    String instructions;

    public RecetteCard(String titre,Vector<Item> ingredients){
        this.titre = titre;
        this.ingredients = ingredients;
        this.instructions = "Remplir les instructions...";
    }
    public RecetteCard(String titre,Vector<Item> ingredients,String instructions){
        this.titre = titre;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public RecetteCard(String titre){
        this.titre = titre;
        this.ingredients = new Vector<>();
        this.instructions = "Remplir les instructions...";

    }


    public void addIngredient(Item ingredient){
        ingredients.add(ingredient);
    }
    public void setInstructions(String instructions) {this.instructions = instructions;}
    public void setTitre(String titre) {this.titre = titre;}
    public Vector<Item> getIngredients(){
        return this.ingredients;
    }
    public String getTitre(){
        return this.titre;
    }

    public String getInstructions(){
        return this.instructions;
    }

}
