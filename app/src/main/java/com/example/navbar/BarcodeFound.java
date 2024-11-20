package com.example.navbar;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.material.button.MaterialButton;


public class BarcodeFound extends Fragment {

    ImageView produit;
    ImageView ecoview;
    ImageView nutriview;
    String ecoscore;
    String nutriscore;
    String imageUrl;
    RequestQueue rq;


    public BarcodeFound(String ecoscore, String nutriscore, String imageUrl, RequestQueue rq) {
        this.ecoscore = ecoscore;
        this.nutriscore = nutriscore;
        this.imageUrl = imageUrl;
        this.rq = rq;
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

        MaterialButton bouton = view.findViewById(R.id.bouttonRescan);
        bouton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout, new Scan());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

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

        ImageLoader imageLoader = new ImageLoader(rq, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
        imageLoader.get(imageUrl, ImageLoader.getImageListener(view.findViewById(R.id.imageProduit),R.drawable.baseline_file_download_24,R.drawable.baseline_question_mark_24));
        return view;
    }
}