package com.example.myapplication.Domain;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Plan implements Serializable {
    private int id;
    private int userId;
    private String name;

    public Plan(int id, int userId, String name) {
        this.id = id;
        this.userId = userId;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}