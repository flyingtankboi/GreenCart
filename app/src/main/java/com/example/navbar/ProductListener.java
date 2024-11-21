package com.example.navbar;

import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;


import org.json.JSONException;
import org.json.JSONObject;

public class ProductListener implements Response.Listener<String> {
    String ecoscore;
    String nutriscore;
    String imageUrl;
    FragmentManager fragmentManager;

    View view;

    RequestQueue rq;

    public ProductListener(FragmentManager fragmentManager, View view, RequestQueue rq){
        this.fragmentManager = fragmentManager;
        this.view = view;
        this.rq = rq;
    }
    @Override
    public void onResponse(String response) {
        try {
            JSONObject rep = new JSONObject(response);
            if (rep.getInt("status") == 0){
                Toast.makeText(view.getContext(), "Le produit n'est pas trouvé. Vérifiez que vous ne scannez pas un QR code adjacent.", Toast.LENGTH_SHORT).show();
            }
            else {
                JSONObject prod = rep.getJSONObject("product");
                ecoscore = prod.getString("ecoscore_grade");
                nutriscore = prod.getString("nutriscore_grade");
                imageUrl = prod.getString("image_url");
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frameLayout, new BarcodeFound(ecoscore,nutriscore,imageUrl,rq));
                transaction.commit();
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
