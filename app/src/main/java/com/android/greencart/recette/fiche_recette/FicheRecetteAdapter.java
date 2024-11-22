package com.android.greencart.recette.fiche_recette;


import static com.android.greencart.shoppinglist.Liste.db;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;


import com.android.greencart.utils.Item;
import com.android.greencart.R;
import com.android.greencart.recette.Recette;
import com.android.greencart.recette.RecetteCard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

public class FicheRecetteAdapter extends RecyclerView.Adapter<FicheRecetteAdapter.ViewHolder> {

    Vector<Item> ingredients;
    RecetteCard recette;
    FragmentActivity activity;
    int json_position;
    final static String FILENAME = "recipes.json";


    public FicheRecetteAdapter(RecetteCard recette, FragmentActivity activity, int json_position) {
        this.recette = recette;
        this.ingredients = recette.getIngredients();
        this.activity = activity;
        this.json_position = json_position;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.liste_ingredients, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textViewIngredient.setText(ingredients.get(position).getName(db));
        holder.textViewIngredient.setTextColor(Color.parseColor("#000000"));
        holder.delete_ingredient.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    ingredients.remove(currentPosition);
                    editJSONRecettes(json_position, recette.getTitre(), ingredients, recette.getInstructions());
                    notifyItemRemoved(currentPosition);
                }
            }
        });

    }


    public int getItemCount() {
        return ingredients.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewIngredient;
        ImageView delete_ingredient;

        ViewHolder(View view) {
            super(view);
            textViewIngredient = view.findViewById(R.id.text_view_ingredient);
            delete_ingredient = view.findViewById(R.id.delete_ingredient);
        }
    }

    public void editJSONRecettes(int position, String titre, Vector<Item> ingredients_item, String instructions) {
        try {
            JSONArray recettes = new JSONArray(Recette.readJson(activity.getApplicationContext()));
            recettes.remove(position);

            JSONObject newRecette = new JSONObject();
            newRecette.put("title", titre);

            JSONArray ingredients = new JSONArray();
            for (int i = 0; i < ingredients_item.size(); i++)
                ingredients.put(ingredients_item.elementAt(i).getId());
            newRecette.put("ingredients", ingredients);
            newRecette.put("instructions", instructions);

            recettes.put(position, newRecette);

            FileOutputStream file = activity.getApplicationContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
            file.write(recettes.toString().getBytes());

        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
