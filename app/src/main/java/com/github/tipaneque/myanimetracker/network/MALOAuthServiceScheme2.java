package com.github.tipaneque.myanimetracker.network;

import com.github.tipaneque.myanimetracker.models.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Interface para OAuth Scheme 2 - Client credentials in request body
 * Client authentication via client_id and client_secret in form data
 */
public interface MALOAuthServiceScheme2 {

    /**
     * Exchange authorization code for access token using client credentials in body
     *
     * @param clientId The client ID from MAL app registration
     * @param clientSecret Empty string for Android apps (no client secret)
     * @param grantType MUST be "authorization_code"
     * @param code The authorization code received from MAL
     * @param redirectUri The same redirect URI used in authorization request
     * @param codeVerifier The PKCE code verifier (43-128 characters)
     * @return TokenResponse with access_token and refresh_token
     */
    @FormUrlEncoded
    @POST("token")
    Call<TokenResponse> getAccessToken(
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret, // Empty for Android apps
            @Field("grant_type") String grantType,
            @Field("code") String code,
            @Field("redirect_uri") String redirectUri,
            @Field("code_verifier") String codeVerifier
    );
}