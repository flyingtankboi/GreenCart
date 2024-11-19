package com.example.navbar;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private List<String> names;

    public CardAdapter(List<String> names) {
        this.names = names;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = names.get(position);
        holder.textViewName.setText(name);

        // Set click listeners to toggle visibility
        holder.textViewName.setOnClickListener(v -> {
            holder.textViewName.setVisibility(View.GONE);
            holder.editTextName.setVisibility(View.VISIBLE);
            holder.editTextName.setText(name);
        });

        holder.editTextName.setOnEditorActionListener((v, actionId, event) -> {
            if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                // Update the text and hide the EditText
                holder.textViewName.setText(holder.editTextName.getText().toString());
                holder.textViewName.setVisibility(View.VISIBLE);
                holder.editTextName.setVisibility(View.GONE);
                names.set(position, holder.editTextName.getText().toString());
                return true; // Consume the "Enter" key event
            }
            return false;
        });

        holder.editTextName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                holder.textViewName.setVisibility(View.VISIBLE);
                holder.editTextName.setVisibility(View.GONE);
                names.set(position, holder.editTextName.getText().toString());
                holder.textViewName.setText(names.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        EditText editTextName;

        ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            editTextName = itemView.findViewById(R.id.edit_text_name);
        }
    }
}
