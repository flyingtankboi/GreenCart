package com.example.navbar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Vector;

public class ResearchAdapter extends RecyclerView.Adapter<ResearchViewHolder> {

    Context context;
    Vector<Item> items;

    public ResearchAdapter(Context context, Vector<Item> items){
        this.context = context;
        this.items = items;
    }

    @Override
    public ResearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ResearchViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view_research, parent, false));
    }

    @Override
    public void onBindViewHolder(ResearchViewHolder holder, int position) {
        holder.textView.setText(items.elementAt(position).getName());
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
