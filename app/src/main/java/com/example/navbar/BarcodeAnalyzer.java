package com.example.navbar;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

public class BarcodeAnalyzer implements ImageAnalysis.Analyzer {
    @OptIn(markerClass = ExperimentalGetImage.class)
    FragmentManager fm;
    String temp;
    int count;
    RequestQueue rq;
    View view;
    Context context;
    public BarcodeAnalyzer(FragmentManager fm, Context context, View view){
        this.fm = fm;
        this.context = context;
        this.view = view;

    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    @Override
    public void analyze(ImageProxy imageProxy) {
        @androidx.camera.core.ExperimentalGetImage
        android.media.Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
            BarcodeScanning.getClient()
                    .process(image)
                    .addOnSuccessListener(barcodes -> {
                        if (!barcodes.isEmpty()) {
                            String barcode = barcodes.get(0).getRawValue();
                            if (barcode.equals(temp)){
                                count++;
                            }
                            else{
                                temp = barcode;
                                count=0;
                            }
                            if (count==5) {
                                count = 0;
                                Log.d("Barcode", "Detected: " + barcode);
                                rq = Volley.newRequestQueue(context);
                                ProductListener responseListener = new ProductListener(fm,view);
                                Response.ErrorListener errorListener = new ErrorListener();
                                StringRequest SR = new StringRequest(Request.Method.GET,
                                        "https://world.openfoodfacts.net/api/v2/product/"+barcode+"/?product_type=all&fields=ecoscore_grade%2Cnutriscore_grade",
                                        responseListener,
                                        errorListener);
                                rq.add(SR);
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Barcode", "Detection failed", e))
                    .addOnCompleteListener(task -> imageProxy.close());
        } else {
            imageProxy.close();
        }
    }
}