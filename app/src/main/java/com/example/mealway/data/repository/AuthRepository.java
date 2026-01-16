package com.example.mealway.data.repository;

public interface AuthRepository {
    boolean isLoggedIn();
    boolean login(String email, String password);
}