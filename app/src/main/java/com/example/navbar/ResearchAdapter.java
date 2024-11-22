package com.example.navbar;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Vector;

public class ResearchAdapter extends RecyclerView.Adapter<ResearchViewHolder> {

    Context context;
    Vector<Item> items;
    Vector<Item> shopping_list;
    Vector<String> ingredients_string;
    Vector<Item> ingredients;

    RecetteCard recette;
    ShoppingListAdapter adapter;
    FicheRecetteAdapter recette_adapter;

    DatabaseHelper databaseHelper;
    SearchView searchView;
    FragmentActivity activity;
    int json_position;
    final static String FILENAME = "recipes.json";


    SQLiteDatabase db;


    public ResearchAdapter(Context context, Vector<Item> items, Vector<Item> shopping_list, ShoppingListAdapter adapter, SearchView searchView, FragmentActivity activity, SQLiteDatabase db){

        this.context = context;
        this.items = items;
        this.shopping_list = shopping_list;
        this.adapter = adapter;
        this.searchView = searchView;
        this.activity = activity;
        this.db = db;
    }

    public ResearchAdapter(Context context, Vector<Item> items, RecetteCard recette, FicheRecetteAdapter adapter, SearchView searchView, FragmentActivity activity, SQLiteDatabase db, int json_position){

        this.context = context;
        this.items = items;
        this.recette = recette;
        this.ingredients = recette.getIngredients();
        this.shopping_list = new Vector<>();
        this.recette_adapter = adapter;
        this.searchView = searchView;
        this.activity = activity;
        this.json_position = json_position;
        this.db = db;
    }

    @Override
    public ResearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ResearchViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view_research, parent, false));
    }

    @Override
    public void onBindViewHolder(ResearchViewHolder holder, int position) {
        holder.textView.setText(items.elementAt(position).getName(db));

        //On met un OnClickListener pour récupérer les clicks de l'utilisateur dans la bar de recherche
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int currentPosition = holder.getAdapterPosition();
                if (currentPosition == RecyclerView.NO_POSITION) return;

                Item currentItem = items.elementAt(currentPosition);

                //On vérifie d'abord s'il ne s'agit pas de l'item artificiel d'ajout de produit qui a l'id 1025
                if (currentItem.getId() == 1025) {

                    //On récupère la String écrite dans la barre de recherche
                    String current_text = searchView.getQuery().toString();


                    //On récupère l'id maximal dans la BDD actuel
                    String queryID = "SELECT MAX(id) FROM items";
                    Cursor cursor = db.rawQuery(queryID, new String[]{});
                    cursor.moveToFirst();
                    int id = cursor.getInt(0) + 1;
                    cursor.close();

                    //On insère ensuite la nouvelle entrée dans la base de données
                    ContentValues values = new ContentValues();
                    values.put("id", id);
                    values.put("name", current_text);
                    values.put("name_fr", current_text);
                    values.put("food_group", "Added");
                    values.put("food_subgroup", "Added");

                    db.insert("items", null, values);

                    searchView.setQuery("", false);
                    searchView.clearFocus();

                    if (recette_adapter == null){
                        shopping_list.insertElementAt(new Item(id, false), 0);
                    } else {

                        ingredients.insertElementAt(new Item(id, false), 0);
                        editJSONRecettes(json_position, recette.getTitre(), recette.getIngredients(),recette.getInstructions());
                    }

                } else {
                    //On ajoute juste l'élément à notre shopping liste actuelle
                    searchView.setQuery("", false);
                    searchView.clearFocus();

                    if (recette_adapter == null){
                        shopping_list.insertElementAt(currentItem, 0);
                    } else {
                        ingredients.insertElementAt(currentItem, 0);
                        editJSONRecettes(json_position, recette.getTitre(), recette.getIngredients(),recette.getInstructions());
                    }
                }

                if (recette_adapter == null){
                    adapter.notifyItemInserted(0);

                    int[] progress = getProgress();
                    String stringCompteur = progress[0] + "/" + progress[1];
                    TextView compteur = activity.findViewById(R.id.compteur);
                    compteur.setVisibility(View.VISIBLE);
                    compteur.setText(stringCompteur);
                } else {
                    Log.i("test", String.valueOf(recette.getIngredients().size()));
                    recette_adapter.notifyItemInserted(0);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setFilteredList(Vector<Item> filteredList) {
        this.items = filteredList;
        Log.i("test", String.valueOf(filteredList.size()));
        notifyDataSetChanged();
    }

    public int[] getProgress() {
        int a = 0;
        for (Item item : shopping_list) {
            if (!item.isBought()) {
                a++;
            }
        }
        return new int[]{a, shopping_list.size()};
    }

    public void editJSONRecettes(int position, String titre, Vector<Item> ingredients_item, String instructions) {
        try {
            JSONArray recettes = new JSONArray(Recette.readJson(activity.getApplicationContext()));
            recettes.remove(position);

            JSONObject newRecette = new JSONObject();
            newRecette.put("title", titre);

            JSONArray ingredients = new JSONArray();
            for (int i = 0; i < ingredients_item.size(); i++)
                ingredients.put(ingredients_item.elementAt(i).getId());
            newRecette.put("ingredients", ingredients);
            newRecette.put("instructions", instructions);

            recettes.put(position, newRecette);

            FileOutputStream file = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            file.write(recettes.toString().getBytes());

        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
