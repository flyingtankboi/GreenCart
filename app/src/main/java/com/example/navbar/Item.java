package com.example.navbar;


class Item {
    private String name;
    private boolean isBought;

    public Item(String name, boolean isBought) {
        this.name = name;
        this.isBought = isBought;
    }

    public String getName() {
        return name;
    }

    public boolean isBought() {
        return isBought;
    }

    public void setBought(boolean bought) {
        isBought = bought;
    }
}