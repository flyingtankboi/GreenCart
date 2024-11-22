package com.android.greencart.scan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.greencart.R;
import com.google.common.util.concurrent.ListenableFuture;


public class Scan extends Fragment {
    PreviewView PW;
    View view;


    public Scan() {
    }

    public Scan newInstance() {
        return new Scan();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_scan, container, false);
        PW = view.findViewById(R.id.preview);

        //On vérifie si l'application a le droit d'utiliser la caméra
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            PW.post(this::startCamera);
        } else {
            //On demande la permission
            requestCameraPermission();
        }

        return view;
    }

    public void startCamera() {
        //On récupère le ratio de la caméra
        int aspect_ratio = aspectRatio(PW.getWidth(), PW.getHeight());
        ListenableFuture<ProcessCameraProvider> LF = ProcessCameraProvider.getInstance(requireContext());

        LF.addListener(() -> {
            try {
                //On construit le preview
                ProcessCameraProvider CP = LF.get();
                Preview preview = new Preview.Builder().setTargetAspectRatio(aspect_ratio).build();
                ImageCapture imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build();
                CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

                //On démarre l'analyse de la photo grâce à BarcodeAnalyzer
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(requireContext()), new BarcodeAnalyzer(getParentFragmentManager(),requireContext(),view));

                CP.unbindAll();
                CP.bindToLifecycle(getViewLifecycleOwner(), cameraSelector, preview, imageAnalysis);

                //On lance la preview de la caméra
                preview.setSurfaceProvider(PW.getSurfaceProvider());
            } catch (Exception e) {
                Log.e("CameraError", "Error initializing camera", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void requestCameraPermission() {
        activityResultLauncher.launch(Manifest.permission.CAMERA);
    }

    //Si l'utilisateur n'a pas accepté l'utilisation de sa caméra la première fois, on l'envoie sur la page de permission de l'application
    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result) {
                    PW.post(this::startCamera);
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
                        startActivity(intent);
                        startCamera();
                    }
                }
            }
    );

    //On fait soit une preview en 4/3 ou en 16/9
    private int aspectRatio(int width, int height){
        double previewRatio = (double) Math.max(width,height) / Math.min(width,height);
        if (Math.abs(previewRatio - 4.0/3.0) <= Math.abs(previewRatio - 16.0 / 9.0)){
            return AspectRatio.RATIO_4_3;
        }
        else{
            return AspectRatio.RATIO_16_9;
        }
    }
}