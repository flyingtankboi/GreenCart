package com.example.navbar;


import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Vector;

public class FicheRecetteAdapter extends RecyclerView.Adapter<FicheRecetteAdapter.ViewHolder> {

    private Vector<String> ingredients_string;

    public FicheRecetteAdapter(SQLiteDatabase db, Vector<Item> ingredients) {
        ingredients_string = new Vector<>();
        for(int i = 0; i < ingredients.size(); i++){
            this.ingredients_string.add(ingredients.get(i).getName(db));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.liste_ingredients, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.i("debug","ajout d'un ingredient");
        holder.textViewIngredient.setText(ingredients_string.get(position));
        holder.textViewIngredient.setTextColor(Color.parseColor("#000000"));

    }


    public int getItemCount() {
        return ingredients_string.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewIngredient;

        ViewHolder(View view) {
            super(view);
            textViewIngredient = view.findViewById(R.id.text_view_ingredient);
        }
    }
}
