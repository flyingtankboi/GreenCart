package com.example.navbar;


import static com.example.navbar.Liste.db;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;


import java.util.Vector;

public class FicheRecetteAdapter extends RecyclerView.Adapter<FicheRecetteAdapter.ViewHolder> {

    private Vector<Item> ingredients;

    public FicheRecetteAdapter(Vector<Item> ingredients) {
        this.ingredients = ingredients;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.liste_ingredients, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.i("debug","ajout d'un ingredient");
        holder.textViewIngredient.setText(ingredients.get(position).getName(db));
        holder.textViewIngredient.setTextColor(Color.parseColor("#000000"));

    }


    public int getItemCount() {
        return ingredients.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewIngredient;

        ViewHolder(View view) {
            super(view);
            textViewIngredient = view.findViewById(R.id.text_view_ingredient);
        }
    }
}
