package com.example.navbar;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private List<String> names;
    private List<String> dates;
    final String FILENAME_HISTORIQUE = "historique.json";
    final String FILENAME = "current_list.json";
    final String DEFAULT_HISTORIQUE = "[]";
    JSONArray historique;

    public CardAdapter(List<String> names, List<String> dates, JSONArray historique) {
        this.names = names;
        this.dates = dates;
        this.historique = historique;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = names.get(position);
        String date = dates.get(position);
        holder.textViewName.setText(name);
        holder.textViewDate.setText(date);

        // Set click listeners to toggle visibility
        holder.textViewName.setOnClickListener(v -> {
            holder.textViewName.setVisibility(View.GONE);
            holder.editTextName.setVisibility(View.VISIBLE);
            holder.editTextName.setText(name);
        });

        holder.editTextName.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || actionId == EditorInfo.IME_ACTION_DONE) {
                // Update the text and hide the EditText
                holder.textViewName.setText(holder.editTextName.getText().toString());
                holder.textViewName.setVisibility(View.VISIBLE);
                holder.editTextName.setVisibility(View.GONE);
                names.set(position, holder.editTextName.getText().toString());
                changeName(v.getContext().getApplicationContext(), holder.editTextName.getText().toString(), position);
                return true; // Consume the "Enter" key event
            }
            return false;
        });

        holder.editTextName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                holder.textViewName.setVisibility(View.VISIBLE);
                holder.editTextName.setVisibility(View.GONE);
                names.set(position, holder.editTextName.getText().toString());
                holder.textViewName.setText(names.get(position));
            }
        });

        holder.importerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try (FileOutputStream file = view.getContext().openFileOutput(FILENAME, Context.MODE_PRIVATE)) {
                    file.write(historique.getJSONObject(historique.length() - 1 - position).toString().getBytes());
                } catch (IOException e) {
                    Log.i("error", "N'a pas réussi à ecrire dans le fichier par defaut");
                } catch (JSONException e) {
                    Log.i("error", "Pas JSON");
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewDate;
        EditText editTextName;

        Button importerButton;

        ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            editTextName = itemView.findViewById(R.id.edit_text_name);
            importerButton = itemView.findViewById(R.id.button_ajouter);
        }
    }

    private void changeName(Context context, String name, int index){
        File list = new File(context.getFilesDir(), FILENAME_HISTORIQUE);

        String string_du_fichier;
        try {
            FileInputStream file_input = context.openFileInput(FILENAME_HISTORIQUE);
            int size = file_input.available();
            byte[] buffer = new byte[size];
            file_input.read(buffer);
            file_input.close();
            string_du_fichier = new String(buffer, "UTF-8");
            JSONArray historique = new JSONArray(string_du_fichier);
            historique.getJSONObject(historique.length() - 1 - index).put("name", name);

            FileOutputStream file_output = context.openFileOutput(FILENAME_HISTORIQUE, Context.MODE_PRIVATE);
            file_output.write(historique.toString().getBytes());

        } catch (IOException e) {
            Log.i("error", "N'a pas réussi à écrire dans le fichier par defaut");
        } catch (JSONException e) {
            Log.i("error", "Historique n'est pas un JSONArray");
        }
    }

    private String getHistorique(Context context) {

        File list = new File(context.getFilesDir(), FILENAME_HISTORIQUE);
        if (!list.exists()) {
            try (FileOutputStream file = context.openFileOutput(FILENAME_HISTORIQUE, Context.MODE_PRIVATE)) {
                file.write(DEFAULT_HISTORIQUE.getBytes());
            } catch (IOException e) {
                Log.i("error", "N'a pas réussi à ecrire dans le fichier par defaut");
            }
            return DEFAULT_HISTORIQUE;
        } else {
            String string_du_fichier = "";
            try {
                FileInputStream file = context.openFileInput(FILENAME_HISTORIQUE);
                int size = file.available();
                byte[] buffer = new byte[size];
                file.read(buffer);
                file.close();
                string_du_fichier = new String(buffer, "UTF-8");

            } catch (IOException e) {
                Log.i("error", "N'a pas réussi à écrire dans le fichier par defaut");
            }
            return string_du_fichier;
        }
    }
}

