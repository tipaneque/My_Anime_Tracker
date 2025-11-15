package com.github.tipaneque.myanimetracker.network;

import com.github.tipaneque.myanimetracker.models.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Interface para OAuth Scheme 1 - HTTP Basic Authentication
 * Client authentication via HTTP Basic Auth header
 */
public interface MALOAuthService {

    /**
     * Exchange authorization code for access token using HTTP Basic Auth
     *
     * @param grantType MUST be "authorization_code"
     * @param code The authorization code received from MAL
     * @param redirectUri The same redirect URI used in authorization request
     * @param codeVerifier The PKCE code verifier (43-128 characters)
     * @return TokenResponse with access_token and refresh_token
     */
    @FormUrlEncoded
    @POST("token")
    Call<TokenResponse> getAccessToken(
            @Field("grant_type") String grantType,
            @Field("code") String code,
            @Field("redirect_uri") String redirectUri,
            @Field("code_verifier") String codeVerifier
            // Note: client_id is passed via HTTP Basic Auth header, not in form data
    );
}