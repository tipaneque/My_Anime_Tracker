package com.gitlab.bluestring.myanimetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.gitlab.bluestring.myanimetracker.adapters.PostAdapter;
import com.gitlab.bluestring.myanimetracker.model.Post;
import com.gitlab.bluestring.myanimetracker.model.Tag;
import com.gitlab.bluestring.myanimetracker.model.FavoriteResponse;
import com.gitlab.bluestring.myanimetracker.model.PostViewModel;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    private PostViewModel viewModel;
    private PostAdapter adapter;
    private ProgressBar progressBar;
    private EditText searchEditText;
    private Button searchButton, refreshButton;
    private Spinner tagsSpinner;
    private RecyclerView postsRecyclerView;
    private androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        initViews();
        setupViewModel();
        setupRecyclerView();
        setupObservers();
        setupClickListeners();

        // Carregar dados iniciais
        viewModel.loadPopularTags();
        viewModel.loadPosts("rating:safe");
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        refreshButton = findViewById(R.id.refreshButton);
        tagsSpinner = findViewById(R.id.tagsSpinner);
        postsRecyclerView = findViewById(R.id.postsRecyclerView);
        toolbar = findViewById(R.id.toolbar);

        // Configurar toolbar - AGORA DEVE FUNCIONAR
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Anime Gallery");
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(PostViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new PostAdapter();
        postsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        postsRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post post) {
                openPostDetail(post.getId());
            }

            @Override
            public void onFavoriteClick(Post post) {
                if (post.getIsFavorited() != null && post.getIsFavorited()) {
                    viewModel.unfavoritePost(post.getId()); // DELETE
                } else {
                    viewModel.favoritePost(post.getId()); // POST
                }
            }
        });
    }

    private void setupObservers() {
        viewModel.getPosts().observe(this, posts -> {
            if (posts != null) {
                adapter.setPosts(posts);
            }
        });

        viewModel.getTags().observe(this, tags -> {
            if (tags != null) {
                updateTagsSpinner(tags);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getFavoriteResult().observe(this, result -> {
            if (result != null) {
                String message = result.isSuccess() ?
                        "Operação realizada!" : "Erro: " + result.getMessage();
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        if (searchButton != null) {
            searchButton.setOnClickListener(v -> {
                String query = searchEditText.getText().toString();
                if (!query.isEmpty()) {
                    viewModel.loadPosts(query);
                }
            });
        }

        if (refreshButton != null) {
            refreshButton.setOnClickListener(v -> {
                viewModel.loadPosts("rating:safe");
            });
        }

        if (tagsSpinner != null) {
            tagsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Tag selectedTag = (Tag) parent.getItemAtPosition(position);
                    if (selectedTag != null) {
                        viewModel.loadPosts(selectedTag.getName());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
    }

    private void updateTagsSpinner(List<Tag> tags) {
        if (tagsSpinner != null && tags != null) {
            ArrayAdapter<Tag> spinnerAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    tags
            );
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            tagsSpinner.setAdapter(spinnerAdapter);
        }
    }

    private void openPostDetail(int postId) {
        Toast.makeText(this, "Abrir detalhes do post: " + postId, Toast.LENGTH_SHORT).show();

        // Exemplo: mudar rating usando PUT
        viewModel.updatePostRating(postId, "safe");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}