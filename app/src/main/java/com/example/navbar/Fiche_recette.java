package com.example.navbar;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;


public class Fiche_recette extends Fragment {


    private static RecetteCard fiche_recette;
    FicheRecetteAdapter adapter;
    int position;
    TextView notes_recette;
    EditText edit_notes_recette;
    TextView titre_recette;
    EditText edit_titre_recette;
    Button save_button;
    final static String FILENAME = "recipes.json";

    public Fiche_recette() {
        // Required empty public constructor
    }


    public static Fiche_recette newInstance(RecetteCard recetteCard,int position) {
        Fiche_recette fragment = new Fiche_recette();
        Bundle args = new Bundle();
        args.putSerializable("arg_recipe", recetteCard);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fiche_recette = (RecetteCard) getArguments().getSerializable("arg_recipe");
            position = getArguments().getInt("position");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_fiche_recette, container, false);

        save_button = view.findViewById(R.id.save_button);
        save_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // mettre le fait de revenir au fragment précédent !!!

            }
        });

        edit_titre_recette = view.findViewById(R.id.edit_titre_recette);
        edit_titre_recette.setVisibility(View.GONE);
        edit_titre_recette.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String text = v.getText().toString();

                    if (text.isEmpty()){
                        text = "Nom Recette";
                    }

                    edit_titre_recette.setVisibility(View.INVISIBLE);

                    fiche_recette.setTitre(text);

                    editJSONRecettes(position, text, fiche_recette.getIngredients(), fiche_recette.getInstructions());

                    titre_recette.setText(text);
                    titre_recette.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }
        });

        titre_recette = view.findViewById(R.id.titre_recette);
        titre_recette.setText(fiche_recette.getTitre());
        titre_recette.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                titre_recette.setVisibility(View.INVISIBLE);
                edit_titre_recette.setVisibility(View.VISIBLE);
                edit_titre_recette.setText(titre_recette.getText());
                edit_titre_recette.requestFocus();
            }
        });

        edit_notes_recette = view.findViewById(R.id.edit_notes_recette);
        edit_notes_recette.setVisibility(View.GONE);
        edit_notes_recette.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String text = v.getText().toString();

                    if (text.isEmpty()){
                        text = "Remplir les instructions...";
                    }

                    edit_notes_recette.setVisibility(View.GONE);

                    fiche_recette.setInstructions(text);

                    editJSONRecettes(position, fiche_recette.getTitre(), fiche_recette.getIngredients(), text);

                    notes_recette.setText(text);
                    notes_recette.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }
        });

        notes_recette = view.findViewById(R.id.notes_recette);
        notes_recette.setText(fiche_recette.getInstructions());
        notes_recette.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                notes_recette.setVisibility(View.GONE);
                edit_notes_recette.setVisibility(View.VISIBLE);
                edit_notes_recette.setText(notes_recette.getText());
                edit_notes_recette.requestFocus();
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new FicheRecetteAdapter(fiche_recette.getIngredients());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext().getApplicationContext()));




        View delete_view = view.findViewById(R.id.imageView);
        delete_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONArray recettes  = null;
                try {
                    recettes = new JSONArray(Recette.readJson(requireActivity().getApplicationContext()));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                recettes.remove(position);
                FileOutputStream file = null;
                try {
                    file = requireContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                try {
                    file.write(recettes.toString().getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                getParentFragmentManager().popBackStack();

            }
        });
        return view;

    }

    public void editJSONRecettes (int position, String titre, Vector<String> ingredients_string, String instructions){
        try {
            JSONArray recettes = new JSONArray(Recette.readJson(requireActivity().getApplicationContext()));
            recettes.remove(position);

            JSONObject newRecette = new JSONObject();
            newRecette.put("title", titre);

            JSONArray ingredients = new JSONArray();
            for(int i = 0; i < ingredients_string.size(); i ++) ingredients.put(ingredients_string.elementAt(i));
            newRecette.put("ingredients", ingredients);
            newRecette.put("instructions", instructions);

            recettes.put(position, newRecette);

            FileOutputStream file = requireContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
            file.write(recettes.toString().getBytes());

        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}