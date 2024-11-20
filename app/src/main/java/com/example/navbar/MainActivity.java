package com.example.navbar;

import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    // Navigation
    BottomNavigationView nav_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Bar de navigation et ouverture des fragments en fonction
        nav_bar = findViewById(R.id.navBar);
        replaceFragment(new Liste());
        if (nav_bar == null){
            Log.i("error", "wtf");
        }
        nav_bar.setOnItemSelectedListener(item ->{
            int itemId = item.getItemId();
            if (itemId == R.id.scan) {
                replaceFragment(new Scan());
            } else if (itemId == R.id.frigo) {
                replaceFragment(new Frigo());
            } else if (itemId == R.id.liste) {
                replaceFragment(new Liste());
            } else if (itemId == R.id.recette) {
                replaceFragment(new Recette());
            } else if (itemId == R.id.profile) {
                replaceFragment(new Profile());
            }
            return true;
        });
        nav_bar.setSelectedItemId(R.id.liste);



    }

    // Fonction pour remplacer le fragment en cours d'utilisation
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}
