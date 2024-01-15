package com.sheilajnieto.myshoplistsqlite.models;/*
@author sheila j. nieto 
@version 0.1 2024 -01 - 09
*/

public class User {

    private int id;
    private String name;

    public User() {
    }

    public User(String name) {
        this.name = name;

    }

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
