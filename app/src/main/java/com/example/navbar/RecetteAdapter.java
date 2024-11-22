package com.example.navbar;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecetteAdapter extends RecyclerView.Adapter<RecetteAdapter.ViewHolder> {

    private final List<RecetteCard> recettes;
    private final FragmentManager fragmentManager;

    public RecetteAdapter(List<RecetteCard> recettes, FragmentManager fragmentManager) {
        this.recettes = recettes;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recette, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        RecetteCard recette = recettes.get(position);
        holder.textViewName.setText(recette.getTitre());
        holder.textViewName.setTextColor(Color.parseColor("#000000"));
        holder.cardView.setOnClickListener(view -> {

            Fiche_recette fragment = Fiche_recette.newInstance(recette,position);
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            transaction.replace(R.id.frameLayout, fragment);

            transaction.addToBackStack(null);

            transaction.commit();
        });
    }


    public int getItemCount() {
        return recettes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        CardView cardView;


        ViewHolder(View view) {
            super(view);
            textViewName = view.findViewById(R.id.text_view_name);
            cardView = view.findViewById(R.id.cardview);
        }
    }
}
