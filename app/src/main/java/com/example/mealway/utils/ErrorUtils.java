package com.example.mealway.utils;

import android.content.Context;
import android.util.Log;

import com.example.mealway.R;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class ErrorUtils {
    
    public static String getAuthErrorMessage(Context context, Exception e) {
        if (e == null) return context.getString(R.string.error_unknown);
        
        if (e instanceof FirebaseAuthInvalidCredentialsException) {
            return context.getString(R.string.error_invalid_credentials);
        } else if (e instanceof FirebaseAuthInvalidUserException) {
            String errorCode = ((FirebaseAuthInvalidUserException) e).getErrorCode();
            if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
                return context.getString(R.string.error_invalid_credentials);
            } else if (errorCode.equals("ERROR_USER_DISABLED")) {
                return "This account has been disabled";
            }
            return context.getString(R.string.error_invalid_credentials);
        } else if (e instanceof FirebaseAuthUserCollisionException) {
            return context.getString(R.string.error_email_already_in_use);
        } else if (e instanceof FirebaseNetworkException) {
            return context.getString(R.string.error_network);
        } else if (e instanceof FirebaseAuthException) {
            String errorCode = ((FirebaseAuthException) e).getErrorCode();
            if (errorCode.equals("ERROR_TOO_MANY_REQUESTS")) {
                return context.getString(R.string.error_too_many_requests);
            }
        }
        
        Log.e("ErrorUtils", "Unmapped exception: " + e.getMessage());
        return e.getMessage() != null ? e.getMessage() : context.getString(R.string.error_unknown);
    }
}
