package com.gitlab.bluestring.myanimetracker.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

import com.gitlab.bluestring.myanimetracker.network.DanbooruRepository;

public class PostViewModel extends ViewModel {
    private DanbooruRepository repository;

    private MutableLiveData<List<Post>> posts = new MutableLiveData<>();
    private MutableLiveData<List<Tag>> tags = new MutableLiveData<>();
    private MutableLiveData<Post> currentPost = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<FavoriteResponse> favoriteResult = new MutableLiveData<>();

    public PostViewModel() {
        repository = new DanbooruRepository();
        isLoading.setValue(false);
    }

    // === MÉTODO 1: GET - Carregar posts ===
    public void loadPosts(String tags) {
        isLoading.setValue(true);
        repository.getPosts(tags, 20, new DanbooruRepository.ApiCallback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> result) {
                isLoading.setValue(false);
                posts.setValue(result);
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue("GET Error: " + error);
            }
        });
    }

    // === MÉTODO 2: GET - Carregar tags populares ===
    public void loadPopularTags() {
        repository.getPopularTags(15, new DanbooruRepository.ApiCallback<List<Tag>>() {
            @Override
            public void onSuccess(List<Tag> result) {
                tags.setValue(result);
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue("GET Tags Error: " + error);
            }
        });
    }

    // === MÉTODO 3: POST - Favoritar post ===
    public void favoritePost(int postId) {
        repository.favoritePost(postId, new DanbooruRepository.ApiCallback<FavoriteResponse>() {
            @Override
            public void onSuccess(FavoriteResponse result) {
                favoriteResult.setValue(result);
                // Atualizar estado local
                updatePostFavoriteStatus(postId, true);
            }

            @Override
            public void onError(String error) {
                favoriteResult.setValue(new FavoriteResponse(false, error));
            }
        });
    }

    // === MÉTODO 4: PUT - Atualizar rating do post ===
    public void updatePostRating(int postId, String newRating) {
        repository.updatePostRating(postId, newRating, new DanbooruRepository.ApiCallback<Post>() {
            @Override
            public void onSuccess(Post result) {
                currentPost.setValue(result);
                // Atualizar na lista também
                updatePostInList(result);
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue("PUT Error: " + error);
            }
        });
    }

    // === MÉTODO 5: DELETE - Remover favorito ===
    public void unfavoritePost(int postId) {
        repository.unfavoritePost(postId, new DanbooruRepository.ApiCallback<FavoriteResponse>() {
            @Override
            public void onSuccess(FavoriteResponse result) {
                favoriteResult.setValue(result);
                // Atualizar estado local
                updatePostFavoriteStatus(postId, false);
            }

            @Override
            public void onError(String error) {
                favoriteResult.setValue(new FavoriteResponse(false, error));
            }
        });
    }

    // Métodos auxiliares para atualizar estado local
    private void updatePostFavoriteStatus(int postId, boolean isFavorited) {
        List<Post> currentPosts = posts.getValue();
        if (currentPosts != null) {
            for (int i = 0; i < currentPosts.size(); i++) {
                Post post = currentPosts.get(i);
                if (post.getId() == postId) {
                    post.setIsFavorited(isFavorited);
                    break;
                }
            }
            posts.setValue(currentPosts);
        }
    }

    private void updatePostInList(Post updatedPost) {
        List<Post> currentPosts = posts.getValue();
        if (currentPosts != null) {
            for (int i = 0; i < currentPosts.size(); i++) {
                if (currentPosts.get(i).getId() == updatedPost.getId()) {
                    currentPosts.set(i, updatedPost);
                    break;
                }
            }
            posts.setValue(currentPosts);
        }
    }

    // Getters para LiveData
    public LiveData<List<Post>> getPosts() { return posts; }
    public LiveData<List<Tag>> getTags() { return tags; }
    public LiveData<Post> getCurrentPost() { return currentPost; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<FavoriteResponse> getFavoriteResult() { return favoriteResult; }
}