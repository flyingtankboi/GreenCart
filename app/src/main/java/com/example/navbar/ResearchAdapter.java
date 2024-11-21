package com.example.navbar;

import static com.example.navbar.Liste.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;
import java.util.Vector;

public class ResearchAdapter extends RecyclerView.Adapter<ResearchViewHolder> {

    Context context;
    Vector<Item> items;
    Vector<Item> shopping_list;
    ShoppingListAdapter adapter;

    DatabaseHelper databaseHelper;
    SearchView searchView;
    FragmentActivity activity;

    SQLiteDatabase db;


    public ResearchAdapter(Context context, Vector<Item> items, Vector<Item> shopping_list, ShoppingListAdapter adapter, DatabaseHelper databaseHelper, SearchView searchView, FragmentActivity activity, SQLiteDatabase db){

        this.context = context;
        this.items = items;
        this.shopping_list = shopping_list;
        this.adapter = adapter;
        this.databaseHelper = databaseHelper;
        this.searchView = searchView;
        this.activity = activity;
        this.db = db;
    }

    @Override
    public ResearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ResearchViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view_research, parent, false));
    }

    @Override
    public void onBindViewHolder(ResearchViewHolder holder, int position) {
        holder.textView.setText(items.elementAt(position).getName(db));
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int currentPosition = holder.getAdapterPosition();
                if (currentPosition == RecyclerView.NO_POSITION) return;

                Item currentItem = items.elementAt(currentPosition);

                if (currentItem.getName(db).equals("+ Add product")) {

                    String current_text = searchView.getQuery().toString();

                    String queryID = "SELECT MAX(id) FROM items";
                    Cursor cursor = db.rawQuery(queryID, new String[]{});
                    cursor.moveToFirst();
                    int id = cursor.getInt(0) + 1;
                    cursor.close();

                    ContentValues values = new ContentValues();
                    values.put("id", id);
                    values.put("name", current_text);
                    values.put("name_fr", current_text);
                    values.put("food_group", "Added");
                    values.put("food_subgroup", "Added");

                    db.insert("items", null, values);

                    searchView.setQuery("", false);
                    searchView.clearFocus();

                    shopping_list.insertElementAt(new Item(id, false), 0);
                    adapter.notifyItemInserted(0);

                } else {
                    searchView.setQuery("", false);
                    searchView.clearFocus();

                    shopping_list.insertElementAt(currentItem, 0);
                    adapter.notifyItemInserted(0);
                }
                int[] progress = getProgress();
                String stringCompteur = progress[0] + "/" + progress[1];
                TextView compteur = activity.findViewById(R.id.compteur);
                compteur.setVisibility(View.VISIBLE);
                compteur.setText(stringCompteur);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setFilteredList(Vector<Item> filteredList) {
        this.items = filteredList;
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
}
