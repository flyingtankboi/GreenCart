package com.example.navbar;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Vector;


public class Liste extends Fragment {


    public Liste() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_liste, container, false);

        Vector<Item> shoppingList;
        ShoppingListAdapter adapter;

        super.onCreate(savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        // Initialisez la liste de courses
        // TODO
        shoppingList = new Vector<Item>();
        shoppingList.add(new Item("Pommes", false));
        shoppingList.add(new Item("Pates", false));
        shoppingList.add(new Item("Lait", false));
        shoppingList.add(new Item("Beurre", false));


        // Configurez l'adapter et la RecyclerView
        adapter = new ShoppingListAdapter(shoppingList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
        recyclerView.setAdapter(adapter);

        // Ajouter le glissement pour marquer comme acheté
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false; // Pas besoin de gérer les déplacements
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Item item = shoppingList.get(position);
                item.setBought(!item.isBought());
                int newPosition;
                if (item.isBought()) { // Selon si l'article est coché ou non, on l'ajoute au début ou à la fin de la liste.
                    newPosition = shoppingList.size() - 1;
                } else {
                    newPosition = 0;
                }
                shoppingList.remove(position);
                shoppingList.add(newPosition, item);

                adapter.notifyItemMoved(position, newPosition);
                adapter.notifyItemChanged(newPosition);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }
}