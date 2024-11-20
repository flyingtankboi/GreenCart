package com.example.navbar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fiche_recette#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fiche_recette extends Fragment {


    private static RecetteCard fiche_recette;
    FicheRecetteAdapter adapter;



    public Fiche_recette() {
        // Required empty public constructor
    }


    public static Fiche_recette newInstance(RecetteCard recetteCard) {
        Fiche_recette fragment = new Fiche_recette();
        Bundle args = new Bundle();
        args.putSerializable("arg_recipe", recetteCard);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fiche_recette = (RecetteCard) getArguments().getSerializable("arg_recipe");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_fiche_recette, container, false);
        TextView titre_recette_tv = view.findViewById(R.id.titre_recette);
        titre_recette_tv.setText(fiche_recette.getTitre());


        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new FicheRecetteAdapter(fiche_recette.getIngredients());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));



        return view;
    }
}