package com.example.navbar;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecetteAdapter extends RecyclerView.Adapter<RecetteAdapter.ViewHolder> {

    private List<RecetteCard> recettes;

    public RecetteAdapter(List<RecetteCard> recettes) {
        this.recettes = recettes;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recette, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {
        RecetteCard recette = recettes.get(position);
        holder.textViewName.setText(recette.getTitre());
        holder.textViewName.setTextColor(Color.parseColor("#000000"));
        holder.textViewName.setOnClickListener(view -> {
            //TODO
            Toast.makeText(view.getContext(), "TODO fiche recette", Toast.LENGTH_SHORT).show();

        });
    }


    public int getItemCount() {
        return recettes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;


        ViewHolder(View view) {
            super(view);
            textViewName = view.findViewById(R.id.text_view_name);
        }
    }
}
