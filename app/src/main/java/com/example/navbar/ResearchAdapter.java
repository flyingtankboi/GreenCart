package com.example.navbar;

import static com.example.navbar.Liste.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Vector;

public class ResearchAdapter extends RecyclerView.Adapter<ResearchViewHolder> {

    Context context;
    Vector<Item> items;
    Vector<Item> shopping_list;
    ShoppingListAdapter adapter;

    public ResearchAdapter(Context context, Vector<Item> items, Vector<Item> shopping_list, ShoppingListAdapter adapter){
        this.context = context;
        this.items = items;
        this.shopping_list = shopping_list;
        this.adapter = adapter;
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
                shopping_list.add(new Item(items.elementAt(position).getName(), false));
                adapter.notifyItemChanged(shopping_list.size() - 1);
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
