package com.example.pre_lovedshopping.Fragments;

public class ClassListItems {
    public String img; //Image URL
    public String name; //Name
    public String price;

    public ClassListItems(String name, String img, String price)
    {
        this.img = img;
        this.name = name;
        this.price = price;
    }

    public ClassListItems(String name)
    {
        this.name = name;
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

}
