package com.example.mealway.data.remote.firebase;

import com.example.mealway.data.model.Meal;
import com.example.mealway.data.model.MealAppointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public class FirebaseManager {
    private final FirebaseFirestore db;
    private final String userId;

    public FirebaseManager() {
        this.db = FirebaseFirestore.getInstance();
        this.userId = FirebaseAuth.getInstance().getUid();
    }

    // Favorites Sync
    public Completable addFavorite(Meal meal) {
        if (userId == null) return Completable.complete();
        return Completable.create(emitter -> {
            db.collection("users").document(userId)
                    .collection("favorites").document(meal.getIdMeal())
                    .set(meal)
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Completable removeFavorite(String mealId) {
        if (userId == null) return Completable.complete();
        return Completable.create(emitter -> {
            db.collection("users").document(userId)
                    .collection("favorites").document(mealId)
                    .delete()
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    // Appointments Sync
    public Completable addAppointment(MealAppointment appointment) {
        if (userId == null) return Completable.complete();
        return Completable.create(emitter -> {
            db.collection("users").document(userId)
                    .collection("appointments").document(appointment.getId())
                    .set(appointment)
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Completable removeAppointment(String appointmentId) {
        if (userId == null) return Completable.complete();
        return Completable.create(emitter -> {
            db.collection("users").document(userId)
                    .collection("appointments").document(appointmentId)
                    .delete()
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }
}
