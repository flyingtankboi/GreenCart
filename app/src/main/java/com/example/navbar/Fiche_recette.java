package com.example.navbar;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fiche_recette#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fiche_recette extends Fragment {


    private static RecetteCard fiche_recette;
    FicheRecetteAdapter adapter;
    int position;
    final static String FILENAME = "recipes.json";

    public Fiche_recette() {
        // Required empty public constructor
    }


    public static Fiche_recette newInstance(RecetteCard recetteCard,int position) {
        Fiche_recette fragment = new Fiche_recette();
        Bundle args = new Bundle();
        args.putSerializable("arg_recipe", recetteCard);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fiche_recette = (RecetteCard) getArguments().getSerializable("arg_recipe");
            position = getArguments().getInt("position");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_fiche_recette, container, false);
        TextView titre_recette_tv = view.findViewById(R.id.titre_recette);
        titre_recette_tv.setText(fiche_recette.getTitre());


        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new FicheRecetteAdapter(fiche_recette.getIngredients());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));




        View delete_view = view.findViewById(R.id.imageView);
        delete_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONArray recettes  = null;
                try {
                    recettes = new JSONArray(Recette.readJson(getActivity().getApplicationContext()));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                recettes.remove(position);
                FileOutputStream file = null;
                try {
                    file = getContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                try {
                    file.write(recettes.toString().getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                getParentFragmentManager().popBackStack();

            }
        });
        return view;

    }



    public void supprime_recette(View view) throws JSONException, IOException {


    }
}