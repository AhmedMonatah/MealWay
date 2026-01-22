package com.example.mealway.data.repository;

import com.example.mealway.data.callback.AuthCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.net.Uri;
import java.util.HashMap;
import java.util.Map;

public class AuthRepositoryImpl implements AuthRepository {

    private final FirebaseAuth firebaseAuth;
    private final com.example.mealway.data.local.LocalDataSource localDataSource;

    public AuthRepositoryImpl(android.content.Context context) {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.localDataSource = new com.example.mealway.data.local.LocalDataSource(context);
    }

    @Override
    public boolean isLoggedIn() {
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
    public void register(String email, String password, String fullName, String phone, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = firebaseAuth.getCurrentUser().getUid();
                        
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(fullName)
                                .build();
                        firebaseAuth.getCurrentUser().updateProfile(profileUpdates);

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Map<String, Object> user = new HashMap<>();
                        user.put("fullName", fullName);
                        user.put("phone", phone);
                        user.put("email", email);
                        user.put("uid", uid);

                        db.collection("users").document(uid).set(user)
                                .addOnSuccessListener(aVoid -> {
                                    localDataSource.saveLoginState(true);
                                    callback.onSuccess();
                                })
                                .addOnFailureListener(e -> callback.onFailure("Auth succeeded but failed to save profile"));
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Registration failed";
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
                        callback.onFailure(error);
                    }
                });
    }

    @Override
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    @Override
    public void signOut() {
        firebaseAuth.signOut();
        localDataSource.saveLoginState(false);
    }

    @Override
    public void uploadProfileImage(Uri imageUri, AuthCallback callback) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            callback.onFailure("User not logged in");
            return;
        }

        StorageReference ref = FirebaseStorage.getInstance().getReference().child("profile_pics/" + user.getUid());
        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(downloadUri)
                            .build();
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) callback.onSuccess();
                                else callback.onFailure("Failed to update profile info");
                            });
                }))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    @Override
    public void getUserDetails(UserDataCallback callback) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            callback.onDataFetched("Guest", "N/A", "guest@mealway.com", null);
            return;
        }

        FirebaseFirestore.getInstance().collection("users").document(user.getUid()).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        callback.onDataFetched(
                                doc.getString("fullName"),
                                doc.getString("phone"),
                                user.getEmail(),
                                user.getPhotoUrl()
                        );
                    } else {
                        callback.onDataFetched(user.getDisplayName(), "Not set", user.getEmail(), user.getPhotoUrl());
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
