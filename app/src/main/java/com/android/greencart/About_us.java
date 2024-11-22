package com.android.greencart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.android.greencart.shoppinglist.Liste;
import com.google.android.material.button.MaterialButton;


public class About_us extends Fragment {


    public About_us() {
    }


    public static About_us newInstance() {
        return new About_us();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    //Bouton de réinitialisation de la base de données
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frigo, container, false);
        MaterialButton reinit = view.findViewById(R.id.resetBDD);
        reinit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Liste.db.delete("items", "id > 1025", new String[]{}); //Récupération de la base de données depuis Liste
            }
        });
        return view;
    }
}