package com.android.greencart.scan;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.fragment.app.FragmentManager;

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
    int nb_requests;
    boolean stop=true;
    public BarcodeAnalyzer(FragmentManager fm, Context context, View view){
        this.fm = fm;
        this.context = context;
        this.view = view;

    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    @Override
    public void analyze(ImageProxy imageProxy) {
        //Utilisation de la librairie CameraX pour afficher une preview de la caméra
        @androidx.camera.core.ExperimentalGetImage
        android.media.Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
            BarcodeScanning.getClient() // Utilisation de la librairie BarcodeScanning fournie par Google
                    .process(image)
                    .addOnSuccessListener(barcodes -> {
                        if (!barcodes.isEmpty()) {
                            String barcode = barcodes.get(0).getRawValue();
                            assert barcode != null;
                            if (barcode.equals(temp)){
                                count++;
                            }
                            else{
                                temp = barcode;
                                count=0;
                            }
                            if (count==5) { //On veut que le barcode soit affiché sur l'écran pendant au moins 5 temps pour éviter toute lecture partielle ou faux positif
                                count = 0;
                                nb_requests++;
                                rq = Volley.newRequestQueue(context);
                                StringRequest SR = getStringRequest(barcode);
                                rq.add(SR);
                            }

                            if (nb_requests>3 && stop){ //Si le nombre de requêtes s'accumulent sans réponse, on envoie un Toast de rappel
                                Toast.makeText(context,"Vérifiez que vous êtes connéctés à Internet.", Toast.LENGTH_LONG).show();
                                rq.cancelAll(Request.Method.GET);
                                stop=false;
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Barcode", "Detection failed", e))
                    .addOnCompleteListener(task -> imageProxy.close());
        } else {
            imageProxy.close();
        }
    }

    @NonNull
    private StringRequest getStringRequest(String barcode) { //Récupération des données sur les produits auprès de l'API de Open Food Facts
        ProductListener responseListener = new ProductListener(fm,view,rq);
        Response.ErrorListener errorListener = new ErrorListener();
        return new StringRequest(Request.Method.GET,
                "https://world.openfoodfacts.net/api/v2/product/"+ barcode +"/?product_type=all&fields=nutriscore_grade%2Cecoscore_grade%2Cimage_url%2Cecoscore_data",
                responseListener,
                errorListener);
    }
}