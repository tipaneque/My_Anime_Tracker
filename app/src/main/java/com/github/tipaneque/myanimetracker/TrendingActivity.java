package com.github.tipaneque.myanimetracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.tipaneque.myanimetracker.Adapters.AnimeAdapter;
import com.github.tipaneque.myanimetracker.models.Anime;
import com.github.tipaneque.myanimetracker.models.AnimeRankingResponse;
import com.github.tipaneque.myanimetracker.models.RankingAnimeNode;
import com.github.tipaneque.myanimetracker.network.ApiClient;
import com.github.tipaneque.myanimetracker.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrendingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AnimeAdapter adapter;
    private ProgressBar progressBar, progressBarBottom;
    private TextView emptyText;
    private Button btnLoadMore;

    private int currentOffset = 0;
    private final int LIMIT = 50;
    private boolean isLoading = false;
    private boolean hasMoreData = true;
    private String currentRankingType = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending);

        initializeViews();
        setupRecyclerView();
        loadTrendingAnime(currentRankingType, 0, true); // Primeiro carregamento
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        progressBarBottom = findViewById(R.id.progressBarBottom);
        emptyText = findViewById(R.id.emptyText);
        btnLoadMore = findViewById(R.id.btnLoadMore);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configurar botão "Load More"
        btnLoadMore.setOnClickListener(v -> {
            if (!isLoading && hasMoreData) {
                loadMoreAnime();
            }
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new AnimeAdapter(new ArrayList<>(), false);
        adapter.setOnItemClickListener(anime -> openAnimeDetails(anime));
        recyclerView.setAdapter(adapter);

        // Scroll listener para carregar mais automaticamente
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                // Carregar mais quando estiver perto do final
                if (!isLoading && hasMoreData) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5
                            && firstVisibleItemPosition >= 0) {
                        loadMoreAnime();
                    }
                }
            }
        });
    }

    private void loadTrendingAnime(String rankingType, int offset, boolean showLoading) {
        if (isLoading) return;

        isLoading = true;
        currentRankingType = rankingType;

        if (showLoading) {
            showMainLoading(true);
        } else {
            showBottomLoading(true);
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<AnimeRankingResponse> call = apiService.getAnimeRanking(
                rankingType,
                LIMIT,
                offset,
                "id,title,main_picture,synopsis,mean,media_type,episodes,status,rank"
        );

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<AnimeRankingResponse> call, Response<AnimeRankingResponse> response) {
                isLoading = false;
                showMainLoading(false);
                showBottomLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    AnimeRankingResponse rankingResponse = response.body();
                    List<RankingAnimeNode> rankingData = rankingResponse.getData();

                    if (rankingData != null && !rankingData.isEmpty()) {
                        List<Anime> newAnimeList = new ArrayList<>();
                        for (RankingAnimeNode node : rankingData) {
                            if (node.getNode() != null) {
                                newAnimeList.add(node.getNode());
                            }
                        }

                        if (offset == 0) {
                            // Primeiro carregamento - substituir lista
                            adapter.updateData(newAnimeList);
                        } else {
                            // Carregamento adicional - adicionar à lista
                            adapter.addData(newAnimeList);
                        }

                        // Verificar se há mais dados
                        hasMoreData = newAnimeList.size() == LIMIT;
                        currentOffset = offset + newAnimeList.size();

                        showEmptyState(false);
                        updateLoadMoreButton();

                        Log.d("Trending", "Loaded " + newAnimeList.size() + " anime. Total: " + currentOffset);

                    } else {
                        // Não há mais dados
                        hasMoreData = false;
                        if (offset == 0) {
                            showEmptyState(true);
                        }
                        updateLoadMoreButton();
                    }
                } else {
                    handleLoadError();
                }
            }

            @Override
            public void onFailure(Call<AnimeRankingResponse> call, Throwable t) {
                isLoading = false;
                showMainLoading(false);
                showBottomLoading(false);
                handleLoadError();
            }
        });
    }

    private void loadMoreAnime() {
        if (!isLoading && hasMoreData) {
            loadTrendingAnime(currentRankingType, currentOffset, false);
        }
    }

    private void updateLoadMoreButton() {
        runOnUiThread(() -> {
            if (hasMoreData) {
                btnLoadMore.setText("Load More (" + currentOffset + "+)");
                btnLoadMore.setVisibility(View.VISIBLE);
            } else {
                btnLoadMore.setVisibility(View.GONE);
            }
        });
    }

    private void handleLoadError() {
        Toast.makeText(TrendingActivity.this, "Failed to load anime", Toast.LENGTH_SHORT).show();
        hasMoreData = false;
        updateLoadMoreButton();
    }

    private void showMainLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.GONE);
        }
    }

    private void showBottomLoading(boolean show) {
        progressBarBottom.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLoadMore.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmptyState(boolean show) {
        emptyText.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void openAnimeDetails(Anime anime) {
        Intent intent = new Intent(TrendingActivity.this, AnimeDetailActivity.class);
        intent.putExtra("anime_id", anime.getId());
        intent.putExtra("anime_title", anime.getTitle());
        startActivity(intent);
    }

}
