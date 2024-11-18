package com.example.navbar;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Vector;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

    private final Vector<Item> shoppingList;

    public ShoppingListAdapter(Vector<Item> shoppingList) {
        this.shoppingList = shoppingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {
        Item item = shoppingList.get(position);
        if (item.isBought()){
            holder.textViewItem.setTextColor(Color.GRAY);
        }
        else{
            holder.textViewItem.setTextColor(Color.BLACK);
        }
        holder.textViewItem.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewItem;

        public ViewHolder( View view) {
            super(view);
            textViewItem = view.findViewById(R.id.textViewItem);
        }
    }
}
