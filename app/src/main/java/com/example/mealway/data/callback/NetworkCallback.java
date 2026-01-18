package com.example.mealway.data.callback;

public interface NetworkCallback<T> {
    void onSuccess(T result);
    void onFailure(String message);
}
