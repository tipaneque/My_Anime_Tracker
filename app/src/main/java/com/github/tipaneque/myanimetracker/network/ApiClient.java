package com.github.tipaneque.myanimetracker.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://api.myanimelist.net/v2/";
    private static final String BASE_OAUTH_URL = "https://myanimelist.net/v1/oauth2/";
    private static final String CLIENT_ID = "289851d1277b1be37bae3f85a515c9f7";

    private static Retrofit retrofit = null;

    // NO ApiClient.java - ADICIONAR CLIENT ID NAS REQUESTS PÚBLICAS
    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        okhttp3.Request original = chain.request();
                        okhttp3.Request.Builder requestBuilder = original.newBuilder()
                                .header("X-MAL-CLIENT-ID", CLIENT_ID) // ← ADICIONAR ESTE HEADER
                                .method(original.method(), original.body());
                        return chain.proceed(requestBuilder.build());
                    })
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClientWithAuth(String accessToken) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    okhttp3.Request original = chain.request();
                    okhttp3.Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", "Bearer " + accessToken)
                            .method(original.method(), original.body());
                    return chain.proceed(requestBuilder.build());
                })
                .addInterceptor(logging)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // CLIENTE OAuth COM HTTP BASIC AUTH (Scheme 1)
    public static Retrofit getOAuthClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Scheme 1: HTTP Basic Auth
        String credentials = CLIENT_ID + ":";
        String basicAuth = "Basic " + android.util.Base64.encodeToString(
                credentials.getBytes(),
                android.util.Base64.NO_WRAP
        );

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    okhttp3.Request original = chain.request();
                    okhttp3.Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", basicAuth)
                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .method(original.method(), original.body());
                    return chain.proceed(requestBuilder.build());
                })
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_OAUTH_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // CLIENTE OAuth ALTERNATIVO (Scheme 2)
    public static Retrofit getOAuthClientWithBodyAuth() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_OAUTH_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}