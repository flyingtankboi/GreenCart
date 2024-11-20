package com.example.navbar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;



public class BarcodeFound extends Fragment {

    ImageView produit;
    ImageView ecoview;
    ImageView nutriview;

    String ecoscore;
    String nutriscore;


    public BarcodeFound(String ecoscore, String nutriscore) {
        this.ecoscore = ecoscore;
        this.nutriscore = nutriscore;
    }

    public static BarcodeFound newInstance() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barcode_found, container, false);

        produit = view.findViewById(R.id.imageProduit);
        ecoview = view.findViewById(R.id.EcoScore);
        nutriview = view.findViewById(R.id.NutriScore);

        produit.setImageResource(R.drawable.nutriscorea);
        ecoview.setImageResource(R.drawable.baseline_question_mark_24);
        nutriview.setImageResource(R.drawable.baseline_question_mark_24);
        if (ecoscore.equals("a"))
            ecoview.setImageResource(R.drawable.ecoscorea);
        if (ecoscore.equals("b"))
            ecoview.setImageResource(R.drawable.ecoscoreb);
        if (ecoscore.equals("c"))
            ecoview.setImageResource(R.drawable.ecoscorec);
        if (ecoscore.equals("d"))
            ecoview.setImageResource(R.drawable.ecoscored);
        if (ecoscore.equals("e"))
            ecoview.setImageResource(R.drawable.ecoscoree);

        if (nutriscore.equals("a"))
            nutriview.setImageResource(R.drawable.nutriscorea);
        if (nutriscore.equals("b"))
            nutriview.setImageResource(R.drawable.nutriscoreb);
        if (nutriscore.equals("c"))
            nutriview.setImageResource(R.drawable.nutriscorec);
        if (nutriscore.equals("d"))
            nutriview.setImageResource(R.drawable.nutriscored);
        if (nutriscore.equals("e"))
            nutriview.setImageResource(R.drawable.nutriscoree);

        return view;
    }
}