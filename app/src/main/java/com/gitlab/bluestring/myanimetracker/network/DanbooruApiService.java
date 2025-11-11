package com.gitlab.bluestring.myanimetracker.network;

import com.gitlab.bluestring.myanimetracker.model.FavoriteResponse;
import com.gitlab.bluestring.myanimetracker.model.Post;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface DanbooruApiService {

    // === MÉTODO 1: GET - Buscar posts ===
    @GET("posts.json")
    Call<List<Post>> getPosts(
            @Query("tags") String tags,
            @Query("limit") int limit
    );

    // === MÉTODO 2: GET - Buscar tags populares ===
    @GET("tags.json")
    Call<List<com.gitlab.bluestring.myanimetracker.model.Tag>> getPopularTags(
            @Query("limit") int limit,
            @Query("search[order]") String order
    );

    // === MÉTODO 3: GET - Buscar post por ID ===
    @GET("posts/{id}.json")
    Call<Post> getPostById(@Path("id") int postId);

    // === MÉTODO 4: POST - Favoritar post ===
    @FormUrlEncoded
    @POST("favorites.json")
    Call<FavoriteResponse> favoritePost(@Field("post_id") int postId);

    // === MÉTODO 5: PUT - Atualizar post (simulado) ===
    @FormUrlEncoded
    @PUT("posts/{id}.json")
    Call<Post> updatePostRating(
            @Path("id") int postId,
            @Field("post[rating]") String newRating
    );

    // === MÉTODO 6: DELETE - Remover favorito ===
    @DELETE("favorites/{postId}.json")
    Call<FavoriteResponse> unfavoritePost(@Path("postId") int postId);
}