package com.example.mealway.data.repository;

public interface UserDataCallback {
    void onDataFetched(String fullName, String phone, String email, String profileImage);
    void onError(String error);
}