package com.example.mealway.util;

import android.content.Context;
import com.example.mealway.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class GoogleSignInHelper {

    private static GoogleSignInClient googleSignInClient;

    public static GoogleSignInClient getGoogleSignInClient(Context context) {
        if (googleSignInClient == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(context.getApplicationContext(), gso);
        }
        return googleSignInClient;
    }

    public static void signOut(Context context) {
        getGoogleSignInClient(context).signOut();
    }
}
