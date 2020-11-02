package com.example.socketexampleapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MyDeo {
    @Insert
    public void addUser(User user);

    @Query("select * from users")
    public List<User> getUsers();
}
