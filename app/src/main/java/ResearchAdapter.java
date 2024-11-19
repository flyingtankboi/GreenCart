package com.example.researchbar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Vector;

public class Adapter extends RecyclerView.Adapter<ResearchViewHolder> {

    Context context;
    Vector<Item> items;

    public Adapter(Context context, Vector<Item> items){
        this.context = context;
        this.items = items;
    }

    @Override
    public ResearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageResource(items.elementAt(position).picture);
        holder.textView.setText(items.elementAt(position).product);
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
