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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

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
                double total;
                DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.GERMAN);
                otherSymbols.setDecimalSeparator('.');
                otherSymbols.setGroupingSeparator('.');
                DecimalFormat df = new DecimalFormat("#.##", otherSymbols);
                try {
                    double[] co2 = new double[5];
                    co2[0] = agribalyse.getDouble("co2_agriculture");
                    co2[1] = agribalyse.getDouble("co2_consumption");
                    co2[2] = agribalyse.getDouble("co2_distribution");
                    co2[3] = agribalyse.getDouble("co2_packaging");
                    co2[4] = agribalyse.getDouble("co2_processing");
                    total = agribalyse.getDouble("co2_total");
                    piechart = "https://quickchart.io/chart/render/zm-c35ff8ba-e59f-4358-99d3-1e699eeb3fd9?data1=" + df.format(co2[0]) + "," + df.format(co2[1]) + "," + df.format(co2[2]) + "," + df.format(co2[3]) + "," + df.format(co2[4]);
                } catch (Exception e) {
                    piechart = "Not found";
                    total = 0;
                }

                try {
                    imageUrl = prod.getString("image_url");
                }
                catch (Exception e){
                    imageUrl = "Not found";
                }
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frameLayout, new BarcodeFound(ecoscore,nutriscore,imageUrl,piechart,total,rq));
                transaction.commit();
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
