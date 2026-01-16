package com.example.mealway.data.repository;

import com.example.mealway.data.callback.AuthCallback;
import com.google.firebase.auth.FirebaseAuth;

public class AuthRepositoryImpl implements AuthRepository {

    private final FirebaseAuth firebaseAuth;
    private final com.example.mealway.data.local.LocalDataSource localDataSource;

    public AuthRepositoryImpl(android.content.Context context) {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.localDataSource = new com.example.mealway.data.local.LocalDataSource(context);
    }

    @Override
    public boolean isLoggedIn() {
        // Checking both Firebase and Local Preference for robustness
        return firebaseAuth.getCurrentUser() != null && localDataSource.isLoggedIn();
    }

    @Override
    public void login(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        localDataSource.saveLoginState(true);
                        callback.onSuccess();
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Authentication failed");
                    }
                });
    }

    @Override
    public void register(String email, String password, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        localDataSource.saveLoginState(true);
                        callback.onSuccess();
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                        android.util.Log.e("AuthRepo", "Register Error: " + error);
                        callback.onFailure(error);
                    }
                });
    }

    @Override
    public void signInWithGoogle(String idToken, AuthCallback callback) {
        com.google.firebase.auth.AuthCredential credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        localDataSource.saveLoginState(true);
                        callback.onSuccess();
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Google Sign-In failed";
                        android.util.Log.e("AuthRepo", "Google Sign-In Error: " + error);
                        callback.onFailure(error);
                    }
                });
    }
}
