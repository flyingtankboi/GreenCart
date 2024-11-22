package com.example.navbar;

import static com.example.navbar.Liste.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

public class ResearchAdapter extends RecyclerView.Adapter<ResearchViewHolder> {

    Context context;
    Vector<Item> items;
    Vector<Item> shopping_list;
    Vector<String> ingredients;
    RecetteCard recette;
    ShoppingListAdapter adapter;
    FicheRecetteAdapter recette_adapter;

    DatabaseHelper databaseHelper;
    SearchView searchView;
    FragmentActivity activity;
    int json_position;
    final static String FILENAME = "recipes.json";



    public ResearchAdapter(Context context, Vector<Item> items, Vector<Item> shopping_list, ShoppingListAdapter adapter, DatabaseHelper databaseHelper, SearchView searchView, FragmentActivity activity){

        this.context = context;
        this.items = items;
        this.shopping_list = shopping_list;
        this.adapter = adapter;
        this.databaseHelper = databaseHelper;
        this.searchView = searchView;
        this.activity = activity;
    }

    public ResearchAdapter(Context context, Vector<Item> items, RecetteCard recette, FicheRecetteAdapter adapter, DatabaseHelper databaseHelper, SearchView searchView, FragmentActivity activity, int json_position){

        this.context = context;
        this.items = items;
        this.recette = recette;
        this.ingredients = recette.getIngredients();
        this.shopping_list = new Vector<>();
        this.recette_adapter = adapter;
        this.databaseHelper = databaseHelper;
        this.searchView = searchView;
        this.activity = activity;
        this.json_position = json_position;
    }

    @Override
    public ResearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ResearchViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view_research, parent, false));
    }

    @Override
    public void onBindViewHolder(ResearchViewHolder holder, int position) {
        holder.textView.setText(items.elementAt(position).getName());
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int currentPosition = holder.getAdapterPosition();
                if (currentPosition == RecyclerView.NO_POSITION) return;

                Item currentItem = items.elementAt(currentPosition);

                if (currentItem.getName().equals("+ Ajouter produit")) {

                    String current_text = searchView.getQuery().toString();

                    searchView.setQuery("", false);
                    searchView.clearFocus();

                    databaseHelper.addItem(current_text);

                    if (recette_adapter == null){
                        shopping_list.insertElementAt(new Item(current_text, false), 0);
                    } else {
                        ingredients.insertElementAt(current_text, 0);
                        editJSONRecettes(json_position, recette.getTitre(), recette.getIngredients(),recette.getInstructions());
                    }

                } else {
                    searchView.setQuery("", false);
                    searchView.clearFocus();

                    if (recette_adapter == null){
                        shopping_list.insertElementAt(new Item(currentItem.getName(), false), 0);
                    } else {
                        ingredients.insertElementAt(currentItem.getName(), 0);
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

    public void editJSONRecettes (int position, String titre, Vector<String> ingredients_string, String instructions){
        try {
            JSONArray recettes = new JSONArray(Recette.readJson(activity.getApplicationContext()));
            recettes.remove(position);

            JSONObject newRecette = new JSONObject();
            newRecette.put("title", titre);

            JSONArray ingredients = new JSONArray();
            for(int i = 0; i < ingredients_string.size(); i ++) ingredients.put(ingredients_string.elementAt(i));
            newRecette.put("ingredients", ingredients);
            newRecette.put("instructions", instructions);

            recettes.put(position, newRecette);

            FileOutputStream file = activity.getApplicationContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
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
