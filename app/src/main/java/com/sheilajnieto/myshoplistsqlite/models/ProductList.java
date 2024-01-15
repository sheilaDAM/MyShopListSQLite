package com.sheilajnieto.myshoplistsqlite.models;/*
@author sheila j. nieto 
@version 0.1 2024 -01 - 09
*/

public class ProductList {

    private ListClass list;
    private Product[] product;
    private boolean bought;

    public ProductList() {
    }

    public ProductList(ListClass list, Product[] product, boolean bought) {
        this.list = list;
        this.product = product;
        this.bought = bought;
    }

    public ListClass getList() {
        return list;
    }

    public void setList(ListClass list) {
        this.list = list;
    }

    public Product[] getProduct() {
        return product;
    }

    public void setProduct(Product[] product) {
        this.product = product;
    }

    public boolean isBought() {
        return bought;
    }
}
