package com.example.navbar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
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



import androidx.camera.core.Camera;



import com.google.common.util.concurrent.ListenableFuture;


import java.util.Objects;


public class Scan extends Fragment {
    PreviewView PW;


    public Scan() {
    }

    public Scan newInstance() {
        Scan fragment = new Scan();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        PW = view.findViewById(R.id.preview);

        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            PW.post(this::startCamera);
        } else {
            requestCameraPermission();
        }

        return view;
    }

    public void startCamera() {
        Log.d("Camera", "Starting camera...");
        if (PW == null || PW.getWidth() == 0 || PW.getHeight() == 0) {
            return;
        }

        int aspect_ratio = aspectRatio(PW.getWidth(), PW.getHeight());
        ListenableFuture<ProcessCameraProvider> LF = ProcessCameraProvider.getInstance(requireContext());

        LF.addListener(() -> {
            try {
                ProcessCameraProvider CP = LF.get();
                Preview preview = new Preview.Builder().setTargetAspectRatio(aspect_ratio).build();
                ImageCapture imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build();
                CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(requireContext()), new BarcodeAnalyzer());

                CP.unbindAll();
                Camera camera = CP.bindToLifecycle(getViewLifecycleOwner(), cameraSelector, preview, imageAnalysis);
                preview.setSurfaceProvider(PW.getSurfaceProvider());
            } catch (Exception e) {
                Log.e("CameraError", "Error initializing camera", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale("android.permission.camera")) {
            // Show a rationale dialog here if necessary
        }

        activityResultLauncher.launch("android.permission.camera");
    }

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
                    }
                }
            }
    );

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