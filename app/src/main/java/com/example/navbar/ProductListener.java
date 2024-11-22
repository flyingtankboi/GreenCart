package com.example.navbar;

import android.graphics.Bitmap;
import android.util.Log;
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
                JSONObject data = prod.getJSONObject("ecoscore_data");
                JSONObject agribalyse = data.getJSONObject("agribalyse");
                String piechart;
                try {
                    double[] co2 = new double[6];
                    co2[0] = agribalyse.getDouble("co2_agriculture");
                    co2[1] = agribalyse.getDouble("co2_consumption");
                    co2[2] = agribalyse.getDouble("co2_distribution");
                    co2[3] = agribalyse.getDouble("co2_packaging");
                    co2[4] = agribalyse.getDouble("co2_processing");
                    co2[5] = agribalyse.getDouble("co2_total");
                    piechart = "https://quickchart.io/chart/render/zm-b82ec21d-233a-46d2-94c5-ae7511cfc2f9?data1=" + String.valueOf(co2[0]) + "," + String.valueOf(co2[1]) + "," + String.valueOf(co2[2]) + "," + String.valueOf(co2[3]) + "," + String.valueOf(co2[4]);
                } catch (Exception e) {
                    piechart = "Not found";
                }

                try {
                    imageUrl = prod.getString("image_url");
                }
                catch (Exception e){
                    imageUrl = "Not found";
                }


                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frameLayout, new BarcodeFound(ecoscore,nutriscore,imageUrl,piechart,rq));
                transaction.commit();
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
