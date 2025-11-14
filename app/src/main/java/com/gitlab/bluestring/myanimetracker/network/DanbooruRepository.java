package com.gitlab.bluestring.myanimetracker.network;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

import com.gitlab.bluestring.myanimetracker.model.FavoriteResponse;

public class DanbooruRepository {
    private final DanbooruApiService apiService;

    public DanbooruRepository() {
        apiService = ApiClient.getApiService();
    }

    // Fetch posts
    public void getPosts(String tags, int limit, final ApiCallback<List<com.gitlab.bluestring.myanimetracker.model.Post>> callback) {
        Call<List<com.gitlab.bluestring.myanimetracker.model.Post>> call = apiService.getPosts(tags, limit);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<com.gitlab.bluestring.myanimetracker.model.Post>> call, Response<List<com.gitlab.bluestring.myanimetracker.model.Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch posts: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<com.gitlab.bluestring.myanimetracker.model.Post>> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Fetch popular tags
    public void getPopularTags(int limit, final ApiCallback<List<com.gitlab.bluestring.myanimetracker.model.Tag>> callback) {
        Call<List<com.gitlab.bluestring.myanimetracker.model.Tag>> call = apiService.getPopularTags(limit, "count");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<com.gitlab.bluestring.myanimetracker.model.Tag>> call, @NonNull Response<List<com.gitlab.bluestring.myanimetracker.model.Tag>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch tags: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<com.gitlab.bluestring.myanimetracker.model.Tag>> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Favorite posts
    public void favoritePost(int postId, final ApiCallback<FavoriteResponse> callback) {
        Call<FavoriteResponse> call = apiService.favoritePost(postId, "tipaneque", "nZBeunmtDTxNiYvk5ddpWcKN");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<FavoriteResponse> call, @NonNull Response<FavoriteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed. CSRF auth required " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<FavoriteResponse> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Update post rating
    public void updatePostRating(int postId, String newRating, final ApiCallback<com.gitlab.bluestring.myanimetracker.model.Post> callback) {
        Call<com.gitlab.bluestring.myanimetracker.model.Post> call = apiService.updatePostRating(postId, newRating);
        call.enqueue(new Callback<com.gitlab.bluestring.myanimetracker.model.Post>() {
            @Override
            public void onResponse(@NonNull Call<com.gitlab.bluestring.myanimetracker.model.Post> call, @NonNull Response<com.gitlab.bluestring.myanimetracker.model.Post> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed. CSRF auth required " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<com.gitlab.bluestring.myanimetracker.model.Post> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Remove favorite
    public void unfavoritePost(int postId, final ApiCallback<FavoriteResponse> callback) {
        Call<FavoriteResponse> call = apiService.unfavoritePost(postId);
        call.enqueue(new Callback<FavoriteResponse>() {
            @Override
            public void onResponse(@NonNull Call<FavoriteResponse> call, @NonNull Response<FavoriteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to unfavorite post: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<FavoriteResponse> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Interface for callbacks
    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
