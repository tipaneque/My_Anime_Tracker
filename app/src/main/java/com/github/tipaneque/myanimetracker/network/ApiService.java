package com.github.tipaneque.myanimetracker.network;

import com.github.tipaneque.myanimetracker.models.Anime;
import com.github.tipaneque.myanimetracker.models.AnimeRankingResponse;
import com.github.tipaneque.myanimetracker.models.AnimeSearchResponse;
import com.github.tipaneque.myanimetracker.models.TokenResponse;
import com.github.tipaneque.myanimetracker.models.UpdateAnimeStatusResponse;
import com.github.tipaneque.myanimetracker.models.UserAnimeListResponse;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // GET - Fetch animes
    @GET("anime")
    Call<AnimeSearchResponse> searchAnime(
            @Query("q") String query,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset,
            @Query("fields") String fields
    );

    // GET - Details of one anime
    @GET("anime/{anime_id}")
    Call<Anime> getAnimeDetails(
            @Path("anime_id") int animeId,
            @Query("fields") String fields
    );

    // GET - User's anime list
    @GET("users/@me/animelist")
    Call<UserAnimeListResponse> getUserAnimeList(
            @Query("fields") String fields,
            @Query("status") String status,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    // GET - Trending anime
    @GET("anime/ranking")
    Call<AnimeRankingResponse> getAnimeRanking(
            @Query("ranking_type") String rankingType,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset,
            @Query("fields") String fields
    );

    // POST/PUT - Add/Update anime to the list
    @FormUrlEncoded
    @PUT("anime/{anime_id}/my_list_status")
    Call<UpdateAnimeStatusResponse> updateAnimeStatus(
            @Path("anime_id") int animeId,
            @Field("status") String status,
            @Field("score") int score,
            @Field("num_watched_episodes") int episodesWatched,
            @Field("is_rewatching") boolean isRewatching,
            @Field("start_date") String startDate,
            @Field("finish_date") String finishDate,
            @Field("priority") int priority,
            @Field("num_times_rewatched") int timesRewatched,
            @Field("rewatch_value") int rewatchValue,
            @Field("tags") String tags,
            @Field("comments") String comments
    );

    // DELETE - Remove anime from list
    @DELETE("anime/{anime_id}/my_list_status")
    Call<Void> deleteAnimeFromList(@Path("anime_id") int animeId);

    // OAuth - Token exchange
    @FormUrlEncoded
    @POST("token")
    Call<TokenResponse> getAccessToken(
            @Field("grant_type") String grantType,
            @Field("code") String code,
            @Field("code_verifier") String codeVerifier,
            @Field("redirect_uri") String redirectUri
    );
}
