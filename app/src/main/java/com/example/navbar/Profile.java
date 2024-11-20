package com.example.navbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Profile extends Fragment {

    final String FILENAME_HISTORIQUE = "historique.json";
    final String DEFAULT_HISTORIQUE = "[]";
    JSONArray historique;

    public Profile() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private String getHistorique(Context context) {

        File list = new File(context.getFilesDir(), FILENAME_HISTORIQUE);
        if (!list.exists()) {
            try (FileOutputStream file = context.openFileOutput(FILENAME_HISTORIQUE, Context.MODE_PRIVATE)) {
                file.write(DEFAULT_HISTORIQUE.getBytes());
            } catch (IOException e) {
                Log.i("error", "N'a pas réussi à ecrire dans le fichier par defaut");
            }
            return DEFAULT_HISTORIQUE;
        } else {
            String string_du_fichier = "";
            try {
                FileInputStream file = context.openFileInput(FILENAME_HISTORIQUE);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext().getApplicationContext(), 1));


        try {
            historique = new JSONArray(getHistorique(getContext().getApplicationContext()));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        List<String> names = new ArrayList<>();
        List<String> dates = new ArrayList<>();

        for (int i = historique.length() - 1; i >= 0; i--) {
            // Récupérer l'élément à l'index i
            JSONObject liste = null;
            try {
                liste = historique.getJSONObject(i);
                names.add(liste.getString("name"));
                dates.add(liste.getString("date"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        CardAdapter adapter = new CardAdapter(names, dates, historique, requireActivity());
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Log.i("error", getHistorique(getContext().getApplicationContext()));
                deleteItemFromJSON(getContext().getApplicationContext(),position);
                Log.i("error", getHistorique(getContext().getApplicationContext()));
                names.remove(position);
                dates.remove(position);
                adapter.notifyItemRemoved(position);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);



        return view;
    }


    private void deleteItemFromJSON(Context context, int index){
        File list = new File(context.getFilesDir(), FILENAME_HISTORIQUE);

        String string_du_fichier;
        try {
            FileInputStream file_input = context.openFileInput(FILENAME_HISTORIQUE);
            int size = file_input.available();
            byte[] buffer = new byte[size];
            file_input.read(buffer);
            file_input.close();
            string_du_fichier = new String(buffer, "UTF-8");
            JSONArray historique = new JSONArray(string_du_fichier);
            historique.remove(historique.length() - 1 - index);

            FileOutputStream file_output = context.openFileOutput(FILENAME_HISTORIQUE, Context.MODE_PRIVATE);
            file_output.write(historique.toString().getBytes());

        } catch (IOException e) {
            Log.i("error", "N'a pas réussi à écrire dans le fichier par defaut");
        } catch (JSONException e) {
            Log.i("error", "Historique n'est pas un JSONArray");
        }
    }
}