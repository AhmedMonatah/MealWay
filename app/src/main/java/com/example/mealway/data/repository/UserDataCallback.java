package com.example.mealway.data.repository;

public interface UserDataCallback {
    void onDataFetched(String fullName, String phone, String email);
    void onError(String error);
}