package com.example.navbar;

import static com.example.navbar.Liste.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Vector;

public class ResearchAdapter extends RecyclerView.Adapter<ResearchViewHolder> {

    Context context;
    Vector<Item> items;
    Vector<Item> shopping_list;
    ShoppingListAdapter adapter;
    DatabaseHelper databaseHelper;
    SearchView searchView;

    public ResearchAdapter(Context context, Vector<Item> items, Vector<Item> shopping_list, ShoppingListAdapter adapter, DatabaseHelper databaseHelper, SearchView searchView){
        this.context = context;
        this.items = items;
        this.shopping_list = shopping_list;
        this.adapter = adapter;
        this.databaseHelper = databaseHelper;
        this.searchView = searchView;
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

                    shopping_list.add(new Item(current_text, false));
                    adapter.notifyItemChanged(shopping_list.size() - 1);

                } else {
                    searchView.setQuery("", false);
                    searchView.clearFocus();

                    shopping_list.add(new Item(currentItem.getName(), false));
                    adapter.notifyItemChanged(shopping_list.size() - 1);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setFilteredList(Vector<Item> filteredList){
        this.items = filteredList;
        notifyDataSetChanged();
    }
}
