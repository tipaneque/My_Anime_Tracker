package com.github.tipaneque.myanimetracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.tipaneque.myanimetracker.Adapters.AnimeAdapter;
import com.github.tipaneque.myanimetracker.auth.AuthManager;
import com.github.tipaneque.myanimetracker.models.Anime;
import com.github.tipaneque.myanimetracker.models.UserAnimeListItem;
import com.github.tipaneque.myanimetracker.models.UserAnimeListResponse;
import com.github.tipaneque.myanimetracker.network.ApiClient;
import com.github.tipaneque.myanimetracker.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimeListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AnimeAdapter adapter;
    private AuthManager authManager;
    private TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_list);

        authManager = AuthManager.getInstance(this);
        initializeViews();
        setupRecyclerView();
        loadUserAnimeList();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        emptyText = findViewById(R.id.emptyText);

        // Set RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.btnBackList).setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new AnimeAdapter(new ArrayList<>(), true);

        // Open AnimeDetailActivity when clicking on an anime.
        adapter.setOnItemClickListener(this::openAnimeDetails);

        recyclerView.setAdapter(adapter);
    }

    private void openAnimeDetails(Anime anime) {
        Log.d("AnimeList", "Opening details for: " + anime.getTitle());

        Intent intent = new Intent(AnimeListActivity.this, AnimeDetailActivity.class);
        intent.putExtra("anime_id", anime.getId());
        intent.putExtra("anime_title", anime.getTitle());
        startActivity(intent);

        // Optional: add animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void loadUserAnimeList() {
        String accessToken = authManager.getAccessToken();
        if (accessToken == null) {
            Toast.makeText(this, "Not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("AnimeList", "Loading user anime list...");

        ApiService apiService = ApiClient.getClientWithAuth(accessToken).create(ApiService.class);
        Call<UserAnimeListResponse> call = apiService.getUserAnimeList(
                "list_status,mean,media_type,episodes,status",
                null, // todos os status
                100,
                0
        );

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<UserAnimeListResponse> call, Response<UserAnimeListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserAnimeListResponse animeResponse = response.body();
                    List<UserAnimeListItem> animeList = animeResponse.getData();

                    Log.d("AnimeList", "Response received. Items count: " + (animeList != null ? animeList.size() : "null"));

                    if (animeList != null && !animeList.isEmpty()) {
                        adapter.updateData(animeList);
                        showEmptyState(false);
                        Log.d("AnimeList", "Updated adapter with " + animeList.size() + " items");
                    } else {
                        Log.d("AnimeList", "No anime found in list");
                        showEmptyState(true);
                    }
                } else {
                    Log.e("AnimeList", "Failed to load list. Code: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("AnimeList", "Error: " + errorBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(AnimeListActivity.this, "Failed to load list", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserAnimeListResponse> call, Throwable t) {
                Log.e("AnimeList", "Network error: " + t.getMessage());
                Toast.makeText(AnimeListActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmptyState(boolean show) {
        runOnUiThread(() -> {
            if (emptyText != null) {
                if (show) {
                    emptyText.setText("Your anime list is empty. Start by adding some anime!");
                    emptyText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
