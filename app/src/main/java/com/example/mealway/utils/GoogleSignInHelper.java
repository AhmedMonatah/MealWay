package com.example.mealway.utils;

import android.content.Context;
import android.app.Activity;
import android.util.Log;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.CustomCredential;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.example.mealway.R;
import java.util.concurrent.Executor;
import androidx.core.content.ContextCompat;
import androidx.credentials.exceptions.ClearCredentialException;
import androidx.credentials.exceptions.GetCredentialException;

import android.os.CancellationSignal;

public class GoogleSignInHelper {

    public interface CredentialCallback {
        void onSuccess(String idToken);
        void onFailure(String error);
    }

    public static void signIn(Activity activity, CredentialCallback callback) {
        CredentialManager credentialManager = CredentialManager.create(activity);

        String clientId = activity.getString(R.string.default_web_client_id);
        //Log.d("GoogleSignIn", "Using Client ID: " + clientId);

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(clientId)
                .setAutoSelectEnabled(false)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        CancellationSignal cancellationSignal = new CancellationSignal();
        Executor executor = ContextCompat.getMainExecutor(activity);

        credentialManager.getCredentialAsync(
                activity,
                request,
                cancellationSignal,
                executor,
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        try {
                            if (result.getCredential() instanceof CustomCredential) {
                                CustomCredential credential = (CustomCredential) result.getCredential();
                                if (credential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
                                    GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.getData());
                                    callback.onSuccess(googleIdTokenCredential.getIdToken());
                                } else {
                                    callback.onFailure("Unexpected credential type");
                                }
                            } else {
                                callback.onFailure("Unexpected credential type");
                            }
                        } catch (Exception e) {
                            callback.onFailure(e.getMessage());
                        }
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        Log.e("GoogleSignIn", "Sign In Failed", e);
                        callback.onFailure(e.getClass().getSimpleName() + ": " + e.getMessage());
                    }
                }
        );
    }
    
    public static void signOut(Context context) {
         CredentialManager credentialManager = CredentialManager.create(context);

         credentialManager.clearCredentialStateAsync(
                 new androidx.credentials.ClearCredentialStateRequest(),
                 new CancellationSignal(),
                 ContextCompat.getMainExecutor(context),
                 new CredentialManagerCallback<Void, ClearCredentialException>() {
             @Override
             public void onResult(Void result) {
             }
             
             @Override
             public void onError(ClearCredentialException e) {
             }
         });
    }
}
