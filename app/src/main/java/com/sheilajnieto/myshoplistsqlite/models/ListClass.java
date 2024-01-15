package com.sheilajnieto.myshoplistsqlite.models;/*
@author sheila j. nieto 
@version 0.1 2024 -01 - 09
*/

import java.util.ArrayList;
import java.util.Date;

public class ListClass {
    private int id;
    private String name;
    private Date date;
   private ArrayList<ProductList> productsList;

    public ListClass() {
    }

    public ListClass(String name) {
        this.name = name;
    }

    public ListClass(String name, Date date) {
        this.name = name;
        this.date = date;
    }

    public ListClass(int id, String name, Date date) {
        this.id = id;
        this.name = name;
        this.date = date;
    }

    public ListClass(String name, Date date, ArrayList<ProductList> productsList) {
        this.name = name;
        this.date = date;
        this.productsList = productsList;
    }


    public ListClass(int id, String name, Date date, ArrayList<ProductList> productsList) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.productsList = productsList;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<ProductList> getProductsList() {
        return productsList;
    }

    public void setProductsList(ArrayList<ProductList> productsList) {
        this.productsList = productsList;
    }
}
