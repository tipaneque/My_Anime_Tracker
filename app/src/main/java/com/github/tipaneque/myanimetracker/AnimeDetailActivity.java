package com.github.tipaneque.myanimetracker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.tipaneque.myanimetracker.auth.AuthManager;
import com.github.tipaneque.myanimetracker.models.Anime;
import com.github.tipaneque.myanimetracker.models.UpdateAnimeStatusResponse;
import com.github.tipaneque.myanimetracker.models.UserAnimeListResponse;
import com.github.tipaneque.myanimetracker.network.ApiClient;
import com.github.tipaneque.myanimetracker.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimeDetailActivity extends AppCompatActivity {

    private int animeId;
    private String animeTitle;
    private AuthManager authManager;

    // Views
    private ImageView ivAnimeCover;
    private TextView tvTitle, tvSynopsis, tvScore, tvEpisodes, tvType, tvStatus;
    private Spinner spStatus, spScore;
    private EditText etEpisodesWatched, etComments;
    private Button btnSave, btnDelete;
    private ProgressBar progressBar;

    // Status options
    private static final String[] STATUS_OPTIONS = {
            "watching", "completed", "on_hold", "dropped", "plan_to_watch"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_detail);

        // Initialize AuthManager
        authManager = AuthManager.getInstance(this);

        // Get Intent data
        animeId = getIntent().getIntExtra("anime_id", 0);
        animeTitle = getIntent().getStringExtra("anime_title");

        if (animeId == 0) {
            Toast.makeText(this, "Invalid anime", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupSpinners();
        setupClickListeners();
        loadAnimeDetails();
    }

    private void initializeViews() {
        ivAnimeCover = findViewById(R.id.ivAnimeCover);
        tvTitle = findViewById(R.id.tvTitle);
        tvSynopsis = findViewById(R.id.tvSynopsis);
        tvScore = findViewById(R.id.tvScore);
        tvEpisodes = findViewById(R.id.tvEpisodes);
        tvType = findViewById(R.id.tvType);
        tvStatus = findViewById(R.id.tvStatus);
        spStatus = findViewById(R.id.spStatus);
        spScore = findViewById(R.id.spScore);
        etEpisodesWatched = findViewById(R.id.etEpisodesWatched);
        etComments = findViewById(R.id.etComments);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        progressBar = findViewById(R.id.progressBar);

        // set initial title
        if (animeTitle != null) {
            tvTitle.setText(animeTitle);
        }
    }

    private void setupSpinners() {
        // Status Spinner
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.anime_status_array, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatus.setAdapter(statusAdapter);

        // Score Spinner (0-10)
        ArrayAdapter<CharSequence> scoreAdapter = ArrayAdapter.createFromResource(this,
                R.array.anime_score_array, android.R.layout.simple_spinner_item);
        scoreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spScore.setAdapter(scoreAdapter);

        // Status changes listener
        spStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateUIBasedOnStatus(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateUIBasedOnStatus(int position) {
        switch (position) {
            case 1: // Watching
                etEpisodesWatched.setEnabled(true);
                etEpisodesWatched.setHint("Currently watching episode...");
                break;
            case 2: // Completed
                etEpisodesWatched.setEnabled(true);
                etEpisodesWatched.setHint("Total episodes watched");
                break;
            case 3: // On Hold
            case 4: // Dropped
                etEpisodesWatched.setEnabled(true);
                etEpisodesWatched.setHint("Episodes watched so far");
                break;
            case 5: // Plan to Watch
                etEpisodesWatched.setEnabled(false);
                etEpisodesWatched.setText("0");
                etEpisodesWatched.setHint("Will start watching");
                break;
            default:
                etEpisodesWatched.setEnabled(false);
                break;
        }
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                updateAnimeStatus();
            }
        });

        btnDelete.setOnClickListener(v -> {
            showDeleteConfirmationDialog();
        });
    }

    private boolean validateInput() {
        String episodesText = etEpisodesWatched.getText().toString().trim();

        if (episodesText.isEmpty()) {
            Toast.makeText(this, "Please enter episodes watched", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            int episodes = Integer.parseInt(episodesText);
            if (episodes < 0) {
                Toast.makeText(this, "Episodes cannot be negative", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number for episodes", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void loadAnimeDetails() {
        showLoading(true);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Anime> call = apiService.getAnimeDetails(animeId,
                "id,title,main_picture,synopsis,mean,rank,popularity,media_type,status,episodes,genres,studios,start_date,end_date"
        );

        call.enqueue(new Callback<Anime>() {
            @Override
            public void onResponse(Call<Anime> call, Response<Anime> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    Anime anime = response.body();
                    displayAnimeDetails(anime);
                    checkIfInUserList();
                } else {
                    Log.e("AnimeDetail", "Failed to load details: " + response.code());
                    Toast.makeText(AnimeDetailActivity.this, "Failed to load anime details", Toast.LENGTH_SHORT).show();
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("AnimeDetail", "Error: " + errorBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Anime> call, Throwable t) {
                showLoading(false);
                Log.e("AnimeDetail", "Network error: " + t.getMessage());
                Toast.makeText(AnimeDetailActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayAnimeDetails(Anime anime) {
        // Title
        tvTitle.setText(anime.getTitle());

        // Image
        if (anime.getMain_picture() != null && anime.getMain_picture().getMedium() != null) {
            Glide.with(this)
                    .load(anime.getMain_picture().getMedium())
                    .placeholder(R.drawable.ic_anime_placeholder)
                    .into(ivAnimeCover);
        }

        // Synopsis
        if (anime.getSynopsis() != null && !anime.getSynopsis().isEmpty()) {
            tvSynopsis.setText(anime.getSynopsis());
        } else {
            tvSynopsis.setText("No synopsis available.");
        }

        // Score
        if (anime.getMean() != null) {
            tvScore.setText(String.format("Score: %.2f", anime.getMean()));
        } else {
            tvScore.setText("Score: N/A");
        }

        // Episodes
        tvEpisodes.setText(String.format("Episodes: %d", anime.getEpisodes()));

        // Type and Status
        if (anime.getMedia_type() != null) {
            tvType.setText(String.format("Type: %s", anime.getMedia_type()));
        }

        if (anime.getStatus() != null) {
            tvStatus.setText(String.format("Status: %s", anime.getStatus()));
        }
    }

    private void checkIfInUserList() {
        if (!authManager.isLoggedIn()) {
            updateButtonState(false);
            return;
        }

        String accessToken = authManager.getAccessToken();
        ApiService apiService = ApiClient.getClientWithAuth(accessToken).create(ApiService.class);

        // Search only for this anime in the user's list.
        Call<UserAnimeListResponse> call = apiService.getUserAnimeList(
                "list_status",
                null,
                1,
                0
        );

        // Note: This is a simplified check
        // In a real implementation, you would search for the specific anime
        updateButtonState(true);
    }

    private void updateAnimeStatus() {
        if (!authManager.isLoggedIn()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        String accessToken = authManager.getAccessToken();
        String status = getStatusFromSpinner();
        int score = spScore.getSelectedItemPosition();
        int episodesWatched = Integer.parseInt(etEpisodesWatched.getText().toString());
        String comments = etComments.getText().toString();

        showLoading(true);

        ApiService apiService = ApiClient.getClientWithAuth(accessToken).create(ApiService.class);
        Call<UpdateAnimeStatusResponse> call = apiService.updateAnimeStatus(
                animeId,
                status,
                score,
                episodesWatched,
                false, // is_rewatching
                null,  // start_date
                null,  // finish_date
                0,     // priority
                0,     // num_times_rewatched
                0,     // rewatch_value
                "",    // tags
                comments
        );

        call.enqueue(new Callback<UpdateAnimeStatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<UpdateAnimeStatusResponse> call, @NonNull Response<UpdateAnimeStatusResponse> response) {
                showLoading(false);

                if (response.isSuccessful()) {
                    Log.d("AnimeDetail", "Anime status updated successfully");
                    Toast.makeText(AnimeDetailActivity.this, "Anime added to your list!", Toast.LENGTH_SHORT).show();
                    updateButtonState(true);

                    // Back to list after a delay
                    new android.os.Handler().postDelayed(() -> {
                        finish();
                    }, 1500);

                } else {
                    Log.e("AnimeDetail", "Update failed: " + response.code());
                    Toast.makeText(AnimeDetailActivity.this, "Failed to update anime", Toast.LENGTH_SHORT).show();

                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("AnimeDetail", "Error: " + errorBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<UpdateAnimeStatusResponse> call, @NonNull Throwable t) {
                showLoading(false);
                Log.e("AnimeDetail", "Network error: " + t.getMessage());
                Toast.makeText(AnimeDetailActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteAnimeFromList() {
        if (!authManager.isLoggedIn()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        String accessToken = authManager.getAccessToken();

        showLoading(true);

        ApiService apiService = ApiClient.getClientWithAuth(accessToken).create(ApiService.class);
        Call<Void> call = apiService.deleteAnimeFromList(animeId);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                showLoading(false);

                if (response.isSuccessful()) {
                    Toast.makeText(AnimeDetailActivity.this, "Anime removed from list", Toast.LENGTH_SHORT).show();
                    updateButtonState(false);

                    // Back to list after a delay
                    new android.os.Handler().postDelayed(() -> {
                        finish();
                    }, 1500);

                } else {
                    Log.e("AnimeDetail", "Delete failed: " + response.code());
                    Toast.makeText(AnimeDetailActivity.this, "Failed to remove anime", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                showLoading(false);
                Log.e("AnimeDetail", "Network error: " + t.getMessage());
                Toast.makeText(AnimeDetailActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Remove from List")
                .setMessage("Are you sure you want to remove this anime from your list?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    deleteAnimeFromList();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private String getStatusFromSpinner() {
        int position = spStatus.getSelectedItemPosition();
        if (position >= 1 && position <= 5) {
            return STATUS_OPTIONS[position - 1];
        }
        return "plan_to_watch"; // default
    }

    private void updateButtonState(boolean isInList) {
        if (isInList) {
            btnSave.setText("Update List Status");
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnSave.setText("Add to My List");
            btnDelete.setVisibility(View.GONE);
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!show);
        btnDelete.setEnabled(!show);
    }
}