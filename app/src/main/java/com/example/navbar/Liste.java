package com.example.navbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Vector;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Liste extends Fragment {

    final String FILENAME = "current_list.json";
    final String FILENAME_HISTORIQUE = "historique.json";
    final String DEFAULT_JSON = "{\n" +
            "  \"name\": \"\",\n" +
            "  \"date\": \"\",\n" +
            "  \"items\": [{\"name\" : \"Pomme\", \"isbought\":false}]\n" +
            "}";

    JSONObject liste;
    Vector<Item> shoppingList;
    ShoppingListAdapter adapter;

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

    private void writeHistorique(Context context, JSONObject liste_de_course) throws JSONException {
        File list = new File(context.getFilesDir(), FILENAME_HISTORIQUE);

        LocalDateTime today = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String date = today.format(formatter);

        liste_de_course.put("date", date);
        liste_de_course.put("name", "liste");

        if (!list.exists()) { // Le fichier n'existe pas
            try (FileOutputStream file = context.openFileOutput(FILENAME_HISTORIQUE, Context.MODE_PRIVATE)) {

                JSONArray historique = new JSONArray();
                historique.put(liste_de_course);
                file.write(historique.toString().getBytes());
            } catch (IOException e) {
                Log.i("error", "N'a pas réussi à ecrire dans le fichier par defaut");
            }

        } else { // Le fichier existe
            String string_du_fichier;
            try {
                FileInputStream file_input = context.openFileInput(FILENAME_HISTORIQUE);
                int size = file_input.available();
                byte[] buffer = new byte[size];
                file_input.read(buffer);
                file_input.close();
                string_du_fichier = new String(buffer, "UTF-8");
                JSONArray historique = new JSONArray(string_du_fichier);
                historique.put(liste_de_course);

                FileOutputStream file_output = context.openFileOutput(FILENAME_HISTORIQUE, Context.MODE_PRIVATE);
                file_output.write(historique.toString().getBytes());

            } catch (IOException e) {
                Log.i("error", "N'a pas réussi à écrire dans le fichier par defaut");
            } catch (JSONException e) {
                Log.i("error", "Historique n'est pas un JSONArray");
            }
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

        FloatingActionButton debug_button = view.findViewById(R.id.debug_button);
        debug_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random r = new Random();
                shoppingList.add(new Item(String.valueOf(r.nextInt(100)), false));
                adapter.notifyItemChanged(shoppingList.size() - 1);

            }
        });
        FloatingActionButton reset_button = view.findViewById(R.id.reset_button);
        reset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    updateJSONList();
                    writeJson(getContext().getApplicationContext(), liste.toString());
                    writeHistorique(getContext().getApplicationContext(), liste);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                shoppingList.clear();
                adapter.notifyDataSetChanged();
            }
        });


        super.onCreate(savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        shoppingList = new Vector<Item>();

        try {
            liste = new JSONObject(readJson(getActivity().getApplicationContext()));
            JSONArray items = liste.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                shoppingList.add(new Item(item.getString("name"), item.getBoolean("isbought")));
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

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    Paint paint = new Paint();

                    if (dX > 0) {
                        Drawable icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_check);
                        int iconMargin = (viewHolder.itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                        int iconLeft = viewHolder.itemView.getLeft() + iconMargin;
                        int iconRight = iconLeft + icon.getIntrinsicWidth();
                        int iconTop = viewHolder.itemView.getTop() + iconMargin;
                        int iconBottom = iconTop + icon.getIntrinsicHeight();
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                        paint.setColor(Color.parseColor("#91c9a2"));
                        c.drawRect((float) viewHolder.itemView.getLeft(), (float) viewHolder.itemView.getTop(), dX, (float) viewHolder.itemView.getBottom(), paint);

                        icon.draw(c);

                    } else if (dX < 0) {
                        Drawable icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_delete);
                        int iconMargin = (viewHolder.itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                        int iconRight = viewHolder.itemView.getRight() - iconMargin;
                        int iconLeft = iconRight - icon.getIntrinsicWidth();
                        int iconTop = viewHolder.itemView.getTop() + iconMargin;
                        int iconBottom = iconTop + icon.getIntrinsicHeight();
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                        paint.setColor(Color.parseColor("#9c5959"));
                        c.drawRect((float) viewHolder.itemView.getRight() + dX, (float) viewHolder.itemView.getTop(), (float) viewHolder.itemView.getRight(), (float) viewHolder.itemView.getBottom(), paint);

                        icon.draw(c);

                    } else {
                        super.onChildDraw(c, recyclerView, viewHolder, 0, 0, actionState, isCurrentlyActive);
                    }
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }


            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.RIGHT) {
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
                } else {
                    shoppingList.remove(position);
                    adapter.notifyItemRemoved(position);
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        //permet de rajouter un fonds de couleur derriere les boutons quand on commence à les swipe


        return view;
    }

    public void updateJSONList() {
        // Copy shopping list to the jsonOBJECT
        JSONArray items_array = new JSONArray();
        try {
            for (Item item : shoppingList) {
                JSONObject item_json = new JSONObject();

                item_json.put("name", item.getName());
                item_json.put("isbought", item.isBought());

                items_array.put(item_json);
            }
            liste.put("items", items_array);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onPause() {
        updateJSONList();
        writeJson(getContext().getApplicationContext(), liste.toString());
        super.onPause();
    }


}