package com.example.navbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductListener implements Response.Listener<String> {
    String ecoscore;
    String nutriscore;
    FragmentManager fragmentManager;

    View view;

    public ProductListener(FragmentManager fragmentManager, View view){
        this.fragmentManager = fragmentManager;
        this.view = view;
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
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frameLayout, new BarcodeFound(ecoscore,nutriscore));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
