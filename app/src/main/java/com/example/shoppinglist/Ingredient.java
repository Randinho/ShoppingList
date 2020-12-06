package com.example.shoppinglist;

public class Ingredient {

    private String name;
    private boolean possession;

    public Ingredient(){

    }

    public Ingredient(String name)
    {
        this.name = name;
        this.possession = false;
    }

    public String getName() {
        return name;
    }

    public boolean isPossession() {
        return possession;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPossession(boolean possession) {
        this.possession = possession;
    }
}
