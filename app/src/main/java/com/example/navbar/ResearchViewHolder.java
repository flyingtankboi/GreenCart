package com.example.navbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ResearchViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView textView;
    public ResearchViewHolder( View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.textView);
    }
}
