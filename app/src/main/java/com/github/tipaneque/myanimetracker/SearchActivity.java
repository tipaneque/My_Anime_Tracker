package com.github.tipaneque.myanimetracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.tipaneque.myanimetracker.Adapters.AnimeAdapter;
import com.github.tipaneque.myanimetracker.models.Anime;
import com.github.tipaneque.myanimetracker.models.AnimeSearchResponse;
import com.github.tipaneque.myanimetracker.network.ApiClient;
import com.github.tipaneque.myanimetracker.network.ApiService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private EditText etSearch;
    private RecyclerView recyclerView;
    private AnimeAdapter adapter;
    private Handler handler = new Handler(Looper.getMainLooper());
    private static final int DEBOUNCE_DELAY = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Log.d("SearchActivity", "Activity created");

        initializeViews();
        setupRecyclerView();
        setupSearchListener();
    }

    private void initializeViews() {
        etSearch = findViewById(R.id.etSearch);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d("SearchActivity", "Views initialized");
    }

    private void setupRecyclerView() {
        adapter = new AnimeAdapter(new ArrayList<>(), false);

        //ADICIONAR ESTE LISTENER DE CLIQUE - CRÍTICO!
        adapter.setOnItemClickListener(new AnimeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Anime anime) {
                Log.d("SearchActivity", " Search result clicked: " + anime.getTitle() + " (ID: " + anime.getId() + ")");
                openAnimeDetails(anime);
            }
        });

        recyclerView.setAdapter(adapter);
        Log.d("SearchActivity", "RecyclerView setup with click listener");
    }

    private void openAnimeDetails(Anime anime) {
        Log.d("SearchActivity", "Opening AnimeDetailActivity for: " + anime.getTitle());

        Intent intent = new Intent(SearchActivity.this, AnimeDetailActivity.class);
        intent.putExtra("anime_id", anime.getId());
        intent.putExtra("anime_title", anime.getTitle());
        startActivity(intent);

        // Animação opcional
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void setupSearchListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(final Editable s) {
                // Remove callbacks anteriores
                handler.removeCallbacksAndMessages(null);

                // Agenda nova busca
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String query = s.toString().trim();
                        if (query.length() >= 3) {
                            searchAnime(query);
                        } else if (query.isEmpty()) {
                            adapter.updateData(new ArrayList<>());
                            Log.d("SearchActivity", "Search cleared");
                        }
                    }
                }, DEBOUNCE_DELAY);
            }
        });
    }

    private void searchAnime(String query) {
        Log.d("SearchActivity", "Searching for: " + query);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<AnimeSearchResponse> call = apiService.searchAnime(
                query,
                20,
                0,
                "id,title,main_picture,synopsis,mean,media_type,episodes,status"
        );

        call.enqueue(new Callback<AnimeSearchResponse>() {
            @Override
            public void onResponse(Call<AnimeSearchResponse> call, Response<AnimeSearchResponse> response) {
                Log.d("SearchActivity", "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    AnimeSearchResponse searchResponse = response.body();
                    java.util.List<com.github.tipaneque.myanimetracker.models.AnimeNode> data = searchResponse.getData();

                    Log.d("SearchActivity", "Search successful! Found " + (data != null ? data.size() : 0) + " results");

                    if (data != null && !data.isEmpty()) {
                        for (com.github.tipaneque.myanimetracker.models.AnimeNode node : data) {
                            if (node.getNode() != null) {
                                Log.d("SearchActivity", "Found: " + node.getNode().getTitle());
                            }
                        }
                        adapter.updateData(data);

                        // Log para verificar se o adapter tem dados
                        Log.d("SearchActivity", "Adapter updated with " + data.size() + " items. Click listener: " +
                                (adapter != null ? "SET" : "NULL"));
                    } else {
                        Log.d("SearchActivity", "No results found for: " + query);
                        adapter.updateData(new ArrayList<>());
                        runOnUiThread(() ->
                                Toast.makeText(SearchActivity.this, "No results found", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    Log.e("SearchActivity", "Search failed with code: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("SearchActivity", "Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e("SearchActivity", "Error reading error body: " + e.getMessage());
                    }

                    runOnUiThread(() ->
                            Toast.makeText(SearchActivity.this, "Search failed", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<AnimeSearchResponse> call, Throwable t) {
                Log.e("SearchActivity", "Network error: " + t.getMessage());
                runOnUiThread(() ->
                        Toast.makeText(SearchActivity.this, "Network error", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
