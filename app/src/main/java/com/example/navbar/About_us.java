package com.example.navbar;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.example.navbar.Liste.db;

import com.google.android.material.button.MaterialButton;


public class About_us extends Fragment {

    SQLiteDatabase db;

    public About_us() {
    }


    public static About_us newInstance() {
        About_us fragment = new About_us();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frigo, container, false);
        MaterialButton reinit = view.findViewById(R.id.resetBDD);
        reinit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.example.navbar.Liste.db.delete("items", "id > 1025", new String[]{});
            }
        });
        return view;
    }
}