package com.github.tipaneque.myanimetracker.auth;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {
    private static final String PREF_NAME = "mal_auth_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_CODE_VERIFIER = "code_verifier";
    private static final String KEY_STATE = "state";

    private SharedPreferences prefs;
    private static AuthManager instance;

    private AuthManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context);
        }
        return instance;
    }

    public void saveTokens(String accessToken, String refreshToken) {
        prefs.edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .apply();
    }

    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    public boolean isLoggedIn() {
        return getAccessToken() != null;
    }

    public void logout() {
        prefs.edit()
                .remove(KEY_ACCESS_TOKEN)
                .remove(KEY_REFRESH_TOKEN)
                .remove(KEY_CODE_VERIFIER)
                .remove(KEY_STATE)
                .apply();
    }

    public void saveAuthSession(String codeVerifier, String state) {
        prefs.edit()
                .putString(KEY_CODE_VERIFIER, codeVerifier)
                .putString(KEY_STATE, state)
                .apply();
    }

    public String getCodeVerifier() {
        return prefs.getString(KEY_CODE_VERIFIER, null);
    }

    public String getState() {
        return prefs.getString(KEY_STATE, null);
    }

    public void clearAuthSession() {
        prefs.edit()
                .remove(KEY_CODE_VERIFIER)
                .remove(KEY_STATE)
                .apply();
    }
}
