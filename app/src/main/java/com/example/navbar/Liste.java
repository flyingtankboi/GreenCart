package com.example.navbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.util.Vector;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Liste extends Fragment {

    final String FILENAME = "current_list.json";
    final String DEFAULT_JSON = "{\n" +
            "  \"nom\": \"\",\n" +
            "  \"date\": \"\",\n" +
            "  \"items\": [{\"name\" : \"Pomme\", \"isbought\":false}]\n" +
            "}";

    public Liste() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void resetJson(Context context) {
        File list = new File(context.getFilesDir(), FILENAME);
        try (FileOutputStream file = context.openFileOutput(FILENAME, Context.MODE_PRIVATE)) {
            file.write(DEFAULT_JSON.getBytes());
        } catch (IOException e) {
            Log.i("error", "N'a pas réussi à ecrire dans le fichier par defaut");
        }
    }

    private String readJson(Context context) {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_liste, container, false);

        Vector<Item> shoppingList;
        ShoppingListAdapter adapter;

        super.onCreate(savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        shoppingList = new Vector<Item>();

        try {
            JSONObject liste = new JSONObject(readJson(getActivity().getApplicationContext()));
            JSONArray items = liste.getJSONArray("items");
            for( int i = 0; i< items.length(); i ++){
                JSONObject item = items.getJSONObject(i);
                shoppingList.add(new Item(item.getString("name"),item.getBoolean("isbought")));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }



        adapter = new ShoppingListAdapter(shoppingList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Item item = shoppingList.get(position);
                item.setBought(!item.isBought());
                int newPosition;
                if (item.isBought()) { // Selon si l'article est coché ou non, on l'ajoute au début ou à la fin de la liste.
                    newPosition = shoppingList.size() - 1;
                } else {
                    newPosition = 0;
                }



                shoppingList.remove(position);
                shoppingList.add(newPosition, item);

                adapter.notifyItemMoved(position, newPosition);
                adapter.notifyItemChanged(newPosition);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

}