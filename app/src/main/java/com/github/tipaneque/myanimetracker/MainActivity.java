package com.github.tipaneque.myanimetracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.tipaneque.myanimetracker.auth.AuthManager;
import com.github.tipaneque.myanimetracker.models.TokenResponse;
import com.github.tipaneque.myanimetracker.network.ApiClient;
import com.github.tipaneque.myanimetracker.network.MALOAuthService;
import com.github.tipaneque.myanimetracker.network.MALOAuthServiceScheme2;
import com.github.tipaneque.myanimetracker.utils.PKCEUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "289851d1277b1be37bae3f85a515c9f7";
    private static final String REDIRECT_URI = "myanimetracker://auth";

    private AuthManager authManager;
    private Button btnLogin, btnMyList, btnSearch, btnTrending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authManager = AuthManager.getInstance(this);
        initializeViews();
        setupClickListeners();
        checkAuthState();
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void initializeViews() {
        btnLogin = findViewById(R.id.btnLogin);
        btnMyList = findViewById(R.id.btnMyList);
        btnSearch = findViewById(R.id.btnSearch);
        btnTrending = findViewById(R.id.btnTrending);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> {
            if (authManager.isLoggedIn()) {
                logout();
            } else {
                startMALAuthentication();
            }
        });

        btnMyList.setOnClickListener(v -> {
            if (authManager.isLoggedIn()) {
                startActivity(new Intent(this, AnimeListActivity.class));
            } else {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            }
        });

        btnSearch.setOnClickListener(v -> {
            startActivity(new Intent(this, SearchActivity.class));
        });

        btnTrending.setOnClickListener(v -> {
            openTrendingActivity();
        });
    }

    private void openTrendingActivity() {
        Intent intent = new Intent(this, TrendingActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void checkAuthState() {
        if (authManager.isLoggedIn()) {
            btnLogin.setText("Logout");
            btnMyList.setBackgroundResource(R.drawable.btn_my_list_enabled);
            btnMyList.setTextColor(getColor(R.color.text_light));
            btnMyList.setEnabled(true);
        } else {
            btnLogin.setText("Login with MAL");
            btnMyList.setBackgroundResource(R.drawable.btn_my_list_disabled);
            btnMyList.setTextColor(getColor(R.color.text_secondary));
            btnMyList.setEnabled(false);
        }
    }

    private void startMALAuthentication() {
        String codeVerifier = PKCEUtils.generateCodeVerifier();
        String state = PKCEUtils.generateRandomState();

        authManager.saveAuthSession(codeVerifier, state);

        String authUrl = "https://myanimelist.net/v1/oauth2/authorize?" +
                "response_type=code" +
                "&client_id=" + CLIENT_ID +
                "&redirect_uri=" + Uri.encode(REDIRECT_URI) +
                "&code_challenge=" + codeVerifier +
                "&code_challenge_method=plain" +
                "&state=" + state;

        Log.d("OAuth", "Using PLAIN method as per documentation");

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
        startActivity(intent);
    }

    private void handleIntent(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null && "myanimetracker".equals(uri.getScheme()) && "auth".equals(uri.getHost())) {
            String authorizationCode = uri.getQueryParameter("code");
            String receivedState = uri.getQueryParameter("state");
            String error = uri.getQueryParameter("error");
            String errorDescription = uri.getQueryParameter("error_description");

            Log.d("OAuth", "Redirect received - Code: " + authorizationCode);
            Log.d("OAuth", "State: " + receivedState);
            Log.d("OAuth", "Error: " + error);

            if (error != null) {
                Toast.makeText(this, "Auth error: " + error + " - " + errorDescription, Toast.LENGTH_LONG).show();
            } else if (authorizationCode != null && receivedState != null) {
                // Validate state for security.
                String savedState = authManager.getState();
                if (receivedState.equals(savedState)) {
                    exchangeCodeForToken(authorizationCode);
                } else {
                    Log.e("OAuth", "State mismatch - Saved: " + savedState + " Received: " + receivedState);
                    Toast.makeText(this, "Security validation failed", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "No authorization code received", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void exchangeCodeForToken(String authorizationCode) {
        String codeVerifier = authManager.getCodeVerifier();

        Log.d("OAuth", "=== TOKEN EXCHANGE ===");
        Log.d("OAuth", "Using HTTP Basic Auth (Scheme 1)");

        // FIRST: Try Scheme 1 (HTTP Basic Auth)
        MALOAuthService oauthService = ApiClient.getOAuthClient().create(MALOAuthService.class);

        Call<TokenResponse> call = oauthService.getAccessToken(
                "authorization_code",
                authorizationCode,
                REDIRECT_URI,
                codeVerifier
        );

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("OAuth", "🎉 SUCCESS with HTTP Basic Auth!");
                    handleTokenSuccess(response.body());
                } else {
                    Log.e("OAuth", "Scheme 1 failed, trying Scheme 2...");
                    tryScheme2(authorizationCode, codeVerifier);
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Log.e("OAuth", "Scheme 1 network error: " + t.getMessage());
                tryScheme2(authorizationCode, codeVerifier);
            }
        });
    }

    private void tryScheme2(String authorizationCode, String codeVerifier) {
        // SECOND: Try Scheme 2 (client in the body)
        MALOAuthServiceScheme2 oauthService = ApiClient.getOAuthClientWithBodyAuth().create(MALOAuthServiceScheme2.class);

        Call<TokenResponse> call = oauthService.getAccessToken(
                CLIENT_ID,
                "", // Empty client_secret for Android
                "authorization_code",
                authorizationCode,
                REDIRECT_URI,
                codeVerifier
        );

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("OAuth", "🎉 SUCCESS with Scheme 2!");
                    handleTokenSuccess(response.body());
                } else {
                    Log.e("OAuth", " Both schemes failed");
                    handleTokenError(response);
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Log.e("OAuth", "Scheme 2 network error: " + t.getMessage());
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_LONG).show());
            }
        });
    }

    private void handleTokenSuccess(TokenResponse tokenResponse) {
        authManager.saveTokens(tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());
        authManager.clearAuthSession();

        runOnUiThread(() -> {
            Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
            checkAuthState();
        });
    }

    private void handleTokenError(Response<TokenResponse> response) {
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
            Log.e("OAuth", "Final error: " + response.code() + " - " + errorBody);
        } catch (Exception e) {
            e.printStackTrace();
        }

        runOnUiThread(() ->
                Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_LONG).show());
    }

    private void logout() {
        authManager.logout();
        checkAuthState();
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
    }
}