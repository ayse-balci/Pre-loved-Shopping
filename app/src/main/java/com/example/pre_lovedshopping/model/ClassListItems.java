package com.example.pre_lovedshopping.model;

public class ClassListItems {
    public int cont_id;
    public String img; //Image URL
    public String name; //Name
    public String price;
    public int clickedContribution;

    public ClassListItems(int cont_id, String name, String img, String price)
    {
        this.cont_id = cont_id;
        this.img = img;
        this.name = name;
        this.price = price;
    }

    public void changeName(String text) {
        name = text;
    }

    public ClassListItems(String name)
    {
        this.name = name;
    }

    public ClassListItems()
    {

    }

    public String getImg() {
        return img;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public int getClickedContribution() { return clickedContribution; }



}
