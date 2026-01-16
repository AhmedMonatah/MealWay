package com.example.mealway.data.repository;

// AuthRepositoryImpl.java
public class AuthRepositoryImpl implements AuthRepository {

    private boolean loggedIn = false;

    @Override
    public boolean isLoggedIn() {
        return loggedIn;
    }

    @Override
    public boolean login(String email, String password) {
        // Mock login
        if(email.equals("test@mail.com") && password.equals("123456")){
            loggedIn = true;
            return true;
        }
        return false;
    }
}
