package com.example.mealway.data.remote.firebase;

import com.example.mealway.data.model.Meal;
import com.example.mealway.data.model.MealAppointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class FirebaseManager {
    private final FirebaseFirestore db;

    public FirebaseManager() {
        this.db = FirebaseFirestore.getInstance();
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    // Favorites Sync
    public Completable addFavorite(Meal meal) {
        String userId = getUserId();
        if (userId == null) return Completable.complete();
        return Completable.create(emitter -> {
            db.collection("users").document(userId)
                    .collection("fav").document(meal.getIdMeal())
                    .set(meal)
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Completable removeFavorite(String mealId) {
        String userId = getUserId();
        if (userId == null) return Completable.complete();
        return Completable.create(emitter -> {
            db.collection("users").document(userId)
                    .collection("fav").document(mealId)
                    .delete()
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    // Appointments Sync
    public Completable addAppointment(MealAppointment appointment) {
        String userId = getUserId();
        if (userId == null) return Completable.complete();
        return Completable.create(emitter -> {
            db.collection("users").document(userId)
                    .collection("appointment").document(appointment.getId())
                    .set(appointment)
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Completable removeAppointment(String appointmentId) {
        String userId = getUserId();
        if (userId == null) return Completable.complete();
        return Completable.create(emitter -> {
            db.collection("users").document(userId)
                    .collection("appointment").document(appointmentId)
                    .delete()
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Single<List<Meal>> getFavorites() {
        String userId = getUserId();
        if (userId == null) return Single.error(new Exception("Not logged in"));
        return Single.create(emitter -> {
            db.collection("users").document(userId)
                    .collection("fav").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        emitter.onSuccess(queryDocumentSnapshots.toObjects(Meal.class));
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Single<List<MealAppointment>> getAppointments() {
        String userId = getUserId();
        if (userId == null) return Single.error(new Exception("Not logged in"));
        return Single.create(emitter -> {
            db.collection("users").document(userId)
                    .collection("appointment").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        emitter.onSuccess(queryDocumentSnapshots.toObjects(MealAppointment.class));
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }
}
