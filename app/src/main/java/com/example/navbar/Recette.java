package com.example.navbar;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Recette extends Fragment {

    public Recette() {
        // Required empty public constructor
    }

    RecetteAdapter adapter;
    Vector<RecetteCard> liste_recettes;
    static final String FILENAME = "recipes.json";
    static final String DEFAULT_JSON = "[{\"title\" : \"Lasagnes\",\n" +
            "                       \"ingredients\" : [\"Pomme\", \"Poire\"],\n\"instructions\" : \"Faire des lasagnes\"}]\n"
            ;


    public static String readJson(Context context) {

        File list = new File(context.getFilesDir(), FILENAME);
        if (!list.exists()) {
            try (FileOutputStream file = context.openFileOutput(FILENAME, Context.MODE_PRIVATE)) {
                file.write(DEFAULT_JSON.getBytes());
            } catch (IOException e) {
                Log.i("error", "N'a pas réussi à ecrire dans le fichier par defaut");
            }
            return DEFAULT_JSON;
        } else {
            String string_du_fichier = "";
            try {
                FileInputStream file = context.openFileInput(FILENAME);
                int size = file.available();
                byte[] buffer = new byte[size];
                file.read(buffer);
                file.close();
                string_du_fichier = new String(buffer, "UTF-8");


            } catch (IOException e) {
                Log.i("error", "N'a pas réussi à écrire dans le fichier par defaut");
            }
            return string_du_fichier;
        }
    }

    private void writeJson(Context context, String donnee) {
        try (FileOutputStream file = context.openFileOutput(FILENAME, Context.MODE_PRIVATE)) {
            file.write(donnee.getBytes());
        } catch (IOException e) {
            Log.i("error", "N'a pas réussi à ecrire dans le fichier par defaut");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        liste_recettes = new Vector<RecetteCard>();

        JSONArray recettes = null;
        try {
            recettes = new JSONArray(readJson(getActivity().getApplicationContext()));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < recettes.length(); i++) {
            JSONObject recette = null;
            try {
                recette = recettes.getJSONObject(i);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            JSONArray ingredients_json;
            try {
                ingredients_json = recette.getJSONArray("ingredients");
                Vector<String> ingredients_vector = new Vector<>();

                for (int ing = 0; ing < ingredients_json.length(); ing++) {

                    ingredients_vector.add(ingredients_json.get(ing).toString());

                }
                String instructions = recette.getString("instructions");
                liste_recettes.add(new RecetteCard(recette.getString("title"), ingredients_vector, instructions));
            } catch (JSONException ex) {
                throw new RuntimeException(ex);
            }


        }


        View view = inflater.inflate(R.layout.fragment_recette, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new RecetteAdapter(liste_recettes, getParentFragmentManager());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));


        FloatingActionButton recette_button = view.findViewById(R.id.nouvelle_recette);
        recette_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openRecipeCreator();
            }
        });



        return view;

    }


    private void openRecipeCreator() {
        try {
            JSONArray recettes = new JSONArray(readJson(getActivity().getApplicationContext()));
            JSONObject newRecette = new JSONObject();
            newRecette.put("title", "Nouvelle Recette");
            newRecette.put("ingredients", new JSONArray());
            newRecette.put("instructions", "");
            recettes.put(newRecette);
            FileOutputStream file = getContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
            file.write(recettes.toString().getBytes());
            liste_recettes.add(new RecetteCard("Nouvelle Recette"));
            adapter.notifyItemInserted(liste_recettes.size() - 1);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Fiche_recette fragment = Fiche_recette.newInstance(new RecetteCard("Nouvelle Recette"),liste_recettes.size()-1);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

        transaction.replace(R.id.frameLayout, fragment);

        transaction.addToBackStack(null);

        transaction.commit();
    }

}