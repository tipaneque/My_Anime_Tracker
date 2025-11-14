package com.gitlab.bluestring.myanimetracker.network;

import com.gitlab.bluestring.myanimetracker.model.FavoriteResponse;
import com.gitlab.bluestring.myanimetracker.model.Post;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface DanbooruApiService {

    // Fetch posts
    @GET("posts.json")
    Call<List<Post>> getPosts(
            @Query("tags") String tags,
            @Query("limit") int limit
    );

    // Fetch popular tags
    @GET("tags.json")
    Call<List<com.gitlab.bluestring.myanimetracker.model.Tag>> getPopularTags(
            @Query("limit") int limit,
            @Query("search[order]") String order
    );

    // Fetch post by id
    @GET("posts/{id}.json")
    Call<Post> getPostById(@Path("id") int postId);

    // ===  POST - Favoritar post ===
    @FormUrlEncoded
    @POST("favorites.json")
    Call<FavoriteResponse> favoritePost(
            @Field("post_id") int postId,
            @Field("login") String username,
            @Field("api_key") String apiKey
    );

    // update post
    @FormUrlEncoded
    @PUT("posts/{id}.json")
    Call<Post> updatePostRating(
            @Path("id") int postId,
            @Field("post[rating]") String newRating
    );

    // Remove favorite
    @DELETE("favorites/{postId}.json")
    Call<FavoriteResponse> unfavoritePost(@Path("postId") int postId);
}