package com.android.greencart.shoppinglist;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.greencart.R;

public class ResearchViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    public TextView textView;
    public ResearchViewHolder(View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.textView);
    }
}
