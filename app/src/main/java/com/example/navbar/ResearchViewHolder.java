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
        imageView = itemView.findViewById(R.id.imageView);
        textView = itemView.findViewById(R.id.textView);
    }
}
