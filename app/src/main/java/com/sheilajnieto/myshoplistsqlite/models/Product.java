package com.sheilajnieto.myshoplistsqlite.models;/*
@author sheila j. nieto 
@version 0.1 2024 -01 - 09
*/

import android.graphics.Bitmap;

public class Product {

    private int id;
    private String name;
    private String category;
    private Bitmap image;

    public Product() {
    }

    public Product(String name, String category, Bitmap image) {
        this.name = name;
        this.category = category;
        this.image = image;
    }

    public Product(int id, String name, String category, Bitmap image) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
