package com.sheilajnieto.myshoplistsqlite.models;/*
@author sheila j. nieto 
@version 0.1 2024 -01 - 09
*/

import android.graphics.Bitmap;

public class Product {

    private int id;
    private String name;
    private int fkCategory;
    private boolean isPurchased;
    private Bitmap image;

    public Product() {
    }

    public Product(String name, int fkCategory, Bitmap image) {
        this.name = name;
        this.fkCategory = fkCategory;
        this.image = image;
    }

    public Product(int id, String name, int fkCategory, Bitmap image) {
        this.id = id;
        this.name = name;
        this.fkCategory = fkCategory;
        this.image = image;
    }

    public Product(int id, String name, int fkCategory, boolean isPurchased, Bitmap image) {
        this.id = id;
        this.name = name;
        this.fkCategory = fkCategory;
        this.isPurchased = isPurchased;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getFkCategory() {
        return fkCategory;
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

    public void setFkCategory(int fkCategory) {
        this.fkCategory = fkCategory;
    }
}
