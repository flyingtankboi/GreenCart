package com.android.greencart;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.greencart.recette.Recette;
import com.android.greencart.scan.Scan;
import com.android.greencart.shoppinglist.Liste;
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


        // Bar de navigation et ouverture des fragments en fonction de l'icon clickée
        nav_bar = findViewById(R.id.navBar);
        replaceFragment(new Liste());
        if (nav_bar == null){
            throw (new RuntimeException());
        }
        nav_bar.setOnItemSelectedListener(item ->{
            int itemId = item.getItemId();
            if (itemId == R.id.scan) {
                replaceFragment(new Scan());
            } else if (itemId == R.id.frigo) {
                replaceFragment(new About_us());
            } else if (itemId == R.id.liste) {
                replaceFragment(new Liste());
            } else if (itemId == R.id.recette) {
                replaceFragment(new Recette());
            } else if (itemId == R.id.profile) {
                replaceFragment(new Historique());
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
