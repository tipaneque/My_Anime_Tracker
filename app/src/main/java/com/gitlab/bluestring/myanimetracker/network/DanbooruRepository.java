package com.gitlab.bluestring.myanimetracker.network;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

import com.gitlab.bluestring.myanimetracker.model.FavoriteResponse;

public class DanbooruRepository {
    private DanbooruApiService apiService;

    public DanbooruRepository() {
        apiService = ApiClient.getApiService();
    }

    // === MÉTODO 1: GET - Buscar posts ===
    public void getPosts(String tags, int limit, final ApiCallback<List<com.gitlab.bluestring.myanimetracker.model.Post>> callback) {
        Call<List<com.gitlab.bluestring.myanimetracker.model.Post>> call = apiService.getPosts(tags, limit);
        call.enqueue(new Callback<List<com.gitlab.bluestring.myanimetracker.model.Post>>() {
            @Override
            public void onResponse(Call<List<com.gitlab.bluestring.myanimetracker.model.Post>> call, Response<List<com.gitlab.bluestring.myanimetracker.model.Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch posts: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<com.gitlab.bluestring.myanimetracker.model.Post>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // === MÉTODO 2: GET - Buscar tags populares ===
    public void getPopularTags(int limit, final ApiCallback<List<com.gitlab.bluestring.myanimetracker.model.Tag>> callback) {
        Call<List<com.gitlab.bluestring.myanimetracker.model.Tag>> call = apiService.getPopularTags(limit, "count");
        call.enqueue(new Callback<List<com.gitlab.bluestring.myanimetracker.model.Tag>>() {
            @Override
            public void onResponse(Call<List<com.gitlab.bluestring.myanimetracker.model.Tag>> call, Response<List<com.gitlab.bluestring.myanimetracker.model.Tag>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch tags: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<com.gitlab.bluestring.myanimetracker.model.Tag>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // === MÉTODO 3: POST - Favoritar post ===
    public void favoritePost(int postId, final ApiCallback<FavoriteResponse> callback) {
        Call<FavoriteResponse> call = apiService.favoritePost(postId);
        call.enqueue(new Callback<FavoriteResponse>() {
            @Override
            public void onResponse(Call<FavoriteResponse> call, Response<FavoriteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to favorite post: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<FavoriteResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // === MÉTODO 4: PUT - Atualizar rating do post ===
    public void updatePostRating(int postId, String newRating, final ApiCallback<com.gitlab.bluestring.myanimetracker.model.Post> callback) {
        Call<com.gitlab.bluestring.myanimetracker.model.Post> call = apiService.updatePostRating(postId, newRating);
        call.enqueue(new Callback<com.gitlab.bluestring.myanimetracker.model.Post>() {
            @Override
            public void onResponse(Call<com.gitlab.bluestring.myanimetracker.model.Post> call, Response<com.gitlab.bluestring.myanimetracker.model.Post> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to update post: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<com.gitlab.bluestring.myanimetracker.model.Post> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // === MÉTODO 5: DELETE - Remover favorito ===
    public void unfavoritePost(int postId, final ApiCallback<FavoriteResponse> callback) {
        Call<FavoriteResponse> call = apiService.unfavoritePost(postId);
        call.enqueue(new Callback<FavoriteResponse>() {
            @Override
            public void onResponse(Call<FavoriteResponse> call, Response<FavoriteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to unfavorite post: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<FavoriteResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Interface para callbacks
    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
