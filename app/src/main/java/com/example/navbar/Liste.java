package com.example.navbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Vector;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Liste extends Fragment {
    final String FILENAME = "current_list.json";
    final String FILENAME_HISTORIQUE = "historique.json";
    final String DEFAULT_JSON = "{\"name\":\"\",\"date\":\"\",\"items\":[{\"id\":172,\"isbought\":false},{\"id\":270,\"isbought\":false},{\"id\":632,\"isbought\":true}]}";
    Vector<Item> items;
    androidx.appcompat.widget.SearchView searchView;
    RecyclerView recyclerView;
    RecyclerView researchRecyclerView;
    ResearchAdapter researchAdapter;
    TextView compteurView;

    JSONObject liste;
    Vector<Item> shoppingList;
    static ShoppingListAdapter adapter;
    static SQLiteDatabase db;

    public Liste() {
    }

    public static int getID(String name){
        String sqlQuery;
        if (Locale.getDefault().getLanguage().equals("fr")) {
            sqlQuery = "SELECT id FROM items WHERE name_fr = ?";
        }
        else{
            sqlQuery = "SELECT id FROM items WHERE name = ?";
        }
        Cursor cursor = db.rawQuery(sqlQuery, new String[]{"%" + String.valueOf(name) + "%"});
        cursor.moveToFirst();
        try {
            int res = cursor.getInt(0);
            cursor.close();
            return res;
        }
        catch (Exception e) {
            cursor.close();
            return 0;
        }
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
        JSONArray items = liste_de_course.getJSONArray("items");
        for(int i = 0; i < items.length(); i++){
            items.getJSONObject(i).put("isbought", false);
        }

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
                int _placeholder = file_input.read(buffer);
                file_input.close();
                string_du_fichier = new String(buffer, StandardCharsets.UTF_8);
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
                int _placeholer = file.read(buffer);
                file.close();
                string_du_fichier = new String(buffer, StandardCharsets.UTF_8);


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
        View view = inflater.inflate(R.layout.fragment_liste, container, false);

        FloatingActionButton share_button = view.findViewById(R.id.share_button);

        //Création d'un OnClickListener pour le bouton de partage
        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Utilisation d'un intent implicit SEND
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                StringBuilder liste_de_course = new StringBuilder(getString(R.string.voice_ma_liste_de_courses) + "\n");
                boolean temp1 = true;
                boolean temp2 = true;

                //Création de la string qui sera envoyée
                for(int i=0; i<shoppingList.size(); i++){
                    if (shoppingList.get(i).isBought()){
                        if (temp1){
                            liste_de_course.append(getString(R.string.ce_que_j_ai_d_j_achet)).append("\n");
                            temp1=false;
                        }
                        liste_de_course.append("✅ ");
                    }
                    else{
                        if (temp2){
                            liste_de_course.append(getString(R.string.ce_qu_il_reste_acheter)).append("\n");
                            temp2=false;
                        }
                        liste_de_course.append("❌ ");
                    }
                    liste_de_course.append(shoppingList.get(i).getName(db)).append("\n");
                }
                intent.putExtra(Intent.EXTRA_TEXT, liste_de_course.toString());

                //Create chooser permet de choisir l'application souhaitée et la personne à qui l'envoyer
                startActivity(Intent.createChooser(intent, "Envoyer à"));
            }
        });

        shoppingList = new Vector<>();

        adapter = new ShoppingListAdapter(shoppingList,db);

        compteurView = view.findViewById(R.id.compteur);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext().getApplicationContext()));
        recyclerView.setAdapter(adapter);


        researchRecyclerView = view.findViewById(R.id.researchRecyclerView);
        researchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        databaseHelper.copyDatabase();
        db = databaseHelper.openDatabase();

        searchView = view.findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                    if (!newText.isEmpty()) {
                        researchRecyclerView.setVisibility(View.VISIBLE);
                        loadSuggestions(db, newText);
                    } else {
                        closeSuggestions();
                    }
                return true;
            }

        });

        researchAdapter = new ResearchAdapter(getContext(), items, shoppingList, adapter, searchView, requireActivity(), db);
        researchRecyclerView.setAdapter(researchAdapter);

        closeSuggestions();

        FloatingActionButton reset_button = view.findViewById(R.id.nouvelle_recette);

        reset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    compteurView.setVisibility(View.GONE);
                    updateJSONList();
                    writeJson(requireContext().getApplicationContext(), liste.toString());
                    writeHistorique(requireContext().getApplicationContext(), liste);
                    Toast.makeText(view.getContext().getApplicationContext(), getString(R.string.sauvegarde_boutton), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                shoppingList.clear();
                adapter.notifyDataSetChanged();
            }
        });

        super.onCreate(savedInstanceState);

        try {
            liste = new JSONObject(readJson(requireActivity().getApplicationContext()));
            JSONArray items = liste.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                try {
                    shoppingList.add(new Item(item.getInt("id"), item.getBoolean("isbought")));
                }
                catch (Exception e){
                    shoppingList.add(new Item(0, false));
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        ItemTouchHelper itemTouchHelper = getItemTouchHelper();
        itemTouchHelper.attachToRecyclerView(recyclerView);

        int[] progress = getProgress();
        String stringCompteur = progress[0] + "/" + progress[1];
        compteurView.setText(stringCompteur);

        if (progress[1] == 0){
            compteurView.setVisibility(View.GONE);
        }

        return view;

    }

    @NonNull
    private ItemTouchHelper getItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    Paint paint = new Paint();

                    if (dX > 0) {
                        Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_check);
                        assert icon != null;
                        int iconMargin = (viewHolder.itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                        int iconLeft = viewHolder.itemView.getLeft() + iconMargin;
                        int iconRight = iconLeft + icon.getIntrinsicWidth();
                        int iconTop = viewHolder.itemView.getTop() + iconMargin;
                        int iconBottom = iconTop + icon.getIntrinsicHeight();
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                        LinearGradient gradient = new LinearGradient(
                                viewHolder.itemView.getLeft(),
                                viewHolder.itemView.getTop(),
                                dX,
                                viewHolder.itemView.getBottom(),
                                Color.parseColor("#91c9a2"), // Start color (green)
                                Color.parseColor("#FCF5FD"),                 // End color (white)
                                Shader.TileMode.CLAMP
                        );
                        paint.setShader(gradient);
                        c.drawRect((float) viewHolder.itemView.getLeft() + 2, (float) viewHolder.itemView.getTop()+2, dX, (float) viewHolder.itemView.getBottom()-2, paint);

                        icon.setTint(Color.parseColor("#FCF5FD"));
                        icon.draw(c);

                    } else if (dX < 0) {
                        Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete);
                        assert icon != null;
                        int iconMargin = (viewHolder.itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                        int iconRight = viewHolder.itemView.getRight() - iconMargin;
                        int iconLeft = iconRight - icon.getIntrinsicWidth();
                        int iconTop = viewHolder.itemView.getTop() + iconMargin;
                        int iconBottom = iconTop + icon.getIntrinsicHeight();
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                        LinearGradient gradient = new LinearGradient(
                                viewHolder.itemView.getRight(),
                                viewHolder.itemView.getTop(),
                                viewHolder.itemView.getRight() + dX, // Start at the edge and move left
                                viewHolder.itemView.getBottom(),
                                 // Start color (white)
                                Color.parseColor("#9c5959"),
                                Color.parseColor("#FCF5FD"),// End color (red)
                                Shader.TileMode.CLAMP
                        );
                        paint.setShader(gradient);
                        c.drawRect((float) viewHolder.itemView.getRight() + dX, (float) viewHolder.itemView.getTop() + 2, (float) viewHolder.itemView.getRight() - 2, (float) viewHolder.itemView.getBottom() - 2, paint);

                        icon.setTint(Color.parseColor("#FCF5FD"));
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

                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemInserted(newPosition);
                } else {
                    shoppingList.remove(position);
                    adapter.notifyItemRemoved(position);
                }


                int[] progress = getProgress();
                String stringCompteur = progress[0] + "/" + progress[1];
                compteurView.setText(stringCompteur);
                if(progress[0] == 0){
                    String s = getString(R.string.les_courses_sont_finies);
                    Toast.makeText(requireContext().getApplicationContext(),s , Toast.LENGTH_SHORT).show();
                }

                if (shoppingList.isEmpty()){
                    compteurView.setVisibility(View.GONE);
                }


            }
        };

        return new ItemTouchHelper(simpleCallback);
    }

    private void closeSuggestions() {
        researchRecyclerView.setVisibility(View.GONE);
    }

    //Charge les suggestions de produits basé sur ce que l'utilisateur est en train d'écrire
    public void loadSuggestions(SQLiteDatabase database, String query) {
        Vector<Item> filtereditems = new Vector<>();

        filtereditems.add(new Item(1025, true)); //L'item 1025 est un item artificiel intitulé "+ Ajouter produit"
        String sqlQuery;
        //Distinction en fonction de la langue de l'utilisateur
        if (Locale.getDefault().getLanguage().equals("fr")) {
            sqlQuery = "SELECT id FROM items WHERE name_fr LIKE ? LIMIT 20";
        }
        else{
            sqlQuery = "SELECT id FROM items WHERE name LIKE ? LIMIT 20";
        }
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{"%" + query + "%"});

        if (cursor.moveToFirst()) {
            do {
                int itemID = cursor.getInt(0);
                if (itemID != 1025)
                    filtereditems.add(new Item(itemID, false));
            } while (cursor.moveToNext());
        }
        cursor.close();
        researchAdapter.setFilteredList(filtereditems);
    }

    public void updateJSONList() {
        // Copie de la shoppinglist dans un fichier JSON pour le stocker
        JSONArray items_array = new JSONArray();
        try {
            for (Item item : shoppingList) {
                JSONObject item_json = new JSONObject();

                item_json.put("id", item.getId());
                item_json.put("isbought", item.isBought());

                items_array.put(item_json);
            }
            liste.put("items", items_array);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public int[] getProgress() {
        int a = 0;
        for (Item item : shoppingList) {
            if (!item.isBought()) {
                a++;
            }
        }
        return new int[]{a, shoppingList.size()};
    }

    @Override
    public void onPause() {
        updateJSONList();
        writeJson(requireContext().getApplicationContext(), liste.toString());
        super.onPause();
    }
}